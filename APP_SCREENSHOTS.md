# App Screenshots & UI Guide

This document describes the UI screens in the Android Auth0 Authentication App.

## App Screens Overview

The application consists of three main screens following Material Design 3 guidelines.

---

## 1. Login Screen (MainActivity)

**File:** `app/src/main/kotlin/com/example/oauth0authapp/MainActivity.kt`  
**Layout:** `app/src/main/res/layout/activity_main.xml`

### Screen Elements:

```
┌─────────────────────────────────┐
│                                 │
│     Auth0 Auth App              │
│                                 │
│  ┌───────────────────────────┐  │
│  │ Email                     │  │
│  │ [example@email.com      ] │  │
│  └───────────────────────────┘  │
│                                 │
│  ┌───────────────────────────┐  │
│  │ Password                  │  │
│  │ [••••••••••••          👁] │  │
│  └───────────────────────────┘  │
│                                 │
│     ┌─────────────────────┐     │
│     │      LOGIN          │     │
│     └─────────────────────┘     │
│                                 │
│   Don't have an account?        │
│          Sign up                │
│                                 │
└─────────────────────────────────┘
```

### Features:
- ✅ Email input with validation
- ✅ Password input with show/hide toggle
- ✅ Login button
- ✅ Navigation to Sign Up screen
- ✅ Material Design text fields
- ✅ Loading indicator during authentication
- ✅ Error messages displayed inline

### Validation:
- Email: Must be valid format (checked with Android Patterns)
- Password: Must not be empty
- Real-time error feedback

### Flow:
1. User enters email and password
2. Tap "Login" button
3. App validates inputs
4. Launches browser for Auth0 authentication (PKCE flow)
5. Browser redirects back to app
6. Token stored securely
7. Navigate to Home screen

---

## 2. Sign Up Screen (SignUpActivity)

**File:** `app/src/main/kotlin/com/example/oauth0authapp/SignUpActivity.kt`  
**Layout:** `app/src/main/res/layout/activity_sign_up.xml`

### Screen Elements:

```
┌─────────────────────────────────┐
│                                 │
│     Sign Up                     │
│                                 │
│  ┌───────────────────────────┐  │
│  │ Email                     │  │
│  │ [example@email.com      ] │  │
│  └───────────────────────────┘  │
│                                 │
│  ┌───────────────────────────┐  │
│  │ Password                  │  │
│  │ [••••••••••••          👁] │  │
│  └───────────────────────────┘  │
│                                 │
│  ┌───────────────────────────┐  │
│  │ Confirm Password          │  │
│  │ [••••••••••••          👁] │  │
│  └───────────────────────────┘  │
│                                 │
│     ┌─────────────────────┐     │
│     │     SIGN UP         │     │
│     └─────────────────────┘     │
│                                 │
│   Already have an account?      │
│           Login                 │
│                                 │
└─────────────────────────────────┘
```

### Features:
- ✅ Email input with validation
- ✅ Password input with show/hide toggle
- ✅ Confirm password input
- ✅ Sign up button
- ✅ Navigation back to Login screen
- ✅ Material Design text fields
- ✅ Loading indicator during registration
- ✅ Error messages displayed inline

### Validation:
- Email: Must be valid format
- Password: Minimum 8 characters
- Confirm Password: Must match password
- All fields required
- Real-time validation feedback

### Flow:
1. User enters email, password, and confirmation
2. Tap "Sign Up" button
3. App validates all inputs
4. Creates user via Auth0 Management API
5. On success: Navigate to Login screen
6. On error: Display error message with retry option

---

## 3. Home Screen (HomeActivity)

**File:** `app/src/main/kotlin/com/example/oauth0authapp/HomeActivity.kt`  
**Layout:** `app/src/main/res/layout/activity_home.xml`

### Screen Elements:

```
┌─────────────────────────────────┐
│                                 │
│     Home                        │
│                                 │
│  ┌─────────────────────────┐    │
│  │                         │    │
│  │   User Profile          │    │
│  │                         │    │
│  │   Name:                 │    │
│  │   John Doe              │    │
│  │                         │    │
│  │   Email:                │    │
│  │   john.doe@example.com  │    │
│  │                         │    │
│  │   User ID:              │    │
│  │   auth0|123456789       │    │
│  │                         │    │
│  └─────────────────────────┘    │
│                                 │
│     ┌─────────────────────┐     │
│     │      LOGOUT         │     │
│     └─────────────────────┘     │
│                                 │
└─────────────────────────────────┘
```

### Features:
- ✅ Material Design card displaying profile
- ✅ User name display
- ✅ User email display
- ✅ User ID display
- ✅ Logout button
- ✅ Loading indicator while fetching profile
- ✅ Back button disabled (use logout to exit)

### Data Display:
- Name: From ID token claims
- Email: From ID token or user info
- User ID: From Auth0 sub claim

### Flow:
1. Screen loads automatically after login
2. Fetches user profile from Auth0
3. Displays profile information
4. User taps "Logout"
5. App revokes refresh token
6. Clears web session
7. Clears encrypted local storage
8. Navigate back to Login screen

---

## UI Components Used

### Material Design 3 Components:
- **TextInputLayout**: For email and password fields
- **TextInputEditText**: For input fields
- **MaterialButton**: For all buttons
- **MaterialCardView**: For profile display
- **ProgressBar**: For loading indicators
- **ConstraintLayout**: For responsive layouts

### Colors & Theme:
- Primary color: Material Purple
- Secondary color: Material Blue
- Error color: Material Red
- Background: Material Surface
- Text: High contrast for readability

---

## Loading States

### Login Screen Loading:
```
┌─────────────────────────────────┐
│     [Circular Progress]         │
│     Logging in...               │
└─────────────────────────────────┘
```

### Sign Up Screen Loading:
```
┌─────────────────────────────────┐
│     [Circular Progress]         │
│     Creating account...         │
└─────────────────────────────────┘
```

### Home Screen Loading:
```
┌─────────────────────────────────┐
│     [Circular Progress]         │
│     Loading profile...          │
└─────────────────────────────────┘
```

---

## Error States

### Invalid Email:
```
┌───────────────────────────┐
│ Email                     │
│ [invalid-email          ] │
│ ⚠ Please enter a valid   │
│   email address           │
└───────────────────────────┘
```

### Password Too Short:
```
┌───────────────────────────┐
│ Password                  │
│ [••••••                👁] │
│ ⚠ Password must be at     │
│   least 8 characters      │
└───────────────────────────┘
```

### Passwords Don't Match:
```
┌───────────────────────────┐
│ Confirm Password          │
│ [••••••••             👁] │
│ ⚠ Passwords do not match  │
└───────────────────────────┘
```

### Network Error:
```
┌─────────────────────────────────┐
│  ⚠ Network error. Please check  │
│     your connection.             │
└─────────────────────────────────┘
```

---

## Authentication Flow Diagram

```
┌──────────┐
│  Login   │
│  Screen  │
└────┬─────┘
     │ User taps Login
     ▼
┌────────────────┐
│ Validate Input │
└────┬───────────┘
     │ Valid
     ▼
┌──────────────────┐
│ Open Browser     │
│ Auth0 Login Page │
└────┬─────────────┘
     │ User authenticates
     ▼
┌──────────────────┐
│ Browser Redirect │
│ Back to App      │
└────┬─────────────┘
     │ Authorization code
     ▼
┌──────────────────┐
│ Exchange for     │
│ Tokens (PKCE)    │
└────┬─────────────┘
     │ Tokens received
     ▼
┌──────────────────┐
│ Store Encrypted  │
│ Tokens           │
└────┬─────────────┘
     │
     ▼
┌──────────┐
│  Home    │
│  Screen  │
└──────────┘
```

---

## Sign Up Flow Diagram

```
┌──────────┐
│ Sign Up  │
│  Screen  │
└────┬─────┘
     │ User fills form
     ▼
┌────────────────┐
│ Validate Input │
│ - Email format │
│ - Password len │
│ - Match check  │
└────┬───────────┘
     │ All valid
     ▼
┌──────────────────┐
│ Call Auth0       │
│ Management API   │
└────┬─────────────┘
     │ Success
     ▼
┌──────────────────┐
│ Show Success     │
│ Message          │
└────┬─────────────┘
     │ Auto-navigate
     ▼
┌──────────┐
│  Login   │
│  Screen  │
└──────────┘
```

---

## Testing the UI

### Manual Testing Steps:

1. **Install APK**
   - Download from workflow artifacts
   - Transfer to Android device
   - Install (enable unknown sources if needed)

2. **Test Sign Up**
   - Open app → Tap "Sign up"
   - Enter: test@example.com
   - Password: Test1234!
   - Confirm: Test1234!
   - Tap "Sign Up"
   - Should see success and navigate to login

3. **Test Login**
   - Enter registered email
   - Enter password
   - Tap "Login"
   - Browser should open with Auth0 login page
   - Enter credentials
   - Should redirect back and show Home screen

4. **Test Profile Display**
   - Verify name is displayed
   - Verify email is displayed
   - Verify user ID is displayed

5. **Test Logout**
   - Tap "Logout"
   - Should clear session
   - Navigate back to Login screen

### Automated UI Tests (Future)

Espresso tests can be added for:
- Button clicks
- Text input
- Navigation flow
- Error message display
- Loading state display

---

## Device Compatibility

### Minimum Requirements:
- Android 7.0 (API 24) or higher
- Internet connection
- Browser app installed (for Auth0 flow)

### Tested On:
- Android 14 (API 34)
- Android 13 (API 33)
- Android 12 (API 31)
- Android 10 (API 29)

### Screen Sizes:
- ✅ Phone (4.7" - 7")
- ✅ Tablet (7" - 10")
- ✅ Responsive layouts
- ✅ Portrait and landscape

---

## Getting Screenshots

### Method 1: Android Studio
1. Open project in Android Studio
2. Run on emulator
3. Navigate through screens
4. Use Android Studio screenshot tool
5. Screenshots saved to Pictures folder

### Method 2: Physical Device
1. Install APK on device
2. Navigate through screens
3. Use device screenshot (Power + Volume Down)
4. Transfer screenshots via USB/Cloud

### Method 3: ADB
```bash
# Connect device
adb devices

# Take screenshot
adb shell screencap -p /sdcard/screenshot.png

# Pull to computer
adb pull /sdcard/screenshot.png
```

---

## Accessibility

### Features Implemented:
- ✅ Content descriptions for all interactive elements
- ✅ High contrast text
- ✅ Minimum touch target size (48dp)
- ✅ Clear error messages
- ✅ Keyboard navigation support
- ✅ Screen reader compatible

### Future Improvements:
- [ ] Voice input for text fields
- [ ] Biometric authentication
- [ ] Dark mode support
- [ ] Font size adjustments

---

## Summary

The app provides a clean, Material Design 3 interface for Auth0 authentication with:
- ✅ Professional UI following Android guidelines
- ✅ Clear user feedback with loading and error states
- ✅ Secure authentication flow with browser-based OAuth
- ✅ Responsive layouts for all screen sizes
- ✅ Comprehensive validation and error handling

**To see the app in action:**
1. Download APK from workflow artifacts
2. Install on Android device (API 24+)
3. Configure Auth0 Dashboard with callback URLs
4. Test sign up, login, and logout flows

For Auth0 configuration, see `AUTH0_SETUP_CHECKLIST.md`.
