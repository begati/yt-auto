package dev.begati.youtubeauto

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Test

/** Every language ships every translatable string, so no screen falls back to English mid-sentence. */
class I18nTest {
    private fun names(file: File, translatable: Boolean = true) =
        Regex("""<string name="(\w+)"([^>]*)>""").findAll(file.readText())
            .filter { translatable || "translatable=\"false\"" !in it.groupValues[2] }
            .map { it.groupValues[1] }.toSortedSet()

    @Test
    fun `all languages have the same strings as English`() {
        val res = File("src/main/res")
        val english = names(File(res, "values/strings.xml"), translatable = false)
        for (lang in listOf("pt", "es", "fr", "it", "ru", "ar")) {
            assertEquals(lang, english, names(File(res, "values-$lang/strings.xml")))
        }
    }
}
