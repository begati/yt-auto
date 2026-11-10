package dev.begati.youtubeauto

import org.junit.Assert.assertEquals
import org.junit.Test

/** FEED-01..09: history/subscriptions parsing from the page's ytInitialData (spec car-features-verification). */
class FeedParserTest {
    private fun page(json: String) = "<html><script>var ytInitialData = $json;</script></html>"

    private fun video(id: String, title: String, channel: String? = null) =
        """{"videoRenderer":{"videoId":"$id","title":{"runs":[{"text":"$title"}]}""" +
            (channel?.let { ""","shortBylineText":{"runs":[{"text":"$it"}]}""" } ?: "") + "}}"

    @Test
    fun `FEED-01 returns id title and channel in page order`() {
        val html = page("""{"contents":[${video("aaaaaaaaaaa", "Primeiro", "Canal A")},{"nested":[${video("bbbbbbbbbbb", "Segundo", "Canal B")}]}]}""")
        assertEquals(
            listOf(FeedVideo("aaaaaaaaaaa", "Primeiro", "Canal A"), FeedVideo("bbbbbbbbbbb", "Segundo", "Canal B")),
            parseFeed(html),
        )
    }

    @Test
    fun `FEED-02 keeps only the first occurrence of a videoId`() {
        val html = page("""[${video("aaaaaaaaaaa", "Original")},${video("bbbbbbbbbbb", "Outro")},${video("aaaaaaaaaaa", "Repetido")}]""")
        assertEquals(
            listOf(FeedVideo("aaaaaaaaaaa", "Original", ""), FeedVideo("bbbbbbbbbbb", "Outro", "")),
            parseFeed(html),
        )
    }

    @Test
    fun `FEED-03 caps the list at the first 50 videos`() {
        val ids = (0 until 51).map { "v" + it.toString().padStart(10, '0') }
        val result = parseFeed(page(ids.joinToString(",", "[", "]") { video(it, "T$it") }))
        assertEquals(ids.take(50), result.map { it.id })
    }

    @Test
    fun `FEED-04 reads simpleText runs and content titles`() {
        val html = page(
            """[{"videoId":"aaaaaaaaaaa","title":{"simpleText":"Simples"}},""" +
                """{"videoId":"bbbbbbbbbbb","title":{"runs":[{"text":"Parte "},{"text":"um"},{"text":"!"}]}},""" +
                """{"videoId":"ccccccccccc","title":{"content":"Conteúdo"}}]"""
        )
        assertEquals(listOf("Simples", "Parte um!", "Conteúdo"), parseFeed(html).map { it.title })
    }

    @Test
    fun `FEED-05 reads lockup videos by contentId and lockup title`() {
        val html = page(
            """{"lockupViewModel":{"contentId":"lllllllllll","contentType":"LOCKUP_CONTENT_TYPE_VIDEO",""" +
                """"metadata":{"lockupMetadataViewModel":{"title":{"content":"Vídeo novo"}}}}}"""
        )
        assertEquals(listOf(FeedVideo("lllllllllll", "Vídeo novo", "")), parseFeed(html))
    }

    @Test
    fun `FEED-06 ignores lockups that are not videos`() {
        val html = page(
            """[{"lockupViewModel":{"contentId":"PLplaylist1","contentType":"LOCKUP_CONTENT_TYPE_PLAYLIST",""" +
                """"metadata":{"lockupMetadataViewModel":{"title":{"content":"Playlist"}}}}},${video("aaaaaaaaaaa", "Vídeo")}]"""
        )
        assertEquals(listOf("aaaaaaaaaaa"), parseFeed(html).map { it.id })
    }

    @Test
    fun `FEED-07 returns empty list without ytInitialData`() {
        assertEquals(emptyList<FeedVideo>(), parseFeed("<html><script>var other = ${video("aaaaaaaaaaa", "X")};</script></html>"))
        // Edge case from the spec: marker present, JSON with no videos.
        assertEquals(emptyList<FeedVideo>(), parseFeed(page("{}")))
    }

    @Test
    fun `FEED-08 skips videoIds without a title`() {
        val html = page(
            """[{"watchEndpoint":{"videoId":"sssssssssss"}},${video("aaaaaaaaaaa", "Com título")},""" +
                // headline is the third title source FEED-08 names (mobile renderers use it).
                """{"videoId":"hhhhhhhhhhh","headline":{"simpleText":"Só headline"}}]"""
        )
        assertEquals(
            listOf(FeedVideo("aaaaaaaaaaa", "Com título", ""), FeedVideo("hhhhhhhhhhh", "Só headline", "")),
            parseFeed(html),
        )
    }

    @Test
    fun `FEED-09 channel falls back from shortBylineText to ownerText to empty`() {
        val html = page(
            """[{"videoId":"aaaaaaaaaaa","title":{"simpleText":"A"},""" +
                """"shortBylineText":{"runs":[{"text":"Curto"}]},"ownerText":{"runs":[{"text":"Dono"}]}},""" +
                """{"videoId":"bbbbbbbbbbb","title":{"simpleText":"B"},"ownerText":{"runs":[{"text":"Dono"}]}},""" +
                """{"videoId":"ccccccccccc","title":{"simpleText":"C"}}]"""
        )
        assertEquals(listOf("Curto", "Dono", ""), parseFeed(html).map { it.channel })
    }
}
