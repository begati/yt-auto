package dev.begati.youtubeauto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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
                Triple("https://m.youtube.com/feed/playlists", R.string.playlists, R.drawable.ic_video_library),
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
    fun `CSET-01 car settings are quality speed captions navigation`() {
        assertEquals(
            listOf(
                "quality" to R.drawable.ic_high_quality, // Qualidade do vídeo
                "speed" to R.drawable.ic_speed, // Velocidade
                "captions" to R.drawable.ic_closed_caption, // Legendas
                "navOverlay" to R.drawable.ic_navigation, // Navegação na tela
            ),
            CAR_SETTINGS,
        )
    }

    @Test
    fun `PSET-01 phone groups and order`() {
        assertEquals(
            listOf(
                // Reprodução: Qualidade, Velocidade, Proporção, Legendas, Tela cheia ao abrir vídeo
                R.string.group_playback to listOf("quality", "speed", "aspect", "captions", "fullscreen"),
                // Anúncios: Bloquear anúncios, Pular patrocínios
                R.string.group_ads to listOf("skipAds", "sponsorBlock"),
                // No carro: Só áudio, Tocar com outro app na tela, Página inicial, Tamanho do texto
                R.string.group_car to listOf("audioWhileMoving", "background", "navOverlay", "home", "textZoom"),
            ),
            PHONE_GROUPS,
        )
    }

    @Test
    fun `PSET-02 every phone setting has its icon`() {
        val expected = mapOf(
            "quality" to R.drawable.ic_high_quality, "speed" to R.drawable.ic_speed, "aspect" to R.drawable.ic_aspect_ratio,
            "captions" to R.drawable.ic_closed_caption, "fullscreen" to R.drawable.ic_fullscreen, "skipAds" to R.drawable.ic_block,
            "sponsorBlock" to R.drawable.ic_fast_forward, "audioWhileMoving" to R.drawable.ic_headphones,
            "background" to R.drawable.ic_picture_in_picture_alt, "home" to R.drawable.ic_home,
            "textZoom" to R.drawable.ic_format_size, "navOverlay" to R.drawable.ic_navigation,
        )
        PHONE_GROUPS.flatMap { it.second }.forEach { key -> assertEquals(key, expected[key], SETTING_ICONS[key]) }
        assertEquals(expected.keys, PHONE_GROUPS.flatMap { it.second }.toSet())
    }

    @Test
    fun `PSET-03 every switch has its summary`() {
        val expected = mapOf(
            "skipAds" to R.string.summary_skip_ads, // Pula os anúncios dos vídeos
            "sponsorBlock" to R.string.summary_sponsor_block, // Pula trechos patrocinados marcados pela comunidade
            "audioWhileMoving" to R.string.summary_audio_while_moving, // Esconde o vídeo enquanto o carro anda (usa o GPS)
            "fullscreen" to R.string.summary_fullscreen, // Abre o vídeo ocupando a tela do carro
            "background" to R.string.summary_background, // Continua tocando com o Maps ou outro app na frente
            "captions" to R.string.summary_captions, // Mostra legendas quando o vídeo tem
            "navOverlay" to R.string.summary_nav_overlay, // Mostra a próxima curva do Waze ou do Google Maps na tela do carro
        )
        assertEquals(expected.keys, FLAGS.map { it.key }.toSet())
        FLAGS.forEach { f -> assertEquals(f.key, expected[f.key], SWITCH_SUMMARIES[f.key]) }
    }

    @Test
    fun `PSET-04 keys and defaults unchanged`() {
        // Values of release 1.3: keys, defaults and stored value formats.
        assertEquals(
            mapOf(
                "quality" to ("medium" to listOf("auto", "small", "medium", "large", "hd720", "hd1080")),
                "speed" to ("1" to listOf("0.75", "1", "1.25", "1.5", "1.75", "2")),
                "aspect" to ("cover" to listOf("contain", "cover", "fill")),
                "home" to ("https://m.youtube.com" to listOf(
                    "https://m.youtube.com", "https://m.youtube.com/feed/subscriptions", "https://m.youtube.com/feed/history",
                )),
                "textZoom" to ("130" to listOf("100", "115", "130", "150")),
            ),
            CHOICES.associate { it.key to (it.default to it.options.keys.toList()) },
        )
        assertEquals(
            mapOf(
                "skipAds" to true, "sponsorBlock" to true, "audioWhileMoving" to false,
                "fullscreen" to true, "background" to true, "captions" to false, "navOverlay" to true,
            ),
            FLAGS.associate { it.key to it.default },
        )
        // The phone screen shows every setting once, by those keys.
        val shown = PHONE_GROUPS.flatMap { it.second }
        assertEquals(shown.size, shown.toSet().size)
        assertEquals((CHOICES.map { it.key } + FLAGS.map { it.key }).toSet(), shown.toSet())
        // A value saved by 1.3 is read unchanged.
        val saved = MemoryPrefs().apply {
            map["quality"] = "hd720"; map["speed"] = "1.5"; map["aspect"] = "contain"
            map["home"] = "https://m.youtube.com/feed/history"; map["textZoom"] = "100"; map["skipAds"] = false
        }
        assertEquals("hd720", choice(saved, QUALITY))
        assertEquals("1.5", choice(saved, SPEED))
        assertEquals("contain", choice(saved, ASPECT))
        assertEquals("https://m.youtube.com/feed/history", choice(saved, HOME_PAGE))
        assertEquals("100", choice(saved, TEXT_ZOOM))
        assertEquals(false, flag(saved, "skipAds"))
    }

    @Test
    fun `SIGN-04 banner shows only while signed out and not closed`() {
        assertTrue(bannerShown(signedIn = false, closedWhileSignedOut = false))
        assertFalse(bannerShown(signedIn = false, closedWhileSignedOut = true))
        assertFalse(bannerShown(signedIn = true, closedWhileSignedOut = false))
        assertFalse(bannerShown(signedIn = true, closedWhileSignedOut = true))
        // A first launch, signed out, never closed: shown.
        val prefs = MemoryPrefs()
        assertFalse(signInChanged(prefs, false))
        assertTrue(bannerShown(false, bannerClosed(prefs)))
    }

    @Test
    fun `SIGN-06 closing lasts until the sign-in state changes`() {
        val prefs = MemoryPrefs()
        signInChanged(prefs, false)
        closeBanner(prefs)
        assertFalse(bannerShown(false, bannerClosed(prefs)))
        // Still signed out on later pages and launches (same stored prefs): stays closed.
        assertFalse(signInChanged(prefs, false))
        assertFalse(signInChanged(prefs, false))
        assertFalse(bannerShown(false, bannerClosed(prefs)))
        // Signing in is reported once and hides the banner.
        assertTrue(signInChanged(prefs, true))
        assertFalse(bannerShown(true, bannerClosed(prefs)))
        assertFalse(signInChanged(prefs, true))
        // Signing out shows it again, even though it was closed before.
        assertFalse(signInChanged(prefs, false))
        assertTrue(bannerShown(false, bannerClosed(prefs)))
        // Closed while signed out, then sign in and out again: shown again.
        closeBanner(prefs)
        signInChanged(prefs, true)
        signInChanged(prefs, false)
        assertTrue(bannerShown(false, bannerClosed(prefs)))
    }
}
