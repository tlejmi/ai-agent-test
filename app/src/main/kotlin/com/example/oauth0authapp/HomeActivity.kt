package com.example.oauth0authapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.auth0.android.authentication.AuthenticationException
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import android.widget.TextView
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * HomeActivity - Home/Profile Screen
 * Displays user profile information after successful authentication.
 * Provides logout functionality.
 */
class HomeActivity : AppCompatActivity() {

    // UI Components
    private lateinit var welcomeTextView: TextView
    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var userIdTextView: TextView
    private lateinit var logoutButton: MaterialButton
    private lateinit var progressBar: CircularProgressIndicator

    // Auth0 Manager
    private lateinit var auth0Manager: Auth0Manager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize Auth0Manager
        auth0Manager = Auth0Manager(this)

        // Check if user is logged in
        if (!auth0Manager.isLoggedIn()) {
            navigateToLogin()
            return
        }

        // Initialize UI components
        initViews()
        setupListeners()
        
        // Disable back navigation (user must use logout button)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing - prevent back navigation for security
                // User must use logout button
            }
        })
        
        // Load user profile
        loadUserProfile()
    }

    /**
     * Initialize all UI components
     */
    private fun initViews() {
        welcomeTextView = findViewById(R.id.welcomeTextView)
        nameTextView = findViewById(R.id.nameTextView)
        emailTextView = findViewById(R.id.emailTextView)
        userIdTextView = findViewById(R.id.userIdTextView)
        logoutButton = findViewById(R.id.logoutButton)
        progressBar = findViewById(R.id.progressBar)
    }

    /**
     * Setup click listeners for buttons
     */
    private fun setupListeners() {
        // Logout button click listener
        logoutButton.setOnClickListener {
            performLogout()
        }
    }

    /**
     * Load and display user profile information
     * First tries to load cached data, then fetches from API if needed
     */
    private fun loadUserProfile() {
        // Load cached user info first for immediate display
        val (cachedName, cachedEmail, cachedUserId) = auth0Manager.getCachedUserInfo()
        
        if (!cachedName.isNullOrEmpty() || !cachedEmail.isNullOrEmpty()) {
            displayUserInfo(cachedName, cachedEmail, cachedUserId)
        }

        // Fetch fresh user profile from API
        lifecycleScope.launch {
            try {
                val userProfile = auth0Manager.getUserProfile()
                
                // Display updated user information
                displayUserInfo(
                    userProfile.name,
                    userProfile.email,
                    userProfile.getId()
                )

            } catch (e: AuthenticationException) {
                // Handle authentication errors
                handleAuthenticationError(e)
            } catch (e: IOException) {
                // Handle network errors - show cached data if available
                if (cachedName.isNullOrEmpty() && cachedEmail.isNullOrEmpty()) {
                    showError(getString(R.string.error_network))
                }
            } catch (e: Exception) {
                // Handle generic errors
                if (cachedName.isNullOrEmpty() && cachedEmail.isNullOrEmpty()) {
                    showError(getString(R.string.error_generic))
                }
            }
        }
    }

    /**
     * Display user information in the UI
     */
    private fun displayUserInfo(name: String?, email: String?, userId: String?) {
        // Display name or fallback to email
        val displayName = name ?: email?.substringBefore("@") ?: "User"
        welcomeTextView.text = getString(R.string.welcome)
        nameTextView.text = displayName
        
        // Display email
        emailTextView.text = email ?: "N/A"
        
        // Display user ID (truncated for better display)
        userIdTextView.text = userId?.let {
            if (it.length > 20) it.substring(0, 20) + "..." else it
        } ?: "N/A"
    }

    /**
     * Perform logout operation
     * Clears tokens and revokes refresh token on server
     */
    private fun performLogout() {
        // Show loading state
        setLoadingState(true)

        // Launch coroutine for async operation
        lifecycleScope.launch {
            try {
                // Perform logout
                val success = auth0Manager.logout()
                
                if (success) {
                    // Show success message
                    Toast.makeText(
                        this@HomeActivity,
                        getString(R.string.success_logout),
                        Toast.LENGTH_SHORT
                    ).show()

                    // Navigate to login screen
                    navigateToLogin()
                } else {
                    showError(getString(R.string.error_logout_failed))
                }

            } catch (e: AuthenticationException) {
                // Even if logout API fails, clear local tokens and proceed
                showError("Logout completed locally")
                navigateToLogin()
            } catch (e: IOException) {
                // Handle network errors
                showError(getString(R.string.error_network))
            } catch (e: Exception) {
                // Handle generic errors
                showError(getString(R.string.error_generic))
            } finally {
                setLoadingState(false)
            }
        }
    }

    /**
     * Handle authentication errors with appropriate messages
     */
    private fun handleAuthenticationError(error: AuthenticationException) {
        val errorMessage = when {
            error.isNetworkError -> getString(R.string.error_network)
            error.isTokenExpired -> {
                // Token expired, try to refresh
                refreshTokenAndRetry()
                return
            }
            else -> error.getDescription() ?: getString(R.string.error_generic)
        }
        showError(errorMessage)
    }

    /**
     * Attempt to refresh token and retry loading profile
     */
    private fun refreshTokenAndRetry() {
        lifecycleScope.launch {
            try {
                // Refresh access token
                auth0Manager.refreshToken()
                
                // Retry loading profile
                loadUserProfile()
                
            } catch (e: Exception) {
                // If refresh fails, navigate to login
                showError("Session expired. Please login again.")
                navigateToLogin()
            }
        }
    }

    /**
     * Display error message to user
     */
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Set loading state for UI components
     * @param isLoading true to show loading, false to hide
     */
    private fun setLoadingState(isLoading: Boolean) {
        if (isLoading) {
            logoutButton.isEnabled = false
            progressBar.visibility = View.VISIBLE
        } else {
            logoutButton.isEnabled = true
            progressBar.visibility = View.GONE
        }
    }

    /**
     * Navigate to Login screen and clear back stack
     */
    private fun navigateToLogin() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    /**
     * Extension property to check for network errors
     */
    private val AuthenticationException.isNetworkError: Boolean
        get() = this.cause is IOException || this.message?.contains("network", ignoreCase = true) == true

    /**
     * Extension property to check for token expiration
     */
    private val AuthenticationException.isTokenExpired: Boolean
        get() = this.code == "invalid_token" || 
                this.message?.contains("expired", ignoreCase = true) == true
}
