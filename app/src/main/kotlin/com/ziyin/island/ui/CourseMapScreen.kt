package com.ziyin.island.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziyin.island.R
import com.ziyin.island.content.Curriculum
import com.ziyin.island.content.PinyinLesson
import com.ziyin.island.data.ProgressSnapshot
import com.ziyin.island.ui.theme.Coral
import com.ziyin.island.ui.theme.Ink
import com.ziyin.island.ui.theme.Lavender
import com.ziyin.island.ui.theme.Mint
import com.ziyin.island.ui.theme.SkyLight
import com.ziyin.island.ui.theme.Sunflower

@Composable
fun CourseMapScreen(
    progress: ProgressSnapshot,
    onBack: () -> Unit,
    onOpenLesson: (PinyinLesson) -> Unit,
    onOpenReview: () -> Unit,
) {
    val speaker = LocalGuideSpeaker.current
    val stages = Curriculum.pinyinLessons.map { it.stage }.distinct()
    var selectedStage by remember { mutableStateOf(stages.first()) }
    val visibleLessons = Curriculum.pinyinLessons.filter { it.stage == selectedStage }

    SceneBackground(R.drawable.page_pinyin_map_v1, "拼音工坊课程地图", overlayAlpha = 0.08f) {
        Column(Modifier.fillMaxSize()) {
            PageHeader(
                title = "拼音工坊 · 60 课",
                subtitle = "按顺序学，也可以回到学过的课程再练习",
                stars = progress.stars,
                onBack = onBack,
                trailing = {
                    Surface(
                        modifier = Modifier.clickable(onClick = onOpenReview),
                        shape = RoundedCornerShape(18.dp),
                        color = Lavender.copy(alpha = 0.25f),
                    ) { Text("复习乐园", Modifier.padding(12.dp), fontWeight = FontWeight.Bold) }
                },
            )
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .weight(1f),
                color = Color.White.copy(alpha = 0.91f),
                shape = RoundedCornerShape(30.dp),
                shadowElevation = 10.dp,
            ) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        stages.forEach { stage ->
                            FilterChip(
                                selected = selectedStage == stage,
                                onClick = {
                                    selectedStage = stage
                                    speaker.speak("你来到${stage}。点一张课程卡开始学习。", "pinyin-stage-$stage")
                                },
                                label = { Text(stage, fontWeight = FontWeight.Bold) },
                            )
                        }
                    }
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(190.dp),
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(visibleLessons, key = { it.id }) { lesson ->
                            val completed = lesson.id in progress.completedLessons
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 150.dp)
                                    .clickable { onOpenLesson(lesson) },
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (completed) Mint.copy(alpha = 0.28f) else SkyLight.copy(alpha = 0.84f),
                                ),
                            ) {
                                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(lesson.id, color = Ink.copy(alpha = 0.65f), fontWeight = FontWeight.Bold)
                                        Text(if (completed) "✓" else "○", color = if (completed) Mint else Coral, fontSize = 24.sp, fontWeight = FontWeight.Black)
                                    }
                                    Text(lesson.symbols.joinToString(" "), fontSize = 30.sp, fontWeight = FontWeight.Black, color = Coral)
                                    Text(lesson.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                                    Text(lesson.mission, maxLines = 2, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
