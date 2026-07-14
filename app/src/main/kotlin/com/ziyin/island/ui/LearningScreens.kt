package com.ziyin.island.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziyin.island.R
import com.ziyin.island.content.CharacterLesson
import com.ziyin.island.content.LessonPhase
import com.ziyin.island.content.MouthVisual
import com.ziyin.island.content.PinyinLesson
import com.ziyin.island.content.PronunciationGuidance
import com.ziyin.island.speech.AudioPracticeState
import com.ziyin.island.speech.StandardAudioState
import com.ziyin.island.speech.StandardAudioTargets
import com.ziyin.island.ui.theme.Coral
import com.ziyin.island.ui.theme.Ink
import com.ziyin.island.ui.theme.Mint
import com.ziyin.island.ui.theme.SkyLight
import com.ziyin.island.ui.theme.Sunflower

private val toneForms = mapOf(
    "a" to listOf("ā", "á", "ǎ", "à"), "o" to listOf("ō", "ó", "ǒ", "ò"),
    "e" to listOf("ē", "é", "ě", "è"), "i" to listOf("ī", "í", "ǐ", "ì"),
    "u" to listOf("ū", "ú", "ǔ", "ù"), "ü" to listOf("ǖ", "ǘ", "ǚ", "ǜ"),
)

@Composable
fun PinyinLearningScreen(
    lesson: PinyinLesson,
    audioState: AudioPracticeState,
    audioLevel: Int,
    standardAudioState: StandardAudioState,
    onBack: () -> Unit,
    onToneGame: () -> Unit,
    onTracing: () -> Unit,
    onComplete: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onPlayRecording: () -> Unit,
    onResetRecording: () -> Unit,
    onPermissionDenied: () -> Unit,
    onPlayStandardAudio: (String) -> Unit,
) {
    val base = lesson.symbols.firstOrNull().orEmpty()
    val tones = toneForms[base].orEmpty()
    var selectedTarget by remember(lesson.id) { mutableStateOf(tones.firstOrNull() ?: base) }
    var phaseIndex by remember(lesson.id) { mutableIntStateOf(0) }
    var playCount by remember(lesson.id) { mutableIntStateOf(0) }
    var practiceSolved by remember(lesson.id) { mutableStateOf(false) }
    val phase = LessonPhase.entries[phaseIndex]
    val articulation = remember(base) { PronunciationGuidance.forSymbol(base) }
    val standardTarget = remember(base) { StandardAudioTargets.firstToneFor(base) }
    val speaker = LocalGuideSpeaker.current
    val phasePrompt = when (phase) {
        LessonPhase.PLAY -> "小朋友，点三次中间的 $base，注意听一听，每次都跟读一遍。"
        LessonPhase.RECOGNIZE -> articulation.childPrompt
        LessonPhase.TONE -> if (tones.isNotEmpty()) "给 $base 戴上四顶声调帽。点每一张声调卡，听听声音怎么变。" else "看看这些声音朋友。点一个，注意听一听。"
        LessonPhase.SPEAK -> "轮到你来说。先点写着标准音的大按钮，再点麦克风跟读，读完听听自己的声音。"
        LessonPhase.PRACTICE -> "来练一练。点一点中间的 $base 听声音，再从下面的卡片里找到它。"
        LessonPhase.WRITE -> "最后来写一写。点中间的大字形，用手指沿着浅色字形慢慢画。"
    }

    LaunchedEffect(lesson.id, phase) {
        speaker.speak(phasePrompt, "lesson-${lesson.id}-${phase.name}")
    }

    SceneBackground(R.drawable.page_pinyin_lesson_v1, "拼音学习教室", overlayAlpha = 0.02f) {
        Column(Modifier.fillMaxSize()) {
            PageHeader(lesson.title, "${lesson.id} · ${lesson.stage}", onBack = onBack)
            LessonPhaseBar(phase) { selected ->
                phaseIndex = selected.ordinal
                practiceSolved = false
            }
            Box(Modifier.weight(1f).fillMaxWidth().padding(18.dp), contentAlignment = Alignment.Center) {
                when (phase) {
                    LessonPhase.PLAY -> PlayPhase(base, playCount) {
                        playCount = (playCount + 1).coerceAtMost(3)
                        if (playCount >= 3) {
                            speaker.speak("三次都完成啦！点右边的下一步，去看清楚嘴型。", "play-${lesson.id}-done")
                        } else if (standardTarget != null) {
                            onPlayStandardAudio(standardTarget)
                        } else {
                            speaker.speak(if (base == "—") "这是长音，声音要拉得长长的。" else "看着声音轨道，用手指跟我走一遍。", "sound-prep-${lesson.id}-$playCount")
                        }
                    }
                    LessonPhase.RECOGNIZE -> RecognizePhase(
                        symbol = base,
                        guide = articulation,
                        onMouthClick = { speaker.speak(articulation.childPrompt, "mouth-${lesson.id}") },
                        onSymbolClick = {
                            if (standardTarget != null) onPlayStandardAudio(standardTarget)
                            else speaker.speak(articulation.childPrompt, "symbol-${lesson.id}")
                        },
                    )
                    LessonPhase.TONE -> TonePhase(lesson, tones, selectedTarget, onToneGame) { target ->
                        selectedTarget = target
                        if (StandardAudioTargets.contains(target)) onPlayStandardAudio(target)
                        else speaker.speak("你点的是 $target。注意听一听，再观察它的样子。", "no-standard-$target")
                    }
                    LessonPhase.SPEAK -> Box(Modifier.fillMaxWidth(0.78f).verticalScroll(rememberScrollState())) {
                        SpeechPracticeCard(
                            target = selectedTarget,
                            state = audioState,
                            audioLevel = audioLevel,
                            standardAudioState = standardAudioState,
                            accentColor = Coral,
                            onStartRecording = onStartRecording,
                            onStopRecording = onStopRecording,
                            onPlayRecording = onPlayRecording,
                            onResetRecording = onResetRecording,
                            onPermissionDenied = {
                                speaker.speak("需要请爸爸妈妈打开麦克风，才能听到你的声音。", "microphone-permission")
                                onPermissionDenied()
                            },
                            onPlayStandardAudio = { onPlayStandardAudio(selectedTarget) },
                            standardAudioAvailable = StandardAudioTargets.contains(selectedTarget),
                        )
                    }
                    LessonPhase.PRACTICE -> PracticePhase(base, lesson.id, practiceSolved, {
                        if (standardTarget != null) onPlayStandardAudio(standardTarget)
                        else speaker.speak("听清小芽鸟刚才的口令，找到一样的符号。", "practice-listen-${lesson.id}")
                    }) { choice ->
                        if (choice == base) {
                            practiceSolved = true
                            speaker.speak("找对啦！这就是 $base。你已经会认它了。", "practice-correct-${lesson.id}")
                        } else {
                            speaker.speak("这个声音不一样。再点一次中间的 $base，然后看看它的样子。", "practice-retry-${lesson.id}-$choice")
                        }
                    }
                    LessonPhase.WRITE -> WritePhase(base, onTracing, onComplete)
                }
            }
            if (phase != LessonPhase.WRITE) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 8.dp), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = { phaseIndex = (phaseIndex + 1).coerceAtMost(LessonPhase.entries.lastIndex) },
                        shape = RoundedCornerShape(24.dp),
                    ) { Text("下一步  →", Modifier.padding(horizontal = 24.dp, vertical = 6.dp), fontSize = 20.sp, fontWeight = FontWeight.Black) }
                }
            }
        }
    }
}

@Composable
private fun LessonPhaseBar(selected: LessonPhase, onSelect: (LessonPhase) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        LessonPhase.entries.forEach { phase ->
            Surface(
                modifier = Modifier.weight(1f).clickable { onSelect(phase) },
                color = if (phase == selected) SkyLight else Color.White.copy(alpha = 0.92f),
                shape = RoundedCornerShape(22.dp),
                shadowElevation = if (phase == selected) 8.dp else 2.dp,
            ) {
                Column(Modifier.padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(phase.icon, fontSize = 22.sp)
                    Text(phase.label, fontSize = 18.sp, fontWeight = FontWeight.Black, color = if (phase == selected) Ink else Coral)
                }
            }
        }
    }
}

@Composable
private fun PlayPhase(symbol: String, count: Int, onTap: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Text("先和声音做朋友", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Ink)
        Surface(
            modifier = Modifier.size(230.dp).clickable(onClick = onTap),
            shape = CircleShape,
            color = listOf(Sunflower, Mint, SkyLight, Coral.copy(alpha = 0.8f))[count.coerceIn(0, 3)],
            shadowElevation = 14.dp,
        ) { Box(contentAlignment = Alignment.Center) { Text(symbol, fontSize = 118.sp, fontWeight = FontWeight.Black, color = Ink) } }
        Text("${"●".repeat(count)}${"○".repeat(3 - count)}", fontSize = 28.sp, color = Coral)
        Text("点一点击鼓，跟着读三次", fontSize = 21.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun RecognizePhase(
    symbol: String,
    guide: com.ziyin.island.content.PronunciationGuide,
    onMouthClick: () -> Unit,
    onSymbolClick: () -> Unit,
) {
    Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(18.dp)) {
        Surface(
            Modifier.weight(1f).fillMaxSize().clickable(onClick = onMouthClick),
            shape = RoundedCornerShape(30.dp),
            color = Color.White.copy(alpha = 0.9f),
        ) {
            val drawable = mouthDrawable(guide.visual)
            if (drawable != null) {
                Image(painterResource(drawable), "$symbol 口型示范", Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            } else {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text("👄", fontSize = 110.sp)
                    Text(symbol, fontSize = 94.sp, fontWeight = FontWeight.Black, color = Coral)
                }
            }
        }
        Surface(Modifier.weight(1f).fillMaxSize(), shape = RoundedCornerShape(30.dp), color = Color.White.copy(alpha = 0.92f)) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    symbol,
                    modifier = Modifier.clickable(onClick = onSymbolClick).padding(horizontal = 42.dp, vertical = 6.dp),
                    fontSize = 92.sp,
                    fontWeight = FontWeight.Black,
                    color = Coral,
                )
                CueRow("👄", guide.mouthCue)
                CueRow("👅", guide.tongueCue)
                CueRow("💨", guide.airflowCue)
                Surface(color = Sunflower.copy(alpha = 0.34f), shape = RoundedCornerShape(22.dp)) {
                    Text("看嘴型 · 听示范 · 再开口", Modifier.padding(16.dp), fontSize = 20.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
private fun CueRow(icon: String, text: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(icon, fontSize = 30.sp)
        Text(text, Modifier.weight(1f), fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Ink)
    }
}

@Composable
private fun TonePhase(
    lesson: PinyinLesson,
    tones: List<String>,
    selected: String,
    onOpenToneGame: () -> Unit,
    onSelect: (String) -> Unit,
) {
    val choices = tones.ifEmpty { lesson.symbols }
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(if (tones.isEmpty()) "点一点，听声音" else "四顶声调帽", fontSize = 30.sp, fontWeight = FontWeight.Black, color = Ink)
        Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
            choices.forEachIndexed { index, tone ->
                Surface(
                    modifier = Modifier.size(width = 128.dp, height = 122.dp).clickable { onSelect(tone) },
                    shape = RoundedCornerShape(28.dp),
                    color = if (selected == tone) Coral.copy(alpha = 0.75f) else listOf(Mint, Sunflower, SkyLight, Color(0xFFE8D6FF))[index % 4],
                    shadowElevation = 8.dp,
                ) { Box(contentAlignment = Alignment.Center) { Text(tone, fontSize = 58.sp, fontWeight = FontWeight.Black) } }
            }
        }
        if (tones.isNotEmpty()) {
            OutlinedButton(onClick = onOpenToneGame, shape = RoundedCornerShape(22.dp)) {
                Text("🎢  去玩声调过山车", Modifier.padding(horizontal = 18.dp, vertical = 5.dp), fontSize = 19.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun PracticePhase(
    target: String,
    lessonId: String,
    solved: Boolean,
    onListen: () -> Unit,
    onChoose: (String) -> Unit,
) {
    val pool = listOf(target, "a", "o", "e", "i", "u", "ü", "b", "p", "m", "f")
        .distinct().let { list ->
            val shift = kotlin.math.abs(lessonId.hashCode()) % list.size
            (list.drop(shift) + list.take(shift)).take(4).toMutableList().apply {
                if (target !in this) set(lastIndex, target)
            }
        }
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(22.dp)) {
        Text(if (solved) "找对啦！" else "听一听，找一找", fontSize = 30.sp, fontWeight = FontWeight.Black, color = Ink)
        Surface(Modifier.size(118.dp).clickable(onClick = onListen), shape = CircleShape, color = Sunflower, shadowElevation = 8.dp) {
            Box(contentAlignment = Alignment.Center) { Text(target, fontSize = 58.sp, fontWeight = FontWeight.Black, color = Ink) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            pool.forEach { choice ->
                Surface(
                    modifier = Modifier.size(128.dp).clickable { onChoose(choice) },
                    shape = RoundedCornerShape(28.dp),
                    color = if (solved && choice == target) Mint else Color.White.copy(alpha = 0.93f),
                    shadowElevation = 8.dp,
                ) { Box(contentAlignment = Alignment.Center) { Text(choice, fontSize = 60.sp, fontWeight = FontWeight.Black, color = Coral) } }
            }
        }
    }
}

@Composable
private fun WritePhase(symbol: String, onTracing: () -> Unit, onComplete: () -> Unit) {
    Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(22.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(
            Modifier.weight(1f).fillMaxSize().clickable(onClick = onTracing),
            color = Color.White.copy(alpha = 0.9f),
            shape = RoundedCornerShape(32.dp),
        ) {
            Box(contentAlignment = Alignment.Center) { Text(symbol, fontSize = 190.sp, fontWeight = FontWeight.Black, color = Coral.copy(alpha = 0.32f)) }
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Text("用手指描一描", fontSize = 30.sp, fontWeight = FontWeight.Black, color = Ink)
            Button(onClick = onTracing, Modifier.fillMaxWidth().height(66.dp), shape = RoundedCornerShape(24.dp)) {
                Text("✍  开始描写", fontSize = 21.sp, fontWeight = FontWeight.Black)
            }
            Button(onClick = onComplete, Modifier.fillMaxWidth().height(66.dp), shape = RoundedCornerShape(24.dp)) {
                Text("⭐  完成这一课", fontSize = 21.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

private fun mouthDrawable(visual: MouthVisual): Int? = when (visual) {
    MouthVisual.A -> R.drawable.mouth_a_v1
    MouthVisual.O -> R.drawable.mouth_o_v1
    MouthVisual.E -> R.drawable.mouth_e_v1
    MouthVisual.I -> R.drawable.mouth_i_v1
    MouthVisual.U -> R.drawable.mouth_u_v1
    MouthVisual.V -> R.drawable.mouth_v_v1
    else -> null
}

@Composable
fun ToneGameScreen(lesson: PinyinLesson, onBack: () -> Unit, onTracing: () -> Unit) {
    val speaker = LocalGuideSpeaker.current
    val base = lesson.symbols.firstOrNull()?.takeIf { it in toneForms }.orEmpty().ifEmpty { "a" }
    val tones = toneForms.getValue(base)
    var targetIndex by remember(lesson.id) { mutableIntStateOf(0) }
    var message by remember { mutableStateOf("听一听声调名字，再选择正确的字母。") }

    SceneBackground(R.drawable.page_tone_game_v1, "四声轨道游戏", overlayAlpha = 0.04f) {
        Column(Modifier.fillMaxSize()) {
            PageHeader("声调轨道", "第一声平、第二声扬、第三声拐弯、第四声降", onBack = onBack)
            Spacer(Modifier.weight(1f))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                color = Color.White.copy(alpha = 0.92f),
                shape = RoundedCornerShape(30.dp),
                shadowElevation = 10.dp,
            ) {
                Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("请找出第 ${targetIndex + 1} 声", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Ink)
                    Text(message, style = MaterialTheme.typography.bodyLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        tones.forEachIndexed { index, tone ->
                            Surface(
                                modifier = Modifier
                                    .size(88.dp)
                                    .clickable {
                                        if (index == targetIndex) {
                                            message = "找对了！声音沿着这条轨道走。"
                                            speaker.speak("找对了！这是第${targetIndex + 1}声。准备找下一声。", "tone-correct-$targetIndex")
                                            targetIndex = (targetIndex + 1) % 4
                                        } else {
                                            message = "再看看轨道的方向，慢慢来。"
                                            speaker.speak("还差一点。第一声平，第二声扬，第三声拐弯，第四声降。再试一次。", "tone-retry-$targetIndex-$index")
                                        }
                                    },
                                shape = CircleShape,
                                color = listOf(Mint, Sunflower, SkyLight, Coral)[index],
                                shadowElevation = 6.dp,
                            ) { Box(contentAlignment = Alignment.Center) { Text(tone, fontSize = 42.sp, fontWeight = FontWeight.Black) } }
                        }
                    }
                    Button(onClick = onTracing, shape = RoundedCornerShape(22.dp)) { Text("下一步：描一描", Modifier.padding(horizontal = 18.dp, vertical = 6.dp), fontSize = 19.sp) }
                }
            }
        }
    }
}

@Composable
fun TracingPracticeScreen(symbol: String, onBack: () -> Unit, onDone: () -> Unit) {
    val speaker = LocalGuideSpeaker.current
    val points = remember(symbol) { mutableStateListOf<Offset>() }
    var enough by remember(symbol) { mutableStateOf(false) }
    var hasStarted by remember(symbol) { mutableStateOf(false) }

    SceneBackground(R.drawable.page_tracing_v1, "海边描写花园", overlayAlpha = 0.02f) {
        Column(Modifier.fillMaxSize()) {
            PageHeader("手指描一描", "沿着浅色字形慢慢画，不用追求一次完美", onBack = onBack)
            Box(Modifier.weight(1f).padding(horizontal = 190.dp, vertical = 28.dp), contentAlignment = Alignment.Center) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(34.dp))
                        .background(Color.White.copy(alpha = 0.35f))
                        .border(3.dp, Coral.copy(alpha = 0.25f), RoundedCornerShape(34.dp))
                        .pointerInput(symbol) {
                            detectDragGestures(
                                onDragStart = {
                                    points.clear(); points.add(it); enough = false
                                    if (!hasStarted) {
                                        hasStarted = true
                                        speaker.speak("手指不要离开，沿着浅色字形慢慢走。", "tracing-start-$symbol")
                                    }
                                },
                                onDrag = { change, _ -> change.consume(); points.add(change.position); if (points.size > 20) enough = true },
                            )
                        },
                ) {
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            color = android.graphics.Color.argb(70, 51, 67, 91)
                            textAlign = android.graphics.Paint.Align.CENTER
                            textSize = size.minDimension * 0.72f
                            typeface = android.graphics.Typeface.DEFAULT_BOLD
                        }
                        drawText(symbol, center.x, center.y - (paint.ascent() + paint.descent()) / 2, paint)
                    }
                    if (points.size > 1) {
                        val path = Path().apply { moveTo(points.first().x, points.first().y); points.drop(1).forEach { lineTo(it.x, it.y) } }
                        drawPath(path, Coral, style = Stroke(18.dp.toPx(), cap = StrokeCap.Round))
                    }
                }
            }
            Row(Modifier.fillMaxWidth().padding(18.dp), horizontalArrangement = Arrangement.Center) {
                OutlinedButton(onClick = { points.clear(); enough = false }, shape = RoundedCornerShape(22.dp)) { Text("重新画") }
                Spacer(Modifier.width(14.dp))
                Button(onClick = {
                    speaker.speak("描写完成！你的小手很稳。", "tracing-done-$symbol")
                    onDone()
                }, enabled = enough, shape = RoundedCornerShape(22.dp)) { Text(if (enough) "画好了" else "先画一画", Modifier.padding(horizontal = 24.dp)) }
            }
        }
    }
}

@Composable
fun CharacterLearningScreen(lesson: CharacterLesson, onBack: () -> Unit, onTracing: () -> Unit, onComplete: () -> Unit) {
    val speaker = LocalGuideSpeaker.current
    SceneBackground(R.drawable.page_character_lesson_v1, "汉字学习小院", overlayAlpha = 0.03f) {
        Column(Modifier.fillMaxSize()) {
            PageHeader("认识汉字", "${lesson.category}主题", onBack = onBack)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                Surface(
                    Modifier.weight(1f).fillMaxSize().clickable {
                        speaker.speak(
                            "${lesson.character}。${lesson.meaning}。它读${lesson.pinyin}。小朋友，再点一次${lesson.character}，注意听一听。",
                            "character-core-${lesson.character}",
                        )
                    },
                    color = Color.White.copy(alpha = 0.88f),
                    shape = RoundedCornerShape(34.dp),
                ) {
                    Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(lesson.character, fontSize = 130.sp, fontWeight = FontWeight.Black, color = Coral)
                        Text(lesson.pinyin, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Ink)
                        Text(lesson.meaning, style = MaterialTheme.typography.titleLarge)
                        Text(lesson.picture, fontSize = 64.sp)
                    }
                }
                Column(Modifier.weight(1f).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Surface(color = Color.White.copy(alpha = 0.9f), shape = RoundedCornerShape(28.dp)) {
                        Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("放进词语里", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                lesson.words.forEach { word ->
                                    Surface(
                                        modifier = Modifier.clickable { speaker.speak("${word}。这个词语里有${lesson.character}。", "word-${lesson.character}-$word") },
                                        shape = RoundedCornerShape(20.dp),
                                        color = Sunflower.copy(alpha = 0.3f),
                                    ) {
                                        Text(word, Modifier.padding(16.dp), fontSize = 26.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Text("找一找：家里或窗外，哪里还能看到“${lesson.character}”？", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                    OutlinedButton(onClick = onTracing, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp)) { Text("描一描 ${lesson.character}", fontSize = 19.sp) }
                    Button(onClick = onComplete, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp)) { Text("我认识它了", Modifier.padding(vertical = 7.dp), fontSize = 20.sp) }
                }
            }
        }
    }
}
