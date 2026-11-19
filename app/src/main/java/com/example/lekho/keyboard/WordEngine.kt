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
        // Common short words first, so one- and two-letter prefixes are immediately useful.
        "the", "of", "and", "to", "in", "a", "is", "that", "for", "as", "it", "was",
        "with", "on", "be", "not", "by", "i", "this", "are", "he", "or", "from", "at",
        "his", "an", "have", "which", "but", "you", "had", "they", "were", "one", "their",
        "we", "all", "can", "her", "has", "there", "if", "more", "been", "when", "she",
        "would", "will", "other", "who", "no", "so", "these", "its", "what", "may", "do",
        "time", "some", "than", "new", "about", "also", "only", "into", "them", "out",
        "such", "my", "two", "him", "up", "first", "see", "could", "any", "then", "most",
        "your", "between", "me", "our", "after", "should", "like", "over", "many", "did",
        "said", "people", "even", "because", "how", "well", "where", "through", "made", "very",
        "those", "work", "now", "each", "must", "use", "life", "way", "used", "both", "same",
        "state", "being", "before", "years", "much", "however", "under", "world", "man", "make",
        "own", "just", "system", "part", "three", "while", "good", "during", "us", "long", "does",
        "number", "another", "back", "great", "down", "here", "without", "know", "different", "case",
        "general", "right", "high", "found", "still", "since", "little", "might", "against", "too",
        "take", "year", "within", "example", "children", "place", "given", "order", "power", "important", "info", "inform", "information", "hello", "help", "get", "public", "old", "small", "form", "point", "group", "often", "never",
        "water", "large", "less", "go", "law", "second", "data", "come", "set", "every", "end", "need",
        "called", "again", "off", "process", "among", "few", "family", "fact", "control", "human", "left",
        "school", "later", "say", "following", "possible", "house", "study", "present", "hand", "change",
        "thought", "table", "history", "early", "think", "last", "came", "home", "research", "find", "value",
        "several", "level", "until", "around", "far", "four", "course", "yet", "once", "become", "book",
        "area", "using", "means", "give", "period", "problem", "why", "things", "whether", "country", "based",
        "times", "body", "act", "something", "person", "nature", "line", "away", "name", "business", "certain",
        "education", "city", "want", "child", "known", "best", "effect", "type", "white", "help", "company",
        "common", "put", "section", "going", "side", "either", "problems", "next", "least", "young", "interest",
        "individual", "let", "service", "policy", "whole", "words", "better", "total", "taken", "seen", "view",
        "century", "question", "further", "local", "head", "having", "days", "took", "land", "am", "rate",
        "went", "analysis", "result", "age", "action", "itself", "light", "support", "free", "almost", "class",
        "making", "party", "look", "terms", "five", "particular", "major", "members", "sense", "position",
        "language", "love", "together", "experience", "results", "low", "health", "father", "model", "black",
        "mind", "nothing", "already", "along", "mother", "theory", "necessary", "real", "production", "true",
        "full", "usually", "open", "subject", "field", "enough", "knowledge", "done", "able", "face", "students",
        "asked", "changes", "available", "including", "perhaps", "program", "room", "show", "role", "special",
        "function", "care", "told", "air", "half", "structure", "matter", "similar", "word", "kind", "ever",
        "money", "short", "woman", "single", "market", "got", "according", "term", "management", "especially",
        "shown", "force", "provide", "english", "specific", "whose", "evidence", "working", "growth", "held", "areas",
        "eyes", "office", "began", "third", "energy", "report", "night", "art", "quite", "required", "services",
        "clear", "percent", "method", "due", "foreign", "past", "higher", "central", "reason", "trade", "values",
        "future", "material", "population", "treatment", "cost", "countries", "effects", "natural", "increase", "thing",
        "personal", "really", "west", "toward", "sometimes", "mean", "saw", "food", "science", "current", "approach",
        "felt", "probably", "test", "read", "greater", "seems", "practice", "note", "main", "lower", "tell",
        "situation", "keep", "activity", "knew", "surface", "account", "relationship", "cause", "idea", "movement",
        "close", "heart", "living", "related", "provided", "brought", "plan", "story", "patients", "size", "space",
        "source", "amount", "looked", "call", "basis", "turn", "paper", "income", "include", "culture", "blood",
        "difficult", "physical", "nor", "range", "gave", "feel", "needs", "property", "return", "play", "modern",
        "union", "across", "design", "late", "six", "near", "likely", "finally", "soon", "organization", "developed",
        "forms", "generally", "today", "strong", "price", "written", "particularly", "department", "influence", "building",
        "behavior", "pressure", "east", "capital", "believe", "outside", "response", "middle", "basic", "indeed",
        "books", "increased", "patient", "hard", "works", "decision", "makes", "ways", "turned", "addition", "series",
        "anything", "rule", "workers", "significant", "industry", "factors", "attention", "hands", "below", "performance",
        "longer", "methods", "quality", "involved", "simple", "says", "moment", "standard", "behind", "wanted", "son",
        "purpose", "simply", "normal", "needed", "shows", "job", "relations", "review", "sure", "months", "writing",
        "established", "ground", "instead", "center", "types", "tax", "complete", "taking", "direct", "continued",
        "received", "letter", "project", "learning", "issues", "average", "training", "understand", "poor", "levels",
        "front", "stage", "live", "top", "earlier", "hours", "run", "doing", "points", "parts", "religious",
        "lines", "text", "beginning", "cultural", "beyond", "understanding", "college", "lost", "distribution", "labor",
        "difference", "potential", "association", "product", "board", "character", "federal", "alone", "reading", "respect",
        "wife", "big", "object", "original", "resources", "parents", "loss", "river", "england", "town", "region",
        "page", "music", "limited", "medical", "seem", "published", "security", "effective", "followed", "door",
        "india", "forces", "looking", "sea", "meaning", "consider", "authority", "discussion", "answer", "degree",
        "final", "risk", "bank", "earth", "solution", "council", "financial", "application", "rest", "appear",
        "produced", "list", "clearly", "move", "street", "army", "products", "voice", "member", "feet", "rules",
        "leave", "march", "temperature", "pay", "lead", "except", "actually", "volume", "environment", "elements",
        "complex", "everything", "species", "ten", "importance", "million", "expected", "yes", "friends", "step",
        "events", "date", "indian", "europe", "differences", "added", "legal", "schools", "key", "status", "condition",
        "recent", "materials", "wrote", "truth", "primary", "included", "ideas", "bring", "reported", "flow", "fire",
        "road", "unit", "sent", "mass", "throughout", "literature", "image", "individuals", "meeting", "presence",
        "sound", "towards", "oil", "cut", "ask", "else", "morning", "spirit", "industrial", "obtained", "scale",
        "programs", "persons", "appropriate", "directly", "length", "positive", "applied", "appears", "technology",
        "administration", "direction", "planning", "carried", "equal", "try", "deal", "statement", "produce", "factor",
        "cross", "operation", "presented", "computer", "observed", "defined", "thinking", "hold", "site", "extent",
        "post", "start", "base", "reference", "follow", "concept", "determined", "fall", "economy", "hope", "wide",
        "civil", "author", "ability", "expression", "rates", "staff", "library", "student", "talk", "larger", "kept",
        "coming", "success", "deep", "placed", "numbers", "record", "peace", "comes", "critical", "agreement",
        "independent", "concerned", "created", "content", "started", "friend", "former", "pattern", "traditional", "laws",
        "article", "discussed", "choice", "demand", "commission", "processes", "active", "phase", "provides", "additional",
        "sources", "access", "freedom", "create", "share", "functions", "interests", "someone", "wall", "meet", "built",
        "plant", "nation", "entire", "relation", "task", "supply", "chief", "week", "manner", "reached", "certainly",
        "lives", "gives", "occur", "moral", "minutes", "tried", "takes", "politics", "weight", "exchange", "distance",
        "lack", "context", "paid", "principle", "born", "immediately", "passed", "box", "relative", "style", "bad",
        "justice", "communication", "existence", "reasons", "principles", "hundred", "inside", "lot", "met", "attempt",
        "construction", "dead", "highly", "green", "feeling", "introduction", "strength", "useful", "neither", "moved",
        "actual", "noted", "opinion", "internal", "police", "failure", "remember", "parties", "relatively", "develop",
        "car", "instance", "follows", "constant", "popular", "picture", "religion", "trying", "notes", "sample", "companies",
        "determine", "features", "easily", "pain", "contact", "previous", "seven", "news", "measure", "myself", "getting",
        "dark", "memory", "reality", "negative", "january", "stock", "appeared", "serious", "nearly", "speech", "teacher",
        "fear", "teachers", "effort", "october", "suggested", "rise", "separate", "allowed", "despite", "faith", "output",
        "professional", "goods", "worked", "upper", "heat", "units", "investment", "procedure", "film", "impact", "herself",
        "sun", "december", "variety", "continue", "september", "cold", "network", "gone", "miles", "families", "giving",
        "scientific", "division", "examples", "characteristics", "formed", "august", "decided", "teaching", "essential", "minister",
        "initial", "hour", "increasing", "employment", "places", "male", "compared", "marriage", "remain", "equipment",
        "letters", "forward", "event", "capacity", "hear", "female", "district", "nations", "germany", "measures", "girl",
        "mentioned", "speak", "efforts", "caused", "aspects", "whatever", "brown", "add", "frequently", "daily", "stand",
        // Original application-oriented terms remain in the priority set.
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

    // The curated words above are preferred for useful everyday suggestions.
    // A larger offline SCOWL-derived list is loaded from app/src/main/assets at IME startup.
    @Volatile private var englishLexicon: List<String> = englishWords.distinct()
    @Volatile private var englishWordSet: Set<String> = englishLexicon.toHashSet()
    @Volatile private var englishByFirstLetter: Map<Char, List<String>> = englishLexicon.groupBy { it.first() }
    @Volatile private var englishByFirstTwo: Map<String, List<String>> = englishLexicon
        .filter { it.length >= 2 }.groupBy { it.take(2) }
    @Volatile private var englishByLength: Map<Int, List<String>> = englishLexicon.groupBy { it.length }

    /** Merge a bundled offline English dictionary while keeping curated/common words first. */
    @Synchronized
    fun addEnglishDictionaryWords(words: Iterable<String>) {
        val merged = LinkedHashSet<String>()
        englishWords.forEach { word ->
            val normalized = word.trim().lowercase(Locale.ROOT)
            if (normalized.length in 1..18 && normalized.all { it in 'a'..'z' }) merged.add(normalized)
        }
        words.forEach { word ->
            val normalized = word.trim().lowercase(Locale.ROOT)
            if (normalized.length in 2..18 && normalized.all { it in 'a'..'z' }) merged.add(normalized)
        }
        val list = merged.toList()
        englishLexicon = list
        englishWordSet = list.toHashSet()
        englishByFirstLetter = list.groupBy { it.first() }
        englishByFirstTwo = list.filter { it.length >= 2 }.groupBy { it.take(2) }
        englishByLength = list.groupBy { it.length }
    }

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

        if (!marathiMode) return englishSuggestions(token, key)

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

    /** Prefix autocomplete plus one-edit spelling correction, all fully offline. */
    private fun englishSuggestions(originalToken: String, key: String): List<String> {
        val results = linkedSetOf<String>()
        val exact = key in englishWordSet
        if (exact) results.add(originalToken)

        val source = if (key.length >= 2) {
            englishByFirstTwo[key.take(2)].orEmpty()
        } else {
            englishByFirstLetter[key.first()].orEmpty()
        }
        val prefixMatches = source.asSequence()
            .filter { it.startsWith(key) && it != key }
            .toList()

        // When there is no exact word and only a few prefix completions, place likely
        // one-character corrections (including adjacent transpositions) ahead of rare
        // dictionary completions. Example: "helo" should offer "hello" prominently.
        if (!exact && key.length in 4..14 && prefixMatches.size < 8) {
            val corrections = ArrayList<String>()
            for (length in listOf(key.length + 1, key.length)) {
                for (candidate in englishByLength[length].orEmpty()) {
                    if (candidate == key || candidate.startsWith(key)) continue
                    if (candidate.firstOrNull() != key.first()) continue
                    if (oneEditDistance(key, candidate) == 1) corrections.add(candidate)
                }
            }
            corrections.forEach { results.add(applyEnglishCase(originalToken, it)) }
        }

        prefixMatches.forEach { results.add(applyEnglishCase(originalToken, it)) }
        if (results.isEmpty()) results.add(originalToken)
        return results.take(10).toList()
    }

    private fun applyEnglishCase(template: String, candidate: String): String = when {
        template.isNotEmpty() && template.all { it.isUpperCase() } -> candidate.uppercase(Locale.ROOT)
        template.firstOrNull()?.isUpperCase() == true -> candidate.replaceFirstChar { it.uppercase(Locale.ROOT) }
        else -> candidate
    }

    /** Returns 0 or 1 for an exact/one-edit match, otherwise null. Includes adjacent transposition. */
    private fun oneEditDistance(left: String, right: String): Int? {
        if (left == right) return 0
        if (kotlin.math.abs(left.length - right.length) > 1) return null

        if (left.length == right.length) {
            val mismatches = left.indices.filter { left[it] != right[it] }
            return when (mismatches.size) {
                1 -> 1
                2 -> {
                    val a = mismatches[0]
                    val b = mismatches[1]
                    if (b == a + 1 && left[a] == right[b] && left[b] == right[a]) 1 else null
                }
                else -> null
            }
        }

        val shorter = if (left.length < right.length) left else right
        val longer = if (left.length < right.length) right else left
        var shortIndex = 0
        var longIndex = 0
        var skipped = false
        while (shortIndex < shorter.length && longIndex < longer.length) {
            if (shorter[shortIndex] == longer[longIndex]) {
                shortIndex++
                longIndex++
            } else {
                if (skipped) return null
                skipped = true
                longIndex++
            }
        }
        return 1
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
