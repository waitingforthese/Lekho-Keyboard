LEKHO KEYBOARD V1.3 - PERFORMANCE + ORDERED HISTORY

This patch targets the current main branch source version with app versionCode 3 / versionName 0.3.0.

Changes:
1. Preserve repeated word occurrences in history in exact typing order.
2. Save history once when a word is committed (Space, punctuation or Enter), not per keypress.
3. Update the 3 existing suggestion views while typing rather than removing/recreating the full top strip on every character.
4. Avoid rebuilding the entire key layout on every Space/Enter; redraw keys only when one-shot Shift state actually resets.
5. Add unit tests for repeated entries, ordering, max history size, and blank values.
6. Bump app version to 0.4.0 / versionCode 4.

Apply from the repository root:
    git apply LekhoKeyboard_V1_3_Performance_History.patch

Then commit and push. GitHub Actions should run the Android unit tests and debug APK build.

History stores committed words only, locally, up to the existing 100-item limit. Password / private fields remain excluded. This patch does not claim a guaranteed numeric typing-speed gain; it removes avoidable view reconstruction and retains lightweight suggestion calculation.
