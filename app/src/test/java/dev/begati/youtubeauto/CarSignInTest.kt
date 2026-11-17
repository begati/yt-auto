package dev.begati.youtubeauto

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** SIGNIN-01, SIGNIN-03 (spec car-signin). */
class CarSignInTest {
    @Test
    fun `SIGNIN-01 accounts google com is blocked on the car`() {
        for (url in listOf(
            "https://accounts.google.com/ServiceLogin?service=youtube&continue=https://m.youtube.com",
            "https://accounts.google.com/v3/signin/identifier?continue=https%3A%2F%2Fm.youtube.com",
            "https://ACCOUNTS.GOOGLE.COM/AccountChooser",
            "http://accounts.google.com/",
        )) assertTrue(url, isCarSignIn(url))
    }

    @Test
    fun `SIGNIN-03 other hosts and bad urls are not blocked`() {
        for (url in listOf(
            "https://m.youtube.com/",
            "https://m.youtube.com/signin?next=accounts.google.com",
            "https://www.google.com/search?q=accounts.google.com",
            "https://youtube.com/watch?v=abc",
            "https://accounts.google.com.evil.example/",
            "not a url",
            "",
            null,
        )) assertFalse("$url", isCarSignIn(url))
    }
}
