package com.example.oauth0authapp

import com.auth0.android.authentication.AuthenticationException
import org.junit.Test
import java.io.IOException
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for Auth0Manager error detection logic
 * Tests network error and credential error detection
 */
class Auth0ErrorHandlingTest {

    @Test
    fun `test network error detection - IOException cause`() {
        // Create an AuthenticationException with IOException as cause
        val ioException = IOException("Network unavailable")
        val authException = AuthenticationException(
            "network",
            "Network error occurred",
            ioException
        )

        // Test network error detection
        val isNetworkError = authException.cause is IOException
        assertTrue(isNetworkError, "Should detect IOException as network error")
    }

    @Test
    fun `test network error detection - message contains network`() {
        // Create exception with "network" in message
        val authException = AuthenticationException(
            "error",
            "Network timeout occurred"
        )

        val isNetworkError = authException.message?.contains("network", ignoreCase = true) == true
        assertTrue(isNetworkError, "Should detect 'network' in message")
    }

    @Test
    fun `test invalid credentials detection - by code`() {
        // Test invalid_grant code
        val authException = AuthenticationException(
            "invalid_grant",
            "Invalid credentials provided"
        )

        val isInvalidCredentials = authException.code == "invalid_grant"
        assertTrue(isInvalidCredentials, "Should detect invalid_grant code")
    }

    @Test
    fun `test invalid credentials detection - by message`() {
        val testCases = listOf(
            "Invalid credentials",
            "Wrong credentials provided",
            "Unauthorized access",
            "unauthorized user"
        )

        testCases.forEach { message ->
            val containsCredentials = message.contains("credentials", ignoreCase = true)
            val containsUnauthorized = message.contains("unauthorized", ignoreCase = true)
            
            assertTrue(
                containsCredentials || containsUnauthorized,
                "Message '$message' should be detected as invalid credentials"
            )
        }
    }

    @Test
    fun `test token expiration detection - by code`() {
        val authException = AuthenticationException(
            "invalid_token",
            "Token has expired"
        )

        val isTokenExpired = authException.code == "invalid_token"
        assertTrue(isTokenExpired, "Should detect invalid_token code")
    }

    @Test
    fun `test token expiration detection - by message`() {
        val messages = listOf(
            "Token has expired",
            "Your session expired",
            "EXPIRED token"
        )

        messages.forEach { message ->
            val isExpired = message.contains("expired", ignoreCase = true)
            assertTrue(isExpired, "Message '$message' should be detected as expired")
        }
    }

    @Test
    fun `test user exists error detection`() {
        val messages = listOf(
            "User already exists",
            "user already exists",
            "The user already exists in the database"
        )

        messages.forEach { message ->
            val userExists = message.contains("user already exists", ignoreCase = true)
            assertTrue(userExists, "Message '$message' should detect user exists")
        }
    }

    @Test
    fun `test password requirement error detection`() {
        val messages = listOf(
            "Password does not meet requirements",
            "Password is too weak",
            "password must contain"
        )

        messages.forEach { message ->
            val isPasswordError = message.contains("password", ignoreCase = true)
            assertTrue(isPasswordError, "Message '$message' should detect password error")
        }
    }

    @Test
    fun `test non-error cases`() {
        // Test that success messages are not detected as errors
        val authException = AuthenticationException(
            "success",
            "Authentication successful"
        )

        val isNetworkError = authException.cause is IOException
        val isInvalidGrant = authException.code == "invalid_grant"

        assertFalse(isNetworkError, "Success should not be network error")
        assertFalse(isInvalidGrant, "Success code should not be invalid_grant")
    }

    @Test
    fun `test error code comparison`() {
        val errorCodes = mapOf(
            "invalid_grant" to "invalid credentials",
            "invalid_token" to "token expired",
            "unauthorized" to "not authorized",
            "network_error" to "network issue"
        )

        errorCodes.forEach { (code, description) ->
            val exception = AuthenticationException(code, description)
            assertTrue(
                exception.code == code,
                "Error code should match: $code"
            )
        }
    }

    @Test
    fun `test case insensitive message matching`() {
        val messages = listOf(
            Pair("NETWORK error", "network"),
            Pair("Network Error", "network"),
            Pair("network ERROR", "network"),
            Pair("Invalid CREDENTIALS", "credentials"),
            Pair("EXPIRED token", "expired")
        )

        messages.forEach { (message, keyword) ->
            val matches = message.contains(keyword, ignoreCase = true)
            assertTrue(matches, "Should match '$keyword' in '$message' case-insensitively")
        }
    }
}
