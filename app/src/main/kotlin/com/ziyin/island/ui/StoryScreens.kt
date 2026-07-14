package com.ziyin.island.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziyin.island.R
import com.ziyin.island.content.Curriculum
import com.ziyin.island.content.StoryLesson
import com.ziyin.island.data.ProgressSnapshot
import com.ziyin.island.ui.theme.Coral
import com.ziyin.island.ui.theme.Ink
import com.ziyin.island.ui.theme.Lavender
import com.ziyin.island.ui.theme.Mint
import com.ziyin.island.ui.theme.SkyLight
import com.ziyin.island.ui.theme.Sunflower

@Composable
fun StoryTheaterScreen(progress: ProgressSnapshot, onBack: () -> Unit, onOpenStory: (StoryLesson) -> Unit) {
    SceneBackground(R.drawable.page_story_theater_v1, "字音岛故事剧场", overlayAlpha = 0.08f) {
        Column(Modifier.fillMaxSize()) {
            PageHeader("故事剧场 · 12 本", "把拼音和汉字放进故事里", progress.stars, onBack)
            Surface(
                modifier = Modifier.fillMaxSize().padding(18.dp),
                color = Color.White.copy(alpha = 0.89f),
                shape = RoundedCornerShape(30.dp),
                shadowElevation = 10.dp,
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(210.dp),
                    modifier = Modifier.fillMaxSize().padding(18.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    items(Curriculum.stories, key = { it.id }) { story ->
                        val complete = story.id in progress.completedStories
                        Card(
                            modifier = Modifier.clickable { onOpenStory(story) },
                            shape = RoundedCornerShape(26.dp),
                            colors = CardDefaults.cardColors(containerColor = if (complete) Mint.copy(alpha = 0.3f) else Lavender.copy(alpha = 0.18f)),
                        ) {
                            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(if (complete) "📖 ✓" else "📕", fontSize = 34.sp)
                                Text(story.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = Ink)
                                Text("练习：${story.focus}", color = Coral, fontWeight = FontWeight.Bold)
                                Text("${story.pages.size} 页原创短故事", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StoryReaderScreen(story: StoryLesson, onBack: () -> Unit, onComplete: () -> Unit) {
    val speaker = LocalGuideSpeaker.current
    var page by remember(story.id) { mutableIntStateOf(0) }
    val lastPage = page == story.pages.lastIndex
    LaunchedEffect(story.id, page) {
        speaker.speak(story.pages[page] + if (lastPage) "故事讲完啦。点读完了，收藏这颗故事星。" else "听完了，点向右的下一页。", "story-${story.id}-page-$page")
    }
    SceneBackground(R.drawable.page_story_reader_v1, "打开的故事绘本", overlayAlpha = 0.08f) {
        Column(Modifier.fillMaxSize()) {
            PageHeader(story.title, "练习：${story.focus} · ${page + 1}/${story.pages.size}", onBack = onBack)
            Box(Modifier.fillMaxSize().padding(34.dp), contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier.fillMaxWidth(0.78f),
                    color = Color(0xFFFFFCF4).copy(alpha = 0.96f),
                    shape = RoundedCornerShape(40.dp),
                    shadowElevation = 16.dp,
                ) {
                    Column(
                        modifier = Modifier.padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(26.dp),
                    ) {
                        Text(listOf("🌄", "🌿", "⭐")[page % 3], fontSize = 72.sp)
                        Text(
                            story.pages[page],
                            modifier = Modifier.clickable {
                                speaker.speak(
                                    story.pages[page] + if (lastPage) "故事讲完啦。" else "听完了，点下一页。",
                                    "story-core-${story.id}-$page",
                                )
                            },
                            fontSize = 32.sp,
                            lineHeight = 50.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Ink,
                        )
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Button(onClick = { if (page > 0) page-- }, enabled = page > 0, shape = RoundedCornerShape(22.dp)) { Text("上一页") }
                            Button(
                                onClick = { if (lastPage) onComplete() else page++ },
                                shape = RoundedCornerShape(22.dp),
                            ) { Text(if (lastPage) "读完了" else "下一页", Modifier.padding(horizontal = 22.dp), fontSize = 19.sp) }
                        }
                    }
                }
            }
        }
    }
}
