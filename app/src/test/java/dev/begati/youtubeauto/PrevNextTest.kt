package dev.begati.youtubeauto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** PN-01..06 (spec prev-next). */
class PrevNextTest {
    @Test
    fun `PN-01 on a video the side strip is previous, next, aspect, settings`() {
        assertEquals(listOf("previous", "next", "aspect", "settings"), mapStrip(onWatch = true, hasNext = true))
    }

    @Test
    fun `PN-02 without a next video the next button is gone`() {
        assertEquals(listOf("previous", "aspect", "settings"), mapStrip(onWatch = true, hasNext = false))
    }

    @Test
    fun `PN-04 previous opens the previous video when there is one`() {
        assertEquals(JS_PREVIOUS, previousJs(hasPrevious = true))
        assertTrue(JS_PREVIOUS.contains("previousVideo"))
    }

    @Test
    fun `PN-05 previous restarts the video when there is none`() {
        assertEquals(JS_RESTART, previousJs(hasPrevious = false))
        assertTrue(JS_RESTART.contains("currentTime=0"))
    }

    @Test
    fun `FIX-04 off a video the side strip has settings only`() {
        assertEquals(listOf("settings"), mapStrip(onWatch = false, hasNext = true))
        assertEquals(listOf("settings"), mapStrip(onWatch = false, hasNext = false))
        // On a video the aspect stays, before settings.
        assertEquals(listOf("previous", "aspect", "settings"), mapStrip(onWatch = true, hasNext = false))
    }
}

/** FIX-03 (spec critique-fixes). */
class GesturesListTest {
    @Test
    fun `FIX-03 the gestures screen lists double tap, drag and bar in order`() {
        assertEquals(
            listOf(
                Triple(R.string.gesture_double_tap, R.string.gesture_double_tap_text, R.drawable.ic_forward_10),
                Triple(R.string.gesture_drag, R.string.gesture_drag_text, R.drawable.ic_fast_forward),
                Triple(R.string.gesture_bar, R.string.gesture_bar_text, R.drawable.ic_skip_next),
            ),
            GESTURE_ROWS,
        )
    }
}

/** FIX-05 (spec critique-fixes): both centered panels read these, so one value sets both. */
class PanelTokensTest {
    @Test
    fun `FIX-05 the car panels share 16 dp corners and a 6 dp bar`() {
        assertEquals(16, PANEL_CORNER_DP)
        assertEquals(6, PANEL_BAR_DP)
    }
}
