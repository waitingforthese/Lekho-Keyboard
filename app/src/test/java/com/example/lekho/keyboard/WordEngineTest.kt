package com.example.lekho.keyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WordEngineTest {
    @Test fun commonMarathiWordsUseCorrectDictionarySpellings() {
        assertEquals("नमस्कार", WordEngine.transliterate("namaskar"))
        assertEquals("मराठी", WordEngine.transliterate("MARATHI"))
        assertEquals("राहुल", WordEngine.transliterate("Rahul"))
        assertEquals("दादा", WordEngine.transliterate("dada"))
        assertEquals("अर्जुनराव", WordEngine.transliterate("arjunrao"))
        assertEquals("जगताप", WordEngine.transliterate("jagtap"))
        assertEquals("पाटील", WordEngine.transliterate("patil"))
        assertEquals("माझा", WordEngine.transliterate("majha"))
        assertEquals("माहिती", WordEngine.transliterate("mahiti"))
    }

    @Test fun unknownWordsUsePhoneticFallback() {
        assertEquals("कविता", WordEngine.transliterate("kavita"))
        assertEquals("सुख", WordEngine.transliterate("sukh"))
        assertEquals("ज्ञान", WordEngine.transliterate("dnyan"))
        assertEquals("क्षमा", WordEngine.transliterate("kshama"))
        assertEquals("म्हणजे", WordEngine.transliterate("mhanje"))
    }

    @Test fun unknownMarathiRomanWordsProduceTenDistinctCandidatesWhenAmbiguous() {
        val options = WordEngine.suggestions("pratiksha", true)
        assertEquals(10, options.size)
        assertEquals(10, options.distinct().size)
        assertTrue(options.all { candidate -> candidate.all { ch -> ch in '\u0900'..'\u097F' } })
    }

    @Test fun MarathiSuggestionsIncludeDictionaryMatchesAndGeneratedCandidates() {
        assertTrue(WordEngine.suggestions("rah", true).contains("राहुल"))
        assertTrue(WordEngine.suggestions("kavi", true).contains("कविता"))
        assertTrue(WordEngine.suggestions("nam", true).contains("नमस्कार"))
    }

    @Test fun EnglishModeReturnsUpToTenEnglishSuggestions() {
        val options = WordEngine.suggestions("a", false)
        assertEquals(10, options.size)
        assertTrue(options.all { it.all { ch -> ch.isLetter() } })
    }


    @Test fun suggestionsKeepUpdatingForEveryLetterUntilWordEnds() {
        val word = "maharashtra"
        for (length in 1..word.length) {
            val prefix = word.take(length)
            assertTrue("No Marathi candidates for prefix: $prefix", WordEngine.suggestions(prefix, true).isNotEmpty())
        }
        val englishWord = "information"
        for (length in 1..englishWord.length) {
            val prefix = englishWord.take(length)
            assertTrue("No English candidates for prefix: $prefix", WordEngine.suggestions(prefix, false).isNotEmpty())
        }
    }


    @Test fun englishSuggestionsContinueAfterLongerPrefixes() {
        for (prefix in listOf("i", "in", "inf", "info", "infor", "inform")) {
            assertTrue("No English suggestions for: $prefix", WordEngine.suggestions(prefix, false).isNotEmpty())
        }
    }


    @Test fun englishSuggestionsReturnTenOptionsForLongerPrefix() {
        val options = WordEngine.suggestions("infor", false)
        assertEquals(10, options.size)
        assertEquals(10, options.distinct().size)
    }

    @Test fun emptyInputHasNoSuggestionOrTransliteration() {
        assertEquals(null, WordEngine.transliterate(" "))
        assertTrue(WordEngine.suggestions("", true).isEmpty())
    }
}
