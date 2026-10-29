package com.example.kernelsustyleuikit.ui.viewmodel

import com.example.kernelsustyleuikit.data.repository.SettingsRepository
import com.example.kernelsustyleuikit.ui.screen.settings.SettingsUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun blurAndPredictiveBackAreEnabledByDefault() {
        val defaults = SettingsUiState()
        assertTrue(defaults.enableBlur)
        assertTrue(defaults.enablePredictiveBack)
        val viewModel = SettingsViewModel(MemorySettingsRepository())
        assertTrue(viewModel.uiState.value.enableBlur)
        assertTrue(viewModel.uiState.value.enablePredictiveBack)
    }

    @Test
    fun disabledBlurAndPredictiveBackSurviveRecreationAndStyleChanges() {
        val repository = MemorySettingsRepository()
        val viewModel = SettingsViewModel(repository)
        viewModel.setEnableBlur(false)
        viewModel.setEnablePredictiveBack(false)
        assertFalse(repository.enableBlur)
        assertFalse(repository.enablePredictiveBack)

        val restored = SettingsViewModel(repository)
        listOf("material", "miuix").forEach { mode ->
            restored.setUiMode(mode)
            restored.refresh()
            assertFalse(restored.uiState.value.enableBlur)
            assertFalse(restored.uiState.value.enablePredictiveBack)
        }
    }

    @Test
    fun gestureChangesSurviveViewModelRecreationAndUiModeSwitch() {
        val repository = MemorySettingsRepository()
        val viewModel = SettingsViewModel(repository)
        viewModel.setEnableSwipeDismiss(false)
        viewModel.setPagerInterceptionMode(2)
        viewModel.setUiMode("material")
        assertFalse(viewModel.uiState.value.enableSwipeDismiss)
        assertEquals(2, viewModel.uiState.value.pagerInterceptionMode)

        val restored = SettingsViewModel(repository)
        assertFalse(restored.uiState.value.enableSwipeDismiss)
        assertEquals(2, restored.uiState.value.pagerInterceptionMode)
        restored.setUiMode("miuix")
        assertFalse(restored.uiState.value.enableSwipeDismiss)
        assertEquals(2, restored.uiState.value.pagerInterceptionMode)
        assertTrue(restored.uiState.value.enableFloatingBottomBar)
        assertTrue(restored.uiState.value.enableFloatingBottomBarBlur)
    }

    @Test
    fun refreshReadsGestureChangesFromRepository() {
        val repository = MemorySettingsRepository()
        val viewModel = SettingsViewModel(repository)
        repository.enableSwipeDismiss = false
        repository.pagerInterceptionMode = 0
        viewModel.refresh()
        assertFalse(viewModel.uiState.value.enableSwipeDismiss)
        assertEquals(0, viewModel.uiState.value.pagerInterceptionMode)
    }

    @Test
    fun invalidPagerModesAreClampedBeforePersistence() {
        val repository = MemorySettingsRepository()
        val viewModel = SettingsViewModel(repository)
        viewModel.setPagerInterceptionMode(-1)
        assertEquals(0, repository.pagerInterceptionMode)
        assertEquals(0, viewModel.uiState.value.pagerInterceptionMode)
        viewModel.setPagerInterceptionMode(3)
        assertEquals(2, repository.pagerInterceptionMode)
        assertEquals(2, viewModel.uiState.value.pagerInterceptionMode)
    }
}

private class MemorySettingsRepository : SettingsRepository {
    private val defaults = SettingsUiState()
    override var uiMode = defaults.uiMode
    override var checkUpdate = defaults.checkUpdate
    override var themeMode = defaults.themeMode
    override var miuixMonet = defaults.miuixMonet
    override var keyColor = defaults.keyColor
    override var colorStyle = defaults.colorStyle
    override var colorSpec = defaults.colorSpec
    override var enablePredictiveBack = defaults.enablePredictiveBack
    override var enableSwipeDismiss = defaults.enableSwipeDismiss
    override var pagerInterceptionMode = defaults.pagerInterceptionMode
    override var enableBlur = defaults.enableBlur
    override var enableFloatingBottomBar = defaults.enableFloatingBottomBar
    override var enableFloatingBottomBarBlur = defaults.enableFloatingBottomBarBlur
    override var navigationRailExpanded = false
    override var pageScale = defaults.pageScale
}
