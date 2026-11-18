package dev.begati.youtubeauto

import org.junit.Assert.assertEquals
import org.junit.Test

/** Requirements of spec ui-polish decided by pure data and functions. */
class UiPolishTest {
    @Test
    fun `NAV-05 aspect icon follows the aspect`() {
        assertEquals(R.drawable.ic_fit_screen, aspectIcon("contain")) // Ajustar
        assertEquals(R.drawable.ic_crop, aspectIcon("cover")) // Preencher
        assertEquals(R.drawable.ic_open_in_full, aspectIcon("fill")) // Esticar
    }

    @Test
    fun `NAV-06 next aspect has the next icon`() {
        val prefs = MemoryPrefs()
        setChoice(prefs, ASPECT, "contain")
        assertEquals(R.drawable.ic_crop, aspectIcon(nextAspect(prefs)))
        assertEquals(R.drawable.ic_crop, aspectIcon(choice(prefs, ASPECT)))
        assertEquals(R.drawable.ic_open_in_full, aspectIcon(nextAspect(prefs)))
        assertEquals(R.drawable.ic_fit_screen, aspectIcon(nextAspect(prefs)))
        assertEquals(R.drawable.ic_fit_screen, aspectIcon(choice(prefs, ASPECT)))
    }

    @Test
    fun `NAV-03 home menu lists the four pages with icons`() {
        assertEquals(
            listOf(
                Triple("https://m.youtube.com", R.string.home_home, R.drawable.ic_home), // Início
                Triple("https://m.youtube.com/feed/subscriptions", R.string.subscriptions, R.drawable.ic_subscriptions),
                Triple("https://m.youtube.com/feed/history", R.string.history, R.drawable.ic_history),
                Triple("https://m.youtube.com/feed/library", R.string.playlists, R.drawable.ic_video_library),
            ),
            HOME_MENU,
        )
    }

    @Test
    fun `NAV-07 start page is the chosen home page`() {
        val prefs = MemoryPrefs()
        assertEquals("https://m.youtube.com", startUrl(prefs, ""))
        setChoice(prefs, HOME_PAGE, "https://m.youtube.com/feed/subscriptions")
        assertEquals("https://m.youtube.com/feed/subscriptions", startUrl(prefs, ""))
        setChoice(prefs, HOME_PAGE, "https://m.youtube.com/feed/history")
        assertEquals("https://m.youtube.com/feed/history", startUrl(prefs, ""))
    }

    @Test
    fun `CSET-01 car settings are quality speed captions`() {
        assertEquals(
            listOf(
                "quality" to R.drawable.ic_high_quality, // Qualidade do vídeo
                "speed" to R.drawable.ic_speed, // Velocidade
                "captions" to R.drawable.ic_closed_caption, // Legendas
            ),
            CAR_SETTINGS,
        )
    }
}
