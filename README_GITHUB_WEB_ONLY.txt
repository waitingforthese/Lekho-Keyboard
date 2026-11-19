LEKHO KEYBOARD V1.6.4.1 - BUILD FIX

This is a one-file Kotlin syntax correction for the V1.6.4 source.
The failed GitHub Actions build reported:
HistorySequence.kt:6:46 Unsupported escape sequence.

What to do in GitHub website only:
1. Open your current V1.6.4 branch (the commit that ran workflow #23).
2. Open:
   app/src/main/java/com/example/lekho/keyboard/HistorySequence.kt
3. Click the pencil Edit button.
4. Replace the entire file content with the HistorySequence.kt in this ZIP.
5. Commit the change to the same V1.6.4 branch.
6. Open GitHub Actions and confirm testDebugUnitTest and assembleDebug both succeed.

Do not change app/build.gradle.kts or other files for this specific error.
This fix preserves versionCode 10, versionName 1.6.4 and the 2000 ms typing-pause save setting.
