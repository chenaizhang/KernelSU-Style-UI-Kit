package com.example.kernelsustyleuikit.ui.navigation3

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test
import top.yukonga.miuix.kmp.nav.core.navBackStackOf

class NavigatorTest {
    @Test
    fun routesSurviveSerializationForSavedBackStack() {
        val routes: List<Route> = listOf(Route.Main, Route.Home, Route.Settings, Route.About, Route.ColorPalette, Route.Permissions)
        assertEquals(routes, Json.decodeFromString<List<Route>>(Json.encodeToString(routes)))
    }

    @Test
    fun replacingWithExistingDestinationKeepsKeysUnique() {
        val navigator = Navigator(navBackStackOf(Route.Main, Route.About, Route.ColorPalette))
        navigator.replace(Route.About)
        assertEquals(listOf(Route.Main, Route.About), navigator.backStack.toList())
        navigator.replace(Route.Permissions)
        assertEquals(listOf(Route.Main, Route.Permissions), navigator.backStack.toList())
    }

    @Test
    fun replacingStackRemovesDuplicateKeysAndIgnoresEmptyReplacement() {
        val navigator = Navigator(navBackStackOf(Route.Main))
        navigator.replaceAll(listOf(Route.Main, Route.About, Route.About))
        navigator.replaceAll(emptyList())
        assertEquals(listOf(Route.Main, Route.About), navigator.backStack.toList())
    }

    @Test
    fun repeatedTapsDoNotCreateDuplicateDestinationKeys() {
        val navigator = Navigator(navBackStackOf(Route.Main))
        navigator.push(Route.ColorPalette)
        navigator.push(Route.ColorPalette)
        assertEquals(listOf(Route.Main, Route.ColorPalette), navigator.backStack.toList())
    }

    @Test
    fun repeatedBackNeverRemovesTheRootDestination() {
        val navigator = Navigator(navBackStackOf(Route.Main, Route.About))
        navigator.pop()
        navigator.pop()
        assertEquals(listOf(Route.Main), navigator.backStack.toList())
    }

    @Test
    fun popUntilMissingDestinationKeepsRoot() {
        val navigator = Navigator(navBackStackOf(Route.Main, Route.Permissions))
        navigator.popUntil { it == Route.Settings }
        assertEquals(listOf(Route.Main), navigator.backStack.toList())
    }
}
