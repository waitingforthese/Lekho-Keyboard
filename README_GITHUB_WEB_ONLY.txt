LEKHO KEYBOARD V1.6.2 — CONTINUOUS SUGGESTIONS + AUTO SPACE

BASELINE
- V1.6.1 remains the locked reference baseline and is not modified.
- V1.6.2 is a follow-on update based on the exact V1.6.1 build-fix source.
- Existing Gboard-inspired keyboard layout, colors, fonts, key sizes, toolbar, Marathi phonetic engine, history limit/order, Shift/Caps, Enter and hold-Backspace behavior are retained.

CHANGES
1. Continuous candidates while typing:
   - Roman letters are entered as one active InputConnection composing span.
   - Each key updates the complete composing token and refreshes the candidate row.
   - InputMethodService.onUpdateSelection also refreshes the candidate row when an editor reports selection/composition changes.
   - This keeps suggestions updating after the first and second letters through the end of the word, rather than relying only on a stale token snapshot.
2. Candidate selection inserts the selected word followed by one space automatically in both Marathi and English modes.
3. Backspace edits the composing token one grapheme at a time while composition is active.
4. Expanded the curated offline English suggestion vocabulary for common longer prefixes.
5. Updated Android versionName to 1.6.2 and versionCode to 8.

GOOGLE GBOARD PACKAGE CHECK
- Google Play identifies the Gboard application with package id: com.google.android.inputmethod.latin.
- Gboard's suggestion / transliteration engine is proprietary; its internal classes and private package interfaces are not a public Android API and must not be treated as a callable library.
- This implementation therefore uses Android's public InputConnection composing-text interface, the same standard contract available to any Android IME: setComposingText() while a word is being edited, then commitText() to finalize it.

FILES TO REPLACE / ADD
1. Replace app/src/main/java/com/example/lekho/keyboard/LekhoInputMethodService.kt
2. Replace app/src/main/java/com/example/lekho/keyboard/WordEngine.kt
3. Replace app/src/test/java/com/example/lekho/keyboard/WordEngineTest.kt
4. Replace app/build.gradle.kts

GITHUB WEBSITE ONLY
A. Keep V1.6.1 as the saved baseline. Do not overwrite or delete its ZIP/source snapshot.
B. In the GitHub website, create branch: v1.6.2-continuous-suggestions
C. Replace the four existing files above using the full contents from this ZIP.
D. Commit the changes to that branch.
E. Open a Pull Request with base=main and compare=v1.6.2-continuous-suggestions.
F. Wait for GitHub Actions to pass both unit tests and assembleDebug before merging or installing.

TESTING CHECKLIST AFTER APK INSTALL
- Marathi mode: type r -> ra -> rah -> rahu -> rahul; candidates should refresh at each keystroke.
- Marathi mode: type ma -> mah -> maha -> maharashtra; candidates should continue updating through the full token.
- English mode: type h -> he -> hel -> hell -> hello; candidate list should update at each step.
- English mode: type i -> in -> inf -> info -> infor -> inform; suggestions should continue beyond the first two letters.
- Tap any Marathi or English candidate: the word should be inserted with exactly one trailing space; the next word can be typed immediately without pressing Space again.
- Test Backspace while a Roman word is still being composed, then press Space and Enter.

ACCURACY / LIMITATIONS
- The app uses an offline phonetic Marathi dictionary and sound-variant generator. Ten candidates are shown when the engine has ten distinct results; very short or unambiguous prefixes can naturally have fewer meaningful alternatives.
- English suggestions are offline and use a curated vocabulary. Uncommon prefixes may return fewer alternatives than Gboard.
- The package is not a clone of Gboard's private language model. No Gboard APK code or private interface is copied or invoked.
- Full Android Gradle build must be verified by GitHub Actions after these changes are committed. The Kotlin engine was independently compiled and its offline dictionary loading/prefix outputs were tested locally; the Android service itself has not yet been built in this environment.
