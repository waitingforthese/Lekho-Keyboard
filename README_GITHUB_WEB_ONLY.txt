LEKHO KEYBOARD V1.6.3 — ENGLISH SUGGESTIONS + PAUSE-AWARE HISTORY

REFERENCE BASELINE
- V1.6.2 is now the saved reference baseline. Its ZIP snapshot must not be overwritten.
- V1.6.3 is a follow-on update created from the exact V1.6.2 GitHub Web Files package.
- Preserve the existing Gboard-inspired UI, Marathi phonetic engine, 10-candidate strip, automatic space after candidate selection, Shift/Caps, Enter, hold-Backspace, clipboard/history screens and history order.

V1.6.3 CHANGES
1. English suggestions:
   - Adds an offline English dictionary with 61,875 lowercase alphabetic entries.
   - Keeps common application and everyday words at the front of the suggestion ranking.
   - Uses prefix search from the first letters through the complete token, with up to ten results.
   - Adds conservative one-edit / adjacent-letter-transposition correction when there are few prefix completions (for example, "helo" can suggest "hello" and "infrom" can suggest "inform").
   - Preserves typed capitalization for suggestions.
2. Save a word after a typing pause:
   - After 900 ms without a new key, the currently typed token is saved to local Typing History without inserting a space or committing the text into the editor.
   - If typing continues after a pause, the later version of the token can be saved after the next pause as a separate history entry. This follows the existing duplicate-preserving sequence behavior.
   - Pressing Space, Enter, punctuation or selecting a suggestion still completes the word normally. A token already saved during the pause is not saved a second time when it is completed or the same suggestion is selected.
   - Password/PIN fields and editors that set IME_FLAG_NO_PERSONALIZED_LEARNING remain excluded from history and suggestions.
3. App version is 1.6.3 (versionCode 9).

ENGLISH WORDLIST NOTICE
- The bundled offline English vocabulary is filtered from the SCOWL / en_US Hunspell dictionary available in the development environment.
- Only lowercase ASCII alphabetic words of 2–18 characters are included in the asset. The application's curated list separately includes short common English tokens such as "a" and "i".
- The corresponding SCOWL / dictionary copyright and permission notice is included at THIRD_PARTY_NOTICES/SCOWL_ENGLISH_DICTIONARY.txt. Keep that notice with the source and distribution.

FILES TO REPLACE / ADD
1. Replace app/src/main/java/com/example/lekho/keyboard/LekhoInputMethodService.kt
2. Replace app/src/main/java/com/example/lekho/keyboard/WordEngine.kt
3. Replace app/src/test/java/com/example/lekho/keyboard/WordEngineTest.kt
4. Replace app/build.gradle.kts
5. Create app/src/main/assets/english_words.txt
6. Create THIRD_PARTY_NOTICES/SCOWL_ENGLISH_DICTIONARY.txt

GITHUB WEBSITE ONLY
A. Keep the separate V1.6.2 locked baseline ZIP as the reference snapshot.
B. In the GitHub website, create branch: v1.6.3-english-suggestions-idle-history
C. Replace the four existing files above and create the two new files above using the full file contents from this ZIP.
D. Commit the changes to that branch.
E. Open a Pull Request with base=main and compare=v1.6.3-english-suggestions-idle-history.
F. Wait for GitHub Actions to pass unit tests and assembleDebug before merging or installing the APK.

TESTING CHECKLIST AFTER APK INSTALL
- English: type h -> he -> hel -> hell -> hello; suggestions should refresh at each character.
- English: type i -> in -> inf -> info -> infor -> inform; suggestions should continue through the whole token.
- English typo check: type helo and infrom; check for hello and inform among the suggestions.
- Marathi: type rahul and namaskar; Marathi suggestions and transliteration should still work.
- Type a word and pause for about one second without pressing Space; check that the word appears in Typing History while remaining in the text field.
- Continue typing after a pause; check that the newer token version is saved after the next pause.
- Press Space after an already saved pause token; it should not create a second history entry for the same completion.
- Select a suggestion; it should still insert exactly one trailing space.
- Verify history does not save in password/PIN/private editors.

LIMITATIONS
- Gboard's private language model/package internals are proprietary and are not a public API. This version does not copy or call Gboard code; it uses Android's public InputConnection interface and its own offline English/Marathi vocabulary.
- The English dictionary provides broader prefix matches and basic one-edit correction, not a neural next-word prediction model. Its ranking will not be identical to Gboard.
- The full Android Gradle build must be verified by GitHub Actions after the files are committed. The offline WordEngine has been independently compiled and smoke-tested in this environment; the Android IME service has not yet been built here.
