package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import com.example.data.Verse
import com.example.ui.theme.*
import java.io.File
import java.io.FileOutputStream

enum class StatusCardStyle(val title: String, val bgDark: Boolean) {
    PARCHMENT("प्राचीन ताड़पत्र", false),
    TEMPLE_STONE("मंदिर शिला", true),
    COPPER_PLATE("ताम्रपत्र", true),
    KURUKSHETRA("कुरुक्षेत्र", true),
    KRISHNA("दिव्य श्याम", true)
}

@Composable
fun StatusCardModal(
    verse: Verse,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedStyle by remember { mutableStateOf(StatusCardStyle.PARCHMENT) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, GitaDeepGold.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .testTag("status_generator_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📱 9:16 स्टेटस कार्ड",
                        fontFamily = YatraOneFontFamily,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_status_modal")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "बंद करें"
                        )
                    }
                }

                // Style Selector Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    StatusCardStyle.values().forEach { style ->
                        val isSelected = selectedStyle == style
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedStyle = style },
                            label = {
                                Text(
                                    text = style.title,
                                    fontSize = 11.sp,
                                    fontFamily = MartelFontFamily
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GitaDeepGold,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                // Status 9:16 Preview Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    StatusPreviewCard(
                        verse = verse,
                        style = selectedStyle,
                        modifier = Modifier
                            .aspectRatio(9f / 16f)
                            .fillMaxHeight()
                    )
                }

                if (statusMessage != null) {
                    Text(
                        text = statusMessage ?: "",
                        fontFamily = MartelFontFamily,
                        fontSize = 12.sp,
                        color = GitaDeepGold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val file = generateAndSaveStatusBitmap(context, verse, selectedStyle)
                            if (file != null) {
                                statusMessage = "स्टेटस छवि सुरक्षित की गई!"
                            } else {
                                statusMessage = "स्टेटस तैयार नहीं हो सका। कृपया पुनः प्रयास करें।"
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_status_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("सहेजें", fontFamily = YatraOneFontFamily, fontSize = 13.sp)
                    }

                    Button(
                        onClick = {
                            val file = generateAndSaveStatusBitmap(context, verse, selectedStyle)
                            if (file != null) {
                                shareImage(context, file, verse)
                            } else {
                                statusMessage = "स्टेटस तैयार नहीं हो सका। कृपया पुनः प्रयास करें।"
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("share_status_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = GitaDeepGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("साझा करें", fontFamily = YatraOneFontFamily, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun StatusPreviewCard(
    verse: Verse,
    style: StatusCardStyle,
    modifier: Modifier = Modifier
) {
    val bgColor = when (style) {
        StatusCardStyle.PARCHMENT -> GitaParchmentBg
        StatusCardStyle.TEMPLE_STONE -> StatusStoneBg
        StatusCardStyle.COPPER_PLATE -> StatusCopperBg
        StatusCardStyle.KURUKSHETRA -> StatusKurukshetraBg
        StatusCardStyle.KRISHNA -> StatusKrishnaBg
    }

    val textColor = when (style) {
        StatusCardStyle.PARCHMENT -> GitaParchmentText
        else -> GitaTextPrimary
    }

    val goldAccent = GitaDeepGold

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(2.dp, goldAccent.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "॥ श्रीमद्भगवद्गीता ॥",
                    fontFamily = RozhaOneFontFamily,
                    fontSize = 15.sp,
                    color = goldAccent,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "अध्याय ${verse.chapter} • श्लोक ${verse.verse}",
                    fontFamily = YatraOneFontFamily,
                    fontSize = 12.sp,
                    color = goldAccent.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )
            }

            // Exact Sanskrit Verse
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = verse.sanskrit,
                    fontFamily = MartelFontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(1.dp)
                        .background(goldAccent.copy(alpha = 0.7f))
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = verse.hindiMeaning,
                    fontFamily = MartelFontFamily,
                    fontSize = 11.sp,
                    color = textColor.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp,
                    maxLines = 6
                )
            }

            // Footer
            Text(
                text = "॥ श्रीकृष्णार्जुन संवाद ॥",
                fontFamily = YatraOneFontFamily,
                fontSize = 11.sp,
                color = goldAccent.copy(alpha = 0.75f),
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun generateAndSaveStatusBitmap(context: Context, verse: Verse, style: StatusCardStyle): File? {
    return try {
        val width = 1080
        val height = 1920
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val bgHex = when (style) {
            StatusCardStyle.PARCHMENT -> android.graphics.Color.parseColor("#F9F3E5")
            StatusCardStyle.TEMPLE_STONE -> android.graphics.Color.parseColor("#1C1917")
            StatusCardStyle.COPPER_PLATE -> android.graphics.Color.parseColor("#331B10")
            StatusCardStyle.KURUKSHETRA -> android.graphics.Color.parseColor("#180A04")
            StatusCardStyle.KRISHNA -> android.graphics.Color.parseColor("#0F172A")
        }
        val textHex = when (style) {
            StatusCardStyle.PARCHMENT -> android.graphics.Color.parseColor("#2B1D0E")
            else -> android.graphics.Color.parseColor("#F7EEDD")
        }
        val goldHex = android.graphics.Color.parseColor("#D4AF37")

        canvas.drawColor(bgHex)

        // Draw sacred ornamental borders
        val borderPaint = Paint().apply {
            color = goldHex
            this.setStyle(Paint.Style.STROKE)
            strokeWidth = 8f
            isAntiAlias = true
        }
        canvas.drawRect(40f, 40f, (width - 40).toFloat(), (height - 40).toFloat(), borderPaint)
        canvas.drawRect(55f, 55f, (width - 55).toFloat(), (height - 55).toFloat(), Paint().apply {
            color = goldHex
            alpha = 120
            this.setStyle(Paint.Style.STROKE)
            strokeWidth = 2f
            isAntiAlias = true
        })

        val headerPaint = TextPaint().apply {
            color = goldHex
            textSize = 56f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        canvas.drawText("॥ श्रीमद्भगवद्गीता ॥", (width / 2).toFloat(), 200f, headerPaint)

        val subHeaderPaint = TextPaint().apply {
            color = goldHex
            textSize = 40f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT
            isAntiAlias = true
        }
        canvas.drawText("अध्याय ${verse.chapter} • श्लोक ${verse.verse}", (width / 2).toFloat(), 270f, subHeaderPaint)

        // Sanskrit exact text
        val sanskritPaint = TextPaint().apply {
            color = textHex
            textSize = 44f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        val sanskritLayout = StaticLayout.Builder.obtain(
            verse.sanskrit, 0, verse.sanskrit.length, sanskritPaint, width - 200
        ).setAlignment(Layout.Alignment.ALIGN_CENTER).build()

        canvas.save()
        canvas.translate(100f, 440f)
        sanskritLayout.draw(canvas)
        canvas.restore()

        // Divider
        val dividerY = 440f + sanskritLayout.height + 80f
        canvas.drawLine((width / 2 - 150).toFloat(), dividerY, (width / 2 + 150).toFloat(), dividerY, borderPaint)

        // Hindi meaning
        val hindiPaint = TextPaint().apply {
            color = textHex
            textSize = 34f
            typeface = Typeface.DEFAULT
            isAntiAlias = true
        }
        val hindiLayout = StaticLayout.Builder.obtain(
            verse.hindiMeaning, 0, verse.hindiMeaning.length, hindiPaint, width - 200
        ).setAlignment(Layout.Alignment.ALIGN_CENTER).build()

        canvas.save()
        canvas.translate(100f, dividerY + 60f)
        hindiLayout.draw(canvas)
        canvas.restore()

        // Footer
        val footerPaint = TextPaint().apply {
            color = goldHex
            textSize = 36f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT
            isAntiAlias = true
        }
        canvas.drawText("॥ श्रीकृष्णार्जुन संवाद ॥", (width / 2).toFloat(), (height - 120).toFloat(), footerPaint)

        val outputDir = File(context.cacheDir, "status_cards")
        outputDir.mkdirs()
        val file = File(outputDir, "Gita_${verse.chapter}_${verse.verse}_${System.currentTimeMillis()}.png")
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.flush()
        stream.close()
        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun shareImage(context: Context, file: File, verse: Verse) {
    try {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "॥ श्रीमद्भगवद्गीता ॥\nअध्याय ${verse.chapter}, श्लोक ${verse.verse}\n\n${verse.sanskrit}\n\n${verse.hindiMeaning}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "श्लोक स्टेटस साझा करें"))
    } catch (e: Exception) {
        // Fallback to text share
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "॥ श्रीमद्भगवद्गीता ॥\nअध्याय ${verse.chapter}, श्लोक ${verse.verse}\n\n${verse.sanskrit}\n\n${verse.hindiMeaning}")
        }
        context.startActivity(Intent.createChooser(intent, "श्लोक साझा करें"))
    }
}
