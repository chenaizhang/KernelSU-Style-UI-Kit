package com.example.kernelsustyleuikit.ui.screen.home

import android.graphics.Rect
import android.os.SystemClock
import android.view.accessibility.AccessibilityNodeInfo
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
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
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomePermissionCardTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(ComponentActivity::class.java)

    @Test
    fun miuixGrantedCardShowsVersionAndOpensPermissions() = verifyCard(UiMode.Miuix, granted = true)

    @Test
    fun miuixMissingCardShowsVersionAndOpensPermissions() = verifyCard(UiMode.Miuix, granted = false)

    @Test
    fun materialGrantedCardShowsVersionAndOpensPermissions() = verifyCard(UiMode.Material, granted = true)

    @Test
    fun materialMissingCardShowsVersionAndOpensPermissions() = verifyCard(UiMode.Material, granted = false)

    private fun verifyCard(mode: UiMode, granted: Boolean) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val version = "9.8.7 (654)"
        val deviceModel = "Example Phone Pro"
        val androidVersion = "17 (API 37)"
        val clicked = AtomicBoolean(false)
        val openedUrl = AtomicReference<String>()
        val state = HomeUiState(
            checkUpdateEnabled = false,
            latestVersionInfo = LatestVersionInfo(),
            currentAppVersionCode = 654,
            systemInfo = SystemInfo(appVersion = version, deviceModel = deviceModel, androidVersion = androidVersion),
        )
        val permissions = PermissionState(
            storage = granted,
            notification = granted,
            microphone = granted,
            batteryWhitelist = granted,
        )
        val actions = HomeActions(onPermissionsClick = { clicked.set(true) }, onOpenUrl = { openedUrl.set(it) })
        var title = ""
        var summary = ""
        var learnTitle = ""
        var learnUrl = ""
        var density = 1f
        activityRule.scenario.onActivity { activity ->
            title = activity.getString(
                if (granted) R.string.permission_status_ready_title else R.string.permission_status_missing_title
            )
            summary = activity.getString(if (granted) R.string.permission_ready else R.string.permission_missing)
            learnTitle = activity.getString(R.string.home_learn_more_title, activity.getString(R.string.app_name))
            learnUrl = activity.getString(R.string.home_learn_more_url)
            density = activity.resources.displayMetrics.density
            activity.setContent {
                val systemDensity = LocalDensity.current
                CompositionLocalProvider(
                    LocalUiMode provides mode,
                    LocalColorMode provides ColorMode.LIGHT.value,
                    LocalDensity provides Density(systemDensity.density, fontScale = 1f),
                ) {
                    TemplateTheme(
                        appSettings = AppSettings(ColorMode.LIGHT, 0, PaletteStyle.TonalSpot, ColorSpec.SpecVersion.Default),
                        uiMode = mode,
                    ) {
                        when (mode) {
                            UiMode.Miuix -> HomePagerMiuix(state, permissions, actions, 0.dp)
                            UiMode.Material -> HomePagerMaterial(state, permissions, actions, 0.dp)
                        }
                    }
                }
            }
        }

        var card: AccessibilityNodeInfo? = null
        await {
            card = instrumentation.uiAutomation.rootInActiveWindow
                ?.let { findTextNode(it, title) }?.let(::clickableAncestor)
            card != null
        }
        val statusCard = requireNotNull(card)
        assertTrue(
            "Status card must use the supplied app version",
            findTextNode(statusCard, version) != null,
        )
        assertFalse(
            "Home status card should no longer show the permission explanation",
            findTextNode(statusCard, summary) != null,
        )
        val bounds = Rect().also(statusCard::getBoundsInScreen)
        val heightDp = bounds.height() / density
        val maxHeightDp = if (mode == UiMode.Miuix) 120f else 100f
        assertTrue("Unexpected status-card height: $heightDp dp", heightDp in 72f..maxHeightDp)
        assertTrue(statusCard.performAction(AccessibilityNodeInfo.ACTION_CLICK))
        await { clicked.get() }

        val root = requireNotNull(instrumentation.uiAutomation.rootInActiveWindow)
        assertTrue("Device model must come from home state", findTextNode(root, deviceModel) != null)
        assertTrue("Android version must come from home state", findTextNode(root, androidVersion) != null)
        assertTrue("The separate App version row must be removed", findTextNode(root, "App 版本") == null)
        assertTrue("The separate App version row must be removed", findTextNode(root, "App version") == null)
        val learnLink = requireNotNull(findTextNode(root, learnTitle)?.let(::clickableAncestor))
        assertTrue(learnLink.performAction(AccessibilityNodeInfo.ACTION_CLICK))
        await { openedUrl.get() != null }
        assertEquals(learnUrl, openedUrl.get())
    }

    private fun clickableAncestor(node: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        var current: AccessibilityNodeInfo? = node
        while (current != null && !current.isClickable) current = current.parent
        return current
    }

    // Compose exposes virtual descendants, but its provider need not implement text search.
    private fun findTextNode(node: AccessibilityNodeInfo, text: String): AccessibilityNodeInfo? {
        if (node.text?.contains(text) == true || node.contentDescription?.contains(text) == true) return node
        for (index in 0 until node.childCount) {
            val child = node.getChild(index) ?: continue
            findTextNode(child, text)?.let { return it }
        }
        return null
    }

    private fun await(condition: () -> Boolean) {
        val deadline = SystemClock.uptimeMillis() + 10_000
        while (!condition()) {
            check(SystemClock.uptimeMillis() < deadline) { "Timed out waiting for home status card" }
            SystemClock.sleep(50)
        }
    }
}
