package com.ziyin.island.speech

enum class PracticeFeedbackKind {
    READY,
    TOO_SHORT,
    TOO_SOFT,
}

data class PracticeFeedback(
    val kind: PracticeFeedbackKind,
    val title: String,
    val message: String,
)

/**
 * Checks whether a recording is usable. This deliberately does not claim to
 * judge pronunciation accuracy; that requires a validated child speech model.
 */
class SignalQualityEvaluator {
    fun evaluate(durationMs: Long, peakAmplitude: Int): PracticeFeedback = when {
        durationMs < MIN_DURATION_MS -> PracticeFeedback(
            PracticeFeedbackKind.TOO_SHORT,
            "再读完整一点",
            "刚才的声音有点短，慢慢读完再停下来。",
        )

        peakAmplitude < MIN_PEAK_AMPLITUDE -> PracticeFeedback(
            PracticeFeedbackKind.TOO_SOFT,
            "声音再响亮一点",
            "把平板放近一些，用自然的声音再试一次。",
        )

        else -> PracticeFeedback(
            PracticeFeedbackKind.READY,
            "录得很清楚",
            "先听听自己的声音，再和标准音比较。",
        )
    }

    private companion object {
        const val MIN_DURATION_MS = 450L
        const val MIN_PEAK_AMPLITUDE = 650
    }
}

