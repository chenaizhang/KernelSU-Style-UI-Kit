package com.example.kernelsustyleuikit.ui.screen.home

import android.content.ContentValues
import android.graphics.Bitmap
import android.os.SystemClock
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityNodeInfo
import android.view.inspector.WindowInspector
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.example.kernelsustyleuikit.R
import com.example.kernelsustyleuikit.permission.PermissionState
import com.example.kernelsustyleuikit.ui.LocalUiMode
import com.example.kernelsustyleuikit.ui.UiMode
import com.example.kernelsustyleuikit.ui.theme.AppSettings
import com.example.kernelsustyleuikit.ui.theme.ColorMode
import com.example.kernelsustyleuikit.ui.theme.LocalColorMode
import com.example.kernelsustyleuikit.ui.theme.TemplateTheme
import com.example.kernelsustyleuikit.ui.util.LatestVersionInfo
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicReference
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeUpdateCardTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(ComponentActivity::class.java)

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val openedUrls = CopyOnWriteArrayList<String>()
    private val downloadUrl = "https://example.com/template-v0.1.2_102-release.apk"
    private val context get() = instrumentation.targetContext
    private val dialogTitle get() = context.getString(R.string.update_changelog)
    private val updateLabel get() = context.getString(R.string.update)
    private val cancelLabel get() = context.getString(android.R.string.cancel)

    @Test
    fun miuixShowsMarkdownAndSupportsCancelAndUpdate() = verifyMarkdown(UiMode.Miuix)

    @Test
    fun materialShowsMarkdownAndSupportsCancelAndUpdate() = verifyMarkdown(UiMode.Material)

    @Test
    fun miuixDarkModeShowsMarkdownAndSupportsCancelAndUpdate() = verifyMarkdown(UiMode.Miuix, dark = true)

    @Test
    fun materialDarkModeShowsMarkdownAndSupportsCancelAndUpdate() = verifyMarkdown(UiMode.Material, dark = true)

    @Test
    fun miuixEmptyChangelogOpensDownloadWithoutADialog() = verifyEmptyChangelog(UiMode.Miuix)

    @Test
    fun materialEmptyChangelogOpensDownloadWithoutADialog() = verifyEmptyChangelog(UiMode.Material)

    @Test
    fun miuixHidesEqualOlderAndUnavailableVersions() = verifyNoUpdate(UiMode.Miuix)

    @Test
    fun materialHidesEqualOlderAndUnavailableVersions() = verifyNoUpdate(UiMode.Material)

    @Test
    fun miuixDisablingChecksRemovesTheNoticeAndDialog() = verifyDisabled(UiMode.Miuix)

    @Test
    fun materialDisablingChecksRemovesTheNoticeAndDialog() = verifyDisabled(UiMode.Material)

    private fun verifyMarkdown(mode: UiMode, dark: Boolean = false) {
        showHome(mode, state(changelog = changelog()), dark)
        await { node(notice()) != null }
        assertNull("Update logs must not open automatically", node(dialogTitle))
        assertTrue(openedUrls.isEmpty())
        val imagePrefix = "${mode.value}-${if (dark) "dark" else "light"}"
        capture("$imagePrefix-update-home")

        click(notice())
        await { node(dialogTitle) != null && onMain { currentWebView() != null } }
        val view = onMain { requireNotNull(currentWebView()) }
        val result = AtomicReference<JSONObject?>()
        await {
            onMain {
                view.evaluateJavascript("""
                    (function() {
                        if (!document.body || !document.querySelector('h1')) return null;
                        return {
                            heading: document.querySelector('h1').textContent,
                            strong: document.querySelector('strong')?.textContent,
                            listItems: document.querySelectorAll('li').length,
                            code: document.querySelector('pre code')?.textContent.trim(),
                            tables: document.querySelectorAll('table').length,
                            complete: document.body.textContent.includes('End of changelog')
                        };
                    })()
                """.trimIndent()) { value ->
                    result.set(runCatching { JSONObject(value) }.getOrNull())
                }
            }
            result.get()?.optBoolean("complete") == true
        }
        val rendered = requireNotNull(result.get())
        assertEquals("Release 0.1.2", rendered.getString("heading"))
        assertEquals("Important", rendered.getString("strong"))
        assertEquals(2, rendered.getInt("listItems"))
        assertEquals("versionCode = 102", rendered.getString("code"))
        assertEquals(1, rendered.getInt("tables"))
        await { node("Release 0.1.2") != null }
        // Let the loading crossfade and dialog resize finish before visual verification.
        instrumentation.waitForIdleSync()
        SystemClock.sleep(500)
        assertTrue(requireNotNull(node(updateLabel)).isVisibleToUser)
        assertTrue(requireNotNull(node(cancelLabel)).isVisibleToUser)
        capture("$imagePrefix-update-dialog")

        val scrollable = requireNotNull(instrumentation.uiAutomation.rootInActiveWindow?.let { root ->
            findNode(root) { it.isScrollable && it.className?.toString() != WebView::class.java.name }
        })
        assertTrue("Long logs must be scrollable", scrollable.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD))
        assertTrue(requireNotNull(node(updateLabel)).isVisibleToUser)
        click(cancelLabel)
        await { node(dialogTitle) == null }
        assertTrue("Cancel must not start a download", openedUrls.isEmpty())

        click(notice())
        await { node(dialogTitle) != null }
        click(updateLabel)
        await { openedUrls.size == 1 && node(dialogTitle) == null }
        assertEquals(listOf(downloadUrl), openedUrls.toList())
    }

    private fun verifyEmptyChangelog(mode: UiMode) {
        showHome(mode, state(changelog = ""))
        await { node(notice()) != null }
        click(notice())
        await { openedUrls.size == 1 }
        assertEquals(listOf(downloadUrl), openedUrls.toList())
        assertNull(node(dialogTitle))
        assertNull(onMain { currentWebView() })
    }

    private fun verifyNoUpdate(mode: UiMode) {
        val state = showHome(mode, state())
        await { node(notice()) != null }
        for (version in listOf(101, 100, 0)) {
            activityRule.scenario.onActivity {
                state.value = state.value.copy(latestVersionInfo = LatestVersionInfo(version, downloadUrl, "# Release"))
            }
            await { node(notice()) == null && node(notice(version)) == null }
            assertNull(node(notice(version)))
            assertNull(node(dialogTitle))
        }
        assertTrue(openedUrls.isEmpty())
    }

    private fun verifyDisabled(mode: UiMode) {
        val state = showHome(mode, state())
        await { node(notice()) != null }
        click(notice())
        await { node(dialogTitle) != null }
        activityRule.scenario.onActivity { state.value = state.value.copy(checkUpdateEnabled = false) }
        await { node(dialogTitle) == null && node(notice()) == null }
        assertTrue(openedUrls.isEmpty())
        activityRule.scenario.onActivity { state.value = state.value.copy(checkUpdateEnabled = true) }
        await { node(notice()) != null }
        assertNull("Enabling checks must not automatically reopen the log", node(dialogTitle))
    }

    private fun showHome(mode: UiMode, initial: HomeUiState, dark: Boolean = false): MutableState<HomeUiState> {
        val state = mutableStateOf(initial)
        val colorMode = if (dark) ColorMode.DARK else ColorMode.LIGHT
        activityRule.scenario.onActivity { activity ->
            activity.setContent {
                val systemDensity = LocalDensity.current
                CompositionLocalProvider(
                    LocalUiMode provides mode,
                    LocalColorMode provides colorMode.value,
                    LocalDensity provides Density(systemDensity.density, fontScale = 1f),
                ) {
                    TemplateTheme(
                        appSettings = AppSettings(colorMode, 0, PaletteStyle.TonalSpot, ColorSpec.SpecVersion.Default),
                        uiMode = mode,
                    ) {
                        val actions = HomeActions({}, { openedUrls.add(it) })
                        when (mode) {
                            UiMode.Miuix -> HomePagerMiuix(state.value, PermissionState(), actions, 0.dp)
                            UiMode.Material -> HomePagerMaterial(state.value, PermissionState(), actions, 0.dp)
                        }
                    }
                }
            }
        }
        return state
    }

    private fun state(changelog: String = "# Release 0.1.2") = HomeUiState(
        checkUpdateEnabled = true,
        latestVersionInfo = LatestVersionInfo(102, downloadUrl, changelog),
        currentAppVersionCode = 101,
        systemInfo = SystemInfo("0.1.1 (101)", "Example Phone", "17 (API 37)"),
    )

    private fun changelog() = buildString {
        append("# Release 0.1.2\n\n**Important**\n\n- First change\n- Second change\n\n")
        append("| Feature | State |\n| --- | --- |\n| Markdown | Ready |\n\n")
        append("```kotlin\nversionCode = 102\n```\n\n")
        repeat(16) { append("Paragraph ${it + 1}: Review this release before updating. Changes preserve both interface styles and the existing application settings.\n\n") }
        append("End of changelog")
    }

    private fun notice(version: Int = 102) = context.getString(R.string.new_version_available, version)

    private fun node(text: String): AccessibilityNodeInfo? = instrumentation.uiAutomation.rootInActiveWindow?.let { root ->
        findNode(root) { it.isVisibleToUser && (it.text?.toString() == text || it.contentDescription?.toString() == text) }
    }

    private fun click(text: String) {
        var target: AccessibilityNodeInfo? = null
        await {
            target = node(text)
            while (target != null && !target!!.isClickable) target = target!!.parent
            target != null
        }
        assertTrue(requireNotNull(target).performAction(AccessibilityNodeInfo.ACTION_CLICK))
    }

    private fun findNode(node: AccessibilityNodeInfo, predicate: (AccessibilityNodeInfo) -> Boolean): AccessibilityNodeInfo? {
        if (predicate(node)) return node
        for (index in 0 until node.childCount) {
            val child = node.getChild(index) ?: continue
            findNode(child, predicate)?.let { return it }
        }
        return null
    }

    private fun currentWebView(): WebView? = WindowInspector.getGlobalWindowViews().firstNotNullOfOrNull(::findWebView)

    private fun findWebView(view: View): WebView? {
        if (view is WebView) return view
        if (view is ViewGroup) {
            for (index in 0 until view.childCount) findWebView(view.getChildAt(index))?.let { return it }
        }
        return null
    }

    private fun <T> onMain(block: () -> T): T {
        var result: Result<T>? = null
        instrumentation.runOnMainSync { result = runCatching(block) }
        return requireNotNull(result).getOrThrow()
    }

    private fun capture(name: String) {
        if (InstrumentationRegistry.getArguments().getString("captureScreenshots") != "true") return
        val bitmap = requireNotNull(instrumentation.uiAutomation.takeScreenshot())
        try {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "$name.png")
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/TemplateUpdateTests")
            }
            val uri = requireNotNull(context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values))
            requireNotNull(context.contentResolver.openOutputStream(uri)).use { output ->
                assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, 100, output))
            }
        } finally {
            bitmap.recycle()
        }
    }

    private fun await(condition: () -> Boolean) {
        val deadline = SystemClock.uptimeMillis() + 15_000
        while (!condition()) {
            if (SystemClock.uptimeMillis() >= deadline) {
                runCatching { capture("update-timeout") }
                error("Timed out waiting for update UI")
            }
            SystemClock.sleep(50)
        }
    }
}
