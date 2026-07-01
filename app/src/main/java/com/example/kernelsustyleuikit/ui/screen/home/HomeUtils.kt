package com.example.kernelsustyleuikit.ui.screen.home

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.core.content.pm.PackageInfoCompat

@Immutable
data class AppVersion(
    val versionName: String,
    val versionCode: Long
)

@Immutable
data class SystemInfo(
    val appVersion: String,
)

fun getAppVersion(context: Context): AppVersion {
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)!!
    val versionCode = PackageInfoCompat.getLongVersionCode(packageInfo)
    return AppVersion(
        versionName = packageInfo.versionName!!,
        versionCode = versionCode
    )
}
