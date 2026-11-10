package dev.begati.youtubeauto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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

    @Test
    fun `THUMB-01 accepts 11 char ids from the allowed alphabet`() {
        for (id in listOf("dQw4w9WgXcQ", "a_b-c123XYZ", "___________", "-----------", "ABCDEFGHIJK", "0123456789z")) {
            assertTrue(id, isVideoId(id))
        }
    }

    @Test
    fun `THUMB-02 rejects ids with slash dot or wrong length`() {
        for (id in listOf("../../etc/p", "abc/def_ghi", "dQw4w9WgXc.", "dQw4w9WgXc", "dQw4w9WgXcQx", "", "dQw4w9WgX c", "dQw4w9WgXçQ")) {
            assertFalse(id, isVideoId(id))
        }
    }
}
