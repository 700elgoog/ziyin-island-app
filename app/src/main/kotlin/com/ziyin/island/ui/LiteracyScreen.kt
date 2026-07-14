package com.ziyin.island.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.ziyin.island.content.CharacterLesson
import com.ziyin.island.content.Curriculum
import com.ziyin.island.data.ProgressSnapshot
import com.ziyin.island.ui.theme.Coral
import com.ziyin.island.ui.theme.Ink
import com.ziyin.island.ui.theme.Mint
import com.ziyin.island.ui.theme.SkyLight
import com.ziyin.island.ui.theme.Sunflower

@Composable
fun LiteracyTownScreen(
    progress: ProgressSnapshot,
    onBack: () -> Unit,
    onOpenCharacter: (CharacterLesson) -> Unit,
) {
    val speaker = LocalGuideSpeaker.current
    val categories = Curriculum.characterLessons.map { it.category }.distinct()
    var selectedCategory by remember { mutableStateOf(categories.first()) }
    val visible = Curriculum.characterLessons.filter { it.category == selectedCategory }

    SceneBackground(R.drawable.page_literacy_town_v1, "汉字小镇", overlayAlpha = 0.08f) {
        Column(Modifier.fillMaxSize()) {
            PageHeader("汉字小镇", "从生活和图画里认识汉字", progress.stars, onBack)
            Surface(
                modifier = Modifier.fillMaxSize().padding(18.dp),
                color = Color.White.copy(alpha = 0.91f),
                shape = RoundedCornerShape(30.dp),
                shadowElevation = 9.dp,
            ) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(9.dp),
                    ) {
                        categories.forEach { category ->
                            FilterChip(
                                selected = selectedCategory == category,
                                onClick = {
                                    selectedCategory = category
                                    speaker.speak("你打开了${category}。点一张大字卡，认识一个新汉字。", "literacy-category-$category")
                                },
                                label = { Text(category, fontWeight = FontWeight.Bold) },
                            )
                        }
                    }
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(150.dp),
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(visible, key = { it.character }) { lesson ->
                            val known = lesson.character in progress.knownCharacters
                            Card(
                                modifier = Modifier.clickable { onOpenCharacter(lesson) },
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = if (known) Mint.copy(alpha = 0.28f) else SkyLight.copy(alpha = 0.88f)),
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(15.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Text(lesson.picture, fontSize = 34.sp)
                                    Text(lesson.character, fontSize = 52.sp, fontWeight = FontWeight.Black, color = Coral)
                                    Text(lesson.pinyin, fontWeight = FontWeight.Bold, color = Ink)
                                    Text(if (known) "已经认识 ✓" else lesson.meaning, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
