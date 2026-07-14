package com.ziyin.island.speech

import android.content.Context
import android.media.MediaPlayer
import org.json.JSONObject

data class StandardAudioEntry(
    val display: String,
    val reference: String,
    val assetPath: String,
)

data class StandardAudioPack(
    val schemaVersion: Int,
    val packId: String,
    val status: String,
    val replaceBeforeCommercialRelease: Boolean,
    val entries: Map<String, StandardAudioEntry>,
) {
    fun validationError(): String? = when {
        schemaVersion != 1 -> "不支持的标准音资源版本"
        packId.isBlank() -> "标准音资源包缺少编号"
        entries.isEmpty() -> "标准音资源包中没有音频"
        entries.values.any { it.display.isBlank() || it.reference.isBlank() || it.assetPath.isBlank() } ->
            "标准音资源清单不完整"
        else -> null
    }
}

sealed interface StandardAudioState {
    data object Idle : StandardAudioState
    data class Loading(val target: String) : StandardAudioState
    data class Playing(val target: String) : StandardAudioState
    data class Error(val message: String) : StandardAudioState
}

object StandardAudioTargets {
    private val targets = setOf(
        "ā", "á", "ǎ", "à", "ō", "ó", "ǒ", "ò", "ē", "é", "ě", "è",
        "ī", "í", "ǐ", "ì", "ū", "ú", "ǔ", "ù", "ǖ", "ǘ", "ǚ", "ǜ",
        "b", "p", "m", "f", "d", "t", "n", "l", "g", "k", "h", "j", "q", "x",
        "zh", "ch", "sh", "r", "z", "c", "s", "y", "w",
        "ai", "ei", "ui", "ao", "ou", "iu", "ie", "üe", "er",
        "an", "en", "in", "un", "ün", "ang", "eng", "ing", "ong",
        "zhi", "chi", "shi", "ri", "zi", "ci", "si", "yi", "wu", "yu",
        "ye", "yue", "yuan", "yin", "yun", "ying",
    )

    fun contains(target: String): Boolean = target in targets

    fun firstToneFor(base: String): String? = when (base) {
        "a" -> "ā"; "o" -> "ō"; "e" -> "ē"; "i" -> "ī"; "u" -> "ū"; "ü" -> "ǖ"
        else -> base.takeIf(::contains)
    }
}

class StandardAudioPlayer(private val context: Context) {
    private val _state = kotlinx.coroutines.flow.MutableStateFlow<StandardAudioState>(StandardAudioState.Idle)
    val state: kotlinx.coroutines.flow.StateFlow<StandardAudioState> = _state

    private val pack: StandardAudioPack by lazy(::loadPack)
    private var player: MediaPlayer? = null

    fun play(target: String) {
        stop()
        val validationError = runCatching { pack.validationError() }.getOrElse {
            _state.value = StandardAudioState.Error("标准音资源暂时不可用。")
            return
        }
        if (validationError != null) {
            _state.value = StandardAudioState.Error(validationError)
            return
        }
        val entry = pack.entries[target]
        if (entry == null) {
            _state.value = StandardAudioState.Error("这个声音还在准备中。")
            return
        }

        _state.value = StandardAudioState.Loading(target)
        runCatching {
            val descriptor = context.assets.openFd(entry.assetPath)
            MediaPlayer().apply {
                descriptor.use { setDataSource(it.fileDescriptor, it.startOffset, it.length) }
                setOnPreparedListener {
                    _state.value = StandardAudioState.Playing(target)
                    it.start()
                }
                setOnCompletionListener {
                    releasePlayer()
                    _state.value = StandardAudioState.Idle
                }
                setOnErrorListener { _, _, _ ->
                    releasePlayer()
                    _state.value = StandardAudioState.Error("标准音没有播放成功，请再试一次。")
                    true
                }
                prepareAsync()
            }.also { player = it }
        }.onFailure {
            releasePlayer()
            _state.value = StandardAudioState.Error("标准音没有播放成功，请再试一次。")
        }
    }

    fun stop() {
        releasePlayer()
        _state.value = StandardAudioState.Idle
    }

    fun release() = stop()

    private fun loadPack(): StandardAudioPack {
        val json = context.assets.open("audio/pinyin/manifest.json")
            .bufferedReader(Charsets.UTF_8)
            .use { JSONObject(it.readText()) }
        val entriesJson = json.getJSONArray("entries")
        val entries = buildMap {
            repeat(entriesJson.length()) { index ->
                val item = entriesJson.getJSONObject(index)
                val entry = StandardAudioEntry(
                    display = item.getString("display"),
                    reference = item.getString("reference"),
                    assetPath = item.getString("file"),
                )
                require(put(entry.display, entry) == null) { "Duplicate standard audio: ${entry.display}" }
            }
        }
        return StandardAudioPack(
            schemaVersion = json.getInt("schemaVersion"),
            packId = json.getString("packId"),
            status = json.getString("status"),
            replaceBeforeCommercialRelease = json.optBoolean("replaceBeforeCommercialRelease", false),
            entries = entries,
        )
    }

    private fun releasePlayer() {
        runCatching { player?.release() }
        player = null
    }
}
