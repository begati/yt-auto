package dev.begati.youtubeauto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** INST-01..06 (spec install-check). */
class InstallCheckTest {
    @Test
    fun `INST-01 warns when the installer is not the Play Store`() {
        assertTrue(installWarning(null, updatedAt = 100, ignoredAt = 0)) // adb install
        assertTrue(installWarning("com.google.android.packageinstaller", updatedAt = 100, ignoredAt = 0)) // APK opened
        assertFalse(installWarning("com.android.vending", updatedAt = 100, ignoredAt = 0)) // KingInstaller / Play
    }

    @Test
    fun `INST-02 the button opens KingInstaller or its download page`() {
        assertEquals(KingAction.OPEN, kingAction(kingInstalled = true))
        assertEquals(KingAction.DOWNLOAD, kingAction(kingInstalled = false))
        assertEquals("https://github.com/fcaronte/KingInstaller/releases", KING_RELEASES)
        assertEquals("com.example.kinginstaller", KING_PACKAGE)
    }

    @Test
    fun `INST-03 notifies only when Android Auto connects with a warning`() {
        assertTrue(notifyInstall("com.google.android.projection.gearhead", warning = true))
        assertFalse(notifyInstall("com.google.android.projection.gearhead", warning = false))
        assertFalse(notifyInstall("com.android.systemui", warning = true)) // another media client
    }

    @Test
    fun `INST-06 ignoring hides the warning until the app is installed again`() {
        assertFalse(installWarning(null, updatedAt = 100, ignoredAt = 100))
        assertTrue(installWarning(null, updatedAt = 200, ignoredAt = 100)) // installed again, still wrong
    }
}
