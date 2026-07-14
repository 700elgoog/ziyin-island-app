package com.ziyin.island.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun ParentScreen(progress: ProgressSnapshot, onBack: () -> Unit, onDailyLimitChanged: (Int) -> Unit) {
    var sliderValue by remember(progress.dailyLimitMinutes) { mutableFloatStateOf(progress.dailyLimitMinutes.toFloat()) }
    SceneBackground(R.drawable.page_parent_center_v1, "家长中心阅读角", overlayAlpha = 0.04f) {
        Column(Modifier.fillMaxSize()) {
            PageHeader("家长中心", "学习进度、健康节奏与隐私说明", onBack = onBack)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    ParentStat("拼音短课", "${progress.completedLessons.size}/${Curriculum.pinyinLessons.size}", Coral, Modifier.weight(1f))
                    ParentStat("认识汉字", "${progress.knownCharacters.size}/${Curriculum.characterLessons.size}", Mint, Modifier.weight(1f))
                    ParentStat("读完故事", "${progress.completedStories.size}/${Curriculum.stories.size}", Lavender, Modifier.weight(1f))
                    ParentStat("探索星", progress.stars.toString(), Sunflower, Modifier.weight(1f))
                }
                Card(shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f))) {
                    Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("每日学习时长", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                        Text("建议每次 8—10 分钟，两次学习之间活动眼睛和身体。")
                        Text("${sliderValue.toInt()} 分钟", color = Coral, fontSize = 30.sp, fontWeight = FontWeight.Black)
                        Slider(
                            value = sliderValue,
                            onValueChange = { sliderValue = ((it / 5).toInt() * 5).toFloat() },
                            onValueChangeFinished = { onDailyLimitChanged(sliderValue.toInt()) },
                            valueRange = 10f..30f,
                            steps = 3,
                        )
                    }
                }
                Card(shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f))) {
                    Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("本周陪伴建议", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                        Text(if (progress.completedLessons.isEmpty()) "先陪孩子完成一节声音或单韵母课，熟悉操作后再独立探索。" else "让孩子在生活中找一找学过的声音和汉字，不用追加机械抄写。")
                        Text("已探索拼音：${progress.exploredLetters.sorted().take(12).joinToString("、").ifEmpty { "尚未开始" }}")
                        Text("已认识汉字：${progress.knownCharacters.sorted().take(18).joinToString("、").ifEmpty { "尚未开始" }}")
                    }
                }
                Card(shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Lavender.copy(alpha = 0.2f))) {
                    Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("隐私与语音", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = Ink)
                        Text("麦克风只在点击录音后申请。当前录音保存在应用缓存，用于立即回听；重录、离开课程或关闭应用时删除，不上传服务器。云端评测正式启用前，必须增加监护人单独同意。")
                    }
                }
            }
        }
    }
}

@Composable
private fun ParentStat(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.28f))) {
        Column(Modifier.padding(17.dp)) {
            Text(value, fontSize = 28.sp, fontWeight = FontWeight.Black, color = Ink)
            Text(title, fontWeight = FontWeight.Bold, color = Ink)
        }
    }
}
