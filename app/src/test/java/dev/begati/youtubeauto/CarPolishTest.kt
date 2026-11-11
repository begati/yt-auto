package dev.begati.youtubeauto

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** PLAYBTN-01, PLAYBTN-02, PLAYBTN-03 and PLAYBTN-05 (spec car-polish). */
class CarPolishTest {
    private val watch = "https://m.youtube.com/watch?v=dQw4w9WgXcQ&pp=0gcJCVgCo7VqN5tD"

    @Test
    fun `PLAYBTN-01 shown on a watch page playing or paused`() {
        assertTrue(playToggleShown(watch, playing = true))
        assertTrue(playToggleShown(watch, playing = false))
        assertTrue(playToggleShown("https://m.youtube.com/watch?list=PL1&v=dQw4w9WgXcQ", playing = false))
    }

    @Test
    fun `PLAYBTN-02 shown off the watch page while a video plays`() {
        assertTrue(playToggleShown("https://m.youtube.com/", playing = true))
        assertTrue(playToggleShown("https://m.youtube.com/feed/subscriptions", playing = true))
        assertTrue(playToggleShown("https://m.youtube.com/results?search_query=rock", playing = true))
    }

    @Test
    fun `PLAYBTN-03 hidden on home search and feeds when nothing plays`() {
        assertFalse(playToggleShown("https://m.youtube.com/", playing = false))
        assertFalse(playToggleShown("https://m.youtube.com/feed/subscriptions", playing = false))
        assertFalse(playToggleShown("https://m.youtube.com/feed/history", playing = false))
        assertFalse(playToggleShown("https://m.youtube.com/results?search_query=watch?v=dQw4w9WgXcQ", playing = false))
        assertFalse(playToggleShown("https://m.youtube.com/@canal/videos", playing = false))
        assertFalse(playToggleShown("", playing = false))
    }

    @Test
    fun `PLAYBTN-05 leaving the watch page paused hides the button`() {
        assertTrue(playToggleShown(watch, playing = false))
        assertFalse(playToggleShown("https://m.youtube.com/", playing = false))
    }
}
