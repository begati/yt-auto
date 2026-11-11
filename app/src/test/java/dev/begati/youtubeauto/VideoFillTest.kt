package dev.begati.youtubeauto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** FILL-01..04 and ASPECT-01..09 (spec video-fill). */
class VideoFillTest {
    @Test
    fun `FILL-01 watch page with fullscreen fills the surface`() {
        assertTrue(fillsSurface("https://m.youtube.com/watch?v=dQw4w9WgXcQ", fullscreen = true))
        assertTrue(fillsSurface("https://m.youtube.com/watch?v=dQw4w9WgXcQ&t=42", fullscreen = true))
    }

    @Test
    fun `FILL-02 other pages stay in the visible area`() {
        assertFalse(fillsSurface("https://m.youtube.com", fullscreen = true))
        assertFalse(fillsSurface("https://m.youtube.com/feed/subscriptions", fullscreen = true))
        assertFalse(fillsSurface("https://m.youtube.com/results?search_query=watch", fullscreen = true))
        assertFalse(fillsSurface("https://m.youtube.com/watchlater", fullscreen = true))
        assertFalse(fillsSurface("", fullscreen = true))
    }

    @Test
    fun `FILL-03 fullscreen off keeps watch in the visible area`() {
        assertFalse(fillsSurface("https://m.youtube.com/watch?v=dQw4w9WgXcQ", fullscreen = false))
    }

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
        val tap = tapAspect(prefs, null) // contain -> cover
        assertEquals("cover", aspect(prefs, tap))
        setChoice(prefs, ASPECT, "fill")
        assertEquals("fill", aspect(prefs, tap))
    }

    @Test
    fun `ASPECT-01 tapping cycles contain cover fill contain`() {
        val prefs = MemoryPrefs()
        var tap: Pair<String, String>? = null
        val seen = (1..4).map { tap = tapAspect(prefs, tap); aspect(prefs, tap) }
        assertEquals(listOf("cover", "fill", "contain", "cover"), seen)
        // The cycle starts from the saved default, not from Ajustar.
        val esticar = MemoryPrefs().apply { map["aspect"] = "fill" }
        assertEquals("contain", aspect(esticar, tapAspect(esticar, null)))
    }

    @Test
    fun `ASPECT-04 tapping leaves the saved default unchanged`() {
        val prefs = MemoryPrefs().apply { map["aspect"] = "cover" }
        var tap: Pair<String, String>? = null
        repeat(2) { tap = tapAspect(prefs, tap) }
        assertEquals("contain", aspect(prefs, tap))
        assertEquals(mapOf<String, Any?>("aspect" to "cover"), prefs.map)
    }
}
