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
    fun `TURN-04 nothing shows while the switch is off which is the default`() {
        val prefs = MemoryPrefs()
        val feed = NavFeed().apply { post("waze", NavInstruction("10 m", "Siga")) }
        assertNull(navToShow(prefs, feed))
        setFlag(prefs, "navOverlay", true)
        assertEquals(NavInstruction("10 m", "Siga"), navToShow(prefs, feed))
        setFlag(prefs, "navOverlay", false)
        assertNull(navToShow(prefs, feed))
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
        )
        for ((text, want) in cases) assertEquals(text, want, maneuverOf(text))
    }
}
