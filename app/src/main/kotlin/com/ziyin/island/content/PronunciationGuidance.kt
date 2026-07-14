package com.ziyin.island.content

enum class MouthVisual {
    A, O, E, I, U, V,
    LIPS_CLOSED, LIP_TEETH, TONGUE_TIP, TONGUE_BACK, TONGUE_FRONT, RETROFLEX, GENERIC,
}

data class PronunciationGuide(
    val symbol: String,
    val mouthCue: String,
    val tongueCue: String,
    val airflowCue: String,
    val childPrompt: String,
    val visual: MouthVisual,
)

enum class LessonPhase(val label: String, val icon: String) {
    PLAY("玩", "🎈"),
    RECOGNIZE("认", "👀"),
    TONE("调", "🎢"),
    SPEAK("说", "🎙"),
    PRACTICE("练", "🎯"),
    WRITE("写", "✍"),
}

object PronunciationGuidance {
    private val direct = mapOf(
        "a" to guide("a", "嘴巴张大，像打开一扇大门。", "舌头放平，轻轻躺在下面。", "气流顺顺地出来。", "看着小朋友的嘴巴。张大嘴巴，跟我读：啊——，a。", MouthVisual.A),
        "o" to guide("o", "嘴唇拢圆，像一个小圆圈。", "舌头稍稍往后缩。", "气流从圆圆的嘴唇出来。", "把嘴唇变成圆圆的小洞，跟我读：喔——，o。", MouthVisual.O),
        "e" to guide("e", "嘴巴半开，嘴唇不要拢圆。", "舌头后面轻轻抬起。", "声音从喉咙里送出来。", "嘴巴扁扁半打开，像白鹅唱歌，跟我读：e——。", MouthVisual.E),
        "i" to guide("i", "牙齿靠近，嘴角向两边展开。", "舌头前面抬高。", "气流从窄窄的缝里出来。", "嘴角向两边，像轻轻微笑，跟我读：衣——，i。", MouthVisual.I),
        "u" to guide("u", "嘴唇拢圆，向前突出。", "舌头后面抬高。", "气流从小圆洞里出来。", "嘴唇向前，像要吹小口哨，跟我读：乌——，u。", MouthVisual.U),
        "ü" to guide("ü", "嘴唇像 u 一样拢圆向前。", "舌头却像 i 一样放在前面。", "气流从圆圆的小口出来。", "先摆好 i 的舌头，再把嘴唇拢圆，跟我读：迂——，ü。", MouthVisual.V),

        "b" to guide("b", "双唇先轻轻闭住，再突然打开。", "舌头自然放松。", "气流短短的，不要用力吹。", "双唇闭住再打开，轻轻读：玻，b。", MouthVisual.LIPS_CLOSED),
        "p" to guide("p", "双唇先闭住，再突然打开。", "舌头自然放松。", "送出一股明显的气流。", "把小手放在嘴前，双唇打开时要有风，读：坡，p。", MouthVisual.LIPS_CLOSED),
        "m" to guide("m", "双唇闭紧。", "舌头自然放松。", "声音从鼻子里轻轻出来。", "闭上嘴巴，让鼻子轻轻振动，读：摸，m。", MouthVisual.LIPS_CLOSED),
        "f" to guide("f", "上牙轻轻碰下嘴唇。", "舌头自然放松。", "气流从牙齿和嘴唇中间摩擦出来。", "上牙轻碰下嘴唇，像小风吹过，读：佛，f。", MouthVisual.LIP_TEETH),
        "d" to tongueTip("d", "舌尖碰上牙床，马上放开，气流短短的。", "得，d"),
        "t" to tongueTip("t", "舌尖碰上牙床，放开时送出一股气。", "特，t"),
        "n" to tongueTip("n", "舌尖顶住上牙床，让声音从鼻子出来。", "讷，n"),
        "l" to tongueTip("l", "舌尖顶住上牙床，让气流从舌头两边出来。", "勒，l"),
        "g" to tongueBack("g", "舌根顶住软腭，马上放开，气流短短的。", "哥，g"),
        "k" to tongueBack("k", "舌根顶住软腭，放开时送出明显的气。", "科，k"),
        "h" to tongueBack("h", "舌根靠近软腭，让气流摩擦出来。", "喝，h"),
        "j" to tongueFront("j", "舌面前部贴近硬腭，轻轻放开，不卷舌。", "鸡，j"),
        "q" to tongueFront("q", "舌面前部贴近硬腭，放开时要送气，不卷舌。", "七，q"),
        "x" to tongueFront("x", "舌面前部靠近硬腭，留一条小缝让气流出来。", "西，x"),
        "zh" to retroflex("zh", "舌尖翘起靠近上腭，放开时气流短短的。", "知，zh"),
        "ch" to retroflex("ch", "舌尖翘起靠近上腭，放开时送出一股气。", "吃，ch"),
        "sh" to retroflex("sh", "舌尖翘起靠近上腭，留小缝让气流出来。", "诗，sh"),
        "r" to retroflex("r", "舌尖轻轻翘起，声音带一点摩擦，不要碰住上腭。", "日，r"),
        "z" to tongueTip("z", "舌尖平平地靠近上牙背，马上放开，不卷舌。", "资，z"),
        "c" to tongueTip("c", "舌尖平平地靠近上牙背，放开时送气，不卷舌。", "刺，c"),
        "s" to tongueTip("s", "舌尖靠近上牙背，留小缝让气流摩擦出来。", "丝，s"),
        "y" to guide("y", "嘴角向两边展开。", "舌头前面抬高。", "声音轻轻连向后面的韵母。", "像读 i 一样准备好，轻轻读：衣，y。", MouthVisual.I),
        "w" to guide("w", "嘴唇拢圆向前。", "舌头后面抬高。", "声音轻轻连向后面的韵母。", "像读 u 一样准备好，轻轻读：乌，w。", MouthVisual.U),
    )

    private val finals = mapOf(
        "ai" to "先读 a，嘴形滑向 i：a—i，ai。",
        "ei" to "先读 e，嘴形滑向 i：e—i，ei。",
        "ui" to "先读 u，快速滑向 i：u—i，ui。",
        "ao" to "先张大读 a，再把嘴唇拢圆：a—o，ao。",
        "ou" to "先拢圆读 o，再轻轻滑向 u：o—u，ou。",
        "iu" to "先读 i，再快速滑向 u：i—u，iu。",
        "ie" to "先读 i，再滑向 e：i—e，ie。",
        "üe" to "先读 ü，再滑向 e：ü—e，üe。",
        "er" to "嘴巴半开，舌尖轻轻卷起，读：er。",
        "an" to "先读 a，最后舌尖顶住上牙床，让声音走进鼻子：an。",
        "en" to "先读 e，最后舌尖顶住上牙床，让声音走进鼻子：en。",
        "in" to "先读 i，最后舌尖顶住上牙床，让声音走进鼻子：in。",
        "un" to "先读 u，最后舌尖顶住上牙床，让声音走进鼻子：un。",
        "ün" to "先读 ü，最后舌尖顶住上牙床，让声音走进鼻子：ün。",
        "ang" to "先读 a，最后舌根抬起，让声音走进鼻子：ang。",
        "eng" to "先读 e，最后舌根抬起，让声音走进鼻子：eng。",
        "ing" to "先读 i，最后舌根抬起，让声音走进鼻子：ing。",
        "ong" to "嘴唇拢圆，最后舌根抬起，让声音走进鼻子：ong。",
    )

    fun forSymbol(raw: String): PronunciationGuide {
        val symbol = normalize(raw)
        direct[symbol]?.let { return it }
        finals[symbol]?.let { prompt ->
            return guide(symbol, "嘴形要连续变化，中间不要停。", "舌头跟着声音平滑移动。", "一口气连起来。", prompt, visualFor(symbol.firstOrNull()))
        }
        if (symbol in listOf("zhi", "chi", "shi", "ri", "zi", "ci", "si", "yi", "wu", "yu", "ye", "yue", "yuan", "yin", "yun", "ying")) {
            val lead = when {
                symbol.startsWith("zh") -> "zh"
                symbol.startsWith("ch") -> "ch"
                symbol.startsWith("sh") -> "sh"
                else -> symbol.take(1)
            }
            val base = direct[lead] ?: direct.getValue("a")
            return base.copy(symbol = symbol, childPrompt = "这是整体认读音节 $symbol。不要拆开，一口气跟我读：$symbol。")
        }
        if (symbol.length > 1 && symbol.first().isLetter()) {
            val base = direct[symbol.take(1)] ?: direct.getValue("a")
            return base.copy(symbol = symbol, childPrompt = "先听老师示范，再把声音连起来，一口气读：$symbol。")
        }
        return guide(symbol, "看清楚老师的嘴巴。", "舌头放松。", "先听，再模仿。", "先点小喇叭听一听，再跟着读一遍。", MouthVisual.GENERIC)
    }

    private fun normalize(raw: String): String = raw.lowercase()
        .replace(Regex("[āáǎà]"), "a").replace(Regex("[ōóǒò]"), "o")
        .replace(Regex("[ēéěè]"), "e").replace(Regex("[īíǐì]"), "i")
        .replace(Regex("[ūúǔù]"), "u").replace(Regex("[ǖǘǚǜ]"), "ü")

    private fun visualFor(first: Char?): MouthVisual = when (first) {
        'a' -> MouthVisual.A; 'o' -> MouthVisual.O; 'e' -> MouthVisual.E
        'i' -> MouthVisual.I; 'u' -> MouthVisual.U; 'ü' -> MouthVisual.V
        else -> MouthVisual.GENERIC
    }

    private fun guide(symbol: String, mouth: String, tongue: String, air: String, prompt: String, visual: MouthVisual) =
        PronunciationGuide(symbol, mouth, tongue, air, prompt, visual)

    private fun tongueTip(symbol: String, detail: String, sound: String) =
        guide(symbol, "嘴唇自然打开。", detail, "留意有没有送气。", "看着嘴巴和舌尖，跟我读：$sound。", MouthVisual.TONGUE_TIP)

    private fun tongueBack(symbol: String, detail: String, sound: String) =
        guide(symbol, "嘴唇自然打开。", detail, "气流从口腔后面出来。", "看着老师的嘴巴，跟我读：$sound。", MouthVisual.TONGUE_BACK)

    private fun tongueFront(symbol: String, detail: String, sound: String) =
        guide(symbol, "嘴角稍微向两边。", detail, "气流从舌面前方出来。", "不要卷舌，跟我读：$sound。", MouthVisual.TONGUE_FRONT)

    private fun retroflex(symbol: String, detail: String, sound: String) =
        guide(symbol, "嘴唇稍稍向前。", detail, "气流从翘起的舌尖附近出来。", "把舌尖轻轻翘起来，跟我读：$sound。", MouthVisual.RETROFLEX)
}
