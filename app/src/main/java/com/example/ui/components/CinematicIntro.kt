package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.GitaDeepGold
import com.example.ui.theme.GitaGoldBright
import com.example.ui.theme.RozhaOneFontFamily
import com.example.ui.theme.YatraOneFontFamily
import kotlinx.coroutines.delay

@Composable
fun CinematicIntro(
    onFinished: () -> Unit,
    onSkip: () -> Unit
) {
    var elapsedSeconds by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        val intervalMs = 50L
        val totalMs = 10_000L // EXACTLY 10 SECONDS
        var currentMs = 0L

        while (currentMs < totalMs) {
            delay(intervalMs)
            currentMs += intervalMs
            elapsedSeconds = currentMs / 1000f
        }
        onFinished()
    }

    // 0-2s: Black screen + golden light appears
    // 2-5s: Kurukshetra atmosphere & chariot approaching
    // 5-8s: Bhagwan Shri Krishna dignified darshan as charioteer
    // 8-10s: Manuscript golden reveal with ॥ श्रीमद्भगवद्गीता ॥ श्रीकृष्णार्जुन संवाद
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Krishna / Kurukshetra image visible from 2s to 10s
        if (elapsedSeconds >= 2.0f) {
            val imageAlpha = when {
                elapsedSeconds in 2f..4f -> (elapsedSeconds - 2f) / 2f
                elapsedSeconds in 4f..8.5f -> 1f
                else -> 1f - ((elapsedSeconds - 8.5f) / 1.5f).coerceIn(0f, 0.4f)
            }
            val scale = 1.0f + (elapsedSeconds - 2f) * 0.035f

            Image(
                painter = painterResource(id = R.drawable.img_krishna_chariot),
                contentDescription = "Bhagwan Shri Krishna Kurukshetra Charioteer",
                modifier = Modifier
                    .fillMaxSize()
                    .scale(scale)
                    .alpha(imageAlpha),
                contentScale = ContentScale.Crop
            )

            // Cinematic dark gradient vignette
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.65f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )
        }

        // Overlay Texts according to 10-second timeline
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top sacred symbol
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (elapsedSeconds >= 1.0f) {
                    val omAlpha = ((elapsedSeconds - 1f) / 1.5f).coerceIn(0f, 1f)
                    Text(
                        text = "॥ ॐ श्रीकृष्णाय नमः ॥",
                        fontFamily = YatraOneFontFamily,
                        fontSize = 16.sp,
                        color = GitaGoldBright.copy(alpha = omAlpha),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Central dramatic caption
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                when {
                    elapsedSeconds in 0.5f..2.5f -> {
                        val a = if (elapsedSeconds < 1.5f) (elapsedSeconds - 0.5f) else (2.5f - elapsedSeconds)
                        Text(
                            text = "धर्मक्षेत्रे कुरुक्षेत्रे...",
                            fontFamily = RozhaOneFontFamily,
                            fontSize = 26.sp,
                            color = GitaGoldBright.copy(alpha = a.coerceIn(0f, 1f)),
                            textAlign = TextAlign.Center
                        )
                    }
                    elapsedSeconds in 3.0f..6.5f -> {
                        val a = if (elapsedSeconds < 4.0f) (elapsedSeconds - 3.0f) else if (elapsedSeconds > 5.5f) (6.5f - elapsedSeconds) else 1f
                        Text(
                            text = "पार्थ सारथि भगवान श्रीकृष्ण",
                            fontFamily = RozhaOneFontFamily,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = GitaGoldBright.copy(alpha = a.coerceIn(0f, 1f)),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "परम दिव्य उपदेश का प्रादुर्भाव",
                            fontFamily = YatraOneFontFamily,
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = a.coerceIn(0f, 0.9f)),
                            textAlign = TextAlign.Center
                        )
                    }
                    elapsedSeconds >= 7.5f -> {
                        val finalAlpha = ((elapsedSeconds - 7.5f) / 1.5f).coerceIn(0f, 1f)
                        Box(
                            modifier = Modifier
                                .background(
                                    Color.Black.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "॥ श्रीमद्भगवद्गीता ॥",
                                    fontFamily = RozhaOneFontFamily,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GitaGoldBright.copy(alpha = finalAlpha),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "श्रीकृष्णार्जुन संवाद",
                                    fontFamily = YatraOneFontFamily,
                                    fontSize = 20.sp,
                                    color = GitaDeepGold.copy(alpha = finalAlpha),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Bottom controls: Timer indicator & Skip Intro button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Progress seconds indicator
                val remainingSec = (10 - elapsedSeconds.toInt()).coerceAtLeast(0)
                Text(
                    text = "${remainingSec}s",
                    fontFamily = YatraOneFontFamily,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )

                Button(
                    onClick = onSkip,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black.copy(alpha = 0.6f),
                        contentColor = GitaGoldBright
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(
                        text = "प्रवेश करें (Skip Intro)",
                        fontFamily = YatraOneFontFamily,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
