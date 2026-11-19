package dev.begati.youtubeauto

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** END-01..03 (spec video-end). */
class VideoEndTest {
    @Test
    fun `END-01 autoplay off starts the countdown at the end`() {
        assertTrue(endCountdownDue(ended = true, autonav = false, endedForMs = 0))
        assertTrue(endCountdownDue(ended = true, autonav = false, endedForMs = 600))
    }

    @Test
    fun `END-02 autoplay on or unknown waits 10 s at the end`() {
        for (autonav in listOf(true, null)) {
            assertFalse(endCountdownDue(ended = true, autonav = autonav, endedForMs = 0))
            assertFalse(endCountdownDue(ended = true, autonav = autonav, endedForMs = 9_999))
            assertTrue(endCountdownDue(ended = true, autonav = autonav, endedForMs = 10_000))
        }
    }

    @Test
    fun `END-03 a video that is not ended never starts the countdown`() {
        for (autonav in listOf(true, false, null)) assertFalse(endCountdownDue(ended = false, autonav = autonav, endedForMs = 60_000))
    }
}
