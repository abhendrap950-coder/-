package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioMode
import com.example.audio.AudioPlayerState
import com.example.audio.PlaybackProgress
import com.example.data.Verse
import com.example.ui.components.VedicDivider
import com.example.ui.theme.GitaDeepGold
import com.example.ui.theme.GitaGoldBright
import com.example.ui.theme.GitaGoldWarm
import com.example.ui.theme.MartelFontFamily
import com.example.ui.theme.RozhaOneFontFamily
import com.example.ui.theme.YatraOneFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerseReaderScreen(
    verse: Verse?,
    isBookmarked: Boolean,
    isPlaying: Boolean,
    isPaused: Boolean,
    playerState: AudioPlayerState,
    errorMessage: String?,
    currentPlayingMode: AudioMode,
    playbackProgress: PlaybackProgress,
    isMadhurataEnabled: Boolean,
    tanpuraVolume: Float,
    sanskritFontSize: Int,
    hindiFontSize: Int,
    onToggleBookmark: () -> Unit,
    onPlaySanskritAudio: () -> Unit,
    onPlayHindiAudio: () -> Unit,
    onPauseAudio: () -> Unit,
    onResumeAudio: () -> Unit,
    onStopAudio: () -> Unit,
    onRetryAudio: () -> Unit,
    onToggleMadhurata: (Boolean) -> Unit,
    onTanpuraVolumeChange: (Float) -> Unit,
    onNextVerse: () -> Unit,
    onPreviousVerse: () -> Unit,
    onOpenStatusModal: () -> Unit,
    onBack: () -> Unit,
    onAdjustSanskritFontSize: (Int) -> Unit,
    onAdjustHindiFontSize: (Int) -> Unit
) {
    val context = LocalContext.current
    var showFontControls by remember { mutableStateOf(false) }
    var showAudioSettings by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (verse != null) "अध्याय ${verse.chapter} — ${verse.chapterName}" else "श्लोक",
                        fontFamily = RozhaOneFontFamily,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("back_from_reader")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "पीछे जाएं"
                        )
                    }
                },
                actions = {
                    // Madhurata drone toggle
                    IconButton(
                        onClick = { showAudioSettings = !showAudioSettings },
                        modifier = Modifier.testTag("toggle_madhurata_btn")
                    ) {
                        Text(
                            text = if (isMadhurataEnabled) "🪕" else "🔇",
                            fontSize = 18.sp
                        )
                    }

                    // Font control toggle
                    IconButton(
                        onClick = { showFontControls = !showFontControls },
                        modifier = Modifier.testTag("toggle_font_controls")
                    ) {
                        Text(text = "Aa", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = GitaDeepGold)
                    }

                    // Bookmark button
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.testTag("bookmark_toggle_btn")
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = if (isBookmarked) "पसंदीदा से हटाएं" else "पसंदीदा बनाएं",
                            tint = if (isBookmarked) GitaGoldBright else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Share button
                    IconButton(
                        onClick = {
                            if (verse != null) {
                                val shareText = "॥ श्रीमद्भगवद्गीता ॥\nअध्याय ${verse.chapter}, श्लोक ${verse.verse}\n\n${verse.sanskrit}\n\nहिंदी अर्थ:\n${verse.hindiMeaning}\n\n-- श्रीमद्भगवद्गीता (श्रीकृष्णार्जुन संवाद)"
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "श्लोक साझा करें"))
                            }
                        },
                        modifier = Modifier.testTag("share_verse_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "साझा करें"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onPreviousVerse,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("prev_verse_btn")
                    ) {
                        Text("◀ पिछला", fontFamily = YatraOneFontFamily, fontSize = 13.sp)
                    }

                    Button(
                        onClick = onOpenStatusModal,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.testTag("open_status_card_btn")
                    ) {
                        Text("📱 Status बनाएं", fontFamily = YatraOneFontFamily, fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = onNextVerse,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("next_verse_btn")
                    ) {
                        Text("अगला ▶", fontFamily = YatraOneFontFamily, fontSize = 13.sp)
                    }
                }
            }
        }
    ) { innerPadding ->
        if (verse == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "इस श्लोक का प्रमाणित पाठ अभी उपलब्ध नहीं है।",
                    fontFamily = MartelFontFamily,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(24.dp)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Clear Error Display Banner with Retry
                if (errorMessage != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "⚠️ " + errorMessage,
                                    fontFamily = MartelFontFamily,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = onRetryAudio,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("पुनः प्रयास", fontSize = 11.sp, fontFamily = YatraOneFontFamily)
                            }
                        }
                    }
                }

                // Madhurata Ambient Soundscape Control Banner
                AnimatedVisibility(visible = showAudioSettings) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, GitaDeepGold.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🪕 ", fontSize = 18.sp)
                                    Column {
                                        Text(
                                            text = "मधुरता मोड (Madhurata Ambient Drone)",
                                            fontFamily = YatraOneFontFamily,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = GitaDeepGold
                                        )
                                        Text(
                                            text = "पृष्ठभूमि में शांत तानपुरा ध्वनि (ExoPlayer)",
                                            fontFamily = MartelFontFamily,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Switch(
                                    checked = isMadhurataEnabled,
                                    onCheckedChange = onToggleMadhurata
                                )
                            }

                            if (isMadhurataEnabled) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "तानपुरा तीव्रता (Volume): ${(tanpuraVolume * 100).toInt()}%",
                                    fontSize = 12.sp,
                                    fontFamily = MartelFontFamily,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Slider(
                                    value = tanpuraVolume,
                                    onValueChange = onTanpuraVolumeChange,
                                    valueRange = 0.05f..0.8f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = GitaDeepGold,
                                        activeTrackColor = GitaDeepGold
                                    )
                                )
                            }
                        }
                    }
                }

                // Font Size Adjustment Controls
                AnimatedVisibility(visible = showFontControls) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "फॉन्ट आकार समायोजन",
                                fontFamily = YatraOneFontFamily,
                                fontSize = 14.sp,
                                color = GitaDeepGold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("संस्कृत आकार: ${sanskritFontSize}sp", fontSize = 12.sp, fontFamily = MartelFontFamily)
                                Row {
                                    TextButton(onClick = { onAdjustSanskritFontSize((sanskritFontSize - 2).coerceAtLeast(14)) }) { Text("A-") }
                                    TextButton(onClick = { onAdjustSanskritFontSize((sanskritFontSize + 2).coerceAtMost(32)) }) { Text("A+") }
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("हिन्दी अर्थ आकार: ${hindiFontSize}sp", fontSize = 12.sp, fontFamily = MartelFontFamily)
                                Row {
                                    TextButton(onClick = { onAdjustHindiFontSize((hindiFontSize - 2).coerceAtLeast(12)) }) { Text("A-") }
                                    TextButton(onClick = { onAdjustHindiFontSize((hindiFontSize + 2).coerceAtMost(26)) }) { Text("A+") }
                                }
                            }
                        }
                    }
                }

                // Sanskrit Shloka Card with Synchronized Audio Controls (Play, Pause, Resume, Stop)
                val isSanskritActive = currentPlayingMode == AudioMode.SANSKRIT && (isPlaying || isPaused)
                val isSanskritPlaying = isPlaying && currentPlayingMode == AudioMode.SANSKRIT
                val isSanskritPaused = isPaused && currentPlayingMode == AudioMode.SANSKRIT
                val isSanskritLoading = playerState == AudioPlayerState.LOADING && currentPlayingMode == AudioMode.SANSKRIT

                val borderHighlightColor by animateColorAsState(
                    targetValue = if (isSanskritActive) GitaGoldBright else GitaDeepGold.copy(alpha = 0.5f),
                    label = "borderHighlight"
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSanskritActive) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f) else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(if (isSanskritActive) 2.dp else 1.dp, borderHighlightColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "श्लोक ${verse.chapter}.${verse.verse}",
                                fontFamily = RozhaOneFontFamily,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = GitaDeepGold
                            )

                            if (isSanskritActive && isMadhurataEnabled) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = GitaGoldWarm.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "🪕 तानपुरा स्वर",
                                        fontFamily = YatraOneFontFamily,
                                        fontSize = 11.sp,
                                        color = GitaGoldBright,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Synchronized Sanskrit lines
                        val sanskritLines = verse.sanskrit.split("\n").filter { it.isNotBlank() }
                        sanskritLines.forEachIndexed { index, line ->
                            val isCurrentLineActive = isSanskritPlaying && (playbackProgress.activeLineIndex == index)
                            val lineTextColor by animateColorAsState(
                                targetValue = if (isCurrentLineActive) GitaGoldBright else MaterialTheme.colorScheme.onSurface,
                                label = "lineTextColor"
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isCurrentLineActive) GitaDeepGold.copy(alpha = 0.18f) else Color.Transparent)
                                    .padding(vertical = 4.dp, horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = line.trim(),
                                    fontFamily = MartelFontFamily,
                                    fontSize = sanskritFontSize.sp,
                                    fontWeight = if (isCurrentLineActive) FontWeight.ExtraBold else FontWeight.Bold,
                                    lineHeight = (sanskritFontSize * 1.5).sp,
                                    color = lineTextColor,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Sanskrit Audio Controls: Play, Pause, Resume, Stop with clear Loading Indicator
                        if (isSanskritLoading) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = GitaDeepGold,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "संस्कृत श्लोक गान लोड हो रहा है...",
                                    fontFamily = MartelFontFamily,
                                    fontSize = 13.sp,
                                    color = GitaDeepGold
                                )
                            }
                        } else if (isSanskritActive) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isSanskritPlaying) {
                                    Button(
                                        onClick = onPauseAudio,
                                        shape = RoundedCornerShape(20.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = GitaGoldWarm, contentColor = Color.Black),
                                        modifier = Modifier.testTag("pause_sanskrit_audio_btn")
                                    ) {
                                        Text("⏸ विश्राम (Pause)", fontFamily = YatraOneFontFamily, fontSize = 13.sp)
                                    }
                                } else if (isSanskritPaused) {
                                    Button(
                                        onClick = onResumeAudio,
                                        shape = RoundedCornerShape(20.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = GitaDeepGold, contentColor = Color.Black),
                                        modifier = Modifier.testTag("resume_sanskrit_audio_btn")
                                    ) {
                                        Text("▶ पुनः प्रारंभ (Resume)", fontFamily = YatraOneFontFamily, fontSize = 13.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                OutlinedButton(
                                    onClick = onStopAudio,
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier.testTag("stop_sanskrit_audio_btn")
                                ) {
                                    Text("⏹ समाप्त (Stop)", fontFamily = YatraOneFontFamily, fontSize = 13.sp)
                                }
                            }
                        } else {
                            Button(
                                onClick = onPlaySanskritAudio,
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GitaDeepGold,
                                    contentColor = Color.Black
                                ),
                                modifier = Modifier.testTag("play_sanskrit_audio_btn")
                            ) {
                                Text(
                                    text = "🔊 संस्कृत श्लोक गान सुनें",
                                    fontFamily = YatraOneFontFamily,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Transliteration (if available)
                if (verse.transliteration.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "लिप्यन्तरण (Transliteration)",
                                fontFamily = YatraOneFontFamily,
                                fontSize = 13.sp,
                                color = GitaDeepGold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = verse.transliteration,
                                fontFamily = MartelFontFamily,
                                fontSize = 13.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Verified Hindi Meaning with Audio Controls (Play, Pause, Resume, Stop)
                val isHindiActive = currentPlayingMode == AudioMode.HINDI && (isPlaying || isPaused)
                val isHindiPlaying = isPlaying && currentPlayingMode == AudioMode.HINDI
                val isHindiPaused = isPaused && currentPlayingMode == AudioMode.HINDI
                val isHindiLoading = playerState == AudioPlayerState.LOADING && currentPlayingMode == AudioMode.HINDI

                val hindiCardBorderColor by animateColorAsState(
                    targetValue = if (isHindiActive) GitaGoldBright else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                    label = "hindiBorderColor"
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isHindiActive) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f) else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(if (isHindiActive) 2.dp else 1.dp, hindiCardBorderColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🕉️ ", fontSize = 14.sp)
                                Text(
                                    text = "हिंदी अर्थ",
                                    fontFamily = RozhaOneFontFamily,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GitaDeepGold
                                )
                            }

                            if (isHindiLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = GitaDeepGold, strokeWidth = 2.dp)
                            } else if (isHindiActive) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isHindiPlaying) {
                                        IconButton(
                                            onClick = onPauseAudio,
                                            modifier = Modifier.size(36.dp).testTag("pause_hindi_audio_btn")
                                        ) {
                                            Text(text = "⏸", fontSize = 16.sp)
                                        }
                                    } else if (isHindiPaused) {
                                        IconButton(
                                            onClick = onResumeAudio,
                                            modifier = Modifier.size(36.dp).testTag("resume_hindi_audio_btn")
                                        ) {
                                            Text(text = "▶", fontSize = 16.sp)
                                        }
                                    }
                                    IconButton(
                                        onClick = onStopAudio,
                                        modifier = Modifier.size(36.dp).testTag("stop_hindi_audio_btn")
                                    ) {
                                        Text(text = "⏹", fontSize = 16.sp)
                                    }
                                }
                            } else {
                                Button(
                                    onClick = onPlayHindiAudio,
                                    shape = RoundedCornerShape(18.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("play_hindi_audio_btn")
                                ) {
                                    Text(
                                        text = "🎙️ मधुर हिन्दी अनुवाद सुनें",
                                        fontFamily = YatraOneFontFamily,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        if (isHindiActive && isMadhurataEnabled) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "🪕 तानपुरा परिवेश के साथ मधुर हिन्दी वाचन प्रवाहित हो रहा है...",
                                fontFamily = MartelFontFamily,
                                fontSize = 11.sp,
                                color = GitaGoldWarm
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Synchronized Hindi sentences
                        val hindiSentences = verse.hindiMeaning.split("।", ".").filter { it.isNotBlank() }
                        if (isHindiActive && hindiSentences.size > 1) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                hindiSentences.forEachIndexed { sIdx, sentence ->
                                    val isSentenceActive = isHindiPlaying && (playbackProgress.activeLineIndex == sIdx)
                                    val sentenceColor by animateColorAsState(
                                        targetValue = if (isSentenceActive) GitaGoldBright else MaterialTheme.colorScheme.onSurface,
                                        label = "sentenceColor"
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSentenceActive) GitaDeepGold.copy(alpha = 0.15f) else Color.Transparent)
                                            .padding(horizontal = 6.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = sentence.trim() + "।",
                                            fontFamily = MartelFontFamily,
                                            fontSize = hindiFontSize.sp,
                                            fontWeight = if (isSentenceActive) FontWeight.Bold else FontWeight.Normal,
                                            lineHeight = (hindiFontSize * 1.55).sp,
                                            color = sentenceColor
                                        )
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = verse.hindiMeaning,
                                fontFamily = MartelFontFamily,
                                fontSize = hindiFontSize.sp,
                                lineHeight = (hindiFontSize * 1.55).sp,
                                color = if (isHindiActive) GitaGoldBright else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Simple Bhavarth (if present)
                if (verse.simpleBhavarth.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        border = BorderStroke(1.dp, GitaDeepGold.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "📜 ", fontSize = 14.sp)
                                Text(
                                    text = "सरल भावार्थ",
                                    fontFamily = RozhaOneFontFamily,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = verse.simpleBhavarth,
                                fontFamily = MartelFontFamily,
                                fontSize = hindiFontSize.sp,
                                lineHeight = (hindiFontSize * 1.55).sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Verification and Source Badge
                VedicDivider(ornament = "॥ प्रामाणिकता ॥")

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "स्रोत: ${verse.source}",
                                fontSize = 11.sp,
                                fontFamily = MartelFontFamily,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "प्रमाणीकरण दिनांक: ${verse.verificationDate}",
                                fontSize = 10.sp,
                                fontFamily = MartelFontFamily,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GitaDeepGold.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "✓ प्रमाणित पाठ",
                                fontSize = 11.sp,
                                fontFamily = YatraOneFontFamily,
                                color = GitaDeepGold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
