package dev.begati.youtubeauto

import org.junit.Assert.assertEquals
import org.junit.Test

class ResumeAndNoticeTest {
    private val watch = "https://m.youtube.com/watch?v=abcdefghijk"

    @Test
    fun `resume adds the saved time to the same video`() {
        assertEquals("$watch&t=95s", withResume(watch, "abcdefghijk" to 95))
    }

    @Test
    fun `resume replaces an existing t and keeps other params`() {
        assertEquals("$watch&list=RD1&t=95s", withResume("$watch&t=10s&list=RD1", "abcdefghijk" to 95))
    }

    @Test
    fun `resume leaves other videos, other pages and the first seconds alone`() {
        assertEquals(watch, withResume(watch, "zzzzzzzzzzz" to 95))
        assertEquals(watch, withResume(watch, "abcdefghijk" to 3))
        assertEquals(watch, withResume(watch, null))
        assertEquals("https://m.youtube.com", withResume("https://m.youtube.com", "abcdefghijk" to 95))
    }

    @Test
    fun `feed notice tells login, connection and empty apart`() {
        assertEquals("Faça login no app do celular", feedNotice(signedIn = false, failed = true).second)
        assertEquals("Não foi possível carregar", feedNotice(signedIn = true, failed = true).first)
        assertEquals("Nada por aqui", feedNotice(signedIn = true, failed = false).first)
    }
}
