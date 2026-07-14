package com.ziyin.island.speech

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.SystemClock
import java.io.File
import kotlin.math.max
import kotlin.math.sqrt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

sealed interface AudioPracticeState {
    data object Idle : AudioPracticeState
    data object Recording : AudioPracticeState
    data object PreparingPlayback : AudioPracticeState
    data class Playing(val feedback: PracticeFeedback) : AudioPracticeState
    data class Recorded(val feedback: PracticeFeedback) : AudioPracticeState
    data class Error(val message: String) : AudioPracticeState
}

class AudioPracticeManager(
    private val context: Context,
    private val signalQualityEvaluator: SignalQualityEvaluator = SignalQualityEvaluator(),
) {
    private val _state = MutableStateFlow<AudioPracticeState>(AudioPracticeState.Idle)
    val state: StateFlow<AudioPracticeState> = _state.asStateFlow()
    private val _amplitudeLevel = MutableStateFlow(0)
    val amplitudeLevel: StateFlow<Int> = _amplitudeLevel.asStateFlow()

    private val samplingScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var samplingJob: Job? = null
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var recordingStartedAt = 0L
    private var latestFeedback: PracticeFeedback? = null
    @Volatile private var peakAmplitudeObserved = 0
    private val recordingFile: File
        get() = File(File(context.cacheDir, "voice_practice").apply { mkdirs() }, "latest.m4a")

    fun startRecording() {
        if (_state.value is AudioPracticeState.Recording) return
        releasePlayer()
        releaseRecorder()
        recordingFile.delete()

        runCatching {
            createRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioSamplingRate(16_000)
                setAudioEncodingBitRate(64_000)
                setOutputFile(recordingFile.absolutePath)
                prepare()
                start()
            }.also { recorder = it }
            recordingStartedAt = SystemClock.elapsedRealtime()
            startAmplitudeSampling()
            _state.value = AudioPracticeState.Recording
        }.onFailure {
            releaseRecorder()
            _state.value = AudioPracticeState.Error("暂时无法开始录音，请检查麦克风后再试。")
        }
    }

    fun stopRecording() {
        val activeRecorder = recorder ?: return
        val durationMs = SystemClock.elapsedRealtime() - recordingStartedAt
        val peakAmplitude = max(peakAmplitudeObserved, runCatching { activeRecorder.maxAmplitude }.getOrDefault(0))
        stopAmplitudeSampling()

        runCatching { activeRecorder.stop() }
            .onSuccess {
                releaseRecorder()
                latestFeedback = signalQualityEvaluator.evaluate(durationMs, peakAmplitude)
                _state.value = AudioPracticeState.Recorded(latestFeedback!!)
            }
            .onFailure {
                releaseRecorder()
                recordingFile.delete()
                _state.value = AudioPracticeState.Error("这次录音太短了，准备好后再读一次吧。")
            }
    }

    fun playLatestRecording() {
        val feedback = latestFeedback
        if (!recordingFile.exists() || feedback == null) {
            _state.value = AudioPracticeState.Error("还没有可以播放的录音。")
            return
        }

        releasePlayer()
        _state.value = AudioPracticeState.PreparingPlayback
        runCatching {
            MediaPlayer().apply {
                setDataSource(recordingFile.absolutePath)
                setOnPreparedListener { prepared ->
                    _state.value = AudioPracticeState.Playing(feedback)
                    prepared.start()
                }
                setOnCompletionListener {
                    releasePlayer()
                    _state.value = AudioPracticeState.Recorded(feedback)
                }
                setOnErrorListener { _, _, _ ->
                    releasePlayer()
                    _state.value = AudioPracticeState.Error("播放没有成功，请重新录一次。")
                    true
                }
                prepareAsync()
            }.also { player = it }
        }.onFailure {
            releasePlayer()
            _state.value = AudioPracticeState.Error("播放没有成功，请重新录一次。")
        }
    }

    fun reportPermissionDenied() {
        _state.value = AudioPracticeState.Error("需要家长允许麦克风权限，才能进行跟读练习。")
    }

    fun reset() {
        stopAmplitudeSampling()
        releasePlayer()
        releaseRecorder()
        latestFeedback = null
        recordingFile.delete()
        _state.value = AudioPracticeState.Idle
    }

    fun release() {
        stopAmplitudeSampling()
        releasePlayer()
        releaseRecorder()
        recordingFile.delete()
        samplingScope.cancel()
    }

    @Suppress("DEPRECATION")
    private fun createRecorder(): MediaRecorder =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) MediaRecorder(context) else MediaRecorder()

    private fun releaseRecorder() {
        runCatching { recorder?.release() }
        recorder = null
    }

    private fun startAmplitudeSampling() {
        stopAmplitudeSampling()
        peakAmplitudeObserved = 0
        samplingJob = samplingScope.launch {
            while (isActive) {
                val amplitude = runCatching { recorder?.maxAmplitude ?: 0 }.getOrDefault(0)
                peakAmplitudeObserved = max(peakAmplitudeObserved, amplitude)
                _amplitudeLevel.value = (sqrt(amplitude.coerceIn(0, 32_767) / 32_767f) * 100)
                    .toInt()
                    .coerceIn(0, 100)
                delay(80)
            }
        }
    }

    private fun stopAmplitudeSampling() {
        samplingJob?.cancel()
        samplingJob = null
        _amplitudeLevel.value = 0
    }

    private fun releasePlayer() {
        runCatching { player?.release() }
        player = null
    }
}
