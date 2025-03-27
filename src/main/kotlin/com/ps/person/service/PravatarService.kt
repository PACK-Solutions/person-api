package com.ps.person.service

import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.net.URL

@Service
class PravatarService {
    private val logger = LoggerFactory.getLogger(PravatarService::class.java)

    /**
     * Generates a Pravatar URL for the given email or name.
     * If email is not available, uses the name to generate a unique hash.
     *
     * @param email The email to generate the Pravatar for, or null if not available
     * @param name The name to use as fallback if email is not available
     * @param size The size of the Pravatar image in pixels
     * @return The Pravatar URL
     */
    fun generatePravatarUrl(email: String? = null, name: String, size: Int = 200): String {
        val hash = if (!email.isNullOrBlank()) {
            DigestUtils.md5Hex(email.trim().lowercase())
        } else {
            DigestUtils.md5Hex(name.trim().lowercase())
        }

        return "https://i.pravatar.cc/$size?u=$hash"
    }

    /**
     * Fetches the Pravatar image from the given URL and converts it to base64.
     *
     * @param pravatarUrl The URL to fetch the Pravatar from
     * @return The base64 encoded image
     */
    fun fetchPravatarAsBase64(pravatarUrl: String): String {
        return try {
            val connection = URL(pravatarUrl).openConnection()
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            val imageBytes = connection.getInputStream().use { it.readBytes() }
            val base64 = Base64.encodeBase64String(imageBytes)

            logger.debug("Successfully fetched and encoded Pravatar image")
            base64
        } catch (e: Exception) {
            logger.error("Error fetching Pravatar image: ${e.message}", e)
            "" // Return empty string on error
        }
    }
}
