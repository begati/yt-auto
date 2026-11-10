package dev.begati.youtubeauto

import org.junit.Assert.assertEquals
import org.junit.Test

/** ART-01..02 and THUMB-01..02 (spec car-features-verification). */
class MediaHelpersTest {
    @Test
    fun `ART-01 shrinks the longest side to 320 keeping proportion`() {
        assertEquals(320 to 240, artSize(480, 360))
        assertEquals(180 to 320, artSize(720, 1280))
        // Boundary from the spec's edge cases.
        assertEquals(320 to 320, artSize(321, 321))
    }

    @Test
    fun `ART-02 keeps art of 320 or less untouched`() {
        assertEquals(320 to 180, artSize(320, 180))
        assertEquals(100 to 50, artSize(100, 50))
    }
}
