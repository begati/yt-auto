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
}
