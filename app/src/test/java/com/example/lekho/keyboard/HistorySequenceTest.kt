package com.example.lekho.keyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HistorySequenceTest {
    @Test
    fun preservesRepeatedWordsInTypingOrder() {
        val result = HistorySequence.append(listOf("नमस्कार", "काम"), "नमस्कार")
        assertEquals(listOf("नमस्कार", "काम", "नमस्कार"), result)
    }

    @Test
    fun keepsAtMostTwentyThousandMostRecentWords() {
        val previous = (1..20_000).map { "word$it" }
        val result = HistorySequence.append(previous, "word20001")
        assertEquals(20_000, result.size)
        assertEquals("word2", result.first())
        assertEquals("word20001", result.last())
    }

    @Test
    fun ignoresBlankHistoryEntries() {
        assertEquals(listOf("काम"), HistorySequence.append(listOf("काम"), "   "))
    }

    @Test
    fun convertsLineBreaksToOneHistoryRow() {
        assertEquals(listOf("पहिला दुसरा"), HistorySequence.append(emptyList(), " पहिला\nदुसरा "))
    }

    @Test
    fun extractsLastWordFromSentMessageSnapshot() {
        assertEquals("नमस्कार", HistorySequence.extractLastWord("आजचे काम पूर्ण झाले नमस्कार!  "))
    }

    @Test
    fun returnsNullWhenSnapshotHasNoWord() {
        assertNull(HistorySequence.extractLastWord("   ... 123"))
    }
}
