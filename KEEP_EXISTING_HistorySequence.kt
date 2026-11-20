package com.example.lekho.keyboard

/** Pure helper for an ordered, duplicate-preserving local word history. */
internal object HistorySequence {
    fun append(history: List<String>, word: String, maxItems: Int = 100): List<String> {
        val normalized = word.trim().replace("\n", " ")
        val limit = maxItems.coerceAtLeast(0)
        if (limit == 0) return emptyList()
        if (normalized.isBlank()) return history.takeLast(limit)
        return (history + normalized).takeLast(limit)
    }
}
