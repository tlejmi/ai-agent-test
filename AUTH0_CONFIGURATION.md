# Auth0 SDK Configuration Guide

This guide explains how to properly configure the Auth0 SDK for this Android application.

## Prerequisites

- Auth0 account (sign up at https://auth0.com/)
- Android Studio
- Android device or emulator (API 24+)

## Step-by-Step Configuration

### 1. Create Auth0 Application

1. **Sign up/Login to Auth0**
   - Go to https://auth0.com/
   - Create a new account or log in

2. **Create a Native Application**
   - Navigate to Applications → Applications
   - Click "Create Application"
   - Name: "Android OAuth App" (or your preferred name)
   - Choose "Native" as the application type
   - Click "Create"

3. **Note Your Credentials**
   - You'll see your **Domain** (e.g., `dev-abc123.us.auth0.com`)
   - You'll see your **Client ID** (e.g., `AbCdEf123456789XyZ`)
   - Keep these for the next steps

### 2. Configure Callback URLs

In your Auth0 Application settings:

1. **Allowed Callback URLs**
   ```
   demo://YOUR_DOMAIN/android/com.example.oauth0authapp/callback
   ```
   Replace `YOUR_DOMAIN` with your actual Auth0 domain (e.g., `dev-abc123.us.auth0.com`)
   
   Example:
   ```
   demo://dev-abc123.us.auth0.com/android/com.example.oauth0authapp/callback
   ```

2. **Allowed Logout URLs**
   ```
   demo://YOUR_DOMAIN/android/com.example.oauth0authapp/callback
   ```
   Use the same format as callback URLs

3. **Allowed Web Origins** (optional)
   ```
   https://YOUR_DOMAIN
   ```

4. **Save Changes**

### 3. Enable Database Connection

1. **Navigate to Authentication → Database**
   - If you don't have a database connection, create one
   - Default name is "Username-Password-Authentication"

2. **Enable Sign Ups** (if you want users to register)
   - Go to the database connection settings
   - Enable "Disable Sign Ups" toggle if you only want invited users
   - Or leave it enabled for public registration

3. **Configure Password Policy** (recommended)
   - Minimum length: 8 characters
   - Require: lowercase, uppercase, numbers
   - Optional: special characters

### 4. Update Android Application

**Update `app/src/main/res/values/strings.xml`:**

```xml
<!-- Replace with your actual Auth0 credentials -->
<string name="com_auth0_domain">YOUR_DOMAIN</string>
<string name="com_auth0_client_id">YOUR_CLIENT_ID</string>
```

**Example:**
```xml
<string name="com_auth0_domain">dev-abc123.us.auth0.com</string>
<string name="com_auth0_client_id">AbCdEf1234567890XyZaBcDeF</string>
```

### 5. Verify Configuration

The app uses these configurations in:

**AndroidManifest.xml:**
```xml
<data
    android:host="@string/com_auth0_domain"
    android:pathPrefix="/android/com.example.oauth0authapp/callback"
    android:scheme="demo" />
```

**Auth0Manager.kt:**
```kotlin
private val account = Auth0(
    context.getString(R.string.com_auth0_client_id),
    context.getString(R.string.com_auth0_domain)
)
```

## Auth0 SDK Implementation Details

### Dependencies Used

```kotlin
implementation("com.auth0.android:auth0:2.10.2")
```

This is the official Auth0 SDK for Android, providing:
- OAuth 2.0 with PKCE flow
- Secure token management
- Browser-based authentication
- Token refresh capabilities

### PKCE Flow Implementation

The app uses PKCE (Proof Key for Code Exchange) for enhanced security:

```kotlin
WebAuthProvider.login(account)
    .withScheme("demo")
    .withScope("openid profile email offline_access")
    .withAudience("https://${account.getDomain()}/api/v2/")
    .start(context, callback)
```

**PKCE Benefits:**
- Prevents authorization code interception attacks
- Recommended for mobile applications
- Automatic code challenge/verifier generation

### Token Storage

Tokens are stored securely using `EncryptedSharedPreferences`:

```kotlin
implementation("androidx.security:security-crypto:1.1.0-alpha06")
```

**Security Features:**
- AES256-GCM encryption for values
- AES256-SIV encryption for keys
- No plaintext storage
- Automatic key management with MasterKey

### Scopes Requested

```kotlin
"openid profile email offline_access"
```

- `openid`: Basic OpenID Connect
- `profile`: User profile information (name, picture)
- `email`: User email address
- `offline_access`: Refresh token for token renewal

## Testing Configuration

For unit tests, mock values are provided in:
```
app/src/test/res/values/strings.xml
```

```xml
<string name="com_auth0_domain">test-domain.auth0.com</string>
<string name="com_auth0_client_id">test_client_id_123456</string>
```

Tests use Robolectric to mock Android components without real Auth0 API calls.

## Common Issues and Solutions

### Issue 1: "Callback URL mismatch"

**Cause:** The redirect URI doesn't match Auth0 configuration

**Solution:**
1. Check your `strings.xml` domain matches Auth0
2. Verify callback URL in Auth0 Dashboard:
   ```
   demo://YOUR_ACTUAL_DOMAIN/android/com.example.oauth0authapp/callback
   ```
3. Ensure scheme is "demo" in both places

### Issue 2: "Invalid Client"

**Cause:** Wrong Client ID or Domain

**Solution:**
1. Double-check Client ID in Auth0 Dashboard
2. Ensure domain includes `.auth0.com` or your custom domain
3. Verify no extra spaces in strings.xml

### Issue 3: "Network error"

**Cause:** Internet permission not granted or network unavailable

**Solution:**
1. Check `AndroidManifest.xml` has:
   ```xml
   <uses-permission android:name="android.permission.INTERNET" />
   ```
2. Verify device has internet connection
3. Check Auth0 service status

### Issue 4: "Sign up disabled"

**Cause:** Database connection doesn't allow sign ups

**Solution:**
1. Go to Auth0 Dashboard → Authentication → Database
2. Select your database connection
3. Ensure "Disable Sign Ups" is OFF
4. Save changes

### Issue 5: "User already exists"

**Cause:** Email is already registered

**Solution:**
- User should use login instead
- Or delete the user from Auth0 Dashboard to re-register
- Or implement "Forgot Password" flow

## Security Best Practices

### 1. Never Commit Credentials

Add to `.gitignore` if using a separate config file:
```
auth0-config.properties
local.properties
```

### 2. Use Environment-Specific Configurations

For production, consider:
- Build variants (debug/release)
- Build config fields
- Environment variables in CI/CD

Example:
```kotlin
buildTypes {
    debug {
        buildConfigField("String", "AUTH0_DOMAIN", "\"dev-domain.auth0.com\"")
    }
    release {
        buildConfigField("String", "AUTH0_DOMAIN", "\"prod-domain.auth0.com\"")
    }
}
```

### 3. Implement Token Refresh

The app automatically refreshes tokens:
```kotlin
suspend fun refreshToken(): Credentials
```

Called when tokens expire to maintain user session.

### 4. Revoke Tokens on Logout

The app revokes refresh tokens on logout:
```kotlin
authenticationClient.revoke(refreshToken).start()
```

This invalidates the token on Auth0 servers.

### 5. Use HTTPS Only

Auth0 enforces HTTPS, but ensure:
- Custom domains use valid SSL certificates
- No HTTP traffic in production

## Advanced Configuration

### Custom Domains

If you have a custom domain:
```xml
<string name="com_auth0_domain">auth.yourdomain.com</string>
```

Update callback URLs accordingly.

### Social Connections

To add Google, Facebook, etc.:
1. Go to Auth0 Dashboard → Authentication → Social
2. Enable desired providers
3. Configure OAuth credentials
4. No code changes needed - Auth0 handles it

### Multi-Factor Authentication

Enable MFA in Auth0 Dashboard:
1. Security → Multi-factor Auth
2. Enable SMS, Email, or Authenticator
3. Configure policies
4. Works automatically with existing code

### Custom Claims

Add custom user data:
1. Create Auth0 Rules or Actions
2. Add claims to ID token
3. Access via `UserProfile` in app

## Testing Checklist

Before deploying:

- [ ] Auth0 credentials configured in `strings.xml`
- [ ] Callback URLs match in Auth0 Dashboard
- [ ] Database connection enabled
- [ ] Internet permission in manifest
- [ ] Test login flow
- [ ] Test sign up flow
- [ ] Test logout flow
- [ ] Test token refresh
- [ ] Test error scenarios

## Resources

- **Auth0 Android SDK**: https://github.com/auth0/Auth0.Android
- **Auth0 Documentation**: https://auth0.com/docs/quickstart/native/android
- **PKCE Spec**: https://tools.ietf.org/html/rfc7636
- **Auth0 Dashboard**: https://manage.auth0.com/

## Support

For Auth0-specific issues:
- Community Forum: https://community.auth0.com/
- Support: https://support.auth0.com/
- GitHub Issues: https://github.com/auth0/Auth0.Android/issues

For app-specific issues:
- Check `TESTING_GUIDE.md` for test configuration
- Review `IMPLEMENTATION_GUIDE.md` for architecture details
- Check `BUILD_STATUS.md` for build requirements

---

**Status:** Configuration guide complete
**SDK Version:** Auth0 Android 2.10.2
**Security:** PKCE flow with encrypted storage
**Support:** Full OAuth 2.0 compliance
