# Auth0 Dashboard Configuration Checklist

The app has been configured with your Auth0 credentials:
- **Domain:** `genai-403688105282644.eu.auth0.com`
- **Client ID:** `iYnROUFDfYcYpftY9YVIMwPsUjBRBEDi`

## Required Auth0 Dashboard Configuration

To make the app work properly, please verify these settings in your Auth0 Dashboard:

### 1. Allowed Callback URLs

Navigate to: **Applications → Your Application → Settings → Application URIs**

Add this URL to "Allowed Callback URLs":
```
demo://genai-403688105282644.eu.auth0.com/android/com.example.oauth0authapp/callback
```

### 2. Allowed Logout URLs

In the same section, add this URL to "Allowed Logout URLs":
```
demo://genai-403688105282644.eu.auth0.com/android/com.example.oauth0authapp/callback
```

### 3. Enable Database Connection

Navigate to: **Authentication → Database**

Ensure you have a database connection (e.g., "Username-Password-Authentication") and:
- ✅ Connection is enabled
- ✅ Sign-ups are allowed (if you want users to register)

### 4. Save Changes

Click **"Save Changes"** at the bottom of the settings page.

## Quick Test

Once configured:

1. Build and run the app
2. Try to sign up with a test email
3. Try to log in with the created account
4. Verify profile information displays
5. Test logout functionality

## Troubleshooting

If you encounter issues:

- **"Callback URL mismatch"**: Double-check the callback URLs match exactly (including the scheme "demo://")
- **"Invalid client"**: Verify the Client ID is correct
- **"Sign up disabled"**: Enable sign-ups in the database connection settings
- **Network errors**: Ensure the device has internet access

## Ready to Use ✅

The app is now configured and ready to authenticate users with your Auth0 account!

For detailed configuration instructions, see `AUTH0_CONFIGURATION.md`.
