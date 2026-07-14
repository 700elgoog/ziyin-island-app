package com.ziyin.island.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziyin.island.R
import com.ziyin.island.ui.theme.Coral
import com.ziyin.island.ui.theme.Ink
import com.ziyin.island.ui.theme.Mint
import com.ziyin.island.ui.theme.Sunflower

@Composable
fun OnboardingScreen(onStart: () -> Unit) {
    val speaker = LocalGuideSpeaker.current
    SceneBackground(R.drawable.page_onboarding_v1, "小朋友来到字音岛", overlayAlpha = 0.04f) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(34.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.widthIn(max = 530.dp),
                color = Color.White.copy(alpha = 0.93f),
                shape = RoundedCornerShape(36.dp),
                shadowElevation = 14.dp,
            ) {
                Column(
                    modifier = Modifier.padding(30.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    Text(
                        "欢迎来到字音岛",
                        modifier = Modifier.clickable {
                            speaker.speak("小朋友，欢迎来到字音岛。点一点出发按钮，和小芽鸟一起学习。", "onboarding-core")
                        },
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black,
                        color = Ink,
                    )
                    Text("听声音 · 学拼音 · 认汉字 · 读故事", style = MaterialTheme.typography.titleLarge, color = Coral)
                    Text("每天探索 8—10 分钟。没有排名，也不用怕答错，小芽鸟会陪你慢慢学。", style = MaterialTheme.typography.bodyLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf("♪ 听一听" to Mint, "a 拼一拼" to Sunflower, "日 认一认" to Coral).forEach { (label, color) ->
                            Surface(shape = RoundedCornerShape(18.dp), color = color.copy(alpha = 0.24f)) {
                                Text(label, Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Button(
                        onClick = onStart,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(26.dp),
                    ) {
                        Text("和小芽鸟一起出发", Modifier.padding(vertical = 9.dp), fontSize = 22.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}
