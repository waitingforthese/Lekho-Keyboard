package com.example.lekho.keyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WordEngineTest {
    @Test fun transliteratesKnownMarathiWord() = assertEquals("नमस्कार", WordEngine.transliterate("namaskar"))
    @Test fun transliterationIsCaseInsensitive() = assertEquals("माझा", WordEngine.transliterate("MAJHA"))
    @Test fun transliteratesYashsviExample() = assertEquals("यशस्वी", WordEngine.transliterate("yashsvi"))
    @Test fun returnsMarathiPrefixSuggestions() = assertTrue(WordEngine.suggestions("nam", true).contains("नमस्कार"))
    @Test fun returnsYashasviSuggestion() = assertTrue(WordEngine.suggestions("yash", true).contains("यशस्वी"))
    @Test fun englishModeDoesNotOfferMarathiWords() {
        val suggestions = WordEngine.suggestions("hel", false)
        assertTrue(suggestions.contains("hello"))
        assertFalse(suggestions.contains("नमस्कार"))
    }
    @Test fun blankPrefixHasNoSuggestions() = assertTrue(WordEngine.suggestions("  ", true).isEmpty())
}
