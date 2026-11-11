package dev.begati.youtubeauto

import android.webkit.WebViewClient
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** OFF-09 (spec offline-screen). */
class OfflineTest {
    @Test
    fun `OFF-09 only network errors show the loading screen`() {
        for (code in listOf(
            WebViewClient.ERROR_HOST_LOOKUP, WebViewClient.ERROR_CONNECT, WebViewClient.ERROR_TIMEOUT,
            WebViewClient.ERROR_IO, WebViewClient.ERROR_UNKNOWN,
        )) assertTrue("code $code", isNetworkError(code))
        for (code in listOf(
            WebViewClient.ERROR_UNSUPPORTED_SCHEME, WebViewClient.ERROR_BAD_URL, WebViewClient.ERROR_FILE_NOT_FOUND,
            WebViewClient.ERROR_FAILED_SSL_HANDSHAKE, WebViewClient.ERROR_UNSAFE_RESOURCE,
        )) assertFalse("code $code", isNetworkError(code))
    }
}
