package com.example.kernelsustyleuikit.ui.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DeviceNameHelperTest {
    private val vendorProperties = mapOf(
        "xiaomi" to "ro.product.marketname",
        "oppo" to "ro.vendor.oplus.market.name",
        "oneplus" to "ro.vendor.oplus.market.name",
        "realme" to "ro.vendor.oplus.market.name",
        "oplus" to "ro.vendor.oplus.market.name",
        "vivo" to "ro.vivo.market.name",
        "honor" to "ro.config.marketing_name",
        "huawei" to "ro.config.marketing_name",
        "zte" to "ro.vendor.product.ztename",
        "nubia" to "ro.vendor.product.ztename",
    )

    @Test
    fun allVendorBranchesPreferTheMarketNameOverTheModelCode() {
        vendorProperties.forEach { (manufacturer, expectedKey) ->
            val name = resolveDeviceName(
                manufacturer = manufacturer,
                brand = manufacturer,
                model = "CERTIFIED-MODEL-CODE",
                readProperty = { key ->
                    assertEquals(manufacturer, expectedKey, key)
                    "  Commercial Phone Name  "
                },
                readSamsungName = { error("Wrong vendor branch") },
            )
            assertEquals(manufacturer, "Commercial Phone Name", name)
        }
    }

    @Test
    fun manufacturerMatchingIsCaseInsensitive() {
        assertEquals(
            "Xiaomi 17 Pro Max",
            resolveDeviceName("XIAOMI", "Redmi", "MODEL-CODE", { "Xiaomi 17 Pro Max" }, { null }),
        )
    }

    @Test
    fun invalidMarketNamesFallBackToManufacturerBrandAndModel() {
        vendorProperties.forEach { (manufacturer, _) ->
            listOf("", "  ", "unknown", " UnKnOwN ", "null", " NULL ").forEach { invalid ->
                assertEquals(
                    "$manufacturer sub-brand MODEL-CODE",
                    resolveDeviceName(manufacturer, "sub-brand", "MODEL-CODE", { invalid }, { null }),
                )
            }
        }
    }

    @Test
    fun fallbackDoesNotRepeatTheManufacturerAsBrand() {
        assertEquals(
            "Xiaomi MODEL-CODE",
            resolveDeviceName("Xiaomi", "xiaomi", "MODEL-CODE", { "" }, { null }),
        )
    }

    @Test
    fun unknownManufacturersUseTheUpstreamFallbackWithoutOtherVendorLookups() {
        assertEquals(
            "Google sdk_gphone16k_arm64",
            resolveDeviceName("Google", "google", "sdk_gphone16k_arm64", { error("Unexpected lookup") }, { error("Unexpected Samsung lookup") }),
        )
        assertEquals(
            "Other Subbrand MODEL-CODE",
            resolveDeviceName("Other", "Subbrand", "MODEL-CODE", { error("Unexpected lookup") }, { null }),
        )
    }

    @Test
    fun samsungUsesItsDedicatedLookupInsteadOfSystemProperties() {
        assertEquals(
            "Samsung Galaxy S25 Ultra",
            resolveDeviceName("samsung", "samsung", "SM-CODE", { error("Unexpected property") }, { "Samsung Galaxy S25 Ultra" }),
        )
        assertEquals(
            "samsung SM-CODE",
            resolveDeviceName("samsung", "samsung", "SM-CODE", { error("Unexpected property") }, { null }),
        )
    }

    @Test
    fun samsungFloatingFeatureTakesPrecedenceAndGetsTheBrandPrefix() {
        assertEquals(
            "Samsung Galaxy S25 Ultra",
            getSamsungProductName({ " Galaxy S25 Ultra " }, { error("File fallback must not run") }),
        )
    }

    @Test
    fun samsungPrefixIsNotDuplicatedRegardlessOfCase() {
        assertEquals("Samsung Galaxy S25", getSamsungProductName({ " Samsung Galaxy S25 " }, { null }))
        assertEquals("SAMSUNG Galaxy S25", getSamsungProductName({ "SAMSUNG Galaxy S25" }, { null }))
    }

    @Test
    fun samsungInvalidFloatingFeaturesTryTheFileFallback() {
        listOf(null, "", "  ", "unknown", " UnKnOwN ", "null", " NULL ").forEach { invalid ->
            assertEquals("Samsung Galaxy S25", getSamsungProductName({ invalid }, { " Galaxy S25 " }))
            assertNull(getSamsungProductName({ invalid }, { invalid }))
        }
    }
}
