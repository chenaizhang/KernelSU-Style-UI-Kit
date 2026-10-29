package com.example.kernelsustyleuikit.resources

import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.w3c.dom.Element

@RunWith(Parameterized::class)
class PublishedTranslationsTest(private val locale: String) {
    private val resourcesDir = File(requireNotNull(System.getProperty("app.resourcesDir")))
    private val defaults = readStrings("values")
    private val translated = readStrings(if (locale == "en") "values" else "values-$locale")

    @Test
    fun everyPublishedLocaleHasAllTranslatableStrings() {
        assertFalse("Default strings must not be empty", defaults.isEmpty())
        assertEquals("Missing or unexpected keys in $locale", defaults.keys, translated.keys)
        translated.forEach { (name, value) ->
            assertTrue("Empty translation: $locale/$name", value.isNotBlank())
        }
    }

    @Test
    fun defaultUiCopyDoesNotContainChineseText() {
        defaults.forEach { (name, value) ->
            assertFalse("Chinese text in default English resource: $name", Regex("\\p{IsHan}").containsMatchIn(value))
        }
    }

    @Test
    fun onlySharedIdentifiersAreExcludedFromTranslation() {
        val allDefaults = readStrings("values", includeNonTranslatable = true)
        assertEquals(
            "UI copy must remain translatable",
            setOf("app_name", "home_learn_more_url"),
            allDefaults.keys - defaults.keys,
        )
    }

    @Test
    fun translatedFormatArgumentsMatchTheDefaultStrings() {
        defaults.forEach { (name, value) ->
            assertEquals(
                "Format arguments differ: $locale/$name",
                formatArguments(value),
                formatArguments(translated.getValue(name)),
            )
        }
    }

    private fun readStrings(directory: String, includeNonTranslatable: Boolean = false): Map<String, String> {
        val factory = DocumentBuilderFactory.newInstance().apply {
            setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
        }
        val nodes = factory.newDocumentBuilder()
            .parse(File(resourcesDir, "$directory/strings.xml"))
            .getElementsByTagName("string")
        val strings = (0 until nodes.length).map { nodes.item(it) as Element }
        val names = strings.map { it.getAttribute("name") }
        assertEquals("Duplicate string keys in $directory", names.size, names.distinct().size)
        return strings.filter { includeNonTranslatable || it.getAttribute("translatable") != "false" }
            .associate { it.getAttribute("name") to it.textContent }
    }

    private fun formatArguments(value: String): List<String> =
        Regex("%(?:\\d+\\$)?[-#+ 0,(<]*\\d*(?:\\.\\d+)?(?:[tT][a-zA-Z]|[a-zA-Z%])")
            .findAll(value).map { it.value }.sorted().toList()

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun locales(): List<Array<String>> = requireNotNull(System.getProperty("app.localeFilters"))
            .split(',').map { arrayOf(it) }
    }
}
