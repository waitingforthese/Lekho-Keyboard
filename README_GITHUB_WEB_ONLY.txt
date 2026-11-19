LEKHO KEYBOARD V1.5 — MARATHI PHONETIC TYPING (GITHUB WEBSITE ONLY)

This release fixes Marathi typing while retaining the existing V1.4 Gboard-style keyboard appearance. No Git Bash, terminal, GitHub Desktop, or local clone is required.

WHAT WAS FIXED
- The old Marathi engine only converted words that existed in a small fixed dictionary. Unknown words stayed in English.
- A larger Marathi dictionary now covers common words, phrases, places, and names, including Rahul, Arjunrao, Jagtap, and Patil.
- A phonetic Roman-to-Devanagari fallback now converts words that are not in the dictionary.
- Marathi candidates are shown while typing in the suggestion strip.
- Space, comma, full stop, and Enter now finalize the current Marathi word before adding the separator/action.
- Password/private fields remain excluded from typing history and transliteration suggestions.
- The visual keyboard layout, font sizing, key colours, key shape, and toolbar are not intentionally changed.

HOW TO TYPE MARATHI
1. Select Marathi mode using the globe/language key. The space bar should show “मराठी”.
2. Type with English letters. Example: namaskar, rahul, arjunrao, jagtap, patil, kavita.
3. A Marathi candidate should appear in the suggestion strip. Tap it, or press Space to commit the Marathi form.
4. Comma, full stop, and Enter also convert the current word before continuing.

FILES TO REPLACE / CREATE
1) Replace: app/src/main/java/com/example/lekho/keyboard/LekhoInputMethodService.kt
2) Replace: app/src/main/java/com/example/lekho/keyboard/WordEngine.kt
3) Replace: app/build.gradle.kts
4) Create: app/src/test/java/com/example/lekho/keyboard/WordEngineTest.kt

GITHUB WEBSITE STEPS
A. Open https://github.com/waitingforthese/Lekho-Keyboard
B. Create a new branch from main named: v1.5-marathi-transliteration
C. Open each existing file above, click the pencil (Edit), replace the complete contents with the matching file from this ZIP, and commit to the new branch.
D. For WordEngineTest.kt, use Add file > Create new file and enter the exact path above. Paste the file contents and commit to the branch.
E. Open a Pull Request with base=main and compare=v1.5-marathi-transliteration.
F. Check GitHub Actions. Merge only after the build and unit tests succeed.

VERSION
versionCode = 6
versionName = "0.6.0"

IMPORTANT LIMITATION
This is an offline, dictionary-plus-phonetic transliteration engine, not Google's proprietary Gboard language model. Common spellings and named terms have dictionary entries; other Roman spellings are converted by phonetic rules and may need a different spelling or a suggestion choice in some cases. The source has been Kotlin-compiled and the transliteration cases in the accompanying local test runner passed, but the complete Android Gradle build still needs to pass in GitHub Actions before installing the APK.
