LIKHO KEYBOARD V1.6.7 — UNLIMITED LOCAL TYPING HISTORY
=====================================================

V1.6.6 is preserved as the locked reference baseline. This update removes the application's
fixed 100-word limit while keeping the current Likho branding, logo, PIN lock, Send handling,
Marathi/English suggestions, automatic space, 2-second pause save, and keyboard UI.

VERSION:
- versionCode: 12
- versionName: 1.6.7
- applicationId remains: com.example.lekho.keyboard

WHAT CHANGES:
- No fixed maximum number of saved history words.
- Existing words are not discarded when new words are saved.
- Duplicate words remain separate history entries, in their original order.
- History screen displays all stored entries rather than only the newest 100.
- Existing local history stays in the app's SharedPreferences string; no migration or reset is required.

FILES TO REPLACE:
1. app/build.gradle.kts
2. app/src/main/java/com/example/lekho/keyboard/MainActivity.kt
3. app/src/main/java/com/example/lekho/keyboard/HistorySequence.kt
4. app/src/test/java/com/example/lekho/keyboard/HistorySequenceTest.kt

MANUAL EDIT TO EXISTING SERVICE:
Apply SERVICE_UNLIMITED_HISTORY_PATCH.txt to:
app/src/main/java/com/example/lekho/keyboard/LekhoInputMethodService.kt
This small targeted edit avoids replacing the large keyboard service file and protects its other
features. In particular, keep TYPING_PAUSE_SAVE_DELAY_MS = 2000L.

GITHUB WEBSITE STEPS (NO TERMINAL / GIT BASH / GITHUB DESKTOP):
1. Create branch: v1.6.7-unlimited-history from the current V1.6.6 main branch.
2. Replace the four files listed above using GitHub's web editor.
3. Apply the three service edits from SERVICE_UNLIMITED_HISTORY_PATCH.txt using the GitHub pencil editor.
4. Search the service file for MAX_HISTORY_ITEMS. There must be no remaining reference.
5. Commit to the branch and open a Pull Request targeting main.
6. Merge only after GitHub Actions unit tests and assembleDebug both succeed.
7. Install the APK only after a successful workflow.

IMPORTANT LIMITATION:
The software no longer has a fixed 100-word count cap. Data is stored locally in the existing
SharedPreferences value, so physical device storage and memory still apply. A very large history
may take longer to load. Password fields and editors that disable personalized learning remain
excluded. There is no cloud sync.
