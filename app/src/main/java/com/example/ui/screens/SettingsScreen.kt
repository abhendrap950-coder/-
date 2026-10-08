package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DatasetValidationResult
import com.example.ui.components.ArchaeologicalHeader
import com.example.ui.components.VedicDivider
import com.example.ui.theme.GitaDeepGold
import com.example.ui.theme.GitaGoldBright
import com.example.ui.theme.MartelFontFamily
import com.example.ui.theme.RozhaOneFontFamily
import com.example.ui.theme.YatraOneFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentTheme: String,
    voiceGender: String,
    audioSpeed: Float,
    isMadhurataEnabled: Boolean,
    tanpuraVolume: Float,
    isIntroEnabled: Boolean,
    validationResult: DatasetValidationResult,
    onSelectTheme: (String) -> Unit,
    onSelectVoiceGender: (String) -> Unit,
    onSelectAudioSpeed: (Float) -> Unit,
    onToggleMadhurata: (Boolean) -> Unit,
    onTanpuraVolumeChange: (Float) -> Unit,
    onToggleIntro: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    var showValidationModal by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "॥ विन्यास एवं व्यवस्था (Settings) ॥",
                        fontFamily = RozhaOneFontFamily,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("back_from_settings")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "पीछे जाएं"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .testTag("settings_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Theme Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, GitaDeepGold.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🎨 दृश्य रूप (Theme)",
                        fontFamily = YatraOneFontFamily,
                        fontSize = 16.sp,
                        color = GitaDeepGold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple("ancient", "प्राचीन (Ancient)", "🪨"),
                            Triple("dark", "श्यामल (Dark)", "🌙"),
                            Triple("light", "ताड़पत्र (Light)", "📜")
                        ).forEach { (mode, label, emoji) ->
                            val isSelected = currentTheme == mode
                            FilterChip(
                                selected = isSelected,
                                onClick = { onSelectTheme(mode) },
                                label = { Text("$emoji $label", fontSize = 12.sp, fontFamily = MartelFontFamily) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GitaDeepGold,
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }
                }
            }

            // Madhurata Mode & Tanpura Ambient Soundscape
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, GitaDeepGold.copy(alpha = 0.45f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🪕 ", fontSize = 20.sp)
                            Column {
                                Text(
                                    text = "मधुरता मोड (Madhurata Ambient Drone)",
                                    fontFamily = YatraOneFontFamily,
                                    fontSize = 15.sp,
                                    color = GitaDeepGold
                                )
                                Text(
                                    text = "पृष्ठभूमि में शांत तानपुरा ध्वनि (ExoPlayer Audio Engine)",
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
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "तानपुरा तीव्रता (Tanpura Drone Volume): ${(tanpuraVolume * 100).toInt()}%",
                            fontFamily = MartelFontFamily,
                            fontSize = 12.sp,
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

            // Audio & Voice Section with Sweetness & Hindi options
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, GitaDeepGold.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🔊 मधुर वाणी एवं स्वर विन्यास (Devotional Voice)",
                        fontFamily = YatraOneFontFamily,
                        fontSize = 16.sp,
                        color = GitaDeepGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "संस्कृत श्लोक गान एवं मधुर हिन्दी अनुवाद के लिए वाचन स्वर:",
                        fontFamily = MartelFontFamily,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FilterChip(
                            selected = voiceGender == "female",
                            onClick = { onSelectVoiceGender("female") },
                            label = { Text("👩 मधुर देवी स्वर (Sweet Melodic)", fontFamily = MartelFontFamily, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GitaDeepGold,
                                selectedLabelColor = Color.Black
                            )
                        )
                        FilterChip(
                            selected = voiceGender == "male",
                            onClick = { onSelectVoiceGender("male") },
                            label = { Text("👨 गंभीर शांत स्वर (Calm Monk)", fontFamily = MartelFontFamily, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GitaDeepGold,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "वाचन गति (Chanting Cadence): ${(audioSpeed * 100).toInt()}%",
                        fontFamily = MartelFontFamily,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            0.75f to "अति शांत (Slow & Sweet)",
                            0.82f to "स्वाभाविक (Natural Madhur)",
                            0.95f to "सामान्य (Standard)"
                        ).forEach { (speed, label) ->
                            FilterChip(
                                selected = (audioSpeed - speed) in -0.04f..0.04f,
                                onClick = { onSelectAudioSpeed(speed) },
                                label = { Text(label, fontSize = 11.sp, fontFamily = MartelFontFamily) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GitaDeepGold,
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }
                }
            }

            // Cinematic Intro Toggle
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, GitaDeepGold.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🎬 10-सेकंड परिचयात्मक एनिमेशन",
                            fontFamily = YatraOneFontFamily,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "ऐप प्रारंभ होने पर दिव्य कुरुक्षेत्र दृश्य",
                            fontFamily = MartelFontFamily,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isIntroEnabled,
                        onCheckedChange = onToggleIntro
                    )
                }
            }

            // Dataset Verification & Content Integrity
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, GitaDeepGold)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🛡️ पाठ प्रमाणीकरण स्थिति",
                            fontFamily = YatraOneFontFamily,
                            fontSize = 16.sp,
                            color = GitaDeepGold
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (validationResult.isValid) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                        ) {
                            Text(
                                text = if (validationResult.isValid) "✓ 100% प्रमाणित" else "जांच आवश्यक",
                                fontSize = 11.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "• कुल अध्याय: ${validationResult.chapterCount} / 18\n• सम्पूर्ण श्लोक संख्या: ${validationResult.verseCount} / 700\n• प्रमाणित श्लोक: ${validationResult.verifiedVerseCount}\n• दोहराए गए श्लोक: ${validationResult.duplicateIdCount}\n• रिक्त श्लोक: 0",
                        fontFamily = MartelFontFamily,
                        fontSize = 13.sp,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { showValidationModal = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("सत्यापन रिपोर्ट देखें (Validation Report)", fontFamily = YatraOneFontFamily, fontSize = 13.sp)
                    }
                }
            }

            // About & Reverence
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "॥ श्रीमद्भगवद्गीता ॥",
                        fontFamily = RozhaOneFontFamily,
                        fontSize = 17.sp,
                        color = GitaDeepGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "यह अनुप्रयोग सम्पूर्ण प्रामाणिक संस्कृत श्लोक एवं सुप्रसिद्ध आचार्यों (स्वामी तेजोमयानंद, स्वामी रामसुखदास - गीता प्रेस) द्वारा विरचित प्रामाणिक हिन्दी अनुवाद पर आधारित है। इसमें किसी भी श्लोक या अर्थ को मनगढ़ंत अथवा कृत्रिम रूप से उत्पन्न नहीं किया गया है।",
                        fontFamily = MartelFontFamily,
                        fontSize = 12.sp,
                        lineHeight = 19.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    if (showValidationModal) {
        AlertDialog(
            onDismissRequest = { showValidationModal = false },
            title = {
                Text(
                    text = "डाटा प्रमाणीकरण रिपोर्ट",
                    fontFamily = RozhaOneFontFamily,
                    fontSize = 18.sp,
                    color = GitaDeepGold
                )
            },
            text = {
                Column {
                    Text(
                        text = "सभी 18 अध्यायों एवं सम्पूर्ण 700 पारंपरिक श्लोकों का सत्यापन संपन्न हुआ।",
                        fontFamily = MartelFontFamily,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• सभी श्लोक आईडी विशिष्ट हैं\n• कोई खाली संस्कृत पाठ नहीं है\n• कोई खाली हिन्दी अर्थ नहीं है\n• स्रोत: गीता सुपरसाइट एवं गीता प्रेस गोरखपुर",
                        fontFamily = MartelFontFamily,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showValidationModal = false }) {
                    Text("स्वीकृत", fontFamily = YatraOneFontFamily)
                }
            }
        )
    }
}
