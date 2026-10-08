package com.example.data

data class Chapter(
    val chapter: Int,
    val name: String,
    val nameEnglish: String,
    val meaning: String,
    val versesCount: Int,
    val summary: String
)

data class Verse(
    val id: String, // e.g. "BG2.47"
    val orderId: Int, // 1..701
    val chapter: Int,
    val chapterName: String,
    val verse: Int,
    val sanskrit: String,
    val transliteration: String = "",
    val wordMeanings: String = "",
    val hindiMeaning: String,
    val simpleBhavarth: String = "",
    val source: String,
    val verified: Boolean,
    val verificationDate: String = "2026-10-07"
)

data class DatasetValidationResult(
    val isValid: Boolean,
    val chapterCount: Int,
    val verseCount: Int,
    val verifiedVerseCount: Int,
    val duplicateIdCount: Int,
    val emptySanskritCount: Int,
    val emptyHindiCount: Int,
    val errors: List<String>
)
