package com.example.kernelsustyleuikit.ui.screen.home

import androidx.compose.runtime.Immutable
import com.example.kernelsustyleuikit.ui.util.LatestVersionInfo

@Immutable
data class HomeUiState(
    val checkUpdateEnabled: Boolean,
    val latestVersionInfo: LatestVersionInfo,
    val currentAppVersionCode: Long,
    val systemInfo: SystemInfo,
) {
    val hasUpdate: Boolean
        get() = latestVersionInfo.versionCode > currentAppVersionCode
}

@Immutable
data class HomeActions(
    val onPermissionsClick: () -> Unit,
    val onOpenUrl: (String) -> Unit,
)
