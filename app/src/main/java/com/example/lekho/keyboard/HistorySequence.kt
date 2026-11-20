package com.example.lekho.keyboard

/** Ordered, duplicate-preserving local word history with no application-level item cap. */
internal object HistorySequence {
    fun append(history: List<String>, word: String): List<String> {
        val normalized = word.trim().replace("\n", " ")
        if (normalized.isBlank()) return history
        return history + normalized
    }
}
