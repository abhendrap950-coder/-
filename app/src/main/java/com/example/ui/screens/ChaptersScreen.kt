package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Chapter
import com.example.ui.components.ArchaeologicalHeader
import com.example.ui.components.ChapterManuscriptCard
import com.example.ui.components.VedicDivider
import com.example.ui.theme.RozhaOneFontFamily
import com.example.ui.theme.YatraOneFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChaptersScreen(
    chapters: List<Chapter>,
    onSelectChapter: (Int) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "॥ अष्टादश अध्यायाः ॥",
                        fontFamily = RozhaOneFontFamily,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("back_from_chapters")
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("chapters_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ArchaeologicalHeader(
                    title = "श्रीमद्भगवद्गीता के 18 अध्याय",
                    subtitle = "महाभारत भीष्मपर्व अन्तर्गत सम्पूर्ण 700 श्लोक"
                )
                VedicDivider(ornament = "॥ अध्याय सूची ॥")
            }

            items(chapters) { chapter ->
                ChapterManuscriptCard(
                    chapter = chapter,
                    onClick = { onSelectChapter(chapter.chapter) }
                )
            }
        }
    }
}
