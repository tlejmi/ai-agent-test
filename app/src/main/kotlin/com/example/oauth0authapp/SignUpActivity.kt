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
 * SignUpActivity - Sign Up Screen
 * Handles new user registration using OAuth0 Management API.
 * Validates email, password, and confirm password fields.
 */
class SignUpActivity : AppCompatActivity() {

    // UI Components
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var confirmPasswordInputLayout: TextInputLayout
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var signUpButton: MaterialButton
    private lateinit var progressBar: CircularProgressIndicator
    private lateinit var loginTextView: TextView

    // Auth0 Manager
    private lateinit var auth0Manager: Auth0Manager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Enable back button in action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize Auth0Manager
        auth0Manager = Auth0Manager(this)

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
        confirmPasswordInputLayout = findViewById(R.id.confirmPasswordInputLayout)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        signUpButton = findViewById(R.id.signUpButton)
        progressBar = findViewById(R.id.progressBar)
        loginTextView = findViewById(R.id.loginTextView)
    }

    /**
     * Setup click listeners for buttons and text views
     */
    private fun setupListeners() {
        // Sign up button click listener
        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            // Validate input fields
            if (validateInputs(email, password, confirmPassword)) {
                performSignUp(email, password)
            }
        }

        // Login text click listener
        loginTextView.setOnClickListener {
            finish() // Go back to login screen
        }
    }

    /**
     * Validate email, password, and confirm password inputs
     * @return true if all inputs are valid, false otherwise
     */
    private fun validateInputs(email: String, password: String, confirmPassword: String): Boolean {
        var isValid = true

        // Clear previous errors
        emailInputLayout.error = null
        passwordInputLayout.error = null
        confirmPasswordInputLayout.error = null

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

        // Validate confirm password
        if (confirmPassword.isEmpty()) {
            confirmPasswordInputLayout.error = getString(R.string.error_empty_password)
            isValid = false
        } else if (password != confirmPassword) {
            confirmPasswordInputLayout.error = getString(R.string.error_passwords_dont_match)
            isValid = false
        }

        return isValid
    }

    /**
     * Perform sign up using Auth0 Management API
     * Creates a new user with email and password
     */
    private fun performSignUp(email: String, password: String) {
        // Show loading state
        setLoadingState(true)

        // Launch coroutine for async operation
        lifecycleScope.launch {
            try {
                // Create new user account
                val success = auth0Manager.signUp(email, password)
                
                if (success) {
                    // Fetch user profile after successful sign up
                    try {
                        auth0Manager.getUserProfile()
                    } catch (e: Exception) {
                        // Profile fetch can fail, but sign up was successful
                        // User can still login
                    }
                    
                    // Show success message
                    Toast.makeText(
                        this@SignUpActivity,
                        getString(R.string.success_signup),
                        Toast.LENGTH_SHORT
                    ).show()

                    // Navigate to home screen or login screen
                    navigateToHome()
                } else {
                    showError(getString(R.string.error_signup_failed))
                }

            } catch (e: AuthenticationException) {
                // Handle authentication errors
                handleAuthenticationError(e)
            } catch (e: IOException) {
                // Handle network errors with retry option
                showErrorWithRetry(getString(R.string.error_network), email, password)
            } catch (e: Exception) {
                // Handle generic errors
                showError(e.message ?: getString(R.string.error_generic))
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
            error.message?.contains("user already exists", ignoreCase = true) == true -> 
                "User with this email already exists"
            error.message?.contains("password", ignoreCase = true) == true -> 
                "Password does not meet requirements"
            else -> error.getDescription() ?: getString(R.string.error_signup_failed)
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
     * Display error message with retry option
     */
    private fun showErrorWithRetry(message: String, email: String, password: String) {
        Toast.makeText(this, "$message Tap sign up to retry.", Toast.LENGTH_LONG).show()
    }

    /**
     * Set loading state for UI components
     * @param isLoading true to show loading, false to hide
     */
    private fun setLoadingState(isLoading: Boolean) {
        if (isLoading) {
            signUpButton.isEnabled = false
            emailEditText.isEnabled = false
            passwordEditText.isEnabled = false
            confirmPasswordEditText.isEnabled = false
            loginTextView.isEnabled = false
            progressBar.visibility = View.VISIBLE
        } else {
            signUpButton.isEnabled = true
            emailEditText.isEnabled = true
            passwordEditText.isEnabled = true
            confirmPasswordEditText.isEnabled = true
            loginTextView.isEnabled = true
            progressBar.visibility = View.GONE
        }
    }

    /**
     * Navigate to Home screen and clear back stack
     */
    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    /**
     * Handle back button press in action bar
     */
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    /**
     * Extension property to check for network errors
     */
    private val AuthenticationException.isNetworkError: Boolean
        get() = this.cause is IOException || this.message?.contains("network", ignoreCase = true) == true
}
