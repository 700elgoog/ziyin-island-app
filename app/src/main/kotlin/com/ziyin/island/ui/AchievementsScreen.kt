package com.ziyin.island.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziyin.island.R
import com.ziyin.island.content.Curriculum
import com.ziyin.island.data.ProgressSnapshot
import com.ziyin.island.ui.theme.Coral
import com.ziyin.island.ui.theme.Ink
import com.ziyin.island.ui.theme.Lavender
import com.ziyin.island.ui.theme.Mint
import com.ziyin.island.ui.theme.Sunflower

@Composable
fun AchievementsScreen(progress: ProgressSnapshot, onBack: () -> Unit) {
    val speaker = LocalGuideSpeaker.current
    SceneBackground(R.drawable.page_achievements_v1, "成长花园", overlayAlpha = 0.08f) {
        Column(Modifier.fillMaxSize()) {
            PageHeader("我的成长花园", "记录努力，不和别人比较", progress.stars, onBack)
            Row(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    ProgressCard("拼音短课", progress.completedLessons.size, Curriculum.pinyinLessons.size, Coral) {
                        speaker.speak("你已经完成${progress.completedLessons.size}节拼音短课。每一节都算一次勇敢开口。", "achievement-pinyin")
                    }
                    ProgressCard("认识汉字", progress.knownCharacters.size, Curriculum.characterLessons.size, Mint) {
                        speaker.speak("你已经认识${progress.knownCharacters.size}个汉字。继续从生活里找一找它们。", "achievement-character")
                    }
                    ProgressCard("读完故事", progress.completedStories.size, Curriculum.stories.size, Lavender) {
                        speaker.speak("你已经读完${progress.completedStories.size}个故事。你的故事树正在长高。", "achievement-story")
                    }
                }
                Surface(
                    modifier = Modifier.weight(1f),
                    color = Color.White.copy(alpha = 0.91f),
                    shape = RoundedCornerShape(30.dp),
                ) {
                    Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("今天值得记住", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                        Text("★ 你已经收集 ${progress.stars} 颗探索星", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Ink)
                        Text("每颗星都来自一次认真听、勇敢读或耐心描画。", style = MaterialTheme.typography.bodyLarge)
                        Surface(shape = RoundedCornerShape(22.dp), color = Sunflower.copy(alpha = 0.28f)) {
                            Text("成长建议：学 8—10 分钟后，看看远处、活动身体，再决定是否继续。", Modifier.padding(18.dp), style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgressCard(title: String, value: Int, total: Int, color: Color, onClick: () -> Unit) {
    Surface(modifier = Modifier.clickable(onClick = onClick), color = Color.White.copy(alpha = 0.92f), shape = RoundedCornerShape(28.dp)) {
        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("$title  $value / $total", fontSize = 23.sp, fontWeight = FontWeight.Black, color = Ink)
            LinearProgressIndicator(
                progress = { (value.toFloat() / total.coerceAtLeast(1)).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
                color = color,
                trackColor = color.copy(alpha = 0.18f),
            )
        }
    }
}
