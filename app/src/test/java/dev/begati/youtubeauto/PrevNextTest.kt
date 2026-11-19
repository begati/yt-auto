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
    fun `PN-06 off a video the side strip is aspect and settings`() {
        assertEquals(listOf("aspect", "settings"), mapStrip(onWatch = false, hasNext = true))
        assertEquals(listOf("aspect", "settings"), mapStrip(onWatch = false, hasNext = false))
    }
}
