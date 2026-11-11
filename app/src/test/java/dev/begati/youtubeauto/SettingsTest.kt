package dev.begati.youtubeauto

import android.content.SharedPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** In-memory SharedPreferences: enough of the interface for the settings helpers. */
internal class MemoryPrefs : SharedPreferences {
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
        assertEquals(listOf(R.string.quality_auto, R.string.quality_240, R.string.quality_360, R.string.quality_480, R.string.quality_720, R.string.quality_1080), QUALITY.options.values.toList())
    }

    @Test
    fun `SET-08 home page and text size options in order`() {
        assertEquals(listOf(R.string.home_home, R.string.subscriptions, R.string.history), HOME_PAGE.options.values.toList())
        assertEquals(listOf(R.string.zoom_100, R.string.zoom_115, R.string.zoom_130, R.string.zoom_150), TEXT_ZOOM.options.values.toList())
    }

    @Test
    fun `SET-09 speed defaults to normal, options in order, invalid reads as default`() {
        assertEquals("1", choice(MemoryPrefs(), SPEED))
        assertEquals("1", choice(MemoryPrefs().apply { map["speed"] = "16" }, SPEED))
        assertEquals(listOf(R.string.speed_075, R.string.speed_normal, R.string.speed_125, R.string.speed_150, R.string.speed_175, R.string.speed_200), SPEED.options.values.toList())
    }

    @Test
    fun `SET-10 captions default off and can be turned on`() {
        val prefs = MemoryPrefs()
        assertFalse(flag(prefs, "captions"))
        setFlag(prefs, "captions", true)
        assertTrue(flag(prefs, "captions"))
    }

    @Test
    fun `HOME-01 subscriptions home page labels the button`() {
        val prefs = MemoryPrefs().apply { map["home"] = "https://m.youtube.com/feed/subscriptions" }
        assertEquals(R.string.subscriptions, homeLabel(prefs))
    }

    @Test
    fun `HOME-02 history home page labels the button`() {
        val prefs = MemoryPrefs().apply { map["home"] = "https://m.youtube.com/feed/history" }
        assertEquals(R.string.history, homeLabel(prefs))
    }

    @Test
    fun `HOME-03 default home page labels the button`() {
        assertEquals(R.string.home_home, homeLabel(MemoryPrefs()))
    }
}
