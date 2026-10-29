package com.example.kernelsustyleuikit.ui.util

import android.annotation.SuppressLint
import android.os.Build
import android.util.Xml
import java.io.File
import java.io.Reader
import org.xmlpull.v1.XmlPullParser

private const val SAMSUNG_FLOATING_FEATURE_CLASS = "com.samsung.android.feature.SemFloatingFeature"
private const val KEY_SAMSUNG_PRODUCT_NAME = "SEC_FLOATING_FEATURE_SETTINGS_CONFIG_BRAND_NAME"

private val SAMSUNG_FLOATING_FEATURE_PATHS = listOf(
    "/vendor/etc/floating_feature.xml",
    "/system/etc/floating_feature.xml",
)

// Keep vendor lookup order and fallback naming aligned with KernelSU's DeviceNameHelper.
fun resolveDeviceName(): String = resolveDeviceName(
    manufacturer = Build.MANUFACTURER.orEmpty(),
    brand = Build.BRAND.orEmpty(),
    model = Build.MODEL.orEmpty(),
    readProperty = ::getSystemProperty,
    readSamsungName = { getSamsungProductName() },
)

internal fun resolveDeviceName(
    manufacturer: String,
    brand: String,
    model: String,
    readProperty: (String) -> String,
    readSamsungName: () -> String?,
): String {
    val marketName = when (manufacturer.lowercase()) {
        "samsung" -> readSamsungName()
        "xiaomi" -> readProperty("ro.product.marketname")
        "oppo", "oneplus", "realme", "oplus" -> readProperty("ro.vendor.oplus.market.name")
        "vivo" -> readProperty("ro.vivo.market.name")
        "honor", "huawei" -> readProperty("ro.config.marketing_name")
        "zte", "nubia" -> readProperty("ro.vendor.product.ztename")
        else -> null
    }.takeIfValidDeviceName()
    if (marketName != null) return marketName

    return buildString {
        append(manufacturer)
        if (!brand.equals(manufacturer, ignoreCase = true)) append(" ").append(brand)
        append(" ").append(model)
    }
}

internal fun getSamsungProductName(
    readFloatingFeature: () -> String? = ::getSamsungProductNameFromFloatingFeature,
    readFeatureFile: () -> String? = ::getSamsungProductNameFromFloatingFeatureFile,
): String? {
    val name = readFloatingFeature().takeIfValidDeviceName()
        ?: readFeatureFile().takeIfValidDeviceName()
        ?: return null
    return if (name.startsWith("Samsung", ignoreCase = true)) name else "Samsung $name"
}

@SuppressLint("PrivateApi")
private fun getSamsungProductNameFromFloatingFeature(): String? = try {
    val clazz = Class.forName(SAMSUNG_FLOATING_FEATURE_CLASS)
    val instance = clazz.getMethod("getInstance").invoke(null)
    val value = clazz.getMethod("getString", String::class.java)
        .invoke(instance, KEY_SAMSUNG_PRODUCT_NAME) as? String
    value.takeIfValidDeviceName()
} catch (_: Throwable) {
    null
}

private fun getSamsungProductNameFromFloatingFeatureFile(): String? =
    SAMSUNG_FLOATING_FEATURE_PATHS.firstNotNullOfOrNull { path ->
        try {
            File(path).reader().use(::readSamsungProductName)
        } catch (_: Throwable) {
            null
        }
    }

internal fun readSamsungProductName(reader: Reader): String? = try {
    val parser = Xml.newPullParser().apply { setInput(reader) }
    var name: String? = null
    while (parser.eventType != XmlPullParser.END_DOCUMENT) {
        if (parser.eventType == XmlPullParser.START_TAG && parser.name == KEY_SAMSUNG_PRODUCT_NAME) {
            name = parser.nextText().takeIfValidDeviceName()
            break
        }
        parser.next()
    }
    name
} catch (_: Throwable) {
    null
}

private fun String?.takeIfValidDeviceName(): String? =
    this?.trim()?.takeIf {
        it.isNotEmpty() &&
            !it.equals(Build.UNKNOWN, ignoreCase = true) &&
            !it.equals("null", ignoreCase = true)
    }
