package com.example.oauth0authapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.auth0.android.authentication.AuthenticationException
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import android.widget.TextView
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * MainActivity - Login Screen
 * Handles user authentication using OAuth0 SDK with PKCE flow.
 */
class MainActivity : AppCompatActivity() {

    // UI Components
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var progressBar: CircularProgressIndicator
    private lateinit var signUpTextView: TextView

    // Auth0 Manager
    private lateinit var auth0Manager: Auth0Manager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Auth0Manager
        auth0Manager = Auth0Manager(this)

        // Check if user is already logged in
        if (auth0Manager.isLoggedIn()) {
            navigateToHome()
            return
        }

        // Initialize UI components
        initViews()
        setupListeners()
    }

    /**
     * Initialize all UI components
     */
    private fun initViews() {
        emailInputLayout = findViewById(R.id.emailInputLayout)
        passwordInputLayout = findViewById(R.id.passwordInputLayout)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        progressBar = findViewById(R.id.progressBar)
        signUpTextView = findViewById(R.id.signUpTextView)
    }

    /**
     * Setup click listeners for buttons and text views
     */
    private fun setupListeners() {
        // Login button click listener
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Validate input fields
            if (validateInputs(email, password)) {
                performLogin()
            }
        }

        // Sign up text click listener
        signUpTextView.setOnClickListener {
            navigateToSignUp()
        }
    }

    /**
     * Validate email and password inputs
     * @return true if inputs are valid, false otherwise
     */
    private fun validateInputs(email: String, password: String): Boolean {
        var isValid = true

        // Clear previous errors
        emailInputLayout.error = null
        passwordInputLayout.error = null

        // Validate email
        if (email.isEmpty()) {
            emailInputLayout.error = getString(R.string.error_empty_email)
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.error = getString(R.string.error_invalid_email)
            isValid = false
        }

        // Validate password
        if (password.isEmpty()) {
            passwordInputLayout.error = getString(R.string.error_empty_password)
            isValid = false
        } else if (password.length < 8) {
            passwordInputLayout.error = getString(R.string.error_password_too_short)
            isValid = false
        }

        return isValid
    }

    /**
     * Perform login using Auth0 with PKCE flow
     * This will open a browser for authentication
     */
    private fun performLogin() {
        // Show loading state
        setLoadingState(true)

        // Launch coroutine for async operation
        lifecycleScope.launch {
            try {
                // Perform OAuth0 login with PKCE
                val credentials = auth0Manager.login()
                
                // Fetch user profile after successful login
                val userProfile = auth0Manager.getUserProfile()
                
                // Show success message
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.success_login),
                    Toast.LENGTH_SHORT
                ).show()

                // Navigate to home screen
                navigateToHome()

            } catch (e: AuthenticationException) {
                // Handle authentication errors
                handleAuthenticationError(e)
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
            error.isInvalidCredentials -> getString(R.string.error_login_failed)
            else -> error.getDescription() ?: getString(R.string.error_generic)
        }
        showError(errorMessage)
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
            loginButton.isEnabled = false
            emailEditText.isEnabled = false
            passwordEditText.isEnabled = false
            signUpTextView.isEnabled = false
            progressBar.visibility = View.VISIBLE
        } else {
            loginButton.isEnabled = true
            emailEditText.isEnabled = true
            passwordEditText.isEnabled = true
            signUpTextView.isEnabled = true
            progressBar.visibility = View.GONE
        }
    }

    /**
     * Navigate to Sign Up screen
     */
    private fun navigateToSignUp() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

    /**
     * Navigate to Home screen and finish this activity
     */
    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish() // Prevent going back to login screen
    }

    /**
     * Extension property to check for network errors
     */
    private val AuthenticationException.isNetworkError: Boolean
        get() = this.cause is IOException || this.message?.contains("network", ignoreCase = true) == true

    /**
     * Extension property to check for invalid credentials
     */
    private val AuthenticationException.isInvalidCredentials: Boolean
        get() = this.code == "invalid_grant" || 
                this.message?.contains("credentials", ignoreCase = true) == true ||
                this.message?.contains("unauthorized", ignoreCase = true) == true
}
