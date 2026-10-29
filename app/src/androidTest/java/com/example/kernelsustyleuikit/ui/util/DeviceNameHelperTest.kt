package com.example.kernelsustyleuikit.ui.util

import android.os.Build
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DeviceNameHelperTest {
    private val key = "SEC_FLOATING_FEATURE_SETTINGS_CONFIG_BRAND_NAME"

    @Test
    fun samsungFeatureXmlReturnsTheTrimmedProductName() {
        val xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <SecFloatingFeatureSet>
                <OTHER_SETTING>not a device name</OTHER_SETTING>
                <$key>
                    Galaxy S25 Ultra
                </$key>
            </SecFloatingFeatureSet>
        """.trimIndent()
        assertEquals("Galaxy S25 Ultra", readSamsungProductName(xml.reader()))
    }

    @Test
    fun samsungFeatureXmlSupportsStandardXmlText() {
        assertEquals("Galaxy & Phone", readSamsungProductName("<root><$key>Galaxy &amp; Phone</$key></root>".reader()))
        assertEquals("Galaxy S25", readSamsungProductName("<root><$key><![CDATA[Galaxy S25]]></$key></root>".reader()))
    }

    @Test
    fun samsungFeatureXmlRejectsMissingInvalidOrMalformedValues() {
        listOf("", " ", "unknown", " UnKnOwN ", "null", " NULL ").forEach { invalid ->
            assertNull(readSamsungProductName("<root><$key>$invalid</$key></root>".reader()))
        }
        assertNull(readSamsungProductName("<root><OTHER_SETTING>Galaxy S25</OTHER_SETTING></root>".reader()))
        assertNull(readSamsungProductName("<root><$key>broken".reader()))
    }

    @Test
    fun runtimeLookupMatchesTheSameRulesWithTheRealPlatformInputs() {
        assertEquals(
            resolveDeviceName(
                manufacturer = Build.MANUFACTURER.orEmpty(),
                brand = Build.BRAND.orEmpty(),
                model = Build.MODEL.orEmpty(),
                readProperty = ::getSystemProperty,
                readSamsungName = { getSamsungProductName() },
            ),
            resolveDeviceName(),
        )
    }
}
