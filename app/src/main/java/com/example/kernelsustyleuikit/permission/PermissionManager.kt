package com.example.kernelsustyleuikit.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.PowerManager
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
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
        Manifest.permission.POST_NOTIFICATIONS.takeIf { Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU }

    fun microphonePermission(): String = Manifest.permission.RECORD_AUDIO

    fun legacyStoragePermission(): String? =
        Manifest.permission.READ_EXTERNAL_STORAGE.takeIf { Build.VERSION.SDK_INT < Build.VERSION_CODES.R }

    fun storageSettingsIntent(): Intent =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                data = Uri.parse("package:${appContext.packageName}")
            }
        } else {
            appDetailsIntent()
        }

    fun notificationSettingsIntent(): Intent =
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, appContext.packageName)
        }

    fun batteryWhitelistIntent(): Intent =
        Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            data = Uri.parse("package:${appContext.packageName}")
        }

    fun overlaySettingsIntent(): Intent =
        Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
            data = Uri.parse("package:${appContext.packageName}")
        }

    private fun appDetailsIntent() =
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${appContext.packageName}")
        }

    private fun readState() = PermissionState(
        storage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        },
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
