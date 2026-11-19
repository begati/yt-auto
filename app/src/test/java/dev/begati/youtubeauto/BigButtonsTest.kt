package dev.begati.youtubeauto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** BTN-01..08 (spec big-buttons). Rects are left, top, right, bottom in car surface pixels. */
class BigButtonsTest {
    private val watch = "https://m.youtube.com/watch?v=dQw4w9WgXcQ"
    private val home = "https://m.youtube.com/"

    @Test
    fun `BTN-01 the bar lists back, home, play-pause, search and aspect in order`() {
        assertTrue(bigBar(5))
        assertTrue(bigBar(7))
        val all = listOf(CarButton.BACK, CarButton.HOME, CarButton.TOGGLE, CarButton.SEARCH, CarButton.ASPECT)
        assertEquals(all, barButtons(watch, playing = false)) // a video page: play/pause shows
        assertEquals(all, barButtons(home, playing = true)) // something playing: play/pause shows
        // Nothing playing off a video: no play/pause, the rest in the same order.
        assertEquals(listOf(CarButton.BACK, CarButton.HOME, CarButton.SEARCH, CarButton.ASPECT), barButtons(home, playing = false))
    }

    @Test
    fun `BTN-03 with the big bar the native strip keeps only settings`() {
        val (strip, map) = nativeButtons(bigBar = true, url = watch, playing = true)
        assertEquals(listOf(CarButton.SETTINGS), strip)
        assertEquals(emptyList<CarButton>(), map) // no map action strip at all
    }

    @Test
    fun `BTN-05 a tap goes to the bar only inside the shown bar`() {
        val bar = listOf(200f, 310f, 600f, 400f)
        assertTrue(tapOnBar(400f, 350f, bar))
        assertTrue(tapOnBar(200f, 310f, bar)) // edges belong to the bar
        assertFalse(tapOnBar(400f, 300f, bar)) // above it: the page, even over the progress bar
        assertFalse(tapOnBar(100f, 350f, bar)) // beside it
        assertFalse(tapOnBar(400f, 350f, null)) // hidden bar: the page
    }

    @Test
    fun `BTN-06 below car api 5 the native strips stay as they were`() {
        assertFalse(bigBar(1))
        assertFalse(bigBar(4))
        val (strip, map) = nativeButtons(bigBar = false, url = watch, playing = true)
        assertEquals(listOf(CarButton.BACK, CarButton.HOME, CarButton.TOGGLE, CarButton.SEARCH), strip)
        assertEquals(listOf(CarButton.ASPECT, CarButton.SETTINGS), map)
        val (idle, _) = nativeButtons(bigBar = false, url = home, playing = false)
        assertEquals(listOf(CarButton.BACK, CarButton.HOME, CarButton.SEARCH), idle)
    }

    @Test
    fun `BTN-07 the bar hides by itself only while a watch page plays`() {
        assertTrue(barAutoHides(watch, playing = true))
        assertTrue(barAutoHides("https://m.youtube.com/shorts/dQw4w9WgXcQ", playing = true))
        assertFalse(barAutoHides(watch, playing = false)) // paused: stays
        assertFalse(barAutoHides(home, playing = true)) // a preview playing in the feed: stays
        assertFalse(barAutoHides(home, playing = false))
    }

    @Test
    fun `BTN-08 with the bar fixed the page ends above it`() {
        // 800x400 surface, no inset: the page stops at the bar's top.
        assertEquals(listOf(0, 0, 800, 308), pageBounds(800, 400, null, 92))
        // Floating bar (height 0): same as before.
        assertEquals(listOf(0, 0, 800, 400), pageBounds(800, 400, null, 0))
        // Media card beside the app: the card's side stays, the bottom stops at the bar.
        assertEquals(listOf(336, 0, 800, 308), pageBounds(800, 400, listOf(336, 0, 800, 400), 92))
        // A bar taller than the page leaves the page as it was rather than an empty one.
        assertEquals(listOf(0, 0, 800, 400), pageBounds(800, 400, null, 400))
    }
}
