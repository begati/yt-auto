package dev.begati.youtubeauto

import android.content.SharedPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** In-memory SharedPreferences: enough of the interface for the settings helpers. */
private class MemoryPrefs : SharedPreferences {
    val map = mutableMapOf<String, Any?>()

    override fun getAll(): MutableMap<String, *> = map
    override fun getString(key: String, def: String?) = map[key] as? String ?: def
    override fun getStringSet(key: String, def: MutableSet<String>?) = def
    override fun getInt(key: String, def: Int) = map[key] as? Int ?: def
    override fun getLong(key: String, def: Long) = map[key] as? Long ?: def
    override fun getFloat(key: String, def: Float) = map[key] as? Float ?: def
    override fun getBoolean(key: String, def: Boolean) = map[key] as? Boolean ?: def
    override fun contains(key: String) = key in map
    override fun registerOnSharedPreferenceChangeListener(l: SharedPreferences.OnSharedPreferenceChangeListener?) {}
    override fun unregisterOnSharedPreferenceChangeListener(l: SharedPreferences.OnSharedPreferenceChangeListener?) {}

    override fun edit(): SharedPreferences.Editor = object : SharedPreferences.Editor {
        val pending = mutableMapOf<String, Any?>()
        override fun putString(key: String, value: String?) = apply { pending[key] = value }
        override fun putStringSet(key: String, value: MutableSet<String>?) = apply { pending[key] = value }
        override fun putInt(key: String, value: Int) = apply { pending[key] = value }
        override fun putLong(key: String, value: Long) = apply { pending[key] = value }
        override fun putFloat(key: String, value: Float) = apply { pending[key] = value }
        override fun putBoolean(key: String, value: Boolean) = apply { pending[key] = value }
        override fun remove(key: String) = apply { pending[key] = null }
        override fun clear() = apply { map.clear() }
        override fun commit(): Boolean { apply(); return true }
        override fun apply() { map.putAll(pending) }
    }
}

/** SET-01..08 and HOME-01..03 (spec car-features-verification). */
class SettingsTest {
    @Test
    fun `SET-01 defaults are 360p home page and 130 percent text`() {
        val prefs = MemoryPrefs()
        assertEquals("medium", choice(prefs, QUALITY))
        assertEquals("https://m.youtube.com", choice(prefs, HOME_PAGE))
        assertEquals("130", choice(prefs, TEXT_ZOOM))
    }

    @Test
    fun `SET-02 flags default to on`() {
        val prefs = MemoryPrefs()
        assertTrue(flag(prefs, "skipAds"))
        assertTrue(flag(prefs, "fullscreen"))
        assertTrue(flag(prefs, "background"))
    }

    @Test
    fun `SET-03 invalid stored choice reads as default`() {
        val prefs = MemoryPrefs().apply { map["quality"] = "tiny"; map["home"] = "https://evil.example"; map["textZoom"] = "999" }
        assertEquals("medium", choice(prefs, QUALITY))
        assertEquals("https://m.youtube.com", choice(prefs, HOME_PAGE))
        assertEquals("130", choice(prefs, TEXT_ZOOM))
    }

    @Test
    fun `SET-04 saving an invalid choice keeps the previous value`() {
        val prefs = MemoryPrefs()
        setChoice(prefs, QUALITY, "hd720")
        setChoice(prefs, QUALITY, "highres")
        assertEquals("hd720", choice(prefs, QUALITY))
        assertEquals("hd720", prefs.map["quality"])
    }

    @Test
    fun `SET-05 a saved valid choice is read back`() {
        val prefs = MemoryPrefs()
        setChoice(prefs, QUALITY, "small")
        setChoice(prefs, HOME_PAGE, "https://m.youtube.com/feed/history")
        setChoice(prefs, TEXT_ZOOM, "150")
        assertEquals("small", choice(prefs, QUALITY))
        assertEquals("https://m.youtube.com/feed/history", choice(prefs, HOME_PAGE))
        assertEquals("150", choice(prefs, TEXT_ZOOM))
    }

    @Test
    fun `SET-06 a flag saved off reads off`() {
        val prefs = MemoryPrefs()
        setFlag(prefs, "skipAds", false)
        assertFalse(flag(prefs, "skipAds"))
        assertTrue(flag(prefs, "fullscreen"))
    }

    @Test
    fun `SET-07 quality options are exactly the six labels in order`() {
        assertEquals(listOf("Automática", "240p", "360p", "480p", "720p", "1080p"), QUALITY.options.values.toList())
    }

    @Test
    fun `SET-08 home page and text size options in order`() {
        assertEquals(listOf("Início", "Inscrições", "Histórico"), HOME_PAGE.options.values.toList())
        assertEquals(listOf("100%", "115%", "130%", "150%"), TEXT_ZOOM.options.values.toList())
    }
}
