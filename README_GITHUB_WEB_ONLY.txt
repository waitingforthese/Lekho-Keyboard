LEKHO KEYBOARD V1.6 — TEN MARATHI / ENGLISH SUGGESTIONS (GITHUB WEBSITE ONLY)

This package builds on the V1.5 Marathi transliteration code. It adds ten candidate suggestions and keeps the existing Gboard-inspired visual keyboard unchanged.

WHAT CHANGED
- Marathi typing can show up to ten distinct Devanagari candidates for a Roman token.
- For words not found in the Marathi dictionary, the phonetic engine tries alternate sound readings (for example, short/long vowels and common consonant alternatives) to produce different Devanagari candidates.
- Marathi dictionary results and generated phonetic candidates are combined, deduplicated, and ranked with exact dictionary spelling first.
- English mode now provides up to ten matching English autocomplete suggestions from its offline English word list.
- The top suggestion strip contains ten reusable suggestion cells in a horizontal scroller. Swipe left/right on the suggestion strip to view all available candidates; ten options cannot all fit at readable font size on a typical phone width at once.
- Tapping a candidate replaces the current Roman token with that candidate.
- The keyboard layout, key shapes, font, colours, modifier keys, toolbar icons, spacebar and Enter behaviour are not intentionally changed.

LANGUAGE MODE
- Marathi mode: the space bar reads “मराठी”. Type Roman letters to see Devanagari candidates.
- English mode: tap the globe/language key until the space bar reads “English”. Type an English prefix to see up to ten English matches.

EXAMPLES
- Marathi: namaskar, rahul, jagtap, patil, mahiti, kavita.
- English: type a prefix such as “a” to see up to ten matching English words.
- For Marathi candidates, swipe the top suggestion strip horizontally to reach options 4–10.

FILES TO REPLACE / CREATE
1) Replace: app/src/main/java/com/example/lekho/keyboard/LekhoInputMethodService.kt
2) Replace: app/src/main/java/com/example/lekho/keyboard/WordEngine.kt
3) Replace: app/build.gradle.kts
4) Create or replace: app/src/test/java/com/example/lekho/keyboard/WordEngineTest.kt

GITHUB WEBSITE STEPS (NO TERMINAL / GIT BASH / GITHUB DESKTOP)
A. Open https://github.com/waitingforthese/Lekho-Keyboard
B. Create a new branch from main named: v1.6-ten-suggestions
C. In the branch, open each file listed above. For existing files, click Edit, select the entire old file, and paste the complete matching file from this ZIP. For WordEngineTest.kt, use Add file > Create new file if it does not already exist; otherwise edit it.
D. Commit each file to the v1.6-ten-suggestions branch.
E. Create a Pull Request with base=main and compare=v1.6-ten-suggestions.
F. Check GitHub Actions. Merge only after the Android build and unit tests succeed.

VERSION
versionCode = 7
versionName = "0.7.0"

ACCURACY NOTE
This is an offline phonetic transliteration engine. Marathi candidates are generated from explicit Roman sound alternatives and a local dictionary; this is not the same as Gboard's cloud / proprietary language model. The engine can provide different plausible spellings, but it cannot guarantee that all ten are linguistically correct for every token. The English suggestion list is also offline and returns fewer than ten when fewer matching words exist in the list.

VALIDATION
The Kotlin WordEngine was compiled independently and checked for ten distinct Devanagari candidates on an unknown Roman token, ten distinct English candidates for a prefix, and key transliteration cases. The full Android Gradle build has not been run here. Wait for a successful GitHub Actions run before installing the APK.
