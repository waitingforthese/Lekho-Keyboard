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
        assertEquals("घर", WordEngine.transliterate("ghar"))
        assertEquals("सुख", WordEngine.transliterate("sukh"))
        assertEquals("विषय", WordEngine.transliterate("vishay"))
        assertEquals("काम", WordEngine.transliterate("kaam"))
        assertEquals("ज्ञान", WordEngine.transliterate("dnyan"))
        assertEquals("क्षमा", WordEngine.transliterate("kshama"))
        assertEquals("म्हणजे", WordEngine.transliterate("mhanje"))
    }

    @Test fun suggestionsIncludeMarathiCandidatesForPartialWords() {
        assertTrue(WordEngine.suggestions("rah", true).contains("राहुल"))
        assertTrue(WordEngine.suggestions("kavi", true).contains("कविता"))
        assertTrue(WordEngine.suggestions("nam", true).contains("नमस्कार"))
    }

    @Test fun englishModeKeepsEnglishSuggestions() {
        assertEquals("hello", WordEngine.suggestions("hello", false).first())
    }

    @Test fun emptyInputHasNoSuggestionOrTransliteration() {
        assertEquals(null, WordEngine.transliterate(" "))
        assertTrue(WordEngine.suggestions("", true).isEmpty())
    }
}
