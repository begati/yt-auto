package dev.begati.youtubeauto

import org.junit.Assert.assertEquals
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
}
