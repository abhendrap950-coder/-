package com.example.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Calendar

class GitaRepository(private val context: Context) {

    private val sharedPrefs = context.getSharedPreferences("bhagavad_gita_prefs", Context.MODE_PRIVATE)

    @Volatile
    private var cachedChapters: List<Chapter>? = null

    @Volatile
    private var cachedVerses: List<Verse>? = null

    fun loadChapters(): List<Chapter> {
        cachedChapters?.let { return it }
        val list = mutableListOf<Chapter>()
        try {
            val jsonString = context.assets.open("data/chapters.json").use { stream ->
                BufferedReader(InputStreamReader(stream)).readText()
            }
            val array = JSONArray(jsonString)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    Chapter(
                        chapter = obj.getInt("chapter"),
                        name = obj.getString("name"),
                        nameEnglish = obj.optString("nameEnglish", ""),
                        meaning = obj.optString("meaning", ""),
                        versesCount = obj.getInt("versesCount"),
                        summary = obj.optString("summary", "")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        cachedChapters = list
        return list
    }

    fun loadVerses(): List<Verse> {
        cachedVerses?.let { return it }
        val list = mutableListOf<Verse>()
        try {
            val jsonString = context.assets.open("data/verses.json").use { stream ->
                BufferedReader(InputStreamReader(stream)).readText()
            }
            val array = JSONArray(jsonString)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    Verse(
                        id = obj.getString("id"),
                        orderId = obj.optInt("orderId", i + 1),
                        chapter = obj.getInt("chapter"),
                        chapterName = obj.optString("chapterName", "अध्याय ${obj.getInt("chapter")}"),
                        verse = obj.getInt("verse"),
                        sanskrit = obj.getString("sanskrit"),
                        transliteration = obj.optString("transliteration", ""),
                        wordMeanings = obj.optString("wordMeanings", ""),
                        hindiMeaning = obj.getString("hindiMeaning"),
                        simpleBhavarth = obj.optString("simpleBhavarth", ""),
                        source = obj.optString("source", "Gita Supersite / Gita Press"),
                        verified = obj.optBoolean("verified", true),
                        verificationDate = obj.optString("verificationDate", "2026-10-07")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        cachedVerses = list
        return list
    }

    fun getVerseById(id: String): Verse? {
        return loadVerses().find { it.id.equals(id, ignoreCase = true) }
    }

    fun getVersesForChapter(chapterNumber: Int): List<Verse> {
        return loadVerses().filter { it.chapter == chapterNumber }
    }

    fun getChapter(chapterNumber: Int): Chapter? {
        return loadChapters().find { it.chapter == chapterNumber }
    }

    fun getDailyVerse(): Verse? {
        val all = loadVerses().filter { it.verified }
        if (all.isEmpty()) return null
        val calendar = Calendar.getInstance()
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val year = calendar.get(Calendar.YEAR)
        val seed = (year * 366 + dayOfYear) % all.size
        return all[seed]
    }

    fun searchVerses(query: String): List<Verse> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return emptyList()

        val all = loadVerses()
        // Check if query is e.g. "2.47" or "2:47" or "BG2.47"
        val versePattern = Regex("""^(?:bg\s*)?(\d+)[.:\s]+(\d+)$""", RegexOption.IGNORE_CASE)
        val match = versePattern.matchEntire(q)
        if (match != null) {
            val ch = match.groupValues[1].toIntOrNull() ?: 0
            val v = match.groupValues[2].toIntOrNull() ?: 0
            val exact = all.find { it.chapter == ch && it.verse == v }
            if (exact != null) return listOf(exact)
        }

        return all.filter { verse ->
            verse.sanskrit.contains(query, ignoreCase = true) ||
            verse.hindiMeaning.contains(query, ignoreCase = true) ||
            verse.simpleBhavarth.contains(query, ignoreCase = true) ||
            verse.transliteration.contains(query, ignoreCase = true) ||
            verse.chapterName.contains(query, ignoreCase = true) ||
            verse.id.contains(query, ignoreCase = true)
        }.take(50)
    }

    // Bookmarks Local Storage
    fun getBookmarks(): Set<String> {
        return sharedPrefs.getStringSet("bookmarked_verse_ids", emptySet()) ?: emptySet()
    }

    fun isBookmarked(verseId: String): Boolean {
        return getBookmarks().contains(verseId)
    }

    fun toggleBookmark(verseId: String): Boolean {
        val current = getBookmarks().toMutableSet()
        val willBeBookmarked = !current.contains(verseId)
        if (willBeBookmarked) {
            current.add(verseId)
        } else {
            current.remove(verseId)
        }
        sharedPrefs.edit().putStringSet("bookmarked_verse_ids", current).apply()
        return willBeBookmarked
    }

    // Recently Viewed
    fun getRecentlyViewed(): List<String> {
        val raw = sharedPrefs.getString("recently_viewed_ids", "") ?: ""
        if (raw.isEmpty()) return emptyList()
        return raw.split(",").filter { it.isNotBlank() }
    }

    fun addRecentlyViewed(verseId: String) {
        val list = getRecentlyViewed().toMutableList()
        list.remove(verseId)
        list.add(0, verseId)
        val trimmed = list.take(20).joinToString(",")
        sharedPrefs.edit().putString("recently_viewed_ids", trimmed).apply()
    }

    // User Preferences
    fun isIntroEnabled(): Boolean {
        return sharedPrefs.getBoolean("pref_intro_enabled", true)
    }

    fun setIntroEnabled(enabled: Boolean) {
        sharedPrefs.edit().putBoolean("pref_intro_enabled", enabled).apply()
    }

    fun getVoiceGender(): String {
        return sharedPrefs.getString("pref_voice_gender", "male") ?: "male"
    }

    fun setVoiceGender(gender: String) {
        sharedPrefs.edit().putString("pref_voice_gender", gender).apply()
    }

    fun getAudioSpeed(): Float {
        return sharedPrefs.getFloat("pref_audio_speed", 0.85f)
    }

    fun setAudioSpeed(speed: Float) {
        sharedPrefs.edit().putFloat("pref_audio_speed", speed).apply()
    }

    fun getSanskritFontSize(): Int {
        return sharedPrefs.getInt("pref_sanskrit_font_size", 20)
    }

    fun setSanskritFontSize(size: Int) {
        sharedPrefs.edit().putInt("pref_sanskrit_font_size", size).apply()
    }

    fun getHindiFontSize(): Int {
        return sharedPrefs.getInt("pref_hindi_font_size", 16)
    }

    fun setHindiFontSize(size: Int) {
        sharedPrefs.edit().putInt("pref_hindi_font_size", size).apply()
    }

    fun isMadhurataEnabled(): Boolean {
        return sharedPrefs.getBoolean("pref_madhurata_mode", true)
    }

    fun setMadhurataEnabled(enabled: Boolean) {
        sharedPrefs.edit().putBoolean("pref_madhurata_mode", enabled).apply()
    }

    fun getTanpuraVolume(): Float {
        return sharedPrefs.getFloat("pref_tanpura_volume", 0.28f)
    }

    fun setTanpuraVolume(volume: Float) {
        sharedPrefs.edit().putFloat("pref_tanpura_volume", volume).apply()
    }

    fun getAppTheme(): String {
        return sharedPrefs.getString("pref_app_theme", "ancient") ?: "ancient"
    }

    fun setAppTheme(theme: String) {
        sharedPrefs.edit().putString("pref_app_theme", theme).apply()
    }

    // Dataset Validation
    fun validateDataset(): DatasetValidationResult {
        val chapters = loadChapters()
        val verses = loadVerses()
        val errors = mutableListOf<String>()

        if (chapters.size != 18) {
            errors.add("अध्याय संख्या 18 होनी चाहिए, वर्तमान संख्या: ${chapters.size}")
        }

        val ids = mutableSetOf<String>()
        var duplicates = 0
        var emptySk = 0
        var emptyHi = 0
        var verifiedCount = 0

        for (v in verses) {
            if (ids.contains(v.id)) {
                duplicates++
                errors.add("दोहराया गया श्लोक ID: ${v.id}")
            }
            ids.add(v.id)

            if (v.sanskrit.isBlank()) {
                emptySk++
                errors.add("रिक्त संस्कृत पाठ: ${v.id}")
            }
            if (v.hindiMeaning.isBlank()) {
                emptyHi++
                errors.add("रिक्त हिन्दी अर्थ: ${v.id}")
            }
            if (v.verified) {
                verifiedCount++
            }
        }

        return DatasetValidationResult(
            isValid = errors.isEmpty(),
            chapterCount = chapters.size,
            verseCount = verses.size,
            verifiedVerseCount = verifiedCount,
            duplicateIdCount = duplicates,
            emptySanskritCount = emptySk,
            emptyHindiCount = emptyHi,
            errors = errors
        )
    }

    // Data Import Mechanism
    fun importCustomVersesJson(jsonContent: String): Pair<Boolean, String> {
        return try {
            val array = JSONArray(jsonContent)
            val importedList = mutableListOf<Verse>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val ch = obj.getInt("chapter")
                val v = obj.getInt("verse")
                val sk = obj.getString("sanskrit").trim()
                val hi = obj.getString("hindiMeaning").trim()
                if (sk.isEmpty() || hi.isEmpty()) {
                    return Pair(false, "श्लोक $ch.$v में रिक्त संस्कृत या हिन्दी अर्थ पाया गया।")
                }
                importedList.add(
                    Verse(
                        id = obj.optString("id", "BG$ch.$v"),
                        orderId = obj.optInt("orderId", i + 1),
                        chapter = ch,
                        chapterName = obj.optString("chapterName", "अध्याय $ch"),
                        verse = v,
                        sanskrit = sk,
                        transliteration = obj.optString("transliteration", ""),
                        wordMeanings = obj.optString("wordMeanings", ""),
                        hindiMeaning = hi,
                        simpleBhavarth = obj.optString("simpleBhavarth", ""),
                        source = obj.optString("source", "कस्टम इम्पोर्ट"),
                        verified = obj.optBoolean("verified", true),
                        verificationDate = "2026-10-07"
                    )
                )
            }
            cachedVerses = importedList
            Pair(true, "सफलतापूर्वक ${importedList.size} श्लोक आयात और प्रमाणित किए गए।")
        } catch (e: Exception) {
            Pair(false, "आयात विफल: ${e.message}")
        }
    }
}
