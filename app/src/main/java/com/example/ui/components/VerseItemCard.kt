package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Verse
import com.example.ui.theme.GitaDeepGold
import com.example.ui.theme.MartelFontFamily
import com.example.ui.theme.RozhaOneFontFamily
import com.example.ui.theme.YatraOneFontFamily

@Composable
fun VerseItemCard(
    verse: Verse,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("verse_item_${verse.chapter}_${verse.verse}")
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, GitaDeepGold.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "श्लोक ${verse.chapter}.${verse.verse}",
                    fontFamily = YatraOneFontFamily,
                    fontSize = 14.sp,
                    color = GitaDeepGold,
                    fontWeight = FontWeight.SemiBold
                )

                if (verse.verified) {
                    Text(
                        text = "✓ प्रमाणित",
                        fontSize = 11.sp,
                        fontFamily = MartelFontFamily,
                        color = GitaDeepGold.copy(alpha = 0.8f)
                    )
                } else {
                    Text(
                        text = "सत्यापन लंबित",
                        fontSize = 11.sp,
                        fontFamily = MartelFontFamily,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = verse.sanskrit.lineSequence().firstOrNull() ?: verse.sanskrit,
                fontFamily = MartelFontFamily,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = verse.hindiMeaning,
                fontFamily = MartelFontFamily,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
        }
    }
}
