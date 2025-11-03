# Android OAuth0 App - Quick Reference

## 🎯 Project Files Overview

### Main Application Files

| File | Purpose | Key Features |
|------|---------|--------------|
| `MainActivity.kt` | Login screen | OAuth0 login with PKCE, email/password validation |
| `SignUpActivity.kt` | Registration screen | User creation, password confirmation, validation |
| `HomeActivity.kt` | Profile screen | Display user info, logout, token refresh |
| `Auth0Manager.kt` | OAuth operations | Login, signup, logout, token refresh, user profile |
| `SecureTokenManager.kt` | Token storage | Encrypted storage using AES256-GCM |

### Configuration Files

| File | Purpose |
|------|---------|
| `AndroidManifest.xml` | App permissions, activities, OAuth callback |
| `strings.xml` | OAuth0 credentials, UI strings, error messages |
| `build.gradle.kts` (app) | Dependencies and build configuration |
| `build.gradle.kts` (project) | Plugin versions |
| `gradle.properties` | Gradle settings |

### Layout Files

| File | Screen | Components |
|------|--------|------------|
| `activity_main.xml` | Login | Email, password inputs, login button |
| `activity_sign_up.xml` | Sign Up | Email, password, confirm password, sign up button |
| `activity_home.xml` | Home | Profile card, logout button |

## 🔑 Key Classes and Methods

### Auth0Manager

```kotlin
// Login with OAuth0
suspend fun login(): Credentials

// Create new user
suspend fun signUp(email: String, password: String): Boolean

// Get user profile
suspend fun getUserProfile(): UserProfile

// Logout and clear tokens
suspend fun logout(): Boolean

// Refresh expired token
suspend fun refreshToken(): Credentials

// Check login status
fun isLoggedIn(): Boolean

// Get cached user info
fun getCachedUserInfo(): Triple<String?, String?, String?>
```

### SecureTokenManager

```kotlin
// Token operations
fun saveAccessToken(token: String)
fun getAccessToken(): String?
fun saveIdToken(token: String)
fun getIdToken(): String?
fun saveRefreshToken(token: String)
fun getRefreshToken(): String?

// User data
fun saveUserEmail(email: String)
fun getUserEmail(): String?
fun saveUserName(name: String)
fun getUserName(): String?

// Utility
fun isLoggedIn(): Boolean
fun isTokenExpired(): Boolean
fun clearAll()
```

## 📋 OAuth0 Setup Steps

### 1. Create OAuth0 Application
```
1. Go to https://manage.auth0.com/
2. Click "Applications" → "Create Application"
3. Choose "Native"
4. Note Domain and Client ID
```

### 2. Configure Callback URLs
```
Add to "Allowed Callback URLs":
demo://YOUR_DOMAIN.auth0.com/android/com.example.oauth0authapp/callback

Example:
demo://dev-abc123.us.auth0.com/android/com.example.oauth0authapp/callback
```

### 3. Configure Logout URLs
```
Add to "Allowed Logout URLs":
demo://YOUR_DOMAIN.auth0.com/android/com.example.oauth0authapp/callback
```

### 4. Enable Database
```
1. Go to "Authentication" → "Database"
2. Enable "Username-Password-Authentication"
3. Enable "Disable Sign Ups" (if you want sign-up control)
```

### 5. Update strings.xml
```xml
<string name="com_auth0_domain">YOUR_DOMAIN.auth0.com</string>
<string name="com_auth0_client_id">YOUR_CLIENT_ID</string>
```

## 🏗️ Build Commands

```bash
# Sync Gradle
./gradlew sync

# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Install on device
./gradlew installDebug

# Build and install
./gradlew clean assembleDebug installDebug
```

## 🧪 Testing Checklist

### Login Testing
- [ ] Valid credentials → Success
- [ ] Invalid credentials → Error message
- [ ] Empty fields → Validation error
- [ ] Invalid email format → Error
- [ ] Short password → Error
- [ ] Network off → Network error

### Sign Up Testing
- [ ] Valid inputs → Success
- [ ] Existing email → Error
- [ ] Password mismatch → Error
- [ ] Short password → Error
- [ ] Invalid email → Error
- [ ] Empty fields → Validation error

### Profile Testing
- [ ] Profile loads correctly
- [ ] Name displays
- [ ] Email displays
- [ ] User ID displays
- [ ] Cached data shows immediately

### Logout Testing
- [ ] Logout succeeds
- [ ] Tokens cleared
- [ ] Redirects to login
- [ ] Cannot go back to home

## 🐛 Common Issues & Solutions

### Issue: "Callback URL mismatch"
**Solution**: 
- Check scheme in AndroidManifest.xml is "demo"
- Verify callback URL in Auth0 dashboard
- Ensure domain matches exactly

### Issue: "Network error"
**Solution**:
- Check internet permission in manifest
- Verify device has network connection
- Check Auth0 domain is correct

### Issue: "Invalid client"
**Solution**:
- Verify client ID in strings.xml
- Check Auth0 application type is "Native"
- Ensure application is not deleted

### Issue: "User already exists"
**Solution**:
- User is registered, use login instead
- Or delete user from Auth0 dashboard

### Issue: "Build error"
**Solution**:
- Sync Gradle files
- Clean and rebuild
- Check all dependencies are downloaded
- Verify Gradle version compatibility

## 📱 Minimum Requirements

- **Android Version**: 7.0 (API 24)
- **Target SDK**: 34
- **Kotlin**: 1.9.20
- **Gradle**: 8.2
- **JDK**: 8+

## 🔐 Security Features

✅ PKCE Flow (OAuth 2.0 extension)
✅ AES256-GCM encryption for tokens
✅ Automatic token refresh
✅ Secure token revocation
✅ No plaintext storage
✅ Backup exclusion for sensitive data

## 📦 Key Dependencies

```kotlin
// OAuth0
com.auth0.android:auth0:2.10.2

// Security
androidx.security:security-crypto:1.1.0-alpha06

// Coroutines
org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3

// Material Design
com.google.android.material:material:1.11.0
```

## 🎨 UI Validation Rules

### Email
- Not empty
- Valid email format (contains @, proper domain)

### Password
- Not empty
- Minimum 8 characters

### Confirm Password (Sign Up)
- Not empty
- Matches password

## 🔄 App Flow Summary

```
Launch → Already Logged In?
        ├─ Yes → Home Screen
        └─ No  → Login Screen
                ├─ Login → Home
                └─ Sign Up → Home
                        
Home Screen → Logout → Login Screen
```

## 📖 Additional Documentation

- `ANDROID_README.md` - Full setup guide
- `IMPLEMENTATION_GUIDE.md` - Detailed implementation
- Code comments - Inline documentation

## 🚀 Quick Start

```bash
# 1. Clone repository
git clone <repo-url>

# 2. Open in Android Studio
File → Open → Select project directory

# 3. Update OAuth0 credentials
Edit app/src/main/res/values/strings.xml

# 4. Sync Gradle
Click "Sync Now" when prompted

# 5. Run
Click Run button or Shift+F10
```

## 💡 Pro Tips

1. **Always test with real OAuth0 credentials** before production
2. **Use debug builds** during development
3. **Check logs** in Logcat for detailed errors
4. **Test on multiple Android versions** if possible
5. **Keep OAuth0 SDK updated** for security patches
6. **Use ProGuard** for release builds
7. **Store credentials securely** (never commit to git)
8. **Test network scenarios** (slow, offline, timeout)

---

**Need Help?** 
- Check Auth0 documentation: https://auth0.com/docs
- Review code comments in source files
- Verify configuration in strings.xml
