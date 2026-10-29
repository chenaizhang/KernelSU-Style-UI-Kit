package com.example.kernelsustyleuikit.permission

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.drawable.AdaptiveIconDrawable
import android.provider.Settings
import androidx.test.platform.app.InstrumentationRegistry
import com.example.kernelsustyleuikit.R
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TemplateConfigurationTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun permissionExamplesUseSupportedSettingsIntents() {
        val manager = PermissionManager(context)
        assertEquals(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, manager.storageSettingsIntent().action)
        assertEquals("package:${context.packageName}", manager.storageSettingsIntent().dataString)
        assertEquals(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, manager.overlaySettingsIntent().action)
        assertEquals("package:${context.packageName}", manager.overlaySettingsIntent().dataString)
        assertEquals(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS, manager.batteryWhitelistIntent().action)
        assertNull(manager.batteryWhitelistIntent().data)
        listOf(manager.storageSettingsIntent(), manager.batteryWhitelistIntent(), manager.overlaySettingsIntent())
            .forEach { assertTrue("No system handler for ${it.action}", it.resolveActivity(context.packageManager) != null) }
    }

    @Test
    fun obsoleteStorageAndDirectBatteryExemptionPermissionsAreNotPackaged() {
        @Suppress("DEPRECATION")
        val permissions = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
            .requestedPermissions.orEmpty().toSet()
        assertFalse(Manifest.permission.READ_EXTERNAL_STORAGE in permissions)
        assertFalse(Manifest.permission.WRITE_EXTERNAL_STORAGE in permissions)
        assertFalse(Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS in permissions)
        assertTrue(Manifest.permission.MANAGE_EXTERNAL_STORAGE in permissions)
    }

    @Test
    fun launcherUsesAdaptiveIconWithoutLegacyPngs() {
        assertTrue(context.packageManager.getApplicationIcon(context.packageName) is AdaptiveIconDrawable)
    }

    @Test
    fun uiTextUsesTheSelectedLanguageAndEnglishFallback() {
        val ids = listOf(
            R.string.home_sample_notification,
            R.string.search,
            R.string.clear_search,
            R.string.markdown_commit_suggestion,
        )
        val english = listOf("This is a sample notification", "Search", "Clear search", "Commit")
        val traditional = listOf("這是一則範例通知", "搜尋", "清除搜尋內容", "提交")
        val cases = mapOf(
            "en-US" to english,
            "zh-CN" to listOf("这是一条示例通知", "搜索", "清除搜索内容", "提交"),
            "zh-TW" to traditional,
            "zh-HK" to traditional,
            "fr-FR" to english,
        )
        cases.forEach { (tag, expected) ->
            val configuration = Configuration(context.resources.configuration).apply {
                setLocale(Locale.forLanguageTag(tag))
            }
            val resources = context.createConfigurationContext(configuration).resources
            assertEquals(tag, expected, ids.map { resources.getString(it) })
        }
    }
}
