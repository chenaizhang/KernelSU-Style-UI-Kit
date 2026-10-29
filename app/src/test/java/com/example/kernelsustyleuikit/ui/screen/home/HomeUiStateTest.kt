package com.example.kernelsustyleuikit.ui.screen.home

import com.example.kernelsustyleuikit.ui.util.LatestVersionInfo
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeUiStateTest {
    @Test
    fun newerVersionIsAvailable() {
        assertTrue(state(102).hasUpdate)
    }

    @Test
    fun equalVersionIsNotAnUpdate() {
        assertFalse(state(101).hasUpdate)
    }

    @Test
    fun olderVersionIsNotAnUpdate() {
        assertFalse(state(100).hasUpdate)
    }

    @Test
    fun unavailableReleaseDoesNotShowAnUpdate() {
        assertFalse(state(0).hasUpdate)
    }

    @Test
    fun comparisonUsesTheVersionCodeRatherThanDisplayText() {
        val state = state(102).copy(systemInfo = SystemInfo("99.9.9 (101)", "Phone", "17"))
        assertTrue(state.hasUpdate)
    }

    private fun state(latestCode: Int) = HomeUiState(
        checkUpdateEnabled = true,
        latestVersionInfo = LatestVersionInfo(latestCode, "https://example.com/update.apk", "# Release"),
        currentAppVersionCode = 101,
        systemInfo = SystemInfo("0.1.1 (101)", "Phone", "17"),
    )
}
