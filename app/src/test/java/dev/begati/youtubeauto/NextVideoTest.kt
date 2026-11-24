package dev.begati.youtubeauto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** NEXT-01..07 (spec next-video). */
class NextVideoTest {
    private val video = "kXYiU_JCYtU"

    @Test
    fun `NEXT-01 a video from a playlist node opens with its playlist`() {
        assertEquals("https://m.youtube.com/watch?v=$video&list=WL", watchUrl(playlistMediaId(video, "watch_later")))
        assertEquals("https://m.youtube.com/watch?v=$video&list=LL", watchUrl(playlistMediaId(video, "liked")))
        assertEquals(
            "https://m.youtube.com/watch?v=$video&list=PLZ1dJqY6KWOXGGeIlZqleztqta23wHMGG",
            watchUrl(playlistMediaId(video, "list:PLZ1dJqY6KWOXGGeIlZqleztqta23wHMGG")),
        )
    }

    @Test
    fun `NEXT-02 a video from a feed or the phone opens without a playlist`() {
        assertEquals("https://m.youtube.com/watch?v=$video", watchUrl(playlistMediaId(video, "history")))
        assertEquals("https://m.youtube.com/watch?v=$video", watchUrl(playlistMediaId(video, "subscriptions")))
        // The phone remote sends the bare video id.
        assertEquals("https://m.youtube.com/watch?v=$video", watchUrl(video))
    }

    @Test
    fun `NEXT-03 an invalid playlist id opens the video alone`() {
        assertEquals("https://m.youtube.com/watch?v=$video", watchUrl("$video@bad&list"))
        assertEquals("https://m.youtube.com/watch?v=$video", watchUrl("$video@"))
        assertEquals("https://m.youtube.com/watch?v=$video", watchUrl(playlistMediaId(video, "list:x y")))
    }

    private val alone = "https://m.youtube.com/watch?v=$video"
    private val inList = "https://m.youtube.com/watch?v=$video&list=WL&index=1"

    @Test
    fun `NEXT-04 outside a playlist with autoplay on the end starts the next video`() {
        assertTrue(earlyNextDue(ended = true, autonav = true, url = alone, sent = false))
        assertTrue(earlyNextDue(ended = true, autonav = true, url = "$alone&t=42s", sent = false))
        // Not before the video ends.
        assertFalse(earlyNextDue(ended = false, autonav = true, url = alone, sent = false))
    }

    @Test
    fun `NEXT-05 in a playlist the app leaves the next video to YouTube`() {
        assertFalse(earlyNextDue(ended = true, autonav = true, url = inList, sent = false))
        assertFalse(earlyNextDue(ended = true, autonav = true, url = "https://m.youtube.com/watch?list=RDkXYiU_JCYtU&v=$video", sent = false))
    }

    @Test
    fun `NEXT-06 with autoplay off or unknown the app does not start the next video`() {
        assertFalse(earlyNextDue(ended = true, autonav = false, url = alone, sent = false))
        assertFalse(earlyNextDue(ended = true, autonav = null, url = alone, sent = false))
    }

    @Test
    fun `NEXT-07 one end starts the next video at most once`() {
        assertTrue(earlyNextDue(ended = true, autonav = true, url = alone, sent = false))
        assertFalse(earlyNextDue(ended = true, autonav = true, url = alone, sent = true))
    }
}
