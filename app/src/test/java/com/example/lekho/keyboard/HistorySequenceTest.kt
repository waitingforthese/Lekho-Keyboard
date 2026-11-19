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
    fun keepsOnlyTheMostRecentEntriesWhenLimitIsReached() {
        val result = HistorySequence.append(listOf("एक", "दोन", "तीन"), "चार", maxItems = 3)
        assertEquals(listOf("दोन", "तीन", "चार"), result)
    }

    @Test
    fun ignoresBlankHistoryEntries() {
        assertEquals(listOf("काम"), HistorySequence.append(listOf("काम"), "   "))
    }
}
