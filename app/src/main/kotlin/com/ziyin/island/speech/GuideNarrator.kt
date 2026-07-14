package com.ziyin.island.speech

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class GuideVoiceState(
    val text: String = "",
    val speaking: Boolean = false,
    val ready: Boolean = false,
    val error: String? = null,
)

class GuideNarrator(context: Context) : TextToSpeech.OnInitListener {
    private val appContext = context.applicationContext
    private val audioManager = appContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build(),
        )
        .setOnAudioFocusChangeListener { change ->
            if (change == AudioManager.AUDIOFOCUS_LOSS || change == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) stop()
        }
        .build()
    private val _state = MutableStateFlow(GuideVoiceState())
    val state: StateFlow<GuideVoiceState> = _state.asStateFlow()
    private var pending: Pair<String, String>? = null
    private var initialized = false
    private var released = false
    private val tts = TextToSpeech(appContext, this)

    override fun onInit(status: Int) {
        if (released) return
        if (status != TextToSpeech.SUCCESS) {
            _state.value = _state.value.copy(error = "系统语音没有启动，请在平板设置中启用中文文字转语音。")
            return
        }
        val languageReady = listOf(Locale.SIMPLIFIED_CHINESE, Locale.CHINA, Locale.CHINESE, Locale.getDefault())
            .distinct()
            .any { locale ->
                val result = tts.setLanguage(locale)
                result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
            }
        if (!languageReady) {
            _state.value = _state.value.copy(error = "平板缺少中文语音包，请在文字转语音设置中下载中文语音。")
            return
        }
        initialized = true
        tts.setSpeechRate(0.86f)
        tts.setPitch(1.05f)
        tts.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build(),
        )
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) { _state.value = _state.value.copy(speaking = true) }
            override fun onDone(utteranceId: String?) { finishSpeaking() }
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) { reportPlaybackError() }
            override fun onError(utteranceId: String?, errorCode: Int) { reportPlaybackError() }
        })
        _state.value = _state.value.copy(ready = true, error = null)
        pending?.also { (text, key) -> pending = null; speak(text, key) }
    }

    fun speak(text: String, key: String = UUID.randomUUID().toString()) {
        if (released || text.isBlank()) return
        _state.value = GuideVoiceState(text = text, speaking = initialized, ready = initialized)
        if (!initialized) {
            pending = text to key
            return
        }
        audioManager.requestAudioFocus(focusRequest)
        val result = tts.speak(text, TextToSpeech.QUEUE_FLUSH, Bundle(), "guide-$key")
        if (result == TextToSpeech.ERROR) reportPlaybackError()
    }

    fun replay() {
        val text = _state.value.text
        if (text.isNotBlank()) speak(text)
    }

    fun stop() {
        pending = null
        if (initialized) tts.stop()
        finishSpeaking()
    }

    fun release() {
        released = true
        pending = null
        tts.stop()
        tts.shutdown()
        audioManager.abandonAudioFocusRequest(focusRequest)
    }

    private fun finishSpeaking() {
        _state.value = _state.value.copy(speaking = false)
        audioManager.abandonAudioFocusRequest(focusRequest)
    }

    private fun reportPlaybackError() {
        _state.value = _state.value.copy(
            speaking = false,
            error = "语音没有播放成功，请检查平板的媒体音量和中文文字转语音设置。",
        )
        audioManager.abandonAudioFocusRequest(focusRequest)
    }
}
