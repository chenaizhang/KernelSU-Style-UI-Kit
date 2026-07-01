package com.example.kernelsustyleuikit.ui.navigation3

import android.content.Intent
import android.net.Uri

/**
 * Deep link resolution: maps external Intent/Uri to an initial back stack.
 * Call resolve(intent) at Activity start to seed the back stack.
 */
object DeepLinkResolver {
    fun resolve(intent: Intent?): List<Route> {
        return emptyList()
    }

    fun resolve(uri: Uri?): List<Route> {
        return emptyList()
    }
}
