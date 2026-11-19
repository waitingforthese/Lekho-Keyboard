# Lekho Keyboard V1 — Device Test Plan

This plan must be executed on a real Android device before calling the build release-ready. No device test result is implied by this file.

## Environment record

Record the phone model, Android version, screen resolution, keyboard height, and whether battery saver is enabled.

## Functional checks

- Enable Lekho Keyboard in Android Settings and select it with the IME picker.
- Type in Notes, a browser search field, and a messaging app.
- Verify QWERTY keys, Shift (including visible uppercase key labels), Backspace, Space, comma, period, and Enter/editor actions.
- Verify number and symbol layouts and return to letters.
- In Marathi mode, test exact entries such as `namaskar`, `majha`, `mi`, `mala`, `tumhi`, and `dhanyavad`; test prefix suggestions such as `nam`.
- Verify tapping a Devanagari suggestion replaces the Roman token exactly once.
- In English mode, verify English suggestions such as `hel` → `hello` and that Marathi candidates are not shown.
- Verify unknown Roman words are not silently converted.
- Verify Backspace on Latin text, Devanagari matras, conjuncts, and emoji.
- Verify portrait and landscape layouts on the target device.

## Privacy and history checks

- With history enabled, commit more than 30 words and verify only the latest 30 remain available through the history shortcut.
- Disable history, type additional words, reopen the keyboard, and verify old history is not offered.
- Clear history and verify the history shortcut no longer inserts old content.
- Tap the clipboard shortcut in a normal field and verify it pastes the current clipboard item.
- In password fields and editors that set `IME_FLAG_NO_PERSONALIZED_LEARNING`, verify suggestions, history and clipboard shortcut are not exposed and Space does not transliterate.
- Verify no network permission is present in the merged application manifest.

## Performance checks

Use Android Studio Profiler / System Trace on a physical device, not an emulator alone.

- Open and dismiss the keyboard 20 times; record median and worst open-to-interactive time.
- Type 200 characters at a steady pace; check for missed or duplicated characters.
- Type 50 known Marathi words; check that suggestions do not visibly stall key response.
- Test repeated Backspace on Devanagari and emoji strings.
- Observe memory after 10 minutes of normal use and repeated app switching.
- Repeat with battery saver enabled and on a lower-end device where available.

Suggested initial acceptance targets (to be validated, not claimed): key tap to committed character should feel immediate with no dropped keys; suggestion refresh should not block typing; no crash or ANR during the test set.

## Build checks

GitHub Actions should show both `testDebugUnitTest` and `assembleDebug` successful. Install the resulting APK on the intended phone and repeat the functional and privacy checks above before beginning Version 2.
