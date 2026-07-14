package com.ziyin.island.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziyin.island.speech.AudioPracticeState
import com.ziyin.island.speech.PracticeFeedbackKind
import com.ziyin.island.speech.StandardAudioState
import com.ziyin.island.ui.theme.Coral
import com.ziyin.island.ui.theme.Ink
import com.ziyin.island.ui.theme.Mint
import com.ziyin.island.ui.theme.SkyLight
import com.ziyin.island.ui.theme.Sunflower

@Composable
fun SpeechPracticeCard(
    target: String,
    state: AudioPracticeState,
    audioLevel: Int,
    standardAudioState: StandardAudioState,
    accentColor: Color,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onPlayRecording: () -> Unit,
    onResetRecording: () -> Unit,
    onPermissionDenied: () -> Unit,
    onPlayStandardAudio: () -> Unit,
    standardAudioAvailable: Boolean = true,
) {
    val context = LocalContext.current
    val speaker = LocalGuideSpeaker.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) onStartRecording() else onPermissionDenied()
    }

    val requestOrStartRecording = {
        if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            onStartRecording()
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    LaunchedEffect(state) {
        when (state) {
            is AudioPracticeState.Recorded -> speaker.speak(state.feedback.message, "recorded-${state.feedback.kind}")
            is AudioPracticeState.Error -> speaker.speak(state.message, "recording-error")
            else -> Unit
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = accentColor.copy(alpha = 0.12f),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = accentColor) {
                    Text("🎙", modifier = Modifier.padding(12.dp), fontSize = 24.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("读一读 $target", style = MaterialTheme.typography.titleLarge)
                    Text("录下来，再听听自己的声音。", style = MaterialTheme.typography.bodyLarge)
                }
            }

            val standardAudioBusy = standardAudioState is StandardAudioState.Loading ||
                standardAudioState is StandardAudioState.Playing
            OutlinedButton(
                onClick = onPlayStandardAudio,
                enabled = standardAudioAvailable && state !is AudioPracticeState.Recording && !standardAudioBusy,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = SkyLight.copy(alpha = 0.72f)),
            ) {
                val label = when (standardAudioState) {
                    is StandardAudioState.Loading -> "准备标准音……"
                    is StandardAudioState.Playing -> "正在播放 $target ♪"
                    else -> if (standardAudioAvailable) "🔊 听标准音 $target" else "🔊 这一关听小芽鸟口令"
                }
                Text(label, color = Ink, fontSize = 19.sp, fontWeight = FontWeight.Black)
            }
            if (standardAudioState is StandardAudioState.Error) {
                Text(standardAudioState.message, color = Coral, fontWeight = FontWeight.Bold)
            }

            when (state) {
                AudioPracticeState.Idle -> {
                    Button(
                        onClick = requestOrStartRecording,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        shape = RoundedCornerShape(24.dp),
                    ) {
                        Text("开始录音", fontSize = 20.sp)
                    }
                }

                AudioPracticeState.Recording -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("正在听你读……", color = Coral, fontSize = 21.sp, fontWeight = FontWeight.Black)
                        Spacer(Modifier.height(8.dp))
                        VoiceLevelMeter(level = audioLevel, accentColor = accentColor)
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = onStopRecording,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            shape = RoundedCornerShape(24.dp),
                        ) {
                            Text("读完了", fontSize = 20.sp)
                        }
                    }
                }

                AudioPracticeState.PreparingPlayback -> {
                    Text("正在准备播放……", fontWeight = FontWeight.Bold)
                }

                is AudioPracticeState.Playing -> {
                    Text("正在播放我的声音 ♪", color = accentColor, fontSize = 21.sp, fontWeight = FontWeight.Black)
                }

                is AudioPracticeState.Recorded -> {
                    val feedbackColor = when (state.feedback.kind) {
                        PracticeFeedbackKind.READY -> Mint
                        PracticeFeedbackKind.TOO_SHORT -> Sunflower
                        PracticeFeedbackKind.TOO_SOFT -> Sunflower
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(color = feedbackColor.copy(alpha = 0.28f), shape = RoundedCornerShape(18.dp)) {
                            Column(Modifier.padding(12.dp)) {
                                Text(state.feedback.title, fontWeight = FontWeight.Black, fontSize = 20.sp)
                                Text(state.feedback.message, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = onPlayRecording,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(20.dp),
                            ) {
                                Text("听我的声音")
                            }
                            Button(
                                onClick = requestOrStartRecording,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(20.dp),
                            ) {
                                Text("再读一次")
                            }
                        }
                    }
                }

                is AudioPracticeState.Error -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(state.message, color = Coral, fontWeight = FontWeight.Bold)
                        TextButton(onClick = onResetRecording) { Text("知道了，重新准备") }
                    }
                }
            }
        }
    }
}

@Composable
private fun VoiceLevelMeter(level: Int, accentColor: Color) {
    val thresholds = listOf(8, 20, 34, 50, 66, 80, 92)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom,
    ) {
        thresholds.forEachIndexed { index, threshold ->
            val active = level >= threshold
            val targetHeight = if (active) (28 + index * 4).dp else 12.dp
            val animatedHeight by animateDpAsState(targetHeight, label = "voice-level-$index")
            Box(
                Modifier
                    .padding(horizontal = 3.dp)
                    .width(12.dp)
                    .height(animatedHeight)
                    .background(
                        color = if (active) accentColor else accentColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                    ),
            )
        }
    }
}
