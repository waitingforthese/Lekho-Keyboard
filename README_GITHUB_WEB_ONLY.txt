LEKHO KEYBOARD V1.4 — GBOARD UI ALIGNMENT (GITHUB WEBSITE ONLY)

This package combines the V1.3 performance/history improvements with the Gboard-style visual alignment requested from the two supplied screenshots.
No Git Bash, terminal, GitHub Desktop, or local clone is required.

FILES IN THIS PACKAGE
1) app/src/main/java/com/example/lekho/keyboard/LekhoInputMethodService.kt  (REPLACE)
2) app/build.gradle.kts (REPLACE)
3) app/src/main/java/com/example/lekho/keyboard/HistorySequence.kt (CREATE if absent; otherwise compare/keep the same)
4) app/src/test/java/com/example/lekho/keyboard/HistorySequenceTest.kt (CREATE if absent; otherwise compare/keep the same)

GITHUB WEBSITE STEPS
A. Open https://github.com/waitingforthese/Lekho-Keyboard
B. Create a branch from main named: v1.4-gboard-ui
C. Switch to that branch.
D. Replace the two existing files at the exact paths listed above by opening the file > pencil (Edit) > paste the full file contents from this package > Commit changes.
E. Create the two HistorySequence files only if they do not already exist. Use Add file > Create new file and enter the exact path.
F. Open a Pull Request with base=main and compare=v1.4-gboard-ui.
G. Check GitHub Actions. Merge only after the workflow succeeds.

VISUAL UPDATES
- Match the reference Gboard's soft lavender panel and pale blue modifier keys.
- Use white, slightly raised keys with softer rounded corners and subtle borders/shadows.
- Increase key-row height to match the reference keyboard's overall height and key proportions.
- Align key font size, weight, colour, space-bar label, and globe-key accent colour more closely.
- Keep the same QWERTY row structure and the 5-item top toolbar (tools, settings, language, clipboard, history).

FUNCTIONALITY RETAINED
- Enter/editor action handling.
- Shift and Caps Lock.
- Long-press repeating backspace.
- Marathi/English mode and transliteration.
- Ordered typing history (including repeated words), max 100 entries.
- Efficient in-place suggestion updates.

VERSION
versionCode = 5
versionName = 0.5.0

IMPORTANT
The supplied screenshots allow a close visual match, but exact pixel-for-pixel identity with Gboard cannot be guaranteed across different phones, Android versions, display densities, font rendering, and system insets. This source has not yet been compiled by Android Gradle. Verify the GitHub Actions workflow is green before installing its APK.
