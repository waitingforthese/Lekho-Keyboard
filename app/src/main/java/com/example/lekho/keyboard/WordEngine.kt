package com.example.lekho.keyboard

import java.util.Locale

/**
 * Offline starter dictionary for Marathi phonetic typing.
 * This is a dictionary-backed transliterator, not yet a complete language engine.
 */
object WordEngine {
    private val marathiWords = linkedMapOf(
        "namaskar" to "नमस्कार",
        "namaste" to "नमस्ते",
        "yashsvi" to "यशस्वी",
        "yashasvi" to "यशस्वी",
        "yashaswi" to "यशस्वी",
        "majha" to "माझा",
        "majhi" to "माझी",
        "maze" to "माझे",
        "tumhi" to "तुम्ही",
        "tumcha" to "तुमचा",
        "tumchi" to "तुमची",
        "mi" to "मी",
        "mala" to "मला",
        "maza" to "माझा",
        "aamhi" to "आम्ही",
        "apan" to "आपण",
        "kay" to "काय",
        "kaay" to "काय",
        "ahe" to "आहे",
        "aahe" to "आहे",
        "ahes" to "आहेस",
        "aahet" to "आहेत",
        "nahi" to "नाही",
        "nahie" to "नाही",
        "ho" to "हो",
        "nako" to "नको",
        "ka" to "का",
        "kasa" to "कसा",
        "kashi" to "कशी",
        "kase" to "कसे",
        "kuthe" to "कुठे",
        "kevha" to "केव्हा",
        "kadhi" to "कधी",
        "kiti" to "किती",
        "kon" to "कोण",
        "koni" to "कोणी",
        "kar" to "कर",
        "kara" to "करा",
        "karu" to "करू",
        "karaycha" to "करायचा",
        "pahije" to "पाहिजे",
        "krupaya" to "कृपया",
        "dhanyavad" to "धन्यवाद",
        "abhaar" to "आभार",
        "shubhechha" to "शुभेच्छा",
        "maharashtra" to "महाराष्ट्र",
        "bharat" to "भारत",
        "pune" to "पुणे",
        "mumbai" to "मुंबई",
        "daund" to "दौंड",
        "ahilyanagar" to "अहिल्यानगर",
        "vyavsay" to "व्यवसाय",
        "seva" to "सेवा",
        "paisa" to "पैसा",
        "paise" to "पैसे",
        "kam" to "काम",
        "mahit" to "माहित",
        "mahitii" to "माहिती",
        "mobile" to "मोबाईल",
        "bank" to "बँक",
        "khate" to "खाते",
        "aaj" to "आज",
        "udya" to "उद्या",
        "kal" to "काल",
        "ata" to "आता",
        "lavkar" to "लवकर",
        "changla" to "चांगला",
        "changli" to "चांगली",
        "barobar" to "बरोबर",
        "thik" to "ठीक",
        "majhyasathi" to "माझ्यासाठी"
    )

    private val englishWords = listOf(
        "about", "account", "after", "again", "all", "also", "always", "and", "any", "app",
        "are", "available", "because", "before", "business", "can", "change", "check", "come",
        "day", "dear", "details", "do", "document", "done", "each", "email", "english", "every",
        "fine", "for", "from", "get", "give", "good", "great", "hello", "help", "here", "how",
        "important", "information", "is", "it", "kindly", "know", "like", "login", "made", "make",
        "mobile", "more", "my", "name", "need", "new", "not", "now", "of", "office", "on", "one",
        "our", "please", "service", "share", "sir", "some", "support", "thank", "thanks", "that",
        "the", "there", "this", "today", "tomorrow", "update", "use", "verification", "very", "was",
        "we", "welcome", "what", "when", "where", "which", "with", "work", "you", "your"
    )

    fun transliterate(token: String): String? = marathiWords[token.trim().lowercase(Locale.ROOT)]

    fun suggestions(token: String, marathiMode: Boolean): List<String> {
        val key = token.trim().lowercase(Locale.ROOT)
        if (key.isBlank()) return emptyList()

        return if (marathiMode) {
            buildList {
                marathiWords[key]?.let(::add)
                marathiWords.entries.asSequence()
                    .filter { it.key.startsWith(key) && it.key != key }
                    .take(8)
                    .map { it.value }
                    .forEach(::add)
                add(token)
            }.distinct().take(3)
        } else {
            buildList {
                if (key in englishWords) add(token)
                englishWords.asSequence()
                    .filter { it.startsWith(key) && it != key }
                    .take(5)
                    .forEach(::add)
                if (isEmpty()) add(token)
            }.distinct().take(3)
        }
    }
}
