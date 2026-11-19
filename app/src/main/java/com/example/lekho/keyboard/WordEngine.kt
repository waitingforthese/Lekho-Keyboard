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
        "fine", "for", "from", "get", "give", "good", "great", "hello", "hellos", "hellish", "help",
        "helper", "helpers", "helped", "helpful", "helping", "helps", "helicopter", "health", "healthy",
        "hearing", "heard", "heaven", "heavy", "here", "how",
        "important", "info", "inform", "informal", "informally", "informant", "informed", "informer",
        "informing", "informs", "information", "informational", "informatics", "informative", "is", "it",
        "kindly", "know", "like", "login", "made", "make",
        "mobile", "more", "my", "name", "need", "new", "not", "now", "of", "office", "on", "one",
        "our", "please", "service", "share", "sir", "some", "support", "thank", "thanks", "that",
        "the", "there", "this", "today", "tomorrow", "update", "use", "verification", "very", "was",
        "we", "welcome", "what", "when", "where", "which", "with", "work", "you", "your",
        "able", "accept", "access", "add", "address", "admin", "advance", "advice", "agree", "allow",
        "almost", "along", "already", "answer", "anyone", "anything", "apply", "april", "area", "around",
        "back", "balance", "banking", "basic", "better", "between", "book", "both", "bring", "build",
        "call", "called", "calling", "care", "case", "cash", "certain", "clear", "client", "close",
        "code", "come", "coming", "complete", "correct", "customer", "data", "date", "debit", "december",
        "design", "different", "digital", "direct", "download", "early", "easy", "education", "end", "enough",
        "error", "even", "example", "experience", "extra", "family", "fast", "feel", "file", "final",
        "first", "follow", "form", "found", "free", "full", "future", "general", "going", "greatest",
        "group", "half", "happy", "hard", "have", "having", "head", "hear", "heard", "helpful",
        "high", "home", "hope", "hour", "idea", "image", "immediately", "include", "interest", "issue",
        "job", "just", "keep", "key", "large", "last", "later", "learn", "left", "less", "letter",
        "life", "link", "list", "long", "looking", "love", "main", "manage", "manager", "many",
        "message", "minute", "month", "morning", "much", "must", "never", "next", "number", "often",
        "online", "open", "option", "other", "payment", "people", "phone", "place", "plan", "price",
        "process", "provide", "quick", "read", "ready", "receive", "recent", "regular", "related", "reply",
        "report", "request", "required", "right", "same", "save", "school", "second", "select", "send",
        "simple", "since", "small", "start", "statement", "status", "still", "sure", "system", "take",
        "team", "tell", "text", "time", "together", "total", "transaction", "try", "type", "under",
        "understand", "until", "upload", "user", "valid", "value", "verify", "version", "video", "view",
        "wait", "want", "way", "week", "while", "without", "word", "working", "write", "wrong", "yes",
        "yesterday", "young", "zero", "helping", "helped", "healthy", "hearing", "heaven", "heading"
    )


    // Alternative phonetic readings used to build up to ten Marathi candidates.
    private data class ParseState(val output: String, val pendingConsonant: Boolean)
    private data class RomanPattern(
        val roman: String,
        val consonantChoices: List<String>? = null,
        val vowelChoices: List<Pair<String, String>>? = null // independent vowel to matra
    )

    private val vowelPatterns = listOf(
        RomanPattern("aa", vowelChoices = listOf("आ" to "ा")),
        RomanPattern("ai", vowelChoices = listOf("ऐ" to "ै")),
        RomanPattern("au", vowelChoices = listOf("औ" to "ौ")),
        RomanPattern("ee", vowelChoices = listOf("ई" to "ी")),
        RomanPattern("ii", vowelChoices = listOf("ई" to "ी", "इ" to "ि")),
        RomanPattern("oo", vowelChoices = listOf("ऊ" to "ू")),
        RomanPattern("uu", vowelChoices = listOf("ऊ" to "ू", "उ" to "ु")),
        RomanPattern("ri", vowelChoices = listOf("ऋ" to "ृ")),
        RomanPattern("i", vowelChoices = listOf("इ" to "ि", "ई" to "ी")),
        RomanPattern("u", vowelChoices = listOf("उ" to "ु", "ऊ" to "ू")),
        RomanPattern("e", vowelChoices = listOf("ए" to "े", "ऐ" to "ै")),
        RomanPattern("o", vowelChoices = listOf("ओ" to "ो", "औ" to "ौ")),
        RomanPattern("a", vowelChoices = listOf("अ" to "", "आ" to "ा"))
    )

    private val consonantPatterns = listOf(
        RomanPattern("chh", consonantChoices = listOf("छ")),
        RomanPattern("ksh", consonantChoices = listOf("क्ष")),
        RomanPattern("dny", consonantChoices = listOf("ज्ञ")),
        RomanPattern("jny", consonantChoices = listOf("ज्ञ")),
        RomanPattern("shr", consonantChoices = listOf("श्र")),
        RomanPattern("sh", consonantChoices = listOf("श", "ष")),
        RomanPattern("ch", consonantChoices = listOf("च", "छ")),
        RomanPattern("kh", consonantChoices = listOf("ख", "क")),
        RomanPattern("gh", consonantChoices = listOf("घ", "ग")),
        RomanPattern("jh", consonantChoices = listOf("झ", "ज")),
        RomanPattern("th", consonantChoices = listOf("थ", "ठ", "त")),
        RomanPattern("dh", consonantChoices = listOf("ध", "ढ", "द")),
        RomanPattern("ph", consonantChoices = listOf("फ")),
        RomanPattern("bh", consonantChoices = listOf("भ")),
        RomanPattern("ng", consonantChoices = listOf("ङ्ग", "ंग")),
        RomanPattern("ny", consonantChoices = listOf("ञ", "न्य")),
        RomanPattern("tr", consonantChoices = listOf("त्र")),
        RomanPattern("ṭh", consonantChoices = listOf("ठ")),
        RomanPattern("ḍh", consonantChoices = listOf("ढ")),
        RomanPattern("ṭ", consonantChoices = listOf("ट")),
        RomanPattern("ḍ", consonantChoices = listOf("ड")),
        RomanPattern("ṇ", consonantChoices = listOf("ण")),
        RomanPattern("ḷ", consonantChoices = listOf("ळ")),
        RomanPattern("k", consonantChoices = listOf("क")),
        RomanPattern("g", consonantChoices = listOf("ग")),
        RomanPattern("c", consonantChoices = listOf("च", "क")),
        RomanPattern("j", consonantChoices = listOf("ज", "ज्ञ")),
        RomanPattern("t", consonantChoices = listOf("त", "ट")),
        RomanPattern("d", consonantChoices = listOf("द", "ड")),
        RomanPattern("n", consonantChoices = listOf("न", "ण")),
        RomanPattern("p", consonantChoices = listOf("प")),
        RomanPattern("b", consonantChoices = listOf("ब")),
        RomanPattern("m", consonantChoices = listOf("म")),
        RomanPattern("y", consonantChoices = listOf("य")),
        RomanPattern("r", consonantChoices = listOf("र", "ऱ")),
        RomanPattern("l", consonantChoices = listOf("ल", "ळ")),
        RomanPattern("v", consonantChoices = listOf("व", "व्ह")),
        RomanPattern("w", consonantChoices = listOf("व", "व्ह")),
        RomanPattern("s", consonantChoices = listOf("स", "श")),
        RomanPattern("h", consonantChoices = listOf("ह")),
        RomanPattern("f", consonantChoices = listOf("फ", "फ़")),
        RomanPattern("q", consonantChoices = listOf("क", "क़")),
        RomanPattern("x", consonantChoices = listOf("क्ष")),
        RomanPattern("z", consonantChoices = listOf("ज़", "ज"))
    ).sortedByDescending { it.roman.length }

    private val allPatterns = (vowelPatterns + consonantPatterns)
        .sortedByDescending { it.roman.length }

    /** Returns the best Marathi form. Exact dictionary spellings always take priority. */
    fun transliterate(token: String): String? {
        val key = token.trim().lowercase(Locale.ROOT)
        if (key.isEmpty()) return null
        marathiWords[key]?.let { return it }
        return phoneticCandidates(key, 1).firstOrNull()
    }

    /**
     * Returns up to ten candidates in the selected language.
     * Marathi mode combines dictionary matches with alternative phonetic parses for unknown words.
     * English mode returns actual English dictionary words beginning with the typed prefix.
     */
    fun suggestions(token: String, marathiMode: Boolean): List<String> {
        val key = token.trim().lowercase(Locale.ROOT)
        if (key.isBlank()) return emptyList()

        if (!marathiMode) {
            return buildList {
                if (key in englishWords) add(token)
                englishWords.asSequence()
                    .filter { it.startsWith(key) && it != key }
                    .forEach(::add)
                if (isEmpty()) add(token)
            }.distinct().take(10)
        }

        val results = linkedSetOf<String>()
        marathiWords[key]?.let(results::add)
        marathiWords.entries.asSequence()
            .filter { it.key.startsWith(key) && it.key != key }
            .take(24)
            .map { it.value }
            .forEach(results::add)
        phoneticCandidates(key, 32).forEach(results::add)
        if (results.isEmpty()) results.add(token)
        return results.take(10)
    }

    /** Generate ordered phonetic candidates using a bounded beam over ambiguous Roman sounds. */
    private fun phoneticCandidates(input: String, limit: Int): List<String> {
        if (input.isBlank() || input.any { !it.isLetter() }) return emptyList()
        val states = Array(input.length + 1) { mutableListOf<ParseState>() }
        states[0].add(ParseState("", false))
        val perPositionLimit = 96

        for (index in input.indices) {
            if (states[index].isEmpty()) continue
            val matches = allPatterns.filter { input.startsWith(it.roman, index) }
            if (matches.isEmpty()) return emptyList()

            for (state in states[index]) {
                for (pattern in matches) {
                    val end = index + pattern.roman.length
                    if (pattern.vowelChoices != null) {
                        val choices = if (pattern.roman == "a" && state.pendingConsonant && end == input.length) {
                            pattern.vowelChoices.sortedByDescending { it.second.length }
                        } else pattern.vowelChoices
                        for ((independent, matra) in choices) {
                            val addition = if (state.pendingConsonant) matra else independent
                            val candidate = ParseState(state.output + addition, false)
                            if (states[end].size < perPositionLimit && candidate !in states[end]) {
                                states[end].add(candidate)
                            }
                        }
                    } else {
                        for (sound in pattern.consonantChoices.orEmpty()) {
                            val prefix = if (state.pendingConsonant) "्" else ""
                            val candidate = ParseState(state.output + prefix + sound, true)
                            if (states[end].size < perPositionLimit && candidate !in states[end]) {
                                states[end].add(candidate)
                            }
                        }
                    }
                }
            }
        }

        return states[input.length].asSequence()
            .map { it.output }
            .filter { it.isNotBlank() }
            .distinct()
            .take(limit)
            .toList()
    }

}
