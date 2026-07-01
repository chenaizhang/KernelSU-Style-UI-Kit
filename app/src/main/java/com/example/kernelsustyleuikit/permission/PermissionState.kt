package com.example.kernelsustyleuikit.permission

data class PermissionState(
    val storage: Boolean = false,
    val notification: Boolean = false,
    val microphone: Boolean = false,
    val batteryWhitelist: Boolean = false,
    val overlay: Boolean = false,
) {
    val requiredGranted: Boolean
        get() = storage && notification && microphone && batteryWhitelist
}
