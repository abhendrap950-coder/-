package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.Verse
import com.example.ui.components.ArchaeologicalHeader
import com.example.ui.components.SacredFeatureCard
import com.example.ui.components.VedicDivider
import com.example.ui.theme.GitaDeepGold
import com.example.ui.theme.GitaGoldBright
import com.example.ui.theme.MartelFontFamily
import com.example.ui.theme.RozhaOneFontFamily
import com.example.ui.theme.YatraOneFontFamily

@Composable
fun HomeScreen(
    dailyVerse: Verse?,
    onNavigateToChapters: () -> Unit,
    onNavigateToVerse: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToBookmarks: () -> Unit,
    onNavigateToRecent: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onOpenStatusModal: (Verse) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Hero Section with Krishna background art
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_krishna_chariot),
                    contentDescription = "भगवान श्रीकृष्ण रथ",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Sacred vignette overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.4f),
                                    Color.Black.copy(alpha = 0.85f),
                                    MaterialTheme.colorScheme.background
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "॥ ॐ ॥",
                            fontFamily = YatraOneFontFamily,
                            color = GitaGoldBright,
                            fontSize = 18.sp
                        )
                        IconButton(
                            onClick = onNavigateToSettings,
                            modifier = Modifier.testTag("settings_top_button")
                        ) {
                            Text(text = "⚙️", fontSize = 20.sp)
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "॥ श्रीमद्भगवद्गीता ॥",
                            fontFamily = RozhaOneFontFamily,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = GitaGoldBright,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "श्रीकृष्णार्जुन संवाद",
                            fontFamily = YatraOneFontFamily,
                            fontSize = 16.sp,
                            color = GitaDeepGold,
                            textAlign = TextAlign.Center
                        )
                    }

                    Text(
                        text = "18 अध्याय • 700 श्लोक • प्रामाणिक संस्कृत पाठ",
                        fontFamily = MartelFontFamily,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Daily Verse Card (आज का श्लोक)
        if (dailyVerse != null) {
            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("daily_verse_card")
                            .clickable { onNavigateToVerse(dailyVerse.id) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, GitaDeepGold.copy(alpha = 0.6f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🔱 ", fontSize = 16.sp)
                                    Text(
                                        text = "आज का श्लोक",
                                        fontFamily = RozhaOneFontFamily,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GitaDeepGold
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = "अध्याय ${dailyVerse.chapter}.${dailyVerse.verse}",
                                        fontFamily = YatraOneFontFamily,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = dailyVerse.sanskrit,
                                fontFamily = MartelFontFamily,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 22.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = dailyVerse.hindiMeaning,
                                fontFamily = MartelFontFamily,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 19.sp,
                                maxLines = 3
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = { onOpenStatusModal(dailyVerse) },
                                    modifier = Modifier.testTag("daily_verse_status_btn")
                                ) {
                                    Text(
                                        text = "📱 Status बनाएं",
                                        fontFamily = YatraOneFontFamily,
                                        fontSize = 13.sp,
                                        color = GitaDeepGold
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = { onNavigateToVerse(dailyVerse.id) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = GitaDeepGold,
                                        contentColor = Color.Black
                                    )
                                ) {
                                    Text(
                                        text = "श्लोक पढ़ें ▶",
                                        fontFamily = YatraOneFontFamily,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            VedicDivider(ornament = "॥ पवित्र अनुभाग ॥")
        }

        // Main Navigation Cards
        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SacredFeatureCard(
                    title = "📖 गीता पढ़ें",
                    subtitle = "18 अध्यायों में प्रवेश • सम्पूर्ण 700 श्लोक",
                    iconText = "📜",
                    onClick = onNavigateToChapters,
                    testTag = "nav_chapters_card"
                )

                SacredFeatureCard(
                    title = "🔍 श्लोक खोजें",
                    subtitle = "शब्द, अध्याय, श्लोक संख्या या विषय (कर्म, ज्ञान, भक्ति)",
                    iconText = "🔍",
                    onClick = onNavigateToSearch,
                    testTag = "nav_search_card"
                )

                SacredFeatureCard(
                    title = "⭐ मेरे पसंदीदा",
                    subtitle = "सहेजे गए दिव्य श्लोक (Saved Bookmarks)",
                    iconText = "⭐",
                    onClick = onNavigateToBookmarks,
                    testTag = "nav_bookmarks_card"
                )

                SacredFeatureCard(
                    title = "📜 हाल में पढ़े गए",
                    subtitle = "पठन इतिहास (Recently Viewed Verses)",
                    iconText = "🕰️",
                    onClick = onNavigateToRecent,
                    testTag = "nav_recent_card"
                )
            }
        }
    }
}
