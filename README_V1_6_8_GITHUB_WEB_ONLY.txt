LIKHO KEYBOARD V1.6.8 — GITHUB WEBSITE UPDATE

Included changes
1. External Send History capture: when another app clears a non-sensitive text editor after Send, the keyboard waits 180 ms, verifies that the editor is still empty, and saves only the final word from the previous short-lived editor snapshot. The full message is never written to history.
2. Typing history uses SQLite and keeps the newest 20,000 word entries. Duplicate words remain separate and ordered.
3. Existing SharedPreferences history is migrated once to SQLite on first read/write, preserving up to the newest 20,000 existing entries.
4. History screen loads 100 words per page with a Load Older button; it does not create thousands of TextViews at once.
5. History writes and reads run through a serial background executor, not the keyboard UI thread.
6. English dictionary asset loading and Marathi/English suggestion calculations run in a background executor. Suggestion results are cached (256 recent token/mode keys); stale keypress calculations are discarded.
7. Existing 2-second pause saving, PIN lock, word transliteration, suggestions, keyboard UI, and app logo are preserved.

GitHub Website steps only (no terminal/Git Bash/GitHub Desktop):
1. Create a new branch from the current main branch: v1.6.8-send-history-db-fast-suggestions
2. Replace these files with the complete files from this ZIP:
   - app/src/main/java/com/example/lekho/keyboard/LekhoInputMethodService.kt
   - app/src/main/java/com/example/lekho/keyboard/MainActivity.kt
   - app/src/main/java/com/example/lekho/keyboard/HistorySequence.kt
   - app/build.gradle.kts
3. Create this new file:
   - app/src/main/java/com/example/lekho/keyboard/HistoryStore.kt
4. Replace the unit test file:
   - app/src/test/java/com/example/lekho/keyboard/HistorySequenceTest.kt
5. Create a Pull Request from the new branch to main. Wait for GitHub Actions to pass before merging or installing the APK.

Notes:
- V1.6.8 keeps up to 20,000 words, not unlimited. When entry 20,001 is added, the oldest entry is removed.
- External Send detection is a best-effort Android IME heuristic: Android does not notify third-party keyboards directly that an app's Send button was pressed. It detects a previously non-empty, non-sensitive editor becoming empty and saves only the trailing word after a short confirmation delay. Manual clearing of an entire message can look identical to Send at the IME API level, and some apps may not report selection updates consistently; test in WhatsApp and other target apps.
- If history was previously stored in SharedPreferences, it is migrated once. Existing local entries are preserved up to the newest 20,000.
- SQLite removes the previous one-large-SharedPreferences-string bottleneck. The history UI displays 100 entries per page.
- The complete Android Gradle build must be confirmed by GitHub Actions. This package is source-ready but not claimed as a successful APK build yet.
