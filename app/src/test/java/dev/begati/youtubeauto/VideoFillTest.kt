package dev.begati.youtubeauto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** FILL-01, 04..06 and ASPECT-01..09 (spec video-fill). */
class VideoFillTest {
    @Test
    fun `ASPECT-05 the saved aspect applies`() {
        assertEquals("cover", choice(MemoryPrefs().apply { map["aspect"] = "cover" }, ASPECT))
        assertEquals("fill", choice(MemoryPrefs().apply { map["aspect"] = "fill" }, ASPECT))
    }

    @Test
    fun `ASPECT-06 options are Ajustar Preencher Esticar in order`() {
        assertEquals(R.string.aspect_title, ASPECT.title)
        assertEquals(listOf("contain" to R.string.aspect_fit, "cover" to R.string.aspect_fill, "fill" to R.string.aspect_stretch), ASPECT.options.toList())
        assertTrue(ASPECT in CHOICES)
    }

    @Test
    fun `ASPECT-07 default on a fresh install is cover (fill the screen)`() {
        assertEquals("cover", choice(MemoryPrefs(), ASPECT))
    }

    @Test
    fun `ASPECT-08 invalid stored aspect reads as the default`() {
        assertEquals("cover", choice(MemoryPrefs().apply { map["aspect"] = "stretch" }, ASPECT))
    }

    @Test
    fun `ASPECT-01 the button cycles cover fill contain cover from the default`() {
        val prefs = MemoryPrefs()
        assertEquals(listOf("fill", "contain", "cover", "fill"), (1..4).map { nextAspect(prefs) })
        // The cycle starts from the saved value.
        assertEquals("cover", nextAspect(MemoryPrefs().apply { map["aspect"] = "contain" }))
    }

    @Test
    fun `ASPECT-04 the button pick is saved as the default`() {
        val prefs = MemoryPrefs()
        nextAspect(prefs)
        assertEquals(mapOf<String, Any?>("aspect" to "fill"), prefs.map)
        assertEquals("fill", choice(prefs, ASPECT))
    }

    // Rects measured on the DHU (surface 800x400), as left, top, right, bottom.
    @Test
    fun `FILL-01 margins and hidden strips are covered`() {
        assertEquals(listOf(0, 0, 800, 400), webViewBounds(800, 400, listOf(12, 12, 788, 388)))
    }

    @Test
    fun `FILL-04 shown action strips do not shrink the page`() {
        assertEquals(listOf(0, 0, 800, 400), webViewBounds(800, 400, listOf(12, 88, 712, 388)))
    }

    @Test
    fun `FILL-05 a media card beside the app limits the page`() {
        assertEquals(listOf(0, 0, 464, 400), webViewBounds(800, 400, listOf(22, 34, 464, 376)))
        // Card on the left or at the bottom, the same rule.
        assertEquals(listOf(336, 0, 800, 400), webViewBounds(800, 400, listOf(336, 12, 788, 388)))
        assertEquals(listOf(0, 0, 800, 200), webViewBounds(800, 400, listOf(12, 12, 788, 200)))
        assertEquals(listOf(0, 300, 800, 400), webViewBounds(800, 400, listOf(12, 300, 788, 388)))
        // The cut is a third of the surface: 266 of 800 is covered, 267 limits the page.
        assertEquals(listOf(0, 0, 800, 400), webViewBounds(800, 400, listOf(266, 12, 534, 388)))
        assertEquals(listOf(267, 0, 800, 400), webViewBounds(800, 400, listOf(267, 12, 788, 388)))
        assertEquals(listOf(0, 0, 533, 400), webViewBounds(800, 400, listOf(12, 12, 533, 388)))
        assertEquals(listOf(0, 0, 800, 133), webViewBounds(800, 400, listOf(12, 12, 788, 133)))
    }

    @Test
    fun `FILL-06 no visible area yet fills the surface`() {
        assertEquals(listOf(0, 0, 800, 400), webViewBounds(800, 400, null))
    }

    @Test
    fun `FILL-07 an empty inverted or stale area fills the surface`() {
        assertEquals(listOf(0, 0, 800, 400), webViewBounds(800, 400, listOf(0, 0, 0, 0)))
        assertEquals(listOf(0, 0, 800, 400), webViewBounds(800, 400, listOf(500, 12, 300, 388)))
        // Left-card rect from an 800-wide surface after a rebuild to 300 wide.
        assertEquals(listOf(0, 0, 300, 400), webViewBounds(300, 400, listOf(336, 12, 788, 388)))
    }
}
