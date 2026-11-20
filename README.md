# लिखो Keyboard — Version 1.6.8

Kotlin Android Input Method Editor (IME), with a Gboard-inspired layout. This is an independent keyboard app and is not Google's Gboard software or source code.

## V1.6.8 changes
- External Send History handling: when a non-sensitive editor becomes empty after a previously observed non-empty state, only the final word is recorded after a short confirmation. The full message is never persisted.
- SQLite-based local typing history with a maximum of 20,000 word entries. Duplicate words remain separate and ordered; the oldest entry is removed when the cap is exceeded.
- Existing SharedPreferences history is migrated once to SQLite, preserving up to the newest 20,000 entries.
- History screen displays 100 entries per page with a Load Older control instead of rendering the whole history at once.
- History database reads/writes run on a serial background executor.
- English dictionary asset loading and Marathi/English suggestion calculations run off the keyboard UI thread. A 256-entry recent-query cache is used and stale keypress calculations are discarded.
- Keeps the 2-second pause-save behavior, PIN app lock, Marathi transliteration, 10 suggestion cells, automatic space after suggestion selection, logo and keyboard UI.

## Existing keyboard functions
- QWERTY keyboard, number row and symbols layout.
- Shift, double-tap Caps Lock, Unicode grapheme-aware Backspace and long-press repeat.
- Editor action / Enter handling.
- Marathi + English modes and offline dictionaries.
- Suggestions, emoji shortcuts, clipboard paste and recent history toolbar.
- 4-digit PIN lock for the main app; the keyboard remains available in other apps without the app PIN.
- Password and `IME_FLAG_NO_PERSONALIZED_LEARNING` fields are excluded from history.

## Build configuration
- Application ID: `com.example.lekho.keyboard` (unchanged)
- versionCode: 13
- versionName: 1.6.8
- JDK 17, Gradle 8.9, AGP 8.7.3, Kotlin 2.0.21
- Compile SDK 35 / Target SDK 35 / Minimum SDK 26

GitHub Actions runs unit tests and assembles the debug APK. Install only after the workflow succeeds.

## Important limitations
- Android does not send a universal callback to third-party IMEs when another app's Send button is tapped. External Send is detected by a best-effort editor-clear heuristic. Manual clearing an entire draft can look the same; apps that do not report selection updates consistently may need separate testing.
- The 20,000-word cap applies to local History only. There is no cloud sync.
- Suggestion calculation is moved off the UI thread, but actual typing latency and device compatibility still require testing on physical phones across Android versions and manufacturers.
- Voice typing, swipe typing, full predictive learning and a full Gboard-equivalent language model are not included.
- This is a debug development project, not a release-signed Play Store package.

## GitHub website workflow
1. Create branch `v1.6.8-send-history-db-fast-suggestions` from the current `main`.
2. Replace the files listed in `README_V1_6_8_GITHUB_WEB_ONLY.txt` and create `HistoryStore.kt` at the specified path.
3. Open a Pull Request targeting `main`.
4. Merge only after GitHub Actions unit tests and `assembleDebug` both succeed.

## Privacy
History stays on this device. The transient editor snapshot is used only to infer an external Send clear; it is not stored. Password/private fields are excluded. Clipboard contents are accessed only when the user taps Paste.
