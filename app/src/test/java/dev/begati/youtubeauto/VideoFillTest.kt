package dev.begati.youtubeauto

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** FILL-01..04 and ASPECT-01..09 (spec video-fill). */
class VideoFillTest {
    @Test
    fun `FILL-01 watch page with fullscreen fills the surface`() {
        assertTrue(fillsSurface("https://m.youtube.com/watch?v=dQw4w9WgXcQ", fullscreen = true))
        assertTrue(fillsSurface("https://m.youtube.com/watch?v=dQw4w9WgXcQ&t=42", fullscreen = true))
    }

    @Test
    fun `FILL-02 other pages stay in the visible area`() {
        assertFalse(fillsSurface("https://m.youtube.com", fullscreen = true))
        assertFalse(fillsSurface("https://m.youtube.com/feed/subscriptions", fullscreen = true))
        assertFalse(fillsSurface("https://m.youtube.com/results?search_query=watch", fullscreen = true))
        assertFalse(fillsSurface("https://m.youtube.com/watchlater", fullscreen = true))
        assertFalse(fillsSurface("", fullscreen = true))
    }

    @Test
    fun `FILL-03 fullscreen off keeps watch in the visible area`() {
        assertFalse(fillsSurface("https://m.youtube.com/watch?v=dQw4w9WgXcQ", fullscreen = false))
    }
}
