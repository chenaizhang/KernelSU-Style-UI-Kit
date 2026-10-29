package com.example.kernelsustyleuikit.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.os.PowerManager
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.net.toUri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PermissionManager(context: Context) {

    private val appContext = context.applicationContext
    private val _state = MutableStateFlow(readState())

    val state: StateFlow<PermissionState> = _state.asStateFlow()

    fun refresh() {
        _state.value = readState()
    }

    fun notificationRuntimePermission(): String? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.POST_NOTIFICATIONS
        } else {
            null
        }

    fun microphonePermission(): String = Manifest.permission.RECORD_AUDIO

    fun storageSettingsIntent(): Intent =
        Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
            data = "package:${appContext.packageName}".toUri()
        }

    fun notificationSettingsIntent(): Intent =
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, appContext.packageName)
        }

    fun batteryWhitelistIntent(): Intent =
        Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)

    fun overlaySettingsIntent(): Intent =
        Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
            data = "package:${appContext.packageName}".toUri()
        }

    private fun readState() = PermissionState(
        storage = Environment.isExternalStorageManager(),
        notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            hasPermission(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            NotificationManagerCompat.from(appContext).areNotificationsEnabled()
        },
        microphone = hasPermission(Manifest.permission.RECORD_AUDIO),
        batteryWhitelist =
            (appContext.getSystemService(Context.POWER_SERVICE) as PowerManager)
                .isIgnoringBatteryOptimizations(appContext.packageName),
        overlay = Settings.canDrawOverlays(appContext),
    )

    private fun hasPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(appContext, permission) == PermissionChecker.PERMISSION_GRANTED
}
