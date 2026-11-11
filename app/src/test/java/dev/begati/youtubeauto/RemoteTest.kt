package dev.begati.youtubeauto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** CAST-01, CAST-05 and REMOTE-06 (spec phone-remote). */
class RemoteTest {
    @Test
    fun `CAST-01 video id from watch shorts and youtu-be links`() {
        assertEquals("dQw4w9WgXcQ", videoIdFromUrl("https://m.youtube.com/watch?v=dQw4w9WgXcQ"))
        assertEquals("dQw4w9WgXcQ", videoIdFromUrl("https://m.youtube.com/watch?list=PL1&v=dQw4w9WgXcQ&t=42s"))
        assertEquals("dQw4w9WgXcQ", videoIdFromUrl("/watch?v=dQw4w9WgXcQ&pp=ugUEEgJwdA%3D%3D"))
        assertEquals("abcDEF12_-x", videoIdFromUrl("https://m.youtube.com/shorts/abcDEF12_-x"))
        assertEquals("abcDEF12_-x", videoIdFromUrl("/shorts/abcDEF12_-x?feature=share"))
        assertEquals("dQw4w9WgXcQ", videoIdFromUrl("https://youtu.be/dQw4w9WgXcQ?si=xyz"))
        assertNull(videoIdFromUrl("https://m.youtube.com/"))
        assertNull(videoIdFromUrl("https://m.youtube.com/feed/subscriptions"))
        assertNull(videoIdFromUrl("https://m.youtube.com/results?search_query=watch?v=dQw4w9WgXcQ"))
        assertNull(videoIdFromUrl("https://m.youtube.com/watch?v=short"))
        assertNull(videoIdFromUrl("https://m.youtube.com/@canal/shorts"))
        assertNull(videoIdFromUrl(""))
    }

    @Test
    fun `CAST-05 a short opens as a watch url`() {
        assertEquals("https://m.youtube.com/watch?v=abcDEF12_-x", castUrl("https://m.youtube.com/shorts/abcDEF12_-x"))
        assertEquals("https://m.youtube.com/watch?v=dQw4w9WgXcQ", castUrl("https://m.youtube.com/watch?v=dQw4w9WgXcQ&t=42s"))
        assertNull(castUrl("https://m.youtube.com/"))
    }

    @Test
    fun `REMOTE-06 clock formats m-ss and h-mm-ss`() {
        assertEquals("0:00", clock(0))
        assertEquals("0:09", clock(9))
        assertEquals("1:05", clock(65))
        assertEquals("59:59", clock(3599))
        assertEquals("1:00:00", clock(3600))
        assertEquals("2:03:04", clock(7384))
        assertEquals("0:00", clock(-5))
    }
}
