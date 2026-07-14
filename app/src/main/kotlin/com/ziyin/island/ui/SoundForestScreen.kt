package com.ziyin.island.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import com.ziyin.island.content.RhymeLesson
import com.ziyin.island.ui.theme.Coral
import com.ziyin.island.ui.theme.Ink
import com.ziyin.island.ui.theme.Mint
import com.ziyin.island.ui.theme.Sunflower

@Composable
fun SoundForestScreen(onBack: () -> Unit) {
    val speaker = LocalGuideSpeaker.current
    var selected by remember { mutableStateOf(Curriculum.rhymes.first()) }
    var beats by remember(selected.id) { mutableIntStateOf(0) }
    SceneBackground(R.drawable.page_sound_forest_v1, "声音森林音乐空地", overlayAlpha = 0.07f) {
        Column(Modifier.fillMaxSize()) {
            PageHeader("声音森林 · 24 首", "先听节奏，再开口；每首约 1 分钟", onBack = onBack)
            Row(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Surface(
                    modifier = Modifier.weight(0.8f).fillMaxSize(),
                    color = Color.White.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(28.dp),
                ) {
                    LazyColumn(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(Curriculum.rhymes, key = { it.id }) { rhyme ->
                            Surface(
                                modifier = Modifier.fillMaxWidth().clickable {
                                    selected = rhyme
                                    speaker.speak("你选了${rhyme.title}。先听我念：${rhyme.lines.joinToString("，")}。现在跟着拍一拍。", "rhyme-${rhyme.id}")
                                },
                                shape = RoundedCornerShape(20.dp),
                                color = if (selected == rhyme) Sunflower.copy(alpha = 0.45f) else Mint.copy(alpha = 0.18f),
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    Text("${rhyme.id}  ${rhyme.title}", fontWeight = FontWeight.Black, color = Ink)
                                    Text(rhyme.soundFocus, style = MaterialTheme.typography.bodySmall, color = Coral)
                                }
                            }
                        }
                    }
                }
                Surface(
                    modifier = Modifier.weight(1.2f).fillMaxSize(),
                    color = Color.White.copy(alpha = 0.91f),
                    shape = RoundedCornerShape(32.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Text(selected.title, fontSize = 34.sp, fontWeight = FontWeight.Black, color = Ink)
                        Text("练习：${selected.soundFocus}", color = Coral, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        selected.lines.forEach { line ->
                            Text(line, fontSize = 28.sp, lineHeight = 40.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        }
                        Text("跟着每个字拍一下", style = MaterialTheme.typography.bodyLarge)
                        Surface(
                            modifier = Modifier.size(128.dp).clickable {
                                beats++
                                if (beats + 1 == 8) speaker.speak("八拍完成，节奏很稳！可以换一首，也可以休息一下。", "rhyme-complete-${selected.id}")
                            },
                            shape = CircleShape,
                            color = if (beats % 2 == 0) Coral else Sunflower,
                            shadowElevation = 10.dp,
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(if (beats == 0) "拍" else "拍 $beats", fontSize = 27.sp, fontWeight = FontWeight.Black, color = Ink)
                            }
                        }
                        Text(if (beats >= 8) "节奏很稳！换一首或休息一下。" else "听清楚再拍，不用赶快。", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
