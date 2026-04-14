package com.example.oauth0authapp

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for input validation
 * Tests email and password validation logic
 */
class InputValidationTest {

    @Test
    fun `test valid email format`() {
        val validEmails = listOf(
            "test@example.com",
            "user.name@domain.com",
            "user+tag@example.co.uk",
            "123@test.com",
            "a@b.c"
        )

        validEmails.forEach { email ->
            assertTrue(
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches(),
                "Email '$email' should be valid"
            )
        }
    }

    @Test
    fun `test invalid email format`() {
        val invalidEmails = listOf(
            "notanemail",
            "@example.com",
            "user@",
            "user @example.com",
            "user@.com",
            "",
            "user@domain"
        )

        invalidEmails.forEach { email ->
            assertFalse(
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches(),
                "Email '$email' should be invalid"
            )
        }
    }

    @Test
    fun `test password length validation - valid`() {
        val validPasswords = listOf(
            "12345678",
            "password123",
            "VeryLongPasswordWithManyCharacters",
            "Pass@123"
        )

        validPasswords.forEach { password ->
            assertTrue(
                password.length >= 8,
                "Password with ${password.length} characters should be valid"
            )
        }
    }

    @Test
    fun `test password length validation - invalid`() {
        val invalidPasswords = listOf(
            "",
            "1",
            "12",
            "123",
            "1234",
            "12345",
            "123456",
            "1234567"
        )

        invalidPasswords.forEach { password ->
            assertFalse(
                password.length >= 8,
                "Password with ${password.length} characters should be invalid"
            )
        }
    }

    @Test
    fun `test password matching - valid`() {
        val password = "myPassword123"
        val confirmPassword = "myPassword123"

        assertTrue(
            password == confirmPassword,
            "Matching passwords should be valid"
        )
    }

    @Test
    fun `test password matching - invalid`() {
        val testCases = listOf(
            Pair("password123", "password124"),
            Pair("Password", "password"),
            Pair("test", "Test"),
            Pair("password ", "password"),
            Pair("", "password")
        )

        testCases.forEach { (password, confirmPassword) ->
            assertFalse(
                password == confirmPassword,
                "Passwords '$password' and '$confirmPassword' should not match"
            )
        }
    }

    @Test
    fun `test empty string validation`() {
        val emptyStrings = listOf("", "   ", "\t", "\n")

        emptyStrings.forEach { str ->
            assertTrue(
                str.trim().isEmpty(),
                "String '$str' should be considered empty after trim"
            )
        }
    }

    @Test
    fun `test non-empty string validation`() {
        val nonEmptyStrings = listOf("a", "test", "  test  ", "test\n")

        nonEmptyStrings.forEach { str ->
            assertFalse(
                str.trim().isEmpty(),
                "String '$str' should not be empty after trim"
            )
        }
    }

    @Test
    fun `test combined validation - valid user input`() {
        // Valid case: proper email, long enough password, matching passwords
        val email = "user@example.com"
        val password = "securePassword123"
        val confirmPassword = "securePassword123"

        val isValidEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isValidPassword = password.length >= 8
        val doPasswordsMatch = password == confirmPassword

        assertTrue(isValidEmail, "Email should be valid")
        assertTrue(isValidPassword, "Password should be valid")
        assertTrue(doPasswordsMatch, "Passwords should match")
    }

    @Test
    fun `test combined validation - invalid cases`() {
        // Test case 1: Invalid email
        var email = "invalid-email"
        var password = "validPass123"
        var isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length >= 8
        assertFalse(isValid, "Should be invalid with bad email")

        // Test case 2: Short password
        email = "valid@email.com"
        password = "short"
        isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length >= 8
        assertFalse(isValid, "Should be invalid with short password")

        // Test case 3: Password mismatch
        password = "password123"
        val confirmPassword = "password124"
        isValid = password == confirmPassword
        assertFalse(isValid, "Should be invalid with mismatched passwords")
    }
}
