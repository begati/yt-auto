package dev.begati.youtubeauto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Share target, voice search, browse lists, SponsorBlock, audio-only while moving and the update check. */
class UsabilityTest {
    @Test
    fun `share takes the first YouTube link in the text`() {
        assertEquals("dQw4w9WgXcQ", sharedVideoId("Never Gonna Give You Up https://youtu.be/dQw4w9WgXcQ?si=abc"))
        assertEquals("dQw4w9WgXcQ", sharedVideoId("https://m.youtube.com/watch?v=dQw4w9WgXcQ&t=10"))
        assertEquals("dQw4w9WgXcQ", sharedVideoId("veja https://example.com/x e https://www.youtube.com/shorts/dQw4w9WgXcQ"))
        assertNull(sharedVideoId("https://example.com/watch?v=dQw4w9WgXcQ"))
        assertNull(sharedVideoId("sem link"))
        assertNull(sharedVideoId(null))
    }

    @Test
    fun `voice search falls back to the results page`() {
        assertEquals("https://m.youtube.com/results?search_query=lo+fi+%26+chill", searchUrl("lo fi & chill"))
    }

    @Test
    fun `browse nodes map to feed pages and playlist ids are checked`() {
        assertEquals("https://www.youtube.com/playlist?list=WL", feedUrl("watch_later"))
        assertEquals("https://www.youtube.com/playlist?list=LL", feedUrl("liked"))
        assertEquals("https://www.youtube.com/feed/history", feedUrl("history"))
        assertEquals("https://www.youtube.com/playlist?list=PLabc_-1", feedUrl("list:PLabc_-1"))
        assertNull(feedUrl("list:../evil"))
        assertNull(feedUrl("PLabc"))
    }

    @Test
    fun `playlists page lists lockups and grid renderers without WL and LL`() {
        val html = "<script>var ytInitialData = " +
            """{"a":[{"lockupViewModel":{"contentId":"PL111","contentType":"LOCKUP_CONTENT_TYPE_PLAYLIST",""" +
            """"metadata":{"lockupMetadataViewModel":{"title":{"content":"Rock"}}}}},""" +
            """{"gridPlaylistRenderer":{"playlistId":"PL222","title":{"runs":[{"text":"Pop"}]}}},""" +
            """{"gridPlaylistRenderer":{"playlistId":"WL","title":{"simpleText":"Watch later"}}},""" +
            """{"watchEndpoint":{"videoId":"aaaaaaaaaaa","playlistId":"PL333"}},""" +
            """{"lockupViewModel":{"contentId":"vvvvvvvvvvv","contentType":"LOCKUP_CONTENT_TYPE_VIDEO",""" +
            """"metadata":{"lockupMetadataViewModel":{"title":{"content":"Video"}}}}}]};</script>"""
        assertEquals(listOf("PL111" to "Rock", "PL222" to "Pop"), parsePlaylists(html))
        assertEquals(emptyList<Pair<String, String>>(), parsePlaylists("<html></html>"))
    }

    @Test
    fun `SponsorBlock sends only a 4 digit hash prefix`() {
        val url = sponsorUrl("dQw4w9WgXcQ")
        assertTrue(url, url.startsWith("https://sponsor.ajay.app/api/skipSegments/5f6b?categories="))
        assertFalse(url.contains("dQw4w9WgXcQ"))
    }

    @Test
    fun `SponsorBlock picks this video's skip segments`() {
        val json = """[{"videoID":"other","segments":[{"segment":[1,2],"actionType":"skip"}]},""" +
            """{"videoID":"dQw4w9WgXcQ","segments":[{"segment":[10.5,30],"actionType":"skip"},""" +
            """{"segment":[40,50],"actionType":"mute"},{"segment":[60,55]},{"segment":[70,80]}]}]"""
        assertEquals(listOf(10.5 to 30.0, 70.0 to 80.0), parseSegments(json, "dQw4w9WgXcQ"))
        assertEquals(emptyList<Pair<Double, Double>>(), parseSegments(json, "missing"))
    }

    @Test
    fun `segment jump only inside a segment with more than a second left`() {
        val segs = listOf(10.5 to 30.0)
        assertNull(segmentAt(segs, 10))
        assertEquals(10.5 to 30.0, segmentAt(segs, 11))
        assertEquals(10.5 to 30.0, segmentAt(segs, 28))
        assertNull(segmentAt(segs, 29))
        assertNull(segmentAt(segs, 30))
    }

    @Test
    fun `moving needs over 2 m per s and stops below 0_5`() {
        assertFalse(movingAfter(false, 2f))
        assertTrue(movingAfter(false, 2.1f))
        assertTrue(movingAfter(true, 1f)) // crawling in traffic keeps the cover
        assertTrue(movingAfter(true, 0.5f))
        assertFalse(movingAfter(true, 0.4f))
        assertFalse(movingAfter(false, 1f))
    }

    @Test
    fun `update check compares versions number by number`() {
        assertTrue(isNewer("v1.1", "1.0"))
        assertTrue(isNewer("v1.0.1", "1.0"))
        assertTrue(isNewer("v1.10", "1.9"))
        assertTrue(isNewer("2.0", "1.9.9"))
        assertFalse(isNewer("v1.0", "1.0"))
        assertFalse(isNewer("v1.0", "1.1"))
        assertFalse(isNewer("", "1.0"))
    }
}
