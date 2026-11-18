package dev.begati.youtubeauto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** CTRL-01..17 (spec video-controls). Coordinates are car surface pixels; rects are left, top, right, bottom. */
class VideoControlsTest {
    private val player = listOf(0f, 0f, 800f, 400f)
    private val bar = listOf(30f, 356f, 770f, 400f) // 740 px wide, as measured on the DHU

    @Test
    fun `CTRL-01 double tap on the right half skips forward 10 s`() {
        assertEquals(10, doubleTapSkip(true, 2f, 600f, 200f, player))
        assertEquals(10, doubleTapSkip(true, 2f, 400f, 10f, player)) // the middle belongs to the right half
        // Player beside a media card: halves of the player, not of the surface.
        assertEquals(10, doubleTapSkip(true, 2f, 300f, 200f, listOf(0f, 0f, 464f, 400f)))
    }

    @Test
    fun `CTRL-02 double tap on the left half skips back 10 s`() {
        assertEquals(-10, doubleTapSkip(true, 2f, 100f, 200f, player))
        assertEquals(-10, doubleTapSkip(true, 2f, 399f, 390f, player))
        assertEquals(-10, doubleTapSkip(true, 2f, 200f, 200f, listOf(0f, 0f, 464f, 400f)))
    }

    @Test
    fun `CTRL-05 repeated double taps on the same side within 1 s add up`() {
        assertEquals(20, addSkip(10, 1_000L, 10, 2_000L))
        assertEquals(30, addSkip(20, 1_000L, 10, 1_500L))
        assertEquals(-20, addSkip(-10, 1_000L, -10, 1_900L))
        // After 1 s, or on the other side, the count starts again.
        assertEquals(10, addSkip(10, 1_000L, 10, 2_001L))
        assertEquals(-10, addSkip(20, 1_000L, -10, 1_200L))
        assertEquals(10, addSkip(0, 0L, 10, 50L))
    }

    @Test
    fun `CTRL-07 a pinch scale factor does not skip`() {
        for (f in listOf(1.1f, 0.5f, 1.9f, 2.1f, -1f)) assertEquals(0, doubleTapSkip(true, f, 600f, 200f, player))
    }

    @Test
    fun `CTRL-08 double tap outside the player or off watch does nothing`() {
        // Player at the top of a page scrolled with fullscreen off: a tap on the comments under it.
        assertEquals(0, doubleTapSkip(true, 2f, 600f, 300f, listOf(0f, 0f, 800f, 225f)))
        assertEquals(0, doubleTapSkip(true, 2f, 600f, 200f, null)) // no player on the page
        assertEquals(0, doubleTapSkip(false, 2f, 600f, 200f, player))
    }

    @Test
    fun `CTRL-09 a tap on the visible bar seeks to its fraction of the duration`() {
        assertEquals(500_000L, barTapMs(400f, 378f, bar, 1_000_000L))
        assertEquals(0L, barTapMs(30f, 378f, bar, 1_000_000L))
        assertEquals(1_000_000L, barTapMs(770f, 378f, bar, 1_000_000L))
        assertEquals(250_000L, barTapMs(215f, 360f, bar, 1_000_000L))
    }

    @Test
    fun `CTRL-10 a tap off the bar or with the bar hidden goes to the page`() {
        assertNull(barTapMs(400f, 200f, bar, 1_000_000L))
        assertNull(barTapMs(10f, 378f, bar, 1_000_000L))
        assertNull(barTapMs(400f, 378f, null, 1_000_000L)) // controls hidden
    }

    @Test
    fun `CTRL-11 a tap on the bar without a duration goes to the page`() {
        assertNull(barTapMs(400f, 378f, bar, 0L))
    }

    @Test
    fun `CTRL-12 a mostly horizontal drag on watch seeks`() {
        assertTrue(dragSeeks(true, 1_000_000L, 12f, 3f))
        assertTrue(dragSeeks(true, 1_000_000L, -8f, 7f))
    }

    @Test
    fun `CTRL-13 the drag label shows target, duration and signed difference`() {
        assertEquals("4:32 / 17:15" to "+1:20", dragLabel(272_000L, 1_035_000L, 192_000L))
        assertEquals("0:10 / 17:15" to "-0:35", dragLabel(10_000L, 1_035_000L, 45_000L))
        assertEquals("1:02:05 / 2:00:00" to "+1:00:00", dragLabel(3_725_000L, 7_200_000L, 125_000L))
        assertEquals("0:45 / 17:15" to "+0:00", dragLabel(45_000L, 1_035_000L, 45_000L))
    }

    @Test
    fun `CTRL-14 the drag target follows the finger across the player width`() {
        // Half the width to the right: half the video forward.
        assertEquals(600_000L, dragTargetMs(100_000L, 400f, 800f, 1_000_000L))
        assertEquals(50_000L, dragTargetMs(100_000L, -40f, 800f, 1_000_000L))
        // Limited to the video.
        assertEquals(1_000_000L, dragTargetMs(900_000L, 800f, 800f, 1_000_000L))
        assertEquals(0L, dragTargetMs(100_000L, -800f, 800f, 1_000_000L))
    }

    @Test
    fun `CTRL-16 a mostly vertical drag scrolls the page`() {
        assertFalse(dragSeeks(true, 1_000_000L, 3f, 12f))
        assertFalse(dragSeeks(true, 1_000_000L, 5f, -5f))
    }

    @Test
    fun `CTRL-17 a drag off watch or without a duration scrolls the page`() {
        assertFalse(dragSeeks(false, 1_000_000L, 12f, 0f))
        assertFalse(dragSeeks(true, 0L, 12f, 0f))
    }
}

/** HINT-01..03 (spec gesture-hint). */
class GestureHintTest {
    @Test
    fun `HINT-01 the first video on watch asks for the hint`() {
        assertTrue(takeGestureHint(MemoryPrefs(), onWatch = true, visible = true))
    }

    @Test
    fun `HINT-02 the hint shows only once`() {
        val prefs = MemoryPrefs()
        assertTrue(takeGestureHint(prefs, onWatch = true, visible = true))
        assertFalse(takeGestureHint(prefs, onWatch = true, visible = true))
        // Stored, not held in memory: a restarted app reads the same prefs.
        assertEquals(true, prefs.map["gestureHintShown"])
    }

    @Test
    fun `HINT-03 a video off watch neither shows nor uses up the hint`() {
        val prefs = MemoryPrefs()
        assertFalse(takeGestureHint(prefs, onWatch = false, visible = true))
        assertTrue(takeGestureHint(prefs, onWatch = true, visible = true))
    }

    @Test
    fun `HINT-04 a video not on screen neither shows nor uses up the hint`() {
        val prefs = MemoryPrefs()
        assertFalse(takeGestureHint(prefs, onWatch = true, visible = false))
        assertEquals(null, prefs.map["gestureHintShown"])
        assertTrue(takeGestureHint(prefs, onWatch = true, visible = true))
    }
}
