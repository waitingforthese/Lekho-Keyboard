LEKHO KEYBOARD V1.6.6 — BRANDING + SEND HISTORY UPDATE
======================================================

This ZIP is a GitHub-website-only update package. No Git Bash, Terminal, GitHub Desktop,
or local clone is required.

What changes:
- App display name: "लिखो" (launcher label, app activity, keyboard picker/service label).
- Recreated no-text fountain-pen logo based on the user's uploaded reference (watermark-free,
  new generated artwork), used for launcher icon and in-app PIN/main screen.
- Send history fix: detects an app's external Send button clearing the current composing text
  and records the active word to typing history without putting it back into the empty editor.
- Keeps PIN lock, Marathi/English modes, 2-second pause save, suggestions, and keyboard UI.
- Version code 11 / version name 1.6.6.

IMPORTANT SERVICE PATCH:
The current service file is large. To avoid accidentally replacing unrelated keyboard behavior,
the service correction is included as SERVICE_SEND_HISTORY_FIX.txt with six precise GitHub
web-editor actions. Apply each action carefully to the existing file:
app/src/main/java/com/example/lekho/keyboard/LekhoInputMethodService.kt

FILES TO REPLACE:
1. app/src/main/java/com/example/lekho/keyboard/MainActivity.kt
2. app/src/main/AndroidManifest.xml
3. app/build.gradle.kts

FILES TO CREATE:
4. app/src/main/res/values/strings.xml
5. app/src/main/res/drawable-nodpi/logo_likho.png
6. app/src/main/res/drawable-nodpi/ic_launcher_foreground.png
7. app/src/main/res/drawable/ic_launcher_background.xml
8. app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml
9. app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml
10. Launcher PNGs under mipmap-mdpi, mipmap-hdpi, mipmap-xhdpi, mipmap-xxhdpi,
    mipmap-xxxhdpi (both ic_launcher.png and ic_launcher_round.png).

DO NOT replace PinSecurity.kt or HistorySequence.kt; keep the versions already in main.
Keep the same applicationId/package name: com.example.lekho.keyboard. This is an app update,
not a new app. Existing installed app data, PIN and typing history should remain intact.

GitHub website workflow:
1. Create branch v1.6.6-likho-branding-send-history from the current main branch.
2. Replace the 3 existing files listed above using GitHub's pencil editor and full file contents.
3. Create the new resource files with Add file > Create new file, or upload individual PNG assets
   via Add file > Upload files, preserving the exact folder paths listed above.
4. Apply SERVICE_SEND_HISTORY_FIX.txt changes to LekhoInputMethodService.kt in GitHub's web editor.
5. Commit changes to the new branch and create a Pull Request targeting main.
6. Merge only after GitHub Actions reports a successful unit-test + assembleDebug build.
7. Do not install any APK from a failed or incomplete workflow run.

App icon is black fountain pen artwork with transparent foreground on white adaptive/legacy background.
It contains no text, as requested.
