package com.example.kernelsustyleuikit.ui.util

import com.example.kernelsustyleuikit.templateApp
import okhttp3.Request

fun checkNewVersion(): LatestVersionInfo {
    if (!isNetworkAvailable(templateApp)) return LatestVersionInfo()
    val url = "https://api.github.com/repos/chenaizhang/KernelSU-Style-UI-Kit/releases/latest"
    // default null value if failed
    val defaultValue = LatestVersionInfo()
    runCatching {
        templateApp.okhttpClient.newCall(Request.Builder().url(url).build()).execute()
            .use { response ->
                if (!response.isSuccessful) {
                    return defaultValue
                }
                val body = response.body.string()
                val json = org.json.JSONObject(body)
                val changelog = json.optString("body")

                val assets = json.getJSONArray("assets")
                for (i in 0 until assets.length()) {
                    val asset = assets.getJSONObject(i)
                    val name = asset.getString("name")
                    if (!name.endsWith(".apk")) {
                        continue
                    }

                    val regex = Regex("v(.+?)_(\\d+)-")
                    val matchResult = regex.find(name) ?: continue
                    matchResult.groupValues[1]
                    val versionCode = matchResult.groupValues[2].toInt()
                    val downloadUrl = asset.getString("browser_download_url")

                    return LatestVersionInfo(
                        versionCode,
                        downloadUrl,
                        changelog
                    )
                }

            }
    }
    return defaultValue
}
