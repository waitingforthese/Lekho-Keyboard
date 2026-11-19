# Lekho Keyboard — Version 1 Core

An Android Input Method Editor (IME) starter built with Kotlin. The product name is provisional and can be changed before release.

## Version 1 scope

- Android `InputMethodService` with a QWERTY letter layout.
- Number and symbol layouts, Shift, Backspace, Space and Enter.
- English mode with a small local English prefix list.
- Marathi phonetic mode using an offline starter dictionary: type Roman letters, then tap a Devanagari suggestion or press Space for a supported exact word.
- Three-item suggestion row.
- Clipboard paste shortcut, read only after the user taps the clipboard shortcut.
- Optional local typing history, up to 30 committed words, with in-app enable/disable and clear controls.
- Password and editors that set `IME_FLAG_NO_PERSONALIZED_LEARNING` are excluded from suggestions, clipboard shortcut and local history.
- No `INTERNET` permission and no server-side text processing.

## Current limitations

The Marathi word list is intentionally small. This build is **not** a full rule-based Marathi transliteration engine and does not yet provide a complete Marathi dictionary, ML next-word prediction, swipe typing, voice typing, emoji panel, themes or personal-dictionary management. Backspace deletes a Unicode grapheme cluster where Android's ICU boundary iterator supports it, but must still be tested on multiple keyboards and editor types.

This is a development build. Do not use it for sensitive production workflows until it has been tested on real devices and its privacy behavior has been reviewed.

## Build requirements

- Android Studio with Android SDK Platform 35 installed.
- JDK 17.
- Gradle 8.9.

Open this folder in Android Studio, sync Gradle, and run the `app` configuration. Command line build:

```bash
gradle --no-daemon testDebugUnitTest assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`.

## GitHub Actions

The workflow at `.github/workflows/android.yml` runs unit tests and builds a debug APK on pushes and pull requests to `main`, and on manual workflow dispatch. From a successful run, download the `lekho-keyboard-v1-debug-apk` artifact. A separate test report artifact is uploaded even when tests fail.

## Install and enable

1. Install the debug APK on an Android device.
2. Open Lekho Keyboard and choose **Enable keyboard**.
3. In Android's On-screen keyboard settings, enable Lekho Keyboard.
4. Return to the app and choose **Choose Lekho Keyboard**.
5. Test in a basic text field, then test Marathi and English mode, number/symbol rows, backspace and enter.

Settings labels vary by Android manufacturer.

## Privacy notes

- No network permission is requested.
- Clipboard content is read only after a direct tap on the clipboard shortcut; password and no-personalized-learning fields block this shortcut.
- Typing history is stored in app-private preferences, can be disabled or cleared, and is limited to 30 items.
- Password and `IME_FLAG_NO_PERSONALIZED_LEARNING` fields are excluded from dictionary suggestions and history.

## Package identity

Current development application ID: `com.example.lekho.keyboard`. Choose the final brand and reverse-domain application ID before the first Play Store release; changing the application ID later creates a separate Android app identity.
