package com.ziyin.island.ui

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziyin.island.data.ProgressSnapshot
import com.ziyin.island.speech.AudioPracticeState
import com.ziyin.island.speech.StandardAudioState
import com.ziyin.island.ui.theme.Coral
import com.ziyin.island.ui.theme.Cream
import com.ziyin.island.ui.theme.Ink
import com.ziyin.island.ui.theme.Lavender
import com.ziyin.island.ui.theme.Mint
import com.ziyin.island.ui.theme.SkyBlue
import com.ziyin.island.ui.theme.SkyLight
import com.ziyin.island.ui.theme.Sunflower

private data class LetterLesson(
    val symbol: String,
    val tones: List<String>,
    val mouthHint: String,
    val examples: List<String>,
    val color: Color,
)

private val firstLetters = listOf(
    LetterLesson("a", listOf("ā", "á", "ǎ", "à"), "嘴巴张大，声音响亮", listOf("大", "马", "爸"), Coral),
    LetterLesson("o", listOf("ō", "ó", "ǒ", "ò"), "嘴唇圆圆，像个小圆圈", listOf("我", "火", "朵"), Sunflower),
    LetterLesson("e", listOf("ē", "é", "ě", "è"), "嘴巴扁扁，轻轻发音", listOf("鹅", "河", "乐"), Mint),
)

@Composable
fun LessonScreen(
    progress: ProgressSnapshot,
    audioPracticeState: AudioPracticeState,
    audioLevel: Int,
    standardAudioState: StandardAudioState,
    onBack: () -> Unit,
    onExploreLetter: (String) -> Unit,
    onComplete: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onPlayRecording: () -> Unit,
    onResetRecording: () -> Unit,
    onMicrophonePermissionDenied: () -> Unit,
    onPlayStandardAudio: (String) -> Unit,
) {
    var selectedLetter by remember { mutableStateOf(firstLetters.first()) }
    var selectedTone by remember { mutableStateOf(selectedLetter.tones.first()) }
    var hasTraced by remember(selectedLetter.symbol) { mutableStateOf(false) }

    LaunchedEffect(selectedLetter.symbol) {
        selectedTone = selectedLetter.tones.first()
        onExploreLetter(selectedLetter.symbol)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(SkyBlue, SkyLight, Cream))),
    ) {
        Column(Modifier.fillMaxSize()) {
            LessonHeader(
                stars = progress.stars,
                progress = (progress.exploredLetters.intersect(setOf("a", "o", "e")).size / 3f).coerceAtLeast(0.15f),
                onBack = onBack,
            )

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp, vertical = 14.dp),
            ) {
                if (maxWidth >= 840.dp) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(18.dp),
                    ) {
                        LetterRail(
                            selected = selectedLetter,
                            onSelect = { selectedLetter = it },
                            modifier = Modifier.width(190.dp),
                        )
                        LessonPanel(
                            lesson = selectedLetter,
                            selectedTone = selectedTone,
                            hasTraced = hasTraced,
                            audioPracticeState = audioPracticeState,
                            audioLevel = audioLevel,
                            standardAudioState = standardAudioState,
                            onToneSelected = { selectedTone = it },
                            onTraceChanged = { hasTraced = it },
                            onComplete = onComplete,
                            onStartRecording = onStartRecording,
                            onStopRecording = onStopRecording,
                            onPlayRecording = onPlayRecording,
                            onResetRecording = onResetRecording,
                            onMicrophonePermissionDenied = onMicrophonePermissionDenied,
                            onPlayStandardAudio = onPlayStandardAudio,
                            modifier = Modifier.weight(1f),
                        )
                    }
                } else {
                    Column(Modifier.verticalScroll(rememberScrollState())) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            firstLetters.forEach { lesson ->
                                LetterCard(lesson, lesson == selectedLetter) { selectedLetter = lesson }
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                        LessonPanel(
                            lesson = selectedLetter,
                            selectedTone = selectedTone,
                            hasTraced = hasTraced,
                            audioPracticeState = audioPracticeState,
                            audioLevel = audioLevel,
                            standardAudioState = standardAudioState,
                            onToneSelected = { selectedTone = it },
                            onTraceChanged = { hasTraced = it },
                            onComplete = onComplete,
                            onStartRecording = onStartRecording,
                            onStopRecording = onStopRecording,
                            onPlayRecording = onPlayRecording,
                            onResetRecording = onResetRecording,
                            onMicrophonePermissionDenied = onMicrophonePermissionDenied,
                            onPlayStandardAudio = onPlayStandardAudio,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonHeader(stars: Int, progress: Float, onBack: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White.copy(alpha = 0.93f),
        shadowElevation = 6.dp,
    ) {
        Row(
            modifier = Modifier.padding(start = 18.dp, end = 22.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Surface(
                modifier = Modifier
                    .size(52.dp)
                    .clickable(onClick = onBack),
                shape = CircleShape,
                color = SkyLight,
            ) {
                Box(contentAlignment = Alignment.Center) { Text("←", fontSize = 28.sp, fontWeight = FontWeight.Black) }
            }
            Text("单韵母探险", style = MaterialTheme.typography.titleLarge)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .weight(1f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = Coral,
                trackColor = Coral.copy(alpha = 0.16f),
            )
            Text("★ $stars", color = Ink, fontWeight = FontWeight.Black, fontSize = 20.sp)
        }
    }
}

@Composable
private fun LetterRail(selected: LetterLesson, onSelect: (LetterLesson) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("选一个字母", style = MaterialTheme.typography.titleLarge)
        firstLetters.forEach { lesson ->
            LetterCard(lesson, lesson == selected) { onSelect(lesson) }
        }
    }
}

@Composable
private fun LetterCard(lesson: LetterLesson, selected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(width = 150.dp, height = 116.dp)
            .clickable(onClick = onClick)
            .then(if (selected) Modifier.border(5.dp, lesson.color, RoundedCornerShape(28.dp)) else Modifier),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.96f)),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 10.dp else 4.dp),
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(lesson.symbol, fontSize = 68.sp, fontWeight = FontWeight.Black, color = lesson.color)
        }
    }
}

@Composable
private fun LessonPanel(
    lesson: LetterLesson,
    selectedTone: String,
    hasTraced: Boolean,
    audioPracticeState: AudioPracticeState,
    audioLevel: Int,
    standardAudioState: StandardAudioState,
    onToneSelected: (String) -> Unit,
    onTraceChanged: (Boolean) -> Unit,
    onComplete: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onPlayRecording: () -> Unit,
    onResetRecording: () -> Unit,
    onMicrophonePermissionDenied: () -> Unit,
    onPlayStandardAudio: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(34.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.96f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                Surface(shape = CircleShape, color = lesson.color.copy(alpha = 0.18f)) {
                    Text(
                        selectedTone,
                        modifier = Modifier.padding(horizontal = 28.dp, vertical = 12.dp),
                        fontSize = 76.sp,
                        fontWeight = FontWeight.Black,
                        color = lesson.color,
                    )
                }
                Column {
                    Text(lesson.mouthHint, style = MaterialTheme.typography.titleLarge)
                    Text("点一点四声，再沿着字母画一画。", style = MaterialTheme.typography.bodyLarge)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                lesson.tones.forEach { tone ->
                    FilterChip(
                        selected = selectedTone == tone,
                        onClick = { onToneSelected(tone) },
                        label = { Text(tone, fontSize = 30.sp, fontWeight = FontWeight.Black) },
                        modifier = Modifier.height(58.dp),
                    )
                }
            }

            SpeechPracticeCard(
                target = selectedTone,
                state = audioPracticeState,
                audioLevel = audioLevel,
                standardAudioState = standardAudioState,
                accentColor = lesson.color,
                onStartRecording = onStartRecording,
                onStopRecording = onStopRecording,
                onPlayRecording = onPlayRecording,
                onResetRecording = onResetRecording,
                onPermissionDenied = onMicrophonePermissionDenied,
                onPlayStandardAudio = { onPlayStandardAudio(selectedTone) },
            )

            Text("手指描一描", style = MaterialTheme.typography.titleLarge)
            TracingPad(
                letter = lesson.symbol,
                strokeColor = lesson.color,
                onTraceChanged = onTraceChanged,
            )

            Text("在这些汉字里也能听见它", style = MaterialTheme.typography.titleLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                lesson.examples.forEach { character ->
                    Surface(shape = RoundedCornerShape(22.dp), color = lesson.color.copy(alpha = 0.16f)) {
                        Text(
                            character,
                            modifier = Modifier.padding(horizontal = 22.dp, vertical = 12.dp),
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Black,
                        )
                    }
                }
            }

            Button(
                onClick = onComplete,
                enabled = hasTraced,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp),
                shape = RoundedCornerShape(28.dp),
            ) {
                Text(if (hasTraced) "完成今天的探索" else "先描一描字母", fontSize = 21.sp)
            }
        }
    }
}

@Composable
private fun TracingPad(letter: String, strokeColor: Color, onTraceChanged: (Boolean) -> Unit) {
    val points = remember(letter) { mutableStateListOf<Offset>() }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(SkyLight.copy(alpha = 0.65f))
                .border(3.dp, strokeColor.copy(alpha = 0.35f), RoundedCornerShape(26.dp))
                .pointerInput(letter) {
                    detectDragGestures(
                        onDragStart = { position ->
                            points.clear()
                            points.add(position)
                            onTraceChanged(false)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            points.add(change.position)
                            if (points.size >= 18) onTraceChanged(true)
                        },
                    )
                },
        ) {
            val textPaint = Paint().apply {
                color = android.graphics.Color.argb(48, 51, 67, 91)
                textAlign = Paint.Align.CENTER
                textSize = size.minDimension * 0.78f
                typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            }
            drawContext.canvas.nativeCanvas.drawText(
                letter,
                center.x,
                center.y - (textPaint.ascent() + textPaint.descent()) / 2,
                textPaint,
            )

            if (points.size > 1) {
                val path = Path().apply {
                    moveTo(points.first().x, points.first().y)
                    points.drop(1).forEach { lineTo(it.x, it.y) }
                }
                drawPath(path, strokeColor, style = Stroke(width = 18.dp.toPx(), cap = StrokeCap.Round))
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            OutlinedButton(
                onClick = {
                    points.clear()
                    onTraceChanged(false)
                },
                shape = RoundedCornerShape(18.dp),
            ) {
                Text("重新画")
            }
        }
    }
}
