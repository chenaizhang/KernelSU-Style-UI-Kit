package com.example.kernelsustyleuikit.ui.util

import android.content.Context
import android.os.Build
import android.system.Os
import com.example.kernelsustyleuikit.ui.screen.home.getAppVersion
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.zip.GZIPOutputStream

fun getBugreportFile(context: Context): File {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm")
    val current = LocalDateTime.now().format(formatter)
    val targetFile = File(context.cacheDir, "KernelSUStyleUIKit_bugreport_${current}.txt.gz")

    val report = buildString {
        appendLine("App: ${getAppVersion(context)}")
        appendLine("BRAND: ${Build.BRAND}")
        appendLine("MODEL: ${Build.MODEL}")
        appendLine("PRODUCT: ${Build.PRODUCT}")
        appendLine("MANUFACTURER: ${Build.MANUFACTURER}")
        appendLine("SDK: ${Build.VERSION.SDK_INT}")
        appendLine("PREVIEW_SDK: ${Build.VERSION.PREVIEW_SDK_INT}")
        appendLine("FINGERPRINT: ${Build.FINGERPRINT}")
        appendLine("DEVICE: ${Build.DEVICE}")
        runCatching { Os.uname() }.onSuccess { uname ->
            appendLine("KernelRelease: ${uname.release}")
            appendLine("KernelVersion: ${uname.version}")
            appendLine("Machine: ${uname.machine}")
            appendLine("Nodename: ${uname.nodename}")
            appendLine("Sysname: ${uname.sysname}")
        }
        appendLine()
        appendLine("Logcat:")
        appendLine(readLogcat())
    }

    GZIPOutputStream(targetFile.outputStream()).use { output ->
        output.write(report.toByteArray())
    }

    return targetFile
}

private fun readLogcat(): String {
    return runCatching {
        ProcessBuilder("logcat", "-d", "-v", "time")
            .redirectErrorStream(true)
            .start()
            .inputStream
            .bufferedReader()
            .use { it.readText() }
            .ifBlank { "No logcat output." }
    }.getOrElse { "Unable to read logcat: ${it.message}" }
}
