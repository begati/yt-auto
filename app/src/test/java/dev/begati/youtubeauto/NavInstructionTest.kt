package dev.begati.youtubeauto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** TURN-01..04, TURN-09, TURN-10 (spec nav-instruction). */
class NavInstructionTest {
    @Test
    fun `TURN-01 a navigation notification from waze or maps gives distance and maneuver`() {
        assertEquals(NavInstruction("10 m", "Siga pela rota destacada"), navInstruction("com.waze", "navigation", "10 m", "Siga pela rota destacada"))
        assertEquals(NavInstruction("0.5 mi", "Turn right onto Main St"), navInstruction("com.google.android.apps.maps", "navigation", "0.5 mi", "Turn right onto Main St"))
        // Only one of the two filled: what there is shows.
        assertEquals(NavInstruction("", "Siga pela rota destacada"), navInstruction("com.waze", "navigation", null, "Siga pela rota destacada"))
        assertEquals(NavInstruction("500 m", ""), navInstruction("com.waze", "navigation", "500 m", "  "))
    }

    @Test
    fun `TURN-11 a title that is not a distance is the maneuver`() {
        // Google Maps before the first turn (seen on the phone): the instruction comes in the title, no text.
        assertEquals(
            NavInstruction("", "Siga em direção à R. Roberto Stiegert"),
            navInstruction("com.google.android.apps.maps", "navigation", "Siga em direção à R. Roberto Stiegert", null),
        )
        assertEquals(NavInstruction("", "Rua Halfeld, Centro"), navInstruction("com.google.android.apps.maps", "navigation", "Rua Halfeld", "Centro"))
        // Distances in other scripts and with decimals stay distances.
        assertEquals(NavInstruction("1,2 km", "Vire"), navInstruction("com.waze", "navigation", "1,2 km", "Vire"))
        assertEquals(NavInstruction("٢٠٠ م", "انعطف"), navInstruction("com.waze", "navigation", "٢٠٠ م", "انعطف"))
        // A digit first: 12 characters is still a distance, 13 is a maneuver.
        assertEquals(NavInstruction("123456789012", ""), navInstruction("com.waze", "navigation", "123456789012", null))
        assertEquals(NavInstruction("", "1234567890123"), navInstruction("com.waze", "navigation", "1234567890123", null))
        // Fractions and units elsewhere.
        assertEquals(NavInstruction("½ mi", "Turn left"), navInstruction("com.waze", "navigation", "½ mi", "Turn left"))
        assertEquals(NavInstruction("800 ft", "Turn left"), navInstruction("com.waze", "navigation", "800 ft", "Turn left"))
    }

    @Test
    fun `TURN-02 removing the notification hides the instruction`() {
        val feed = NavFeed()
        feed.post("waze", NavInstruction("10 m", "Siga"))
        feed.remove("waze")
        assertNull(feed.current)
    }

    @Test
    fun `TURN-03 other apps other categories and empty notifications are ignored`() {
        assertNull(navInstruction("com.spotify.music", "navigation", "10 m", "Siga"))
        assertNull(navInstruction("com.waze", null, "10 m", "Siga"))
        assertNull(navInstruction("com.waze", "service", "10 m", "Siga"))
        assertNull(navInstruction("com.waze", "navigation", "", " "))
        assertNull(navInstruction("com.waze", "navigation", null, null))
    }

    @Test
    fun `TURN-04 nothing shows while the switch is off`() {
        val prefs = MemoryPrefs()
        val feed = NavFeed().apply { post("waze", NavInstruction("10 m", "Siga")) }
        setFlag(prefs, "navOverlay", false)
        assertNull(navToShow(prefs, feed))
        setFlag(prefs, "navOverlay", true)
        assertEquals(NavInstruction("10 m", "Siga"), navToShow(prefs, feed))
    }

    @Test
    fun `TURN-15 the phone switch reads on only with notification access`() {
        assertEquals(true, navSwitchShown(stored = true, access = true))
        assertEquals(false, navSwitchShown(stored = true, access = false))
        assertEquals(false, navSwitchShown(stored = false, access = true))
        assertEquals(false, navSwitchShown(stored = false, access = false))
    }

    @Test
    fun `TURN-12 the switch is on by default`() {
        val prefs = MemoryPrefs() // fresh install: nothing stored
        assertEquals(true, flag(prefs, "navOverlay"))
        assertEquals(NavInstruction("10 m", "Siga"), navToShow(prefs, NavFeed().apply { post("waze", NavInstruction("10 m", "Siga")) }))
    }

    @Test
    fun `TURN-09 the last posted instruction wins`() {
        val feed = NavFeed()
        feed.post("waze", NavInstruction("10 m", "Waze"))
        feed.post("maps", NavInstruction("200 m", "Maps"))
        assertEquals("Maps", feed.current?.maneuver)
        // An update of the older one makes it the latest again.
        feed.post("waze", NavInstruction("5 m", "Waze"))
        assertEquals("Waze", feed.current?.maneuver)
        // The shown one ends: the other one, still navigating, takes over.
        feed.remove("waze")
        assertEquals("Maps", feed.current?.maneuver)
    }

    @Test
    fun `TURN-10 the maneuver words pick the icon`() {
        val cases = mapOf(
            "Siga pela rota destacada" to Maneuver.STRAIGHT,
            "Continue na Av. Brasil" to Maneuver.STRAIGHT,
            "" to Maneuver.STRAIGHT,
            "Vire à direita na Rua Halfeld" to Maneuver.RIGHT,
            "Vire à esquerda" to Maneuver.LEFT,
            "Turn left onto Main St" to Maneuver.LEFT,
            "Gire a la derecha" to Maneuver.RIGHT,
            "Tournez à gauche" to Maneuver.LEFT,
            "Svolta a destra" to Maneuver.RIGHT,
            "Поверните налево" to Maneuver.LEFT,
            "انعطف يمينًا" to Maneuver.RIGHT,
            "Mantenha-se à esquerda" to Maneuver.KEEP_LEFT,
            "Keep right" to Maneuver.KEEP_RIGHT,
            "Pegue a saída à direita" to Maneuver.KEEP_RIGHT,
            "Take the exit on the left" to Maneuver.KEEP_LEFT,
            "Na rotatória, pegue a 2ª saída" to Maneuver.ROUNDABOUT,
            "At the roundabout, take the 1st exit" to Maneuver.ROUNDABOUT,
            "Faça o retorno" to Maneuver.U_TURN,
            "Make a U-turn" to Maneuver.U_TURN,
            "Você chegou ao destino" to Maneuver.ARRIVE,
            "You have arrived" to Maneuver.ARRIVE,
            // Two directions: the first one is the turn coming up.
            "Vire à esquerda e depois à direita" to Maneuver.LEFT,
            "Turn right, then left" to Maneuver.RIGHT,
            // A passing word or a street name does not pick the icon (Verifier F3).
            "When you turn left" to Maneuver.LEFT,
            "Turn left toward destination" to Maneuver.LEFT,
            "Vire à direita na Rua do Retorno" to Maneuver.RIGHT,
            "Объезд вокруг квартала направо" to Maneuver.RIGHT,
            // Keep, roundabout, U-turn and arrival in each language (Verifier F4).
            "Mantente a la izquierda" to Maneuver.KEEP_LEFT,
            "En la rotonda, toma la segunda salida" to Maneuver.ROUNDABOUT,
            "Cambio de sentido" to Maneuver.U_TURN,
            "Has llegado a tu destino" to Maneuver.ARRIVE,
            "Restez à droite" to Maneuver.KEEP_RIGHT,
            "Au rond-point, prenez la 1re sortie" to Maneuver.ROUNDABOUT,
            "Faites demi-tour" to Maneuver.U_TURN,
            "Vous êtes arrivé" to Maneuver.ARRIVE,
            "Tieni la sinistra" to Maneuver.KEEP_LEFT,
            "Alla rotonda prendi la seconda uscita" to Maneuver.ROUNDABOUT,
            "Fai inversione a U" to Maneuver.U_TURN,
            "Sei arrivato" to Maneuver.ARRIVE,
            "Держитесь левой стороны" to Maneuver.KEEP_LEFT,
            "Съезд справа" to Maneuver.KEEP_RIGHT,
            "На круговом движении второй съезд" to Maneuver.ROUNDABOUT,
            "Развернитесь" to Maneuver.U_TURN,
            "Вы прибыли" to Maneuver.ARRIVE,
            "الزم اليسار" to Maneuver.KEEP_LEFT,
            "في الدوار، اسلك المخرج الثاني" to Maneuver.ROUNDABOUT,
            "استدر للخلف" to Maneuver.U_TURN,
            "لقد وصلت" to Maneuver.ARRIVE,
        )
        for ((text, want) in cases) assertEquals(text, want, maneuverOf(text))
    }
}
