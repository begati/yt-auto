package dev.begati.youtubeauto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** ASPECT-01..09 (spec video-fill). FILL-01..03 are layout, proven by the walk. */
class VideoFillTest {
    @Test
    fun `ASPECT-05 without a tap the saved default applies`() {
        assertEquals("cover", aspect(MemoryPrefs().apply { map["aspect"] = "cover" }, null))
        assertEquals("fill", aspect(MemoryPrefs().apply { map["aspect"] = "fill" }, null))
    }

    @Test
    fun `ASPECT-06 options are Ajustar Preencher Esticar in order`() {
        assertEquals("Proporção do vídeo", ASPECT.title)
        assertEquals(listOf("contain" to "Ajustar", "cover" to "Preencher", "fill" to "Esticar"), ASPECT.options.toList())
        assertTrue(ASPECT in CHOICES)
    }

    @Test
    fun `ASPECT-07 default is contain`() {
        assertEquals("contain", aspect(MemoryPrefs(), null))
    }

    @Test
    fun `ASPECT-08 invalid stored aspect reads as contain`() {
        assertEquals("contain", aspect(MemoryPrefs().apply { map["aspect"] = "stretch" }, null))
    }

    @Test
    fun `ASPECT-09 changing the default drops the tapped mode`() {
        val prefs = MemoryPrefs()
        var tap: String? = tapAspect(prefs, null) // Ajustar -> Preencher
        assertEquals("cover", aspect(prefs, tap))
        setChoice(prefs, ASPECT, "fill")
        tap = tapAfterChange(ASPECT.key, tap)
        assertEquals("fill", aspect(prefs, tap))
        // Back to the old default: the old pick stays dropped.
        setChoice(prefs, ASPECT, "contain")
        tap = tapAfterChange(ASPECT.key, tap)
        assertEquals("contain", aspect(prefs, tap))
        // Other settings keep the pick.
        assertEquals("fill", tapAfterChange(QUALITY.key, "fill"))
    }

    @Test
    fun `ASPECT-01 tapping cycles contain cover fill contain`() {
        val prefs = MemoryPrefs()
        var tap: String? = null
        val seen = (1..4).map { tap = tapAspect(prefs, tap); aspect(prefs, tap) }
        assertEquals(listOf("cover", "fill", "contain", "cover"), seen)
        // The cycle starts from the saved default, not from Ajustar.
        val esticar = MemoryPrefs().apply { map["aspect"] = "fill" }
        assertEquals("contain", aspect(esticar, tapAspect(esticar, null)))
    }

    @Test
    fun `ASPECT-04 tapping leaves the saved default unchanged`() {
        val prefs = MemoryPrefs().apply { map["aspect"] = "cover" }
        var tap: String? = null
        repeat(2) { tap = tapAspect(prefs, tap) }
        assertEquals("contain", aspect(prefs, tap))
        assertEquals(mapOf<String, Any?>("aspect" to "cover"), prefs.map)
    }
}
