package com.example.oauth0authapp

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * SecureTokenManager handles secure storage of OAuth tokens using EncryptedSharedPreferences.
 * This ensures tokens are encrypted at rest for enhanced security.
 */
class SecureTokenManager(context: Context) {

    companion object {
        private const val PREFS_NAME = "secure_prefs"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_ID_TOKEN = "id_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_TOKEN_TYPE = "token_type"
        private const val KEY_EXPIRES_AT = "expires_at"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_ID = "user_id"
    }

    // Create or retrieve the master key for encryption
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    // Create encrypted shared preferences
    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /**
     * Save access token securely
     */
    fun saveAccessToken(token: String) {
        sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, token).apply()
    }

    /**
     * Get access token
     */
    fun getAccessToken(): String? {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
    }

    /**
     * Save ID token securely
     */
    fun saveIdToken(token: String) {
        sharedPreferences.edit().putString(KEY_ID_TOKEN, token).apply()
    }

    /**
     * Get ID token
     */
    fun getIdToken(): String? {
        return sharedPreferences.getString(KEY_ID_TOKEN, null)
    }

    /**
     * Save refresh token securely
     */
    fun saveRefreshToken(token: String) {
        sharedPreferences.edit().putString(KEY_REFRESH_TOKEN, token).apply()
    }

    /**
     * Get refresh token
     */
    fun getRefreshToken(): String? {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
    }

    /**
     * Save token type
     */
    fun saveTokenType(tokenType: String) {
        sharedPreferences.edit().putString(KEY_TOKEN_TYPE, tokenType).apply()
    }

    /**
     * Get token type
     */
    fun getTokenType(): String? {
        return sharedPreferences.getString(KEY_TOKEN_TYPE, null)
    }

    /**
     * Save token expiration time
     */
    fun saveExpiresAt(expiresAt: Long) {
        sharedPreferences.edit().putLong(KEY_EXPIRES_AT, expiresAt).apply()
    }

    /**
     * Get token expiration time
     */
    fun getExpiresAt(): Long {
        return sharedPreferences.getLong(KEY_EXPIRES_AT, 0)
    }

    /**
     * Check if token is expired
     */
    fun isTokenExpired(): Boolean {
        val expiresAt = getExpiresAt()
        return expiresAt > 0 && System.currentTimeMillis() >= expiresAt
    }

    /**
     * Save user email
     */
    fun saveUserEmail(email: String) {
        sharedPreferences.edit().putString(KEY_USER_EMAIL, email).apply()
    }

    /**
     * Get user email
     */
    fun getUserEmail(): String? {
        return sharedPreferences.getString(KEY_USER_EMAIL, null)
    }

    /**
     * Save user name
     */
    fun saveUserName(name: String) {
        sharedPreferences.edit().putString(KEY_USER_NAME, name).apply()
    }

    /**
     * Get user name
     */
    fun getUserName(): String? {
        return sharedPreferences.getString(KEY_USER_NAME, null)
    }

    /**
     * Save user ID
     */
    fun saveUserId(userId: String) {
        sharedPreferences.edit().putString(KEY_USER_ID, userId).apply()
    }

    /**
     * Get user ID
     */
    fun getUserId(): String? {
        return sharedPreferences.getString(KEY_USER_ID, null)
    }

    /**
     * Check if user is logged in (has valid tokens)
     */
    fun isLoggedIn(): Boolean {
        val accessToken = getAccessToken()
        return !accessToken.isNullOrEmpty() && !isTokenExpired()
    }

    /**
     * Clear all stored tokens and user data (logout)
     */
    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
}
