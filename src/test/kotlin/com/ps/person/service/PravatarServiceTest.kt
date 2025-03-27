package com.ps.person.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PravatarServiceTest {

    private lateinit var pravatarService: PravatarService

    @BeforeEach
    fun setup() {
        pravatarService = PravatarService()
    }

    @Test
    fun `generatePravatarUrl should create valid URL from email`() {
        val email = "test@example.com"
        val url = pravatarService.generatePravatarUrl(email = email, name = "Test User")

        // The MD5 hash of "test@example.com" is "55502f40dc8b7c769880b10874abc9d0"
        val expectedUrl = "https://i.pravatar.cc/200?u=55502f40dc8b7c769880b10874abc9d0"

        assertEquals(expectedUrl, url)
    }

    @Test
    fun `generatePravatarUrl should create valid URL from name when email is null`() {
        val name = "Test User"
        val url = pravatarService.generatePravatarUrl(name = name)

        // The MD5 hash of "test user" (lowercase) is "0d432e6298384cc9b7c6d338ea89bd79"
        val expectedUrl = "https://i.pravatar.cc/200?u=0d432e6298384cc9b7c6d338ea89bd79"

        assertEquals(expectedUrl, url)
    }

    @Test
    fun `fetchPravatarAsBase64 should return non-empty string for valid URL`() {
        val url = "https://i.pravatar.cc/200?u=00000000000000000000000000000000"
        val base64 = pravatarService.fetchPravatarAsBase64(url)

        assertNotNull(base64)
        assertTrue(base64.isNotEmpty())
    }
}