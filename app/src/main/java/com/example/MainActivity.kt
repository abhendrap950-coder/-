package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.audio.AudioMode
import com.example.audio.GitaAudioEngine
import com.example.data.Chapter
import com.example.data.GitaRepository
import com.example.data.Verse
import com.example.ui.components.CinematicIntro
import com.example.ui.components.StatusCardModal
import com.example.ui.screens.*
import com.example.ui.theme.BhagavadGitaTheme

sealed class Screen {
    object Intro : Screen()
    object Home : Screen()
    object Chapters : Screen()
    data class ChapterDetail(val chapterNumber: Int) : Screen()
    data class VerseReader(val verseId: String) : Screen()
    object Search : Screen()
    object Bookmarks : Screen()
    object RecentlyViewed : Screen()
    object Settings : Screen()
}

class MainActivity : ComponentActivity() {

    private lateinit var repository: GitaRepository
    private lateinit var audioEngine: GitaAudioEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        repository = GitaRepository(this)
        audioEngine = GitaAudioEngine(this)

        setContent {
            var currentTheme by remember { mutableStateOf(repository.getAppTheme()) }
            var isIntroEnabled by remember { mutableStateOf(repository.isIntroEnabled()) }
            var voiceGender by remember { mutableStateOf(repository.getVoiceGender()) }
            var audioSpeed by remember { mutableFloatStateOf(repository.getAudioSpeed()) }
            var isMadhurataEnabled by remember { mutableStateOf(repository.isMadhurataEnabled()) }
            var tanpuraVolume by remember { mutableFloatStateOf(repository.getTanpuraVolume()) }
            var sanskritFontSize by remember { mutableIntStateOf(repository.getSanskritFontSize()) }
            var hindiFontSize by remember { mutableIntStateOf(repository.getHindiFontSize()) }

            // Sync audio engine
            LaunchedEffect(voiceGender) {
                audioEngine.setGender(voiceGender)
            }
            LaunchedEffect(audioSpeed) {
                audioEngine.setSpeed(audioSpeed)
            }
            LaunchedEffect(isMadhurataEnabled) {
                audioEngine.setMadhurataEnabled(isMadhurataEnabled)
            }
            LaunchedEffect(tanpuraVolume) {
                audioEngine.setTanpuraVolume(tanpuraVolume)
            }

            BhagavadGitaTheme(appThemeMode = currentTheme) {
                // Navigation state stack
                var screenStack by remember {
                    mutableStateOf(
                        if (isIntroEnabled) listOf<Screen>(Screen.Intro) else listOf<Screen>(Screen.Home)
                    )
                }

                val currentScreen = screenStack.lastOrNull() ?: Screen.Home

                // Active verse for Status 9:16 Modal
                var statusModalVerse by remember { mutableStateOf<Verse?>(null) }

                // Audio playing state
                val isPlaying by audioEngine.isPlaying.collectAsState()
                val isPaused by audioEngine.isPaused.collectAsState()
                val playerState by audioEngine.playerState.collectAsState()
                val errorMessage by audioEngine.errorMessage.collectAsState()
                val currentPlayingId by audioEngine.currentPlayingId.collectAsState()
                val currentPlayingMode by audioEngine.currentMode.collectAsState()
                val playbackProgress by audioEngine.playbackProgress.collectAsState()

                // Bookmarks state trigger
                var bookmarkRefreshKey by remember { mutableIntStateOf(0) }

                fun navigateTo(screen: Screen) {
                    screenStack = screenStack + screen
                }

                fun navigateBack() {
                    if (screenStack.size > 1) {
                        audioEngine.stop()
                        screenStack = screenStack.dropLast(1)
                    }
                }

                // Top level container
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    when (val screen = currentScreen) {
                        is Screen.Intro -> {
                            CinematicIntro(
                                onFinished = {
                                    screenStack = listOf(Screen.Home)
                                },
                                onSkip = {
                                    screenStack = listOf(Screen.Home)
                                }
                            )
                        }

                        is Screen.Home -> {
                            val dailyVerse = remember { repository.getDailyVerse() }
                            HomeScreen(
                                dailyVerse = dailyVerse,
                                onNavigateToChapters = { navigateTo(Screen.Chapters) },
                                onNavigateToVerse = { verseId ->
                                    repository.addRecentlyViewed(verseId)
                                    navigateTo(Screen.VerseReader(verseId))
                                },
                                onNavigateToSearch = { navigateTo(Screen.Search) },
                                onNavigateToBookmarks = { navigateTo(Screen.Bookmarks) },
                                onNavigateToRecent = { navigateTo(Screen.RecentlyViewed) },
                                onNavigateToSettings = { navigateTo(Screen.Settings) },
                                onOpenStatusModal = { verse -> statusModalVerse = verse }
                            )
                        }

                        is Screen.Chapters -> {
                            BackHandler { navigateBack() }
                            val chapters = remember { repository.loadChapters() }
                            ChaptersScreen(
                                chapters = chapters,
                                onSelectChapter = { chNum -> navigateTo(Screen.ChapterDetail(chNum)) },
                                onBack = { navigateBack() }
                            )
                        }

                        is Screen.ChapterDetail -> {
                            BackHandler { navigateBack() }
                            val chapter = remember(screen.chapterNumber) { repository.getChapter(screen.chapterNumber) }
                            val verses = remember(screen.chapterNumber) { repository.getVersesForChapter(screen.chapterNumber) }
                            ChapterDetailScreen(
                                chapter = chapter,
                                verses = verses,
                                onSelectVerse = { verseId ->
                                    repository.addRecentlyViewed(verseId)
                                    navigateTo(Screen.VerseReader(verseId))
                                },
                                onBack = { navigateBack() }
                            )
                        }

                        is Screen.VerseReader -> {
                            BackHandler {
                                audioEngine.stop()
                                navigateBack()
                            }
                            val verse = remember(screen.verseId) { repository.getVerseById(screen.verseId) }
                            val isBookmarked = remember(screen.verseId, bookmarkRefreshKey) {
                                repository.isBookmarked(screen.verseId)
                            }
                            val allVerses = remember { repository.loadVerses() }

                            val isCurrentVerseActive = (isPlaying || isPaused) && (
                                currentPlayingId == "sanskrit_${verse?.id}" || currentPlayingId == "hindi_${verse?.id}"
                            )

                            VerseReaderScreen(
                                verse = verse,
                                isBookmarked = isBookmarked,
                                isPlaying = isPlaying && isCurrentVerseActive,
                                isPaused = isPaused && isCurrentVerseActive,
                                playerState = playerState,
                                errorMessage = errorMessage,
                                currentPlayingMode = currentPlayingMode,
                                playbackProgress = playbackProgress,
                                isMadhurataEnabled = isMadhurataEnabled,
                                tanpuraVolume = tanpuraVolume,
                                sanskritFontSize = sanskritFontSize,
                                hindiFontSize = hindiFontSize,
                                onToggleBookmark = {
                                    verse?.let {
                                        repository.toggleBookmark(it.id)
                                        bookmarkRefreshKey++
                                    }
                                },
                                onPlaySanskritAudio = {
                                    verse?.let {
                                        audioEngine.playSanskritVerse(it.id, it.sanskrit)
                                    }
                                },
                                onPlayHindiAudio = {
                                    verse?.let {
                                        audioEngine.playHindiMeaning(it.id, it.chapter, it.verse, it.hindiMeaning)
                                    }
                                },
                                onPauseAudio = {
                                    audioEngine.pause()
                                },
                                onResumeAudio = {
                                    audioEngine.resume()
                                },
                                onStopAudio = {
                                    audioEngine.stop()
                                },
                                onRetryAudio = {
                                    audioEngine.retry()
                                },
                                onToggleMadhurata = { enabled ->
                                    isMadhurataEnabled = enabled
                                    repository.setMadhurataEnabled(enabled)
                                },
                                onTanpuraVolumeChange = { vol ->
                                    tanpuraVolume = vol
                                    repository.setTanpuraVolume(vol)
                                },
                                onNextVerse = {
                                    verse?.let { currentV ->
                                        audioEngine.stop()
                                        val idx = allVerses.indexOfFirst { it.id == currentV.id }
                                        if (idx != -1 && idx < allVerses.size - 1) {
                                            val nextV = allVerses[idx + 1]
                                            repository.addRecentlyViewed(nextV.id)
                                            screenStack = screenStack.dropLast(1) + Screen.VerseReader(nextV.id)
                                        }
                                    }
                                },
                                onPreviousVerse = {
                                    verse?.let { currentV ->
                                        audioEngine.stop()
                                        val idx = allVerses.indexOfFirst { it.id == currentV.id }
                                        if (idx > 0) {
                                            val prevV = allVerses[idx - 1]
                                            repository.addRecentlyViewed(prevV.id)
                                            screenStack = screenStack.dropLast(1) + Screen.VerseReader(prevV.id)
                                        }
                                    }
                                },
                                onOpenStatusModal = {
                                    statusModalVerse = verse
                                },
                                onBack = {
                                    audioEngine.stop()
                                    navigateBack()
                                },
                                onAdjustSanskritFontSize = { size ->
                                    sanskritFontSize = size
                                    repository.setSanskritFontSize(size)
                                },
                                onAdjustHindiFontSize = { size ->
                                    hindiFontSize = size
                                    repository.setHindiFontSize(size)
                                }
                            )
                        }

                        is Screen.Search -> {
                            BackHandler { navigateBack() }
                            SearchScreen(
                                onSearch = { q -> repository.searchVerses(q) },
                                onSelectVerse = { verseId ->
                                    repository.addRecentlyViewed(verseId)
                                    navigateTo(Screen.VerseReader(verseId))
                                },
                                onBack = { navigateBack() }
                            )
                        }

                        is Screen.Bookmarks -> {
                            BackHandler { navigateBack() }
                            val bookmarkIds = remember(bookmarkRefreshKey) { repository.getBookmarks() }
                            val bookmarkedVerses = remember(bookmarkRefreshKey) {
                                repository.loadVerses().filter { bookmarkIds.contains(it.id) }
                            }
                            BookmarksScreen(
                                verses = bookmarkedVerses,
                                onSelectVerse = { verseId ->
                                    repository.addRecentlyViewed(verseId)
                                    navigateTo(Screen.VerseReader(verseId))
                                },
                                onBack = { navigateBack() }
                            )
                        }

                        is Screen.RecentlyViewed -> {
                            BackHandler { navigateBack() }
                            val recentIds = remember { repository.getRecentlyViewed() }
                            val recentVerses = remember {
                                val all = repository.loadVerses().associateBy { it.id }
                                recentIds.mapNotNull { all[it] }
                            }
                            RecentlyViewedScreen(
                                verses = recentVerses,
                                onSelectVerse = { verseId ->
                                    navigateTo(Screen.VerseReader(verseId))
                                },
                                onBack = { navigateBack() }
                            )
                        }

                        is Screen.Settings -> {
                            BackHandler { navigateBack() }
                            val validationResult = remember { repository.validateDataset() }
                            SettingsScreen(
                                currentTheme = currentTheme,
                                voiceGender = voiceGender,
                                audioSpeed = audioSpeed,
                                isMadhurataEnabled = isMadhurataEnabled,
                                tanpuraVolume = tanpuraVolume,
                                isIntroEnabled = isIntroEnabled,
                                validationResult = validationResult,
                                onSelectTheme = { theme ->
                                    currentTheme = theme
                                    repository.setAppTheme(theme)
                                },
                                onSelectVoiceGender = { gender ->
                                    voiceGender = gender
                                    repository.setVoiceGender(gender)
                                },
                                onSelectAudioSpeed = { speed ->
                                    audioSpeed = speed
                                    repository.setAudioSpeed(speed)
                                },
                                onToggleMadhurata = { enabled ->
                                    isMadhurataEnabled = enabled
                                    repository.setMadhurataEnabled(enabled)
                                },
                                onTanpuraVolumeChange = { vol ->
                                    tanpuraVolume = vol
                                    repository.setTanpuraVolume(vol)
                                },
                                onToggleIntro = { enabled ->
                                    isIntroEnabled = enabled
                                    repository.setIntroEnabled(enabled)
                                },
                                onBack = { navigateBack() }
                            )
                        }
                    }

                    // 9:16 Status card generation modal
                    statusModalVerse?.let { verse ->
                        StatusCardModal(
                            verse = verse,
                            onDismiss = { statusModalVerse = null }
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioEngine.release()
    }
}
