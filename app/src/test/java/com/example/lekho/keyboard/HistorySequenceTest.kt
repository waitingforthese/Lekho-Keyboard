package com.example.lekho.keyboard

import org.junit.Assert.assertEquals
import org.junit.Test

class HistorySequenceTest {
    @Test
    fun preservesRepeatedWordsInTypingOrder() {
        val result = HistorySequence.append(listOf("नमस्कार", "काम"), "नमस्कार")
        assertEquals(listOf("नमस्कार", "काम", "नमस्कार"), result)
    }

    @Test
    fun keepsAllEntriesBeyondOneHundredWithoutDroppingOldWords() {
        val previous = (1..250).map { "word$it" }
        val result = HistorySequence.append(previous, "word251")
        assertEquals(251, result.size)
        assertEquals("word1", result.first())
        assertEquals("word251", result.last())
    }

    @Test
    fun ignoresBlankHistoryEntries() {
        assertEquals(listOf("काम"), HistorySequence.append(listOf("काम"), "   "))
    }

    @Test
    fun doesNotSplitNewLinesIntoSeparateHistoryRows() {
        assertEquals(listOf("पहिला दुसरा"), HistorySequence.append(emptyList(), " पहिला\nदुसरा "))
    }
}
