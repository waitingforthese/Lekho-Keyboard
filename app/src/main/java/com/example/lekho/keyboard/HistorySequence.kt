package com.example.lekho.keyboard

/** Pure helpers for the bounded local word history and external-send capture. */
internal object HistorySequence {
    const val MAX_HISTORY_WORDS = 20_000

    /** Keeps the newest entries, preserving order and repeated words. */
    fun append(
        history: List<String>,
        word: String,
        maxItems: Int = MAX_HISTORY_WORDS
    ): List<String> {
        val normalized = word.trim().replace("\n", " ")
        val limit = maxItems.coerceAtLeast(0)
        if (limit == 0) return emptyList()
        if (normalized.isBlank()) return history.takeLast(limit)
        return (history + normalized).takeLast(limit)
    }

    /** Extracts only the final word from a temporarily captured editor snapshot. */
    fun extractLastWord(snapshot: String): String? {
        val text = snapshot.trimEnd()
        if (text.isEmpty() || text.last().isDigit()) return null
        var end = text.lastIndex
        while (end >= 0 && !isWordCharacter(text[end])) end--
        if (end < 0) return null
        var start = end
        while (start >= 0 && isWordCharacter(text[start])) start--
        return text.substring(start + 1, end + 1).takeIf { it.length >= 2 }
    }

    private fun isWordCharacter(char: Char): Boolean {
        val category = Character.getType(char)
        return char.isLetter() ||
            category == Character.NON_SPACING_MARK.toInt() ||
            category == Character.COMBINING_SPACING_MARK.toInt() ||
            category == Character.ENCLOSING_MARK.toInt() ||
            category == Character.FORMAT.toInt() ||
            char == '\'' || char == '’'
    }
}
