package com.example.oauth0authapp

import android.content.Context
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.auth0.android.result.UserProfile
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Auth0Manager handles all authentication operations including login, sign up, and logout.
 * Uses PKCE flow for enhanced security.
 */
class Auth0Manager(private val context: Context) {

    // Initialize Auth0 account with credentials from resources
    private val account = Auth0(
        context.getString(R.string.com_auth0_client_id),
        context.getString(R.string.com_auth0_domain)
    )

    // Authentication API client for user operations
    private val authenticationClient = AuthenticationAPIClient(account)

    // Secure token storage
    private val tokenManager = SecureTokenManager(context)

    /**
     * Login using OAuth0 with PKCE flow.
     * Opens browser for authentication and handles the callback.
     */
    suspend fun login(): Credentials = suspendCancellableCoroutine { continuation ->
        WebAuthProvider.login(account)
            .withScheme("demo") // Matches the scheme in AndroidManifest.xml
            .withScope("openid profile email offline_access") // Request required scopes
            .withAudience("https://${account.getDomain()}/api/v2/") // API audience
            .start(context, object : Callback<Credentials, AuthenticationException> {
                override fun onSuccess(result: Credentials) {
                    // Save tokens securely
                    result.accessToken.let { tokenManager.saveAccessToken(it) }
                    result.idToken?.let { tokenManager.saveIdToken(it) }
                    result.refreshToken?.let { tokenManager.saveRefreshToken(it) }
                    result.type?.let { tokenManager.saveTokenType(it) }
                    
                    // Calculate and save expiration time
                    val expiresAt = System.currentTimeMillis() + (result.expiresIn * 1000)
                    tokenManager.saveExpiresAt(expiresAt)
                    
                    continuation.resume(result)
                }

                override fun onFailure(error: AuthenticationException) {
                    continuation.resumeWithException(error)
                }
            })

        // Handle cancellation
        continuation.invokeOnCancellation {
            // Cleanup if needed
        }
    }

    /**
     * Sign up a new user using Auth0 Management API.
     * Creates a new database user with email and password.
     */
    suspend fun signUp(email: String, password: String): Boolean = suspendCancellableCoroutine { continuation ->
        authenticationClient
            .signUp(email, password, "Username-Password-Authentication") // Default database connection
            .validateClaims()
            .start(object : Callback<Credentials, AuthenticationException> {
                override fun onSuccess(result: Credentials) {
                    // Save tokens after successful sign up
                    result.accessToken.let { tokenManager.saveAccessToken(it) }
                    result.idToken?.let { tokenManager.saveIdToken(it) }
                    result.refreshToken?.let { tokenManager.saveRefreshToken(it) }
                    
                    // Calculate and save expiration time
                    val expiresAt = System.currentTimeMillis() + (result.expiresIn * 1000)
                    tokenManager.saveExpiresAt(expiresAt)
                    
                    continuation.resume(true)
                }

                override fun onFailure(error: AuthenticationException) {
                    continuation.resumeWithException(error)
                }
            })

        continuation.invokeOnCancellation {
            // Cleanup if needed
        }
    }

    /**
     * Get user profile information from Auth0.
     */
    suspend fun getUserProfile(): UserProfile = suspendCancellableCoroutine { continuation ->
        val accessToken = tokenManager.getAccessToken()
        
        if (accessToken.isNullOrEmpty()) {
            continuation.resumeWithException(Exception("No access token available"))
            return@suspendCancellableCoroutine
        }

        authenticationClient
            .userInfo(accessToken)
            .start(object : Callback<UserProfile, AuthenticationException> {
                override fun onSuccess(result: UserProfile) {
                    // Save user information
                    result.email?.let { tokenManager.saveUserEmail(it) }
                    result.name?.let { tokenManager.saveUserName(it) }
                    result.getId()?.let { tokenManager.saveUserId(it) }
                    
                    continuation.resume(result)
                }

                override fun onFailure(error: AuthenticationException) {
                    continuation.resumeWithException(error)
                }
            })

        continuation.invokeOnCancellation {
            // Cleanup if needed
        }
    }

    /**
     * Logout user by clearing tokens and revoking refresh token.
     */
    suspend fun logout(): Boolean = suspendCancellableCoroutine { continuation ->
        // Clear local tokens
        val refreshToken = tokenManager.getRefreshToken()
        tokenManager.clearAll()

        // Revoke refresh token on server (optional but recommended)
        if (!refreshToken.isNullOrEmpty()) {
            authenticationClient
                .revoke(refreshToken)
                .start(object : Callback<Void?, AuthenticationException> {
                    override fun onSuccess(result: Void?) {
                        // Logout from Auth0 web session
                        logoutWebSession(continuation)
                    }

                    override fun onFailure(error: AuthenticationException) {
                        // Even if revocation fails, proceed with web logout
                        logoutWebSession(continuation)
                    }
                })
        } else {
            logoutWebSession(continuation)
        }

        continuation.invokeOnCancellation {
            // Cleanup if needed
        }
    }

    /**
     * Logout from web session
     */
    private fun logoutWebSession(continuation: kotlinx.coroutines.CancellableContinuation<Boolean>) {
        WebAuthProvider.logout(account)
            .withScheme("demo")
            .start(context, object : Callback<Void?, AuthenticationException> {
                override fun onSuccess(result: Void?) {
                    continuation.resume(true)
                }

                override fun onFailure(error: AuthenticationException) {
                    // Consider logout successful even if web logout fails
                    // since local tokens are already cleared
                    continuation.resume(true)
                }
            })
    }

    /**
     * Refresh access token using refresh token.
     * Implements automatic token refresh for expired tokens.
     */
    suspend fun refreshToken(): Credentials = suspendCancellableCoroutine { continuation ->
        val refreshToken = tokenManager.getRefreshToken()
        
        if (refreshToken.isNullOrEmpty()) {
            continuation.resumeWithException(Exception("No refresh token available"))
            return@suspendCancellableCoroutine
        }

        authenticationClient
            .renewAuth(refreshToken)
            .start(object : Callback<Credentials, AuthenticationException> {
                override fun onSuccess(result: Credentials) {
                    // Update stored tokens
                    result.accessToken.let { tokenManager.saveAccessToken(it) }
                    result.idToken?.let { tokenManager.saveIdToken(it) }
                    result.refreshToken?.let { tokenManager.saveRefreshToken(it) }
                    
                    // Update expiration time
                    val expiresAt = System.currentTimeMillis() + (result.expiresIn * 1000)
                    tokenManager.saveExpiresAt(expiresAt)
                    
                    continuation.resume(result)
                }

                override fun onFailure(error: AuthenticationException) {
                    // If refresh fails, clear tokens
                    tokenManager.clearAll()
                    continuation.resumeWithException(error)
                }
            })

        continuation.invokeOnCancellation {
            // Cleanup if needed
        }
    }

    /**
     * Check if user is currently logged in
     */
    fun isLoggedIn(): Boolean {
        return tokenManager.isLoggedIn()
    }

    /**
     * Get stored user information without API call
     */
    fun getCachedUserInfo(): Triple<String?, String?, String?> {
        return Triple(
            tokenManager.getUserName(),
            tokenManager.getUserEmail(),
            tokenManager.getUserId()
        )
    }
}
