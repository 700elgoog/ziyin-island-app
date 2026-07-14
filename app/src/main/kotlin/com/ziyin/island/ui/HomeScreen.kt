package com.ziyin.island.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziyin.island.R
import com.ziyin.island.data.ProgressSnapshot
import com.ziyin.island.ui.theme.Coral
import com.ziyin.island.ui.theme.Ink
import com.ziyin.island.ui.theme.Lavender
import com.ziyin.island.ui.theme.Mint
import com.ziyin.island.ui.theme.SkyBlue
import com.ziyin.island.ui.theme.Sunflower

@Composable
fun HomeScreen(
    progress: ProgressSnapshot,
    onOpenPinyin: () -> Unit,
    onOpenSound: () -> Unit,
    onOpenLiteracy: () -> Unit,
    onOpenStories: () -> Unit,
    onOpenReview: () -> Unit,
    onOpenAchievements: () -> Unit,
    onOpenParent: () -> Unit,
) {
    val speaker = LocalGuideSpeaker.current
    var showParentGate by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(SkyBlue),
    ) {
        val wide = maxWidth > maxHeight * 1.2f

        Image(
            painter = painterResource(R.drawable.ziyin_island_home_v1),
            contentDescription = "字音岛学习世界",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        Box(
            Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.10f), androidx.compose.ui.graphics.Color.Transparent),
                    ),
                ),
        )

        if (wide) {
            WideHomeHotspots(
                progress = progress,
                onOpenPinyin = onOpenPinyin,
                onOpenSound = onOpenSound,
                onOpenLiteracy = onOpenLiteracy,
                onOpenStories = onOpenStories,
                onOpenReview = onOpenReview,
                onOpenAchievements = onOpenAchievements,
                onParentTap = {
                    showParentGate = true
                    speaker.speak("这里需要请爸爸妈妈来操作。请家长长按屏幕里的按钮。", "parent-gate")
                },
            )
        } else {
            CompactHomePanel(
                progress = progress,
                onOpenPinyin = onOpenPinyin,
                onOpenSound = onOpenSound,
                onOpenLiteracy = onOpenLiteracy,
                onOpenStories = onOpenStories,
                onOpenReview = onOpenReview,
                onOpenAchievements = onOpenAchievements,
                onParentTap = {
                    showParentGate = true
                    speaker.speak("这里需要请爸爸妈妈来操作。请家长长按屏幕里的按钮。", "parent-gate")
                },
            )
        }
    }

    if (showParentGate) {
        ParentGateDialog(
            onDismiss = { showParentGate = false },
            onUnlocked = {
                showParentGate = false
                onOpenParent()
            },
        )
    }
}

@Composable
private fun WideHomeHotspots(
    progress: ProgressSnapshot,
    onOpenPinyin: () -> Unit,
    onOpenSound: () -> Unit,
    onOpenLiteracy: () -> Unit,
    onOpenStories: () -> Unit,
    onOpenReview: () -> Unit,
    onOpenAchievements: () -> Unit,
    onParentTap: () -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 118.dp, top = 54.dp),
            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.92f),
            shape = RoundedCornerShape(24.dp),
            shadowElevation = 7.dp,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("★", color = Sunflower, fontSize = 24.sp)
                Spacer(Modifier.width(6.dp))
                Text("${progress.stars} 颗探索星", modifier = Modifier.clickable(onClick = onOpenAchievements), color = Ink, fontWeight = FontWeight.Bold)
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(14.dp)
                .size(98.dp)
                .clip(CircleShape)
                .semantics { contentDescription = "家长中心" }
                .clickable(onClick = onParentTap),
        )

        ZonePill(
            text = "♪ 声音森林",
            color = Mint,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 72.dp, bottom = 150.dp),
            onClick = onOpenSound,
        )
        ZonePill(
            text = "a 拼音工坊",
            color = Coral,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 92.dp, bottom = 125.dp),
            onClick = onOpenPinyin,
        )
        ZonePill(
            text = "日 汉字小镇",
            color = Sunflower,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 92.dp, bottom = 126.dp),
            onClick = onOpenLiteracy,
        )
        ZonePill(
            text = "▣ 故事剧场",
            color = Lavender,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 104.dp, bottom = 126.dp),
            onClick = onOpenStories,
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 22.dp)
                .size(width = 360.dp, height = 104.dp)
                .clip(RoundedCornerShape(52.dp))
                .semantics { contentDescription = "开始探索拼音课" }
                .clickable(onClick = onOpenPinyin),
        )
    }
}

@Composable
private fun CompactHomePanel(
    progress: ProgressSnapshot,
    onOpenPinyin: () -> Unit,
    onOpenSound: () -> Unit,
    onOpenLiteracy: () -> Unit,
    onOpenStories: () -> Unit,
    onOpenReview: () -> Unit,
    onOpenAchievements: () -> Unit,
    onParentTap: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(shape = RoundedCornerShape(20.dp), color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.94f)) {
                Text("★ ${progress.stars} 颗探索星", modifier = Modifier.clickable(onClick = onOpenAchievements).padding(14.dp), fontWeight = FontWeight.Bold)
            }
            Surface(
                modifier = Modifier.clickable(onClick = onParentTap),
                shape = CircleShape,
                color = Lavender,
            ) {
                Text("🔒", modifier = Modifier.padding(14.dp), fontSize = 24.sp)
            }
        }
        Spacer(Modifier.weight(1f))
        Surface(
            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.94f),
            shape = RoundedCornerShape(28.dp),
            shadowElevation = 12.dp,
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("选择今天的探索", style = MaterialTheme.typography.titleLarge)
                ZonePill("a 拼音工坊", Coral, Modifier.fillMaxWidth(), onOpenPinyin)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ZonePill("♪ 声音", Mint, Modifier.weight(1f), onOpenSound)
                    ZonePill("日 汉字", Sunflower, Modifier.weight(1f), onOpenLiteracy)
                    ZonePill("▣ 故事", Lavender, Modifier.weight(1f), onOpenStories)
                }
            }
        }
    }
}

@Composable
private fun ZonePill(text: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        color = color.copy(alpha = 0.95f),
        contentColor = Ink,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 8.dp,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ParentGateDialog(onDismiss: () -> Unit, onUnlocked: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("这是家长区域") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("请家长长按下面的按钮进入。")
                Spacer(Modifier.height(18.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(onClick = {}, onLongClick = onUnlocked),
                    color = Lavender,
                    shape = RoundedCornerShape(22.dp),
                ) {
                    Text(
                        "长按进入家长中心",
                        modifier = Modifier.padding(18.dp),
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            Text("返回", modifier = Modifier.clickable(onClick = onDismiss).padding(12.dp))
        },
    )
}
