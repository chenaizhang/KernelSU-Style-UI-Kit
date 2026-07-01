package com.example.kernelsustyleuikit.ui.viewmodel

import androidx.compose.runtime.Immutable
import com.example.kernelsustyleuikit.ui.UiMode
import com.example.kernelsustyleuikit.ui.theme.AppSettings

@Immutable
data class MainActivityUiState(
    val appSettings: AppSettings,
    val pageScale: Float,
    val enableBlur: Boolean,
    val enableFloatingBottomBar: Boolean,
    val enableFloatingBottomBarBlur: Boolean,
    val uiMode: UiMode,
)
