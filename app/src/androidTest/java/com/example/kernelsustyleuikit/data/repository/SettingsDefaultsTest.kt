package com.example.kernelsustyleuikit.data.repository

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsDefaultsTest {
    private val prefs = InstrumentationRegistry.getInstrumentation().targetContext
        .getSharedPreferences("settings", Context.MODE_PRIVATE)
    private val keys = listOf("enable_blur", "enable_predictive_back")

    @Test
    fun unsetPreferencesEnableBlurAndPredictiveBackWithoutWritingOverrides() = withRestoredSettings {
        val editor = prefs.edit()
        keys.forEach(editor::remove)
        assertTrue(editor.commit())

        val repository = SettingsRepositoryImpl()
        assertTrue(repository.enableBlur)
        assertTrue(repository.enablePredictiveBack)
        keys.forEach { assertFalse("Reading defaults must not persist them", prefs.contains(it)) }
    }

    @Test
    fun savedDisabledValuesAreRespectedByNewRepositoryInstances() = withRestoredSettings {
        val repository = SettingsRepositoryImpl()
        repository.enableBlur = false
        repository.enablePredictiveBack = false

        val restored = SettingsRepositoryImpl()
        assertFalse(restored.enableBlur)
        assertFalse(restored.enablePredictiveBack)
        keys.forEach { assertTrue(prefs.contains(it)) }
    }

    private fun withRestoredSettings(block: () -> Unit) {
        val previous = keys.associateWith { key ->
            if (prefs.contains(key)) prefs.getBoolean(key, false) else null
        }
        try {
            block()
        } finally {
            val editor = prefs.edit()
            previous.forEach { (key, value) ->
                if (value == null) editor.remove(key) else editor.putBoolean(key, value)
            }
            check(editor.commit()) { "Unable to restore test preferences" }
        }
    }
}
