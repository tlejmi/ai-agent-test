# Android Auth0 Authentication Application

A complete Android application built with **Kotlin** that demonstrates secure user authentication using **Auth0 SDK** with **PKCE (Proof Key for Code Exchange)** flow.

## 📱 Features

- ✅ **Auth0 Authentication** with PKCE flow for enhanced security
- ✅ **Sign Up** functionality for new user registration
- ✅ **Login** with secure token management
- ✅ **Logout** with token revocation
- ✅ **User Profile Display** after successful authentication
- ✅ **Secure Token Storage** using `EncryptedSharedPreferences`
- ✅ **Automatic Token Refresh** for expired tokens
- ✅ **Material Design 3** UI components
- ✅ **Input Validation** for email and password fields
- ✅ **Error Handling** with user-friendly messages
- ✅ **Network Error Retry** logic

## 🏗️ Architecture

### Project Structure

```
app/
├── src/main/
│   ├── kotlin/com/example/oauth0authapp/
│   │   ├── MainActivity.kt              # Login screen
│   │   ├── SignUpActivity.kt            # User registration screen
│   │   ├── HomeActivity.kt              # User profile screen
│   │   ├── Auth0Manager.kt              # Auth0 operations handler
│   │   └── SecureTokenManager.kt        # Encrypted token storage
│   ├── res/
│   │   ├── layout/
│   │   │   ├── activity_main.xml        # Login layout
│   │   │   ├── activity_sign_up.xml     # Sign up layout
│   │   │   └── activity_home.xml        # Home layout
│   │   ├── values/
│   │   │   └── strings.xml              # Auth0 config & strings
│   │   └── xml/
│   │       ├── backup_rules.xml
│   │       └── data_extraction_rules.xml
│   └── AndroidManifest.xml              # App configuration
├── build.gradle.kts                      # App dependencies
└── proguard-rules.pro                    # ProGuard rules
```

## 🔐 Security Features

1. **PKCE Flow**: Implements OAuth 2.0 PKCE extension for enhanced security
2. **Encrypted Storage**: Uses `EncryptedSharedPreferences` with AES256-GCM encryption
3. **Secure Token Management**: Access tokens, ID tokens, and refresh tokens stored securely
4. **Automatic Token Refresh**: Refreshes expired tokens automatically
5. **Token Revocation**: Revokes refresh tokens on logout

## 🚀 Setup Instructions

### Prerequisites

- **Android Studio** (Arctic Fox or later)
- **JDK 8** or higher
- **Auth0 Account** ([Sign up here](https://auth0.com/))

### 1. Auth0 Configuration

1. **Create an Auth0 Application**:
   - Go to [Auth0 Dashboard](https://manage.auth0.com/)
   - Create a new **Native Application**
   - Note down your **Domain** and **Client ID**

2. **Configure Allowed Callback URLs**:
   ```
   demo://YOUR_AUTH0_DOMAIN/android/com.example.oauth0authapp/callback
   ```
   Replace `YOUR_AUTH0_DOMAIN` with your actual Auth0 domain.

3. **Configure Allowed Logout URLs**:
   ```
   demo://YOUR_AUTH0_DOMAIN/android/com.example.oauth0authapp/callback
   ```

4. **Enable Database Connection**:
   - In Auth0 Dashboard, go to **Authentication > Database**
   - Create or enable **Username-Password-Authentication**
   - Enable sign-ups if needed

### 2. Update Application Configuration

Open `app/src/main/res/values/strings.xml` and replace the Auth0 credentials:

```xml
<string name="com_auth0_domain">YOUR_AUTH0_DOMAIN.auth0.com</string>
<string name="com_auth0_client_id">YOUR_CLIENT_ID</string>
```

**Example**:
```xml
<string name="com_auth0_domain">dev-abc123.us.auth0.com</string>
<string name="com_auth0_client_id">AbCdEf123456789XyZ</string>
```

### 3. Build and Run

1. Open the project in **Android Studio**
2. Sync Gradle files
3. Connect an Android device or start an emulator (API 24+)
4. Run the application

## 📖 Usage Guide

### Login Flow

1. **Launch the app** → Opens Login screen
2. **Enter email and password**
3. **Tap "Login"** → Opens browser for Auth0 authentication
4. **Authenticate** → Redirected back to app
5. **Home screen** displays user profile

### Sign Up Flow

1. **Tap "Don't have an account? Sign up"** on Login screen
2. **Enter email, password, and confirm password**
3. **Tap "Sign Up"** → Creates new user account
4. **Redirects to Home screen** after successful registration

### Logout

1. **Tap "Logout"** button on Home screen
2. **Tokens are cleared** and revoked on server
3. **Redirects to Login screen**

## 🛠️ Key Components

### MainActivity (Login Screen)

- Validates email and password inputs
- Performs Auth0 login with PKCE flow
- Handles authentication errors
- Navigates to Home screen on success

### SignUpActivity (Registration Screen)

- Validates email, password, and password confirmation
- Creates new user via Auth0 Management API
- Provides error handling and retry logic
- Navigates to Home screen on success

### HomeActivity (Profile Screen)

- Displays user profile information
- Loads cached data for immediate display
- Fetches fresh profile from API
- Handles token refresh for expired tokens
- Provides logout functionality

### Auth0Manager

- Centralizes all Auth0 operations
- Implements login, sign up, logout, and token refresh
- Uses PKCE flow for enhanced security
- Manages token lifecycle

### SecureTokenManager

- Encrypts tokens using `EncryptedSharedPreferences`
- Provides secure storage for access, ID, and refresh tokens
- Stores user profile information
- Checks token expiration status

## 🎨 UI Components

- **Material Design 3** theme
- **TextInputLayout** for form fields with error display
- **MaterialButton** for actions
- **CircularProgressIndicator** for loading states
- **MaterialCardView** for profile display
- Responsive layouts with **ConstraintLayout**

## 🔧 Dependencies

```kotlin
// Auth0 SDK
implementation("com.auth0.android:auth0:2.10.2")

// Security for encrypted storage
implementation("androidx.security:security-crypto:1.1.0-alpha06")

// Coroutines for async operations
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

// Material Design
implementation("com.google.android.material:material:1.11.0")

// AndroidX Components
implementation("androidx.core:core-ktx:1.12.0")
implementation("androidx.appcompat:appcompat:1.6.1")
```

## 🔍 Error Handling

The app handles various error scenarios:

- **Invalid credentials**: Shows appropriate error message
- **Network errors**: Displays network error with retry option
- **Token expiration**: Automatically refreshes tokens
- **Validation errors**: Shows inline errors for form fields
- **API errors**: Displays user-friendly error messages

## 📝 Validation Rules

### Email
- Must not be empty
- Must be a valid email format

### Password
- Must not be empty
- Minimum 8 characters
- Must match confirmation (for sign up)

## 🌐 Network Configuration

The app requires internet permission, configured in `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## 🔒 Security Best Practices

1. **Never commit Auth0 credentials** to version control
2. **Use environment variables** for sensitive data in production
3. **Enable ProGuard** for release builds
4. **Keep Auth0 SDK updated** to latest version
5. **Implement certificate pinning** for production apps
6. **Use biometric authentication** for enhanced security (optional)

## 🐛 Troubleshooting

### Issue: "Callback URL mismatch"
**Solution**: Ensure the callback URL in Auth0 dashboard matches the scheme in `AndroidManifest.xml`

### Issue: "Network error"
**Solution**: Check internet connection and Auth0 configuration

### Issue: "Invalid credentials"
**Solution**: Verify Auth0 domain and client ID in `strings.xml`

### Issue: "User already exists"
**Solution**: User is already registered, use login instead

## 📄 License

This project is open source and available for educational purposes.

## 🤝 Contributing

Contributions are welcome! Please follow the existing code style and add tests for new features.

## 📧 Support

For issues or questions, please refer to:
- [Auth0 Documentation](https://auth0.com/docs)
- [Auth0 Android SDK](https://github.com/auth0/Auth0.Android)

---

**Built with ❤️ using Kotlin and Auth0**
