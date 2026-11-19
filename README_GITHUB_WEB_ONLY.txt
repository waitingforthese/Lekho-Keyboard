LEKHO KEYBOARD V1.6.4 — ENGLISH SUGGESTIONS + 2-SECOND PAUSE HISTORY

REFERENCE BASELINE
- V1.6.3 is the locked, saved reference baseline. Keep its original ZIP snapshot unchanged.
- V1.6.4 is a follow-on update created from the exact V1.6.3 GitHub Web Files package. Only the typing-pause save delay and app version have changed.
- Preserve the existing Gboard-inspired UI, Marathi phonetic engine, 10-candidate strip, automatic space after candidate selection, Shift/Caps, Enter, hold-Backspace, clipboard/history screens and history order.

V1.6.4 CHANGES
1. English suggestions (retained unchanged from V1.6.3):
   - Uses the offline English dictionary with 61,875 lowercase alphabetic entries.
   - Keeps common application and everyday words at the front of the suggestion ranking.
   - Uses prefix search from the first letters through the complete token, with up to ten results.
   - Adds conservative one-edit / adjacent-letter-transposition correction when there are few prefix completions (for example, "helo" can suggest "hello" and "infrom" can suggest "inform").
   - Preserves typed capitalization for suggestions.
2. Save a word after a typing pause:
   - After 2000 ms (2 seconds) without a new key, the currently typed token is saved to local Typing History without inserting a space or committing the text into the editor.
   - If typing continues after a pause, the later version of the token can be saved after the next pause as a separate history entry. This follows the existing duplicate-preserving sequence behavior.
   - Pressing Space, Enter, punctuation or selecting a suggestion still completes the word normally. A token already saved during the pause is not saved a second time when it is completed or the same suggestion is selected.
   - Password/PIN fields and editors that set IME_FLAG_NO_PERSONALIZED_LEARNING remain excluded from history and suggestions.
3. App version is 1.6.4 (versionCode 10).

ENGLISH WORDLIST NOTICE
- The bundled offline English vocabulary is filtered from the SCOWL / en_US Hunspell dictionary available in the development environment.
- Only lowercase ASCII alphabetic words of 2–18 characters are included in the asset. The application's curated list separately includes short common English tokens such as "a" and "i".
- The corresponding SCOWL / dictionary copyright and permission notice is included at THIRD_PARTY_NOTICES/SCOWL_ENGLISH_DICTIONARY.txt. Keep that notice with the source and distribution.

FILES TO REPLACE / KEEP
1. Replace app/src/main/java/com/example/lekho/keyboard/LekhoInputMethodService.kt
2. Replace app/build.gradle.kts
3. Keep app/src/main/java/com/example/lekho/keyboard/WordEngine.kt unchanged from V1.6.3
4. Keep app/src/test/java/com/example/lekho/keyboard/WordEngineTest.kt unchanged from V1.6.3
5. Keep app/src/main/assets/english_words.txt unchanged from V1.6.3
6. Keep THIRD_PARTY_NOTICES/SCOWL_ENGLISH_DICTIONARY.txt unchanged from V1.6.3
7. Keep app/src/main/java/com/example/lekho/keyboard/HistorySequence.kt unchanged from V1.6.3

Only create any unchanged file above from this ZIP if it is missing on the target branch.
GITHUB WEBSITE ONLY
A. Keep the separate V1.6.3 locked baseline ZIP as the reference snapshot.
B. In the GitHub website, create branch: v1.6.4-two-second-history-pause
C. Replace LekhoInputMethodService.kt and app/build.gradle.kts using the full file contents from this ZIP. Keep WordEngine.kt, WordEngineTest.kt, the English dictionary asset, the dictionary notice, and HistorySequence.kt unchanged from V1.6.3. Create any of those unchanged files only if missing on the target branch.
D. Commit the changes to that branch.
E. Open a Pull Request with base=main and compare=v1.6.4-two-second-history-pause.
F. Wait for GitHub Actions to pass unit tests and assembleDebug before merging or installing the APK.

TESTING CHECKLIST AFTER APK INSTALL
- English: type h -> he -> hel -> hell -> hello; suggestions should refresh at each character.
- English: type i -> in -> inf -> info -> infor -> inform; suggestions should continue through the whole token.
- English typo check: type helo and infrom; check for hello and inform among the suggestions.
- Marathi: type rahul and namaskar; Marathi suggestions and transliteration should still work.
- Type a word and pause for about two seconds without pressing Space; check that the word appears in Typing History while remaining in the text field.
- Continue typing after a pause; check that the newer token version is saved after the next pause.
- Press Space after an already saved pause token; it should not create a second history entry for the same completion.
- Select a suggestion; it should still insert exactly one trailing space.
- Verify history does not save in password/PIN/private editors.

LIMITATIONS
- Gboard's private language model/package internals are proprietary and are not a public API. This version does not copy or call Gboard code; it uses Android's public InputConnection interface and its own offline English/Marathi vocabulary.
- The English dictionary provides broader prefix matches and basic one-edit correction, not a neural next-word prediction model. Its ranking will not be identical to Gboard.
- The full Android Gradle build must be verified by GitHub Actions after the files are committed. The offline WordEngine has been independently compiled and smoke-tested in this environment; the Android IME service has not yet been built here.


V1.6.4 CHANGE SCOPE
- The only runtime behavior change is TYPING_PAUSE_SAVE_DELAY_MS: 900 ms to 2000 ms.
- App version changed to 1.6.4 / versionCode 10.
- English dictionary, suggestion ranking, Marathi transliteration, candidate selection auto-space, keyboard UI, history capacity/order, and sensitive-field exclusions are unchanged from V1.6.3.
- Existing HistorySequence.kt from the V1.6.3 repository baseline is unchanged and must remain in the project. If it is missing on the target branch, recreate it using the exact contents from the V1.6.3 source package baseline/repository.
