# Lekho Keyboard — Version 1.2

Kotlin Android Input Method Editor (IME), using the user's screenshots as the UI reference. This is a Gboard-inspired layout, not Google's Gboard software or branding.

## V1.2 changes
- Gboard-inspired light lavender/pale grey keyboard panel, white rounded keys, subtle borders, active Shift tint and larger Space key.
- Always-visible numeric row in letter mode.
- Full QWERTY rows with centered home row.
- Double-tap Shift enables Caps Lock; single Shift uppercases the next letter; key labels visibly switch to uppercase while active.
- Backspace tap deletes one Unicode grapheme cluster; holding it repeats deletion until release.
- Enter dispatches an editor action for Send/Search/Done fields, sends Enter key events as fallback, and commits a newline for multiline editors.
- Toolbar controls: More/History, Settings, Marathi-English switch, Clipboard paste and recent typing history.
- Emoji shortcut row in the suggestion strip.
- Local history stores up to 100 committed words only, can be turned off or cleared in the app, and skips password/private-learning fields.
- System navigation bar is tinted to match the keyboard and a bottom spacer keeps the final key row above navigation controls.
- Adds `yashsvi` / `yashasvi` / `yashaswi` → `यशस्वी` starter dictionary entries.

## Build
- JDK 17
- Gradle 8.9
- Android Gradle Plugin 8.7.3
- Kotlin 2.0.21
- Android SDK Platform 35

```bash
gradle --no-daemon testDebugUnitTest assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`.

## GitHub Actions
Push this project to the `main` branch. The workflow at `.github/workflows/main.yml` runs unit tests and builds a debug APK artifact. You may also run it manually using Actions → Android APK Build → Run workflow.

## Install and test
1. Install the debug APK.
2. Open Lekho Keyboard → Enable keyboard → choose Lekho Keyboard.
3. Test in a multiline note editor (Enter should add a new line).
4. Test in a messaging field (Enter should invoke that editor's Send action if it exposes one).
5. Tap Shift once and verify next letter uppercase; double-tap Shift and verify Caps Lock keeps all letter labels uppercase.
6. Hold Backspace and verify repeated deletion; include Marathi vowel marks and emoji in tests.
7. Type words, press Space or Enter, open Settings → Recent typed words and verify saved entries. Password fields must not be recorded.
8. Check the final key row stays above the device navigation buttons/gesture area on the target phone.

## Important limitations
- The Marathi engine is still dictionary-backed, not a complete general-purpose phonetic transliteration engine. Unlisted spellings may not convert.
- Voice typing and actual cloud translation are not included. The top toolbar focuses on implemented local actions.
- Enter behavior varies by app/editor. Both editor-action and key-event/newline routes are implemented, but device/app testing is still required.
- Navigation inset behavior differs by manufacturer and gesture/button navigation mode; test on the target phone before release.
- This is a development build, not a release-signed Play Store APK.

## Privacy
No INTERNET permission is declared. Only committed words are saved locally (not full messages). History is capped at 100 words, can be disabled or cleared, and is suppressed for password/private-learning fields. Clipboard text is read only after the user taps Paste.

## Application ID
`com.example.lekho.keyboard` — choose the final reverse-domain application ID before first public release.
