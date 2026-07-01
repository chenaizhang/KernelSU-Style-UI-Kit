package com.example.kernelsustyleuikit.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.kernelsustyleuikit.templateApp
import com.example.kernelsustyleuikit.ui.screen.home.HomeUiState
import com.example.kernelsustyleuikit.ui.screen.home.SystemInfo
import com.example.kernelsustyleuikit.ui.screen.home.getAppVersion
import com.example.kernelsustyleuikit.ui.util.LatestVersionInfo
import com.example.kernelsustyleuikit.ui.util.checkNewVersion

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(buildState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            val baseState = withContext(Dispatchers.IO) { buildState() }
            _uiState.update { baseState }
            if (baseState.checkUpdateEnabled) {
                val latestVersionInfo = withContext(Dispatchers.IO) { checkNewVersion() }
                _uiState.update { it.copy(latestVersionInfo = latestVersionInfo) }
            }
        }
    }

    private fun buildState(): HomeUiState {
        val appVersion = getAppVersion(templateApp)

        return HomeUiState(
            checkUpdateEnabled = templateApp.getSharedPreferences("settings", Context.MODE_PRIVATE)
                .getBoolean("check_update", true),
            latestVersionInfo = LatestVersionInfo(),
            currentAppVersionCode = appVersion.versionCode,
            systemInfo = SystemInfo(
                appVersion = "${appVersion.versionName} (${appVersion.versionCode})",
            ),
        )
    }
}
