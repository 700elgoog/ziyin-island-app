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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.ziyin.island.data.ProgressSnapshot
import com.ziyin.island.ui.theme.Coral
import com.ziyin.island.ui.theme.Ink
import com.ziyin.island.ui.theme.Mint
import com.ziyin.island.ui.theme.SkyLight
import com.ziyin.island.ui.theme.Sunflower

@Composable
fun ReviewGamesScreen(progress: ProgressSnapshot, onBack: () -> Unit) {
    val speaker = LocalGuideSpeaker.current
    val pool = Curriculum.characterLessons.filter { it.character in progress.knownCharacters }
        .ifEmpty { Curriculum.characterLessons.take(12) }
    var questionIndex by remember { mutableIntStateOf(0) }
    var feedback by remember { mutableStateOf("看图和读音，找出正确的汉字。") }
    val answer = pool[questionIndex % pool.size]
    val choices = remember(questionIndex) {
        (listOf(answer) + pool.filter { it != answer }.shuffled().take(2)).shuffled()
    }

    LaunchedEffect(questionIndex) {
        speaker.speak("请找出${answer.meaning}的汉字。它读${answer.pinyin}。", "review-question-$questionIndex")
    }

    SceneBackground(R.drawable.page_review_games_v1, "复习游戏乐园", overlayAlpha = 0.06f) {
        Column(Modifier.fillMaxSize()) {
            PageHeader("复习乐园", "只复习学过的内容，答错也可以再试", progress.stars, onBack)
            Spacer(Modifier.weight(1f))
            Surface(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                color = Color.White.copy(alpha = 0.94f),
                shape = RoundedCornerShape(34.dp),
                shadowElevation = 12.dp,
            ) {
                Column(Modifier.padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("${answer.picture}  ${answer.pinyin}", fontSize = 40.sp, fontWeight = FontWeight.Black)
                    Text(feedback, style = MaterialTheme.typography.bodyLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                        choices.forEach { choice ->
                            Surface(
                                modifier = Modifier.size(105.dp).clickable {
                                    if (choice == answer) {
                                        feedback = "找对了！“${answer.character}”就是${answer.meaning}。"
                                        speaker.speak("找对了！${answer.character}，就是${answer.meaning}。准备下一题。", "review-correct-$questionIndex")
                                        questionIndex++
                                    } else {
                                        feedback = "这个字是“${choice.character}”，再看一看图。"
                                        speaker.speak("这个字读${choice.pinyin}。再看看图，找一找${answer.pinyin}。", "review-retry-$questionIndex-${choice.character}")
                                    }
                                },
                                shape = CircleShape,
                                color = SkyLight,
                                shadowElevation = 7.dp,
                            ) { Box(contentAlignment = Alignment.Center) { Text(choice.character, fontSize = 52.sp, fontWeight = FontWeight.Black, color = Ink) } }
                        }
                    }
                }
            }
        }
    }
}
