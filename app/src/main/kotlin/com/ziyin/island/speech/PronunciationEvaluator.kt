package com.ziyin.island.speech

import java.io.File

data class PronunciationRequest(
    val lessonId: String,
    val referencePinyin: String,
    val audioFile: File,
    val locale: String = "zh-CN",
)

sealed interface PronunciationEvaluation {
    data class Scored(
        val accuracy: Int,
        val tone: Int?,
        val confidence: Float,
        val childFriendlyHint: String,
    ) : PronunciationEvaluation

    data class Unavailable(val childFriendlyHint: String) : PronunciationEvaluation
}

/**
 * Vendor-neutral boundary for a future child pronunciation service.
 *
 * Cloud implementations must be invoked only after separate guardian consent,
 * and must not retain the source audio longer than the declared purpose needs.
 */
fun interface PronunciationEvaluator {
    suspend fun evaluate(request: PronunciationRequest): PronunciationEvaluation
}

class NotConfiguredPronunciationEvaluator : PronunciationEvaluator {
    override suspend fun evaluate(request: PronunciationRequest): PronunciationEvaluation =
        PronunciationEvaluation.Unavailable("先听一听自己的声音，标准发音评测正在准备中。")
}

