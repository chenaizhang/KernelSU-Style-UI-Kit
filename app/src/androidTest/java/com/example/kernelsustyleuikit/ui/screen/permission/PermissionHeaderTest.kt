package com.example.kernelsustyleuikit.ui.screen.permission

import android.graphics.Rect
import android.os.SystemClock
import android.view.MotionEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.example.kernelsustyleuikit.R
import com.example.kernelsustyleuikit.ui.LocalUiMode
import com.example.kernelsustyleuikit.ui.UiMode
import com.example.kernelsustyleuikit.ui.navigation3.LocalNavigator
import com.example.kernelsustyleuikit.ui.navigation3.Navigator
import com.example.kernelsustyleuikit.ui.navigation3.Route
import com.example.kernelsustyleuikit.ui.navigation3.rememberNavigator
import com.example.kernelsustyleuikit.ui.theme.AppSettings
import com.example.kernelsustyleuikit.ui.theme.ColorMode
import com.example.kernelsustyleuikit.ui.theme.LocalColorMode
import com.example.kernelsustyleuikit.ui.theme.LocalEnableBlur
import com.example.kernelsustyleuikit.ui.theme.TemplateTheme
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import java.util.concurrent.atomic.AtomicReference
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PermissionHeaderTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(ComponentActivity::class.java)

    @Test
    fun miuixHeaderCollapsesAndReturnsWithBlurEnabled() = verifyHeader(UiMode.Miuix, blur = true)

    @Test
    fun miuixHeaderCollapsesAndReturnsWithBlurDisabled() = verifyHeader(UiMode.Miuix, blur = false)

    @Test
    fun materialHeaderStartsCompactAndReturnsAfterScrolling() = verifyHeader(UiMode.Material, blur = false)

    private fun verifyHeader(mode: UiMode, blur: Boolean) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val navigatorRef = AtomicReference<Navigator>()
        var title = ""
        var density = 1f
        activityRule.scenario.onActivity { activity ->
            title = activity.getString(R.string.permission_section)
            density = activity.resources.displayMetrics.density
            activity.setContent {
                val navigator = rememberNavigator(Route.Main)
                LaunchedEffect(navigator) {
                    navigator.push(Route.Permissions)
                    navigatorRef.set(navigator)
                }
                val systemDensity = LocalDensity.current
                CompositionLocalProvider(
                    LocalNavigator provides navigator,
                    LocalUiMode provides mode,
                    LocalEnableBlur provides blur,
                    LocalColorMode provides ColorMode.LIGHT.value,
                    LocalDensity provides Density(systemDensity.density, fontScale = 1f),
                ) {
                    TemplateTheme(
                        appSettings = AppSettings(ColorMode.LIGHT, 0, PaletteStyle.TonalSpot, ColorSpec.SpecVersion.Default),
                        uiMode = mode,
                    ) {
                        Box(Modifier.fillMaxSize()) {
                            Box(Modifier.fillMaxWidth().height(420.dp)) {
                                PermissionScreen()
                            }
                        }
                    }
                }
            }
        }

        fun root() = instrumentation.uiAutomation.rootInActiveWindow
        fun titleBounds(): Rect? = root()?.let { tree ->
            findNode(tree) { it.text?.toString() == title && it.isVisibleToUser }
                ?.let { Rect().also(it::getBoundsInScreen) }
                ?.takeUnless(Rect::isEmpty)
        }

        await { navigatorRef.get() != null && titleBounds() != null }
        if (mode == UiMode.Material) await { requireNotNull(titleBounds()).height() <= 40 * density }
        val initialTitle = requireNotNull(titleBounds())
        val list = requireNotNull(root()?.let { findNode(it) { node -> node.isScrollable } })
        val listBounds = Rect().also(list::getBoundsInScreen)

        // Real touch input must exercise nested scrolling, not just change the lazy-list offset.
        val x = listBounds.centerX().toFloat()
        val startY = listBounds.bottom - 24 * density
        val endY = maxOf(listBounds.top + 72 * density, startY - 240 * density)
        val downTime = SystemClock.uptimeMillis()
        fun send(action: Int, y: Float) {
            val event = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), action, x, y, 0)
            try {
                instrumentation.sendPointerSync(event)
            } finally {
                event.recycle()
            }
        }
        send(MotionEvent.ACTION_DOWN, startY)
        repeat(16) { step ->
            SystemClock.sleep(16)
            send(MotionEvent.ACTION_MOVE, startY + (endY - startY) * (step + 1) / 16)
        }
        send(MotionEvent.ACTION_UP, endY)
        if (mode == UiMode.Miuix) {
            await { titleBounds()?.let { it.height() < initialTitle.height() && it.top < initialTitle.top } == true }
        }
        await { titleBounds()?.height()?.let { it <= 40 * density } == true }
        val compactTitle = requireNotNull(titleBounds())
        val backButton = requireNotNull(root()?.let { tree ->
            findNode(tree) { node ->
                val bounds = Rect().also(node::getBoundsInScreen)
                node.isClickable && node.isVisibleToUser && !bounds.isEmpty &&
                    bounds.left < 80 * density && bounds.height() <= 64 * density &&
                    bounds.top < compactTitle.bottom + 24 * density
            }
        })
        assertTrue(backButton.performAction(AccessibilityNodeInfo.ACTION_CLICK))
        await { navigatorRef.get().current() == Route.Main }
    }

    private fun findNode(
        node: AccessibilityNodeInfo,
        predicate: (AccessibilityNodeInfo) -> Boolean,
    ): AccessibilityNodeInfo? {
        if (predicate(node)) return node
        for (index in 0 until node.childCount) {
            val child = node.getChild(index) ?: continue
            findNode(child, predicate)?.let { return it }
        }
        return null
    }

    private fun await(condition: () -> Boolean) {
        val deadline = SystemClock.uptimeMillis() + 10_000
        while (!condition()) {
            check(SystemClock.uptimeMillis() < deadline) { "Timed out waiting for permission header" }
            SystemClock.sleep(50)
        }
    }
}
