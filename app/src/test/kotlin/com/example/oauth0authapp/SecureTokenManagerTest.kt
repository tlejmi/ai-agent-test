package com.example.oauth0authapp

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for SecureTokenManager
 * Tests secure token storage, retrieval, and management
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SecureTokenManagerTest {

    private lateinit var context: Context
    private lateinit var tokenManager: SecureTokenManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        tokenManager = SecureTokenManager(context)
        // Clear any existing data
        tokenManager.clearAll()
    }

    @After
    fun tearDown() {
        tokenManager.clearAll()
    }

    @Test
    fun `test save and retrieve access token`() {
        // Given
        val testToken = "test_access_token_12345"

        // When
        tokenManager.saveAccessToken(testToken)
        val retrievedToken = tokenManager.getAccessToken()

        // Then
        assertEquals(testToken, retrievedToken)
    }

    @Test
    fun `test save and retrieve id token`() {
        // Given
        val testToken = "test_id_token_67890"

        // When
        tokenManager.saveIdToken(testToken)
        val retrievedToken = tokenManager.getIdToken()

        // Then
        assertEquals(testToken, retrievedToken)
    }

    @Test
    fun `test save and retrieve refresh token`() {
        // Given
        val testToken = "test_refresh_token_abcde"

        // When
        tokenManager.saveRefreshToken(testToken)
        val retrievedToken = tokenManager.getRefreshToken()

        // Then
        assertEquals(testToken, retrievedToken)
    }

    @Test
    fun `test save and retrieve token type`() {
        // Given
        val tokenType = "Bearer"

        // When
        tokenManager.saveTokenType(tokenType)
        val retrievedType = tokenManager.getTokenType()

        // Then
        assertEquals(tokenType, retrievedType)
    }

    @Test
    fun `test save and retrieve user email`() {
        // Given
        val email = "test@example.com"

        // When
        tokenManager.saveUserEmail(email)
        val retrievedEmail = tokenManager.getUserEmail()

        // Then
        assertEquals(email, retrievedEmail)
    }

    @Test
    fun `test save and retrieve user name`() {
        // Given
        val name = "Test User"

        // When
        tokenManager.saveUserName(name)
        val retrievedName = tokenManager.getUserName()

        // Then
        assertEquals(name, retrievedName)
    }

    @Test
    fun `test save and retrieve user id`() {
        // Given
        val userId = "auth0|123456789"

        // When
        tokenManager.saveUserId(userId)
        val retrievedId = tokenManager.getUserId()

        // Then
        assertEquals(userId, retrievedId)
    }

    @Test
    fun `test token expiration check - not expired`() {
        // Given
        val futureTime = System.currentTimeMillis() + 3600000 // 1 hour from now
        tokenManager.saveAccessToken("test_token")
        tokenManager.saveExpiresAt(futureTime)

        // When
        val isExpired = tokenManager.isTokenExpired()

        // Then
        assertFalse(isExpired, "Token should not be expired")
    }

    @Test
    fun `test token expiration check - expired`() {
        // Given
        val pastTime = System.currentTimeMillis() - 3600000 // 1 hour ago
        tokenManager.saveAccessToken("test_token")
        tokenManager.saveExpiresAt(pastTime)

        // When
        val isExpired = tokenManager.isTokenExpired()

        // Then
        assertTrue(isExpired, "Token should be expired")
    }

    @Test
    fun `test isLoggedIn returns true when valid token exists`() {
        // Given
        val futureTime = System.currentTimeMillis() + 3600000
        tokenManager.saveAccessToken("valid_token")
        tokenManager.saveExpiresAt(futureTime)

        // When
        val isLoggedIn = tokenManager.isLoggedIn()

        // Then
        assertTrue(isLoggedIn, "Should be logged in with valid token")
    }

    @Test
    fun `test isLoggedIn returns false when no token exists`() {
        // Given - no token saved

        // When
        val isLoggedIn = tokenManager.isLoggedIn()

        // Then
        assertFalse(isLoggedIn, "Should not be logged in without token")
    }

    @Test
    fun `test isLoggedIn returns false when token is expired`() {
        // Given
        val pastTime = System.currentTimeMillis() - 3600000
        tokenManager.saveAccessToken("expired_token")
        tokenManager.saveExpiresAt(pastTime)

        // When
        val isLoggedIn = tokenManager.isLoggedIn()

        // Then
        assertFalse(isLoggedIn, "Should not be logged in with expired token")
    }

    @Test
    fun `test clearAll removes all stored data`() {
        // Given
        tokenManager.saveAccessToken("access_token")
        tokenManager.saveIdToken("id_token")
        tokenManager.saveRefreshToken("refresh_token")
        tokenManager.saveUserEmail("test@example.com")
        tokenManager.saveUserName("Test User")
        tokenManager.saveUserId("user_123")

        // When
        tokenManager.clearAll()

        // Then
        assertNull(tokenManager.getAccessToken())
        assertNull(tokenManager.getIdToken())
        assertNull(tokenManager.getRefreshToken())
        assertNull(tokenManager.getUserEmail())
        assertNull(tokenManager.getUserName())
        assertNull(tokenManager.getUserId())
        assertFalse(tokenManager.isLoggedIn())
    }

    @Test
    fun `test multiple operations in sequence`() {
        // Save all data
        tokenManager.saveAccessToken("access_123")
        tokenManager.saveIdToken("id_456")
        tokenManager.saveRefreshToken("refresh_789")
        tokenManager.saveUserEmail("user@test.com")
        tokenManager.saveUserName("John Doe")
        tokenManager.saveUserId("auth0|abc123")
        
        val futureTime = System.currentTimeMillis() + 3600000
        tokenManager.saveExpiresAt(futureTime)

        // Verify all data
        assertEquals("access_123", tokenManager.getAccessToken())
        assertEquals("id_456", tokenManager.getIdToken())
        assertEquals("refresh_789", tokenManager.getRefreshToken())
        assertEquals("user@test.com", tokenManager.getUserEmail())
        assertEquals("John Doe", tokenManager.getUserName())
        assertEquals("auth0|abc123", tokenManager.getUserId())
        assertTrue(tokenManager.isLoggedIn())
    }

    @Test
    fun `test overwriting existing tokens`() {
        // Given
        tokenManager.saveAccessToken("old_token")
        
        // When
        tokenManager.saveAccessToken("new_token")
        
        // Then
        assertEquals("new_token", tokenManager.getAccessToken())
    }

    @Test
    fun `test empty string handling`() {
        // Given
        tokenManager.saveAccessToken("")
        
        // When
        val token = tokenManager.getAccessToken()
        val isLoggedIn = tokenManager.isLoggedIn()
        
        // Then
        assertEquals("", token)
        assertFalse(isLoggedIn, "Empty token should not count as logged in")
    }
}
