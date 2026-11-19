package com.example.lekho.keyboard

import java.util.Locale

/**
 * Offline Marathi phonetic transliteration for Lekho Keyboard.
 *
 * The dictionary provides correct common spellings and names. Unknown words use a
 * lightweight Roman-to-Devanagari phonetic fallback, so Marathi entry is not limited
 * to words present in the dictionary. This is intentionally offline and dependency-free.
 */
object WordEngine {
    private val marathiWords = linkedMapOf(
        // People and names commonly entered by users
        "rahul" to "राहुल",
        "dada" to "दादा",
        "dadaji" to "दादाजी",
        "bhau" to "भाऊ",
        "tai" to "ताई",
        "kaka" to "काका",
        "kaku" to "काकू",
        "arjunrao" to "अर्जुनराव",
        "jagtap" to "जगताप",
        "patil" to "पाटील",
        "suresh" to "सुरेश",
        "swapnil" to "स्वप्निल",
        "snehal" to "स्नेहल",
        "audumbar" to "औदुंबर",
        "oudumbar" to "औदुंबर",
        "yashsvi" to "यशस्वी",
        "yashasvi" to "यशस्वी",
        "yashaswi" to "यशस्वी",
        "ganesh" to "गणेश",
        "sachin" to "सचिन",
        "amit" to "अमित",
        "prasad" to "प्रसाद",
        "sanjay" to "संजय",
        "anjali" to "अंजली",
        "pooja" to "पूजा",
        "puja" to "पूजा",
        "sneha" to "स्नेहा",
        "kavita" to "कविता",
        "sunil" to "सुनील",
        "ramesh" to "रमेश",
        "mahesh" to "महेश",

        // Basic words and pronouns
        "mi" to "मी",
        "mii" to "मी",
        "mala" to "मला",
        "majha" to "माझा",
        "majhi" to "माझी",
        "majhe" to "माझे",
        "majhya" to "माझ्या",
        "majhyakade" to "माझ्याकडे",
        "majhyasathi" to "माझ्यासाठी",
        "maza" to "माझा",
        "maze" to "माझे",
        "mazi" to "माझी",
        "tu" to "तू",
        "tula" to "तुला",
        "tumhi" to "तुम्ही",
        "tumhala" to "तुम्हाला",
        "tumcha" to "तुमचा",
        "tumchi" to "तुमची",
        "tumche" to "तुमचे",
        "tumchya" to "तुमच्या",
        "tyala" to "त्याला",
        "tila" to "तिला",
        "tyanna" to "त्यांना",
        "amhi" to "आम्ही",
        "aamhi" to "आम्ही",
        "apan" to "आपण",
        "aapan" to "आपण",
        "aapla" to "आपला",
        "aapli" to "आपली",
        "aaple" to "आपले",
        "aaplya" to "आपल्या",
        "he" to "हे",
        "ha" to "हा",
        "hi" to "ही",
        "te" to "ते",
        "ti" to "ती",
        "to" to "तो",
        "ithe" to "इथे",
        "tithe" to "तिथे",
        "yethe" to "येथे",
        "kuthe" to "कुठे",
        "kevha" to "केव्हा",
        "kadhi" to "कधी",
        "kiti" to "किती",
        "kon" to "कोण",
        "koni" to "कोणी",
        "kasa" to "कसा",
        "kashi" to "कशी",
        "kase" to "कसे",
        "kasla" to "कसला",
        "kay" to "काय",
        "kaay" to "काय",
        "ka" to "का",
        "ahe" to "आहे",
        "aahe" to "आहे",
        "ahes" to "आहेस",
        "aahat" to "आहात",
        "aahet" to "आहेत",
        "nahi" to "नाही",
        "naahi" to "नाही",
        "nahie" to "नाही",
        "ho" to "हो",
        "nako" to "नको",
        "pan" to "पण",
        "ani" to "आणि",
        "aani" to "आणि",
        "karan" to "कारण",
        "mhanje" to "म्हणजे",
        "jar" to "जर",
        "tar" to "तर",
        "yacha" to "याचा",
        "yachi" to "याची",
        "yache" to "याचे",
        "tyacha" to "त्याचा",
        "tyachi" to "त्याची",
        "tyache" to "त्याचे",

        // Actions and everyday conversation
        "kar" to "कर",
        "kara" to "करा",
        "karu" to "करू",
        "karto" to "करतो",
        "karte" to "करते",
        "kartoy" to "करतोय",
        "kartat" to "करतात",
        "kele" to "केले",
        "kela" to "केला",
        "keli" to "केली",
        "karaycha" to "करायचा",
        "karaychi" to "करायची",
        "karayche" to "करायचे",
        "pahije" to "पाहिजे",
        "pahijet" to "पाहिजेत",
        "dya" to "द्या",
        "ghya" to "घ्या",
        "sanga" to "सांगा",
        "sang" to "सांग",
        "bagha" to "बघा",
        "paha" to "पहा",
        "chala" to "चला",
        "thamba" to "थांबा",
        "zala" to "झाला",
        "zali" to "झाली",
        "zale" to "झाले",
        "milale" to "मिळाले",
        "milnar" to "मिळणार",
        "chalel" to "चालेल",
        "nakki" to "नक्की",
        "nakkich" to "नक्कीच",
        "barobar" to "बरोबर",
        "thik" to "ठीक",
        "chhan" to "छान",
        "changla" to "चांगला",
        "changli" to "चांगली",
        "changale" to "चांगले",
        "lavkar" to "लवकर",
        "pudhe" to "पुढे",
        "mage" to "मागे",
        "nantar" to "नंतर",
        "aata" to "आता",
        "ata" to "आता",
        "aaj" to "आज",
        "udya" to "उद्या",
        "kal" to "काल",
        "parva" to "परवा",
        "sakali" to "सकाळी",
        "ratri" to "रात्री",
        "sandhyakali" to "संध्याकाळी",
        "vel" to "वेळ",
        "divas" to "दिवस",
        "roj" to "रोज",

        // Family, home, education and daily life
        "aai" to "आई",
        "baba" to "बाबा",
        "vadil" to "वडील",
        "aaji" to "आजी",
        "ajoba" to "आजोबा",
        "mulga" to "मुलगा",
        "mulgi" to "मुलगी",
        "ghari" to "घरी",
        "ghar" to "घर",
        "mitra" to "मित्र",
        "maitri" to "मैत्री",
        "shala" to "शाळा",
        "shikshan" to "शिक्षण",
        "pustak" to "पुस्तक",
        "paani" to "पाणी",
        "dudh" to "दूध",
        "jevan" to "जेवण",
        "bhook" to "भूक",
        "anand" to "आनंद",
        "prem" to "प्रेम",
        "man" to "मन",
        "vichar" to "विचार",
        "prashna" to "प्रश्न",
        "uttar" to "उत्तर",

        // Marathi, places and business terms
        "marathi" to "मराठी",
        "maharashtra" to "महाराष्ट्र",
        "bharat" to "भारत",
        "pune" to "पुणे",
        "mumbai" to "मुंबई",
        "daund" to "दौंड",
        "kashti" to "काष्टी",
        "kaashti" to "काष्टी",
        "kashthi" to "काष्टी",
        "maha" to "महा",
        "suvidha" to "सुविधा",
        "seva" to "सेवा",
        "vyavsay" to "व्यवसाय",
        "vyavasay" to "व्यवसाय",
        "mahiti" to "माहिती",
        "mahitii" to "माहिती",
        "mahit" to "माहित",
        "paisa" to "पैसा",
        "paise" to "पैसे",
        "kam" to "काम",
        "kaam" to "काम",
        "mobile" to "मोबाईल",
        "bank" to "बँक",
        "khate" to "खाते",
        "karyalay" to "कार्यालय",
        "arj" to "अर्ज",
        "sarkar" to "सरकार",
        "yojana" to "योजना",
        "shasan" to "शासन",
        "form" to "फॉर्म",
        "krupaya" to "कृपया",
        "shahar" to "शहर",
        "gaon" to "गाव",

        // Greetings and common phrases
        "namaskar" to "नमस्कार",
        "namaste" to "नमस्ते",
        "dhanyavad" to "धन्यवाद",
        "dhanyawad" to "धन्यवाद",
        "abhaar" to "आभार",
        "shubhechha" to "शुभेच्छा",
        "subhechha" to "शुभेच्छा",
        "hardik" to "हार्दिक",
        "swagat" to "स्वागत",
        "janmadin" to "जन्मदिन",
        "vadhdivas" to "वाढदिवस",
        "vadhdivsachya" to "वाढदिवसाच्या",
        "vishay" to "विषय",
        "dnyan" to "ज्ञान",
        "gyan" to "ज्ञान",
        "dnyaneshwar" to "ज्ञानेश्वर",
        "shabd" to "शब्द",
        "sangit" to "संगीत",
        "sangeet" to "संगीत",
        "kshama" to "क्षमा",
        "prarthana" to "प्रार्थना",
        "krushi" to "कृषी",
        "sheti" to "शेती",
        "shetkari" to "शेतकरी",
        "swatah" to "स्वतः",
        "swaraj" to "स्वराज",
        "shubh" to "शुभ",
        "maaf" to "माफ",
        "maafkara" to "माफ करा"
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

    // Longest matches must be tried first (e.g. "ksh" before "k").
    private val vowels = listOf(
        "aa" to Pair("आ", "ा"),
        "ai" to Pair("ऐ", "ै"),
        "au" to Pair("औ", "ौ"),
        "ee" to Pair("ई", "ी"),
        "ii" to Pair("ई", "ी"),
        "oo" to Pair("ऊ", "ू"),
        "uu" to Pair("ऊ", "ू"),
        "ri" to Pair("ऋ", "ृ"),
        "i" to Pair("इ", "ि"),
        "u" to Pair("उ", "ु"),
        "e" to Pair("ए", "े"),
        "o" to Pair("ओ", "ो"),
        "a" to Pair("अ", "ा")
    )

    private val consonants = listOf(
        "chh" to "छ", "ksh" to "क्ष", "dny" to "ज्ञ", "jny" to "ज्ञ", "shr" to "श्र",
        "kh" to "ख", "gh" to "घ", "ch" to "च", "jh" to "झ", "th" to "थ", "dh" to "ध",
        "ph" to "फ", "bh" to "भ", "sh" to "श", "ny" to "ञ", "tr" to "त्र",
        "ṭh" to "ठ", "ḍh" to "ढ", "ṭ" to "ट", "ḍ" to "ड", "ṇ" to "ण", "ḷ" to "ळ",
        "k" to "क", "g" to "ग", "c" to "च", "j" to "ज", "T" to "ट", "D" to "ड",
        "t" to "त", "d" to "द", "n" to "न", "p" to "प", "b" to "ब", "m" to "म",
        "y" to "य", "r" to "र", "l" to "ल", "v" to "व", "w" to "व", "s" to "स",
        "h" to "ह", "f" to "फ", "q" to "क", "x" to "क्ष", "z" to "ज़"
    ).sortedByDescending { it.first.length }

    /** Returns a Marathi candidate for any supported Roman-letter token. */
    fun transliterate(token: String): String? {
        val key = token.trim().lowercase(Locale.ROOT)
        if (key.isEmpty()) return null
        marathiWords[key]?.let { return it }
        return phoneticFallback(key)
    }

    /** Returns up to three suggestions for the keyboard's suggestion strip. */
    fun suggestions(token: String, marathiMode: Boolean): List<String> {
        val key = token.trim().lowercase(Locale.ROOT)
        if (key.isBlank()) return emptyList()

        if (!marathiMode) {
            return buildList {
                if (key in englishWords) add(token)
                englishWords.asSequence()
                    .filter { it.startsWith(key) && it != key }
                    .take(5)
                    .forEach(::add)
                if (isEmpty()) add(token)
            }.distinct().take(3)
        }

        return buildList {
            marathiWords[key]?.let(::add)
            marathiWords.entries.asSequence()
                .filter { it.key.startsWith(key) && it.key != key }
                .take(10)
                .map { it.value }
                .forEach(::add)
            transliterate(key)?.let(::add)
            add(token)
        }.distinct().take(3)
    }

    private fun phoneticFallback(input: String): String? {
        val output = StringBuilder()
        var index = 0

        while (index < input.length) {
            val vowel = vowels.firstOrNull { (roman, _) -> input.startsWith(roman, index) }
            if (vowel != null) {
                output.append(vowel.second.first)
                index += vowel.first.length
                continue
            }

            val consonant = consonants.firstOrNull { (roman, _) -> input.startsWith(roman, index) }
                ?: return null
            val roman = consonant.first
            val devanagari = consonant.second
            val afterConsonant = index + roman.length
            val followingVowel = vowels.firstOrNull { (vowelRoman, _) ->
                input.startsWith(vowelRoman, afterConsonant)
            }

            if (followingVowel != null) {
                val vowelEnd = afterConsonant + followingVowel.first.length
                output.append(devanagari)
                when {
                    followingVowel.first == "a" && vowelEnd == input.length -> output.append("ा")
                    followingVowel.first == "a" -> Unit // inherent short अ on this consonant
                    else -> output.append(followingVowel.second.second)
                }
                index = vowelEnd
            } else {
                output.append(devanagari)
                val nextIsConsonant = afterConsonant < input.length &&
                    consonants.any { (nextRoman, _) -> input.startsWith(nextRoman, afterConsonant) }
                if (nextIsConsonant) output.append('्')
                index = afterConsonant
            }
        }

        return output.toString().ifBlank { null }
    }
}
