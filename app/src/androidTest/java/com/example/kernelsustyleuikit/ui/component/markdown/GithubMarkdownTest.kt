package com.example.kernelsustyleuikit.ui.component.markdown

import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.example.kernelsustyleuikit.R
import com.example.kernelsustyleuikit.ui.LocalUiMode
import com.example.kernelsustyleuikit.ui.UiMode
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import top.yukonga.miuix.kmp.theme.MiuixTheme

class GithubMarkdownTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(ComponentActivity::class.java)

    private lateinit var activity: ComponentActivity

    private val content = mutableStateOf("# Renderer recovery test")
    private val visible = mutableStateOf(true)
    private val loading = AtomicBoolean(true)

    @Before
    fun captureActivity() {
        activityRule.scenario.onActivity { activity = it }
    }

    @Test
    fun materialRecoversFromActualRendererCrash() = verifyCrashRecovery(UiMode.Material)

    @Test
    fun miuixRecoversFromActualRendererCrash() = verifyCrashRecovery(UiMode.Miuix)

    private fun verifyCrashRecovery(mode: UiMode) {
        showMarkdown(mode)
        val oldView = awaitLoadedWebView()
        onMain { oldView.loadUrl("chrome://crash") }
        val replacement = awaitLoadedWebView(oldView)
        onMain {
            assertNull(oldView.parent)
            assertNotSame(oldView, replacement)
            assertTrue(replacement.tag.toString().contains("Renderer recovery test"))
        }
    }

    @Test
    fun repeatedCrashesStopRetryingAndNewContentCanLoad() {
        showMarkdown()
        repeat(3) {
            val view = awaitLoadedWebView()
            onMain { view.loadUrl("chrome://crash") }
            await { onMain { view.parent == null } }
        }
        await { !loading.get() && onMain { findWebView(activity.window.decorView) == null } }
        assertFalse(loading.get())

        onMain { content.value = "# Different document" }
        val replacement = awaitLoadedWebView()
        onMain { assertTrue(replacement.tag.toString().contains("Different document")) }
    }

    @Test
    fun recoveryWaitsUntilResumedAndUsesNewContent() {
        showMarkdown()
        val oldView = awaitLoadedWebView()
        activityRule.scenario.moveToState(Lifecycle.State.STARTED)
        onMain {
            assertTrue(requireNotNull(oldView.webViewRenderProcess).terminate())
        }
        await { onMain { oldView.parent == null } }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        onMain {
            assertNull(findWebView(activity.window.decorView))
            content.value = "# Updated while in background"
        }
        activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
        val replacement = awaitLoadedWebView(oldView)
        onMain { assertTrue(replacement.tag.toString().contains("Updated while in background")) }
    }

    @Test
    fun disposingContentRemovesViewAndReopeningCreatesFreshView() {
        showMarkdown()
        val oldView = awaitLoadedWebView()
        onMain { visible.value = false }
        await { onMain { findWebView(activity.window.decorView) == null } }
        onMain {
            assertNull(oldView.parent)
            visible.value = true
        }
        awaitLoadedWebView(oldView)
    }

    @Test
    fun updatedLoadingCallbackReceivesCompletionWithoutRecreatingView() {
        val useNewCallback = mutableStateOf(false)
        val newCallbackLoading = AtomicBoolean(true)
        onMain {
            activity.setContent {
                MaterialTheme {
                    CompositionLocalProvider(LocalUiMode provides UiMode.Material) {
                        val callbackState = if (useNewCallback.value) newCallbackLoading else loading
                        GithubMarkdown(content.value, isMarkdown = true, onLoadingChange = callbackState::set)
                    }
                }
            }
        }
        val oldView = awaitLoadedWebView()
        onMain {
            useNewCallback.value = true
            content.value = "# Updated document"
        }
        await { !newCallbackLoading.get() }
        onMain {
            assertEquals(oldView, findWebView(activity.window.decorView))
            assertTrue(oldView.tag.toString().contains("Updated document"))
        }
    }

    @Test
    fun pageFinishedCompletesLoadingWithoutAVisualCommit() {
        showMarkdown()
        val view = awaitLoadedWebView()
        onMain {
            loading.set(true)
            view.webViewClient.onPageFinished(view, view.url)
            assertFalse("A loaded page must not wait indefinitely for a visual commit", loading.get())
        }
    }

    @Test
    fun actionButtonUsesLocalizedPlainText() {
        showMarkdown()
        val view = awaitLoadedWebView()
        val renderButton = """
            var container = document.createElement('div');
            window.github._addSuggestedChangesButton(container, 'translation-test', '');
            var button = container.querySelector('button');
            return { label: button.textContent, images: button.querySelectorAll('img').length };
        """.trimIndent()
        val localized = evaluateResult(view, """
            (function() {
                if (!window.github) return null;
                $renderButton
            })()
        """.trimIndent())
        assertEquals(activity.getString(R.string.markdown_commit_suggestion), localized.getString("label"))

        val plainText = "<img src=x> & \"quoted\""
        val escaped = evaluateResult(view, """
            (function() {
                document.documentElement.setAttribute('data-commit-label', ${JSONObject.quote(plainText)});
                $renderButton
            })()
        """.trimIndent())
        assertEquals(plainText, escaped.getString("label"))
        assertEquals(0, escaped.getInt("images"))
    }

    private fun evaluateResult(view: WebView, script: String): JSONObject {
        val result = AtomicReference<JSONObject?>()
        await {
            onMain {
                view.evaluateJavascript(script) { value ->
                    result.set(runCatching { JSONObject(value) }.getOrNull())
                }
            }
            result.get() != null
        }
        return requireNotNull(result.get())
    }

    private fun showMarkdown(mode: UiMode = UiMode.Material) {
        onMain {
            activity.setContent {
                MaterialTheme {
                    MiuixTheme {
                        CompositionLocalProvider(LocalUiMode provides mode) {
                            if (visible.value) {
                                GithubMarkdown(content.value, isMarkdown = true, onLoadingChange = loading::set)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun awaitLoadedWebView(previous: WebView? = null): WebView {
        var result: WebView? = null
        await {
            onMain {
                result = findWebView(activity.window.decorView)
                result != null && result !== previous && !loading.get()
            }
        }
        return requireNotNull(result)
    }

    private fun await(condition: () -> Boolean) {
        val deadline = SystemClock.uptimeMillis() + 15_000
        while (!condition()) {
            check(SystemClock.uptimeMillis() < deadline) { "Timed out waiting for WebView lifecycle" }
            SystemClock.sleep(50)
        }
    }

    private fun <T> onMain(block: () -> T): T {
        var result: Result<T>? = null
        InstrumentationRegistry.getInstrumentation().runOnMainSync { result = runCatching(block) }
        return requireNotNull(result).getOrThrow()
    }

    private fun findWebView(view: View): WebView? {
        if (view is WebView) return view
        if (view is ViewGroup) {
            for (index in 0 until view.childCount) {
                findWebView(view.getChildAt(index))?.let { return it }
            }
        }
        return null
    }

}
