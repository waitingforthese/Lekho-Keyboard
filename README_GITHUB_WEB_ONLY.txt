Lekho Keyboard V1.6.5 - App PIN Lock
===================================

This update adds a PIN gate to the Lekho Keyboard launcher/settings activity only.
The InputMethodService is not locked, so Lekho Keyboard remains available while typing
inside other apps without asking for the app PIN.

What is included
----------------
1. MainActivity.kt (replace existing file)
   - First launch: create and confirm a 4-digit numeric PIN.
   - Later launches/resumes after the app leaves the foreground: require the PIN.
   - Change App PIN: verifies the current PIN before allowing a new PIN.
   - Numeric PIN pad is drawn inside the app; it does not rely on the selected IME.
   - Five failed attempts cause a 30-second pause.
2. PinSecurity.kt (new file)
   - Stores a salted PBKDF2-HMAC-SHA256 PIN hash, never the raw PIN.
   - Stores data in app-private SharedPreferences.
3. HistorySequence.kt (replace existing file)
   - Includes the Kotlin newline sanitizing compile fix: replace("\n", " ").
4. app/build.gradle.kts (replace existing file)
   - versionCode = 11
   - versionName = "1.6.5"

GitHub website-only steps
-------------------------
1. Open the Lekho-Keyboard repository in your browser.
2. Create a new branch named: v1.6.5-app-pin-lock
   Use the current main branch as the starting point.
3. Extract this ZIP on your computer.
4. Replace these existing files by opening each file in GitHub, choosing Edit (pencil),
   selecting all old content, and pasting the complete replacement file content:
   - app/src/main/java/com/example/lekho/keyboard/MainActivity.kt
   - app/src/main/java/com/example/lekho/keyboard/HistorySequence.kt
   - app/build.gradle.kts
5. Create this new file using Add file > Create new file:
   - app/src/main/java/com/example/lekho/keyboard/PinSecurity.kt
6. Commit the changes to v1.6.5-app-pin-lock.
7. Open a Pull Request to main and check GitHub Actions. Merge only if tests and APK build pass.

Important behavior and recovery
-------------------------------
- PIN applies only when opening the Lekho Keyboard app/settings screen.
- PIN does NOT lock the keyboard service when it is used in WhatsApp, Messages, or other apps.
- A 4-digit PIN is required. Five incorrect attempts trigger a 30-second cooldown.
- The app has android:allowBackup="false" in the existing manifest.
- If the PIN is forgotten, clearing app data or reinstalling resets the PIN, but also removes
  local typing history and app settings. There is intentionally no unauthenticated PIN bypass.

Build status
------------
The ZIP contents and paths should be verified before use. Android Gradle Build has not been
run for this update in this environment. GitHub Actions must pass before installing the APK.
