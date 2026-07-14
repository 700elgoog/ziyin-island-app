package com.ziyin.island.speech

import java.io.File

/** Temporary Tencent Cloud credentials issued by the app's own backend. */
data class TencentEvaluationSession(
    val appId: String,
    val secretId: String,
    val token: String,
    val expiresAtEpochSeconds: Long,
)

fun interface EvaluationSessionProvider {
    suspend fun fetchTemporarySession(): TencentEvaluationSession?
}

data class TencentSoeRawResult(
    val accuracy: Int,
    val tone: Int?,
    val confidence: Float,
)

fun interface TencentSoeEngine {
    suspend fun evaluate(
        session: TencentEvaluationSession,
        numericReference: String,
        audioFile: File,
    ): TencentSoeRawResult
}

object TencentPinyinReferenceMapper {
    private val markedVowels = mapOf(
        'ā' to "a1", 'á' to "a2", 'ǎ' to "a3", 'à' to "a4",
        'ō' to "o1", 'ó' to "o2", 'ǒ' to "o3", 'ò' to "o4",
        'ē' to "e1", 'é' to "e2", 'ě' to "e3", 'è' to "e4",
        'ī' to "i1", 'í' to "i2", 'ǐ' to "i3", 'ì' to "i4",
        'ū' to "u1", 'ú' to "u2", 'ǔ' to "u3", 'ù' to "u4",
        'ǖ' to "v1", 'ǘ' to "v2", 'ǚ' to "v3", 'ǜ' to "v4",
    )

    @JvmStatic
    fun toNumeric(reference: String): String? = markedVowels[reference.singleOrNull()]
}

class TencentSandboxPronunciationEvaluator(
    private val sessionProvider: EvaluationSessionProvider,
    private val engine: TencentSoeEngine,
    private val nowEpochSeconds: () -> Long = { System.currentTimeMillis() / 1_000L },
) : PronunciationEvaluator {
    override suspend fun evaluate(request: PronunciationRequest): PronunciationEvaluation {
        val numericReference = TencentPinyinReferenceMapper.toNumeric(request.referencePinyin)
            ?: return PronunciationEvaluation.Unavailable("这个发音还在准备评测，先听标准音再试一次吧。")
        val session = runCatching { sessionProvider.fetchTemporarySession() }.getOrNull()
            ?: return PronunciationEvaluation.Unavailable("评测暂时休息了，先听听自己的声音吧。")
        if (session.token.isBlank() || session.expiresAtEpochSeconds <= nowEpochSeconds() + 30L) {
            return PronunciationEvaluation.Unavailable("评测暂时休息了，先听听自己的声音吧。")
        }

        val result = runCatching { engine.evaluate(session, numericReference, request.audioFile) }.getOrNull()
            ?: return PronunciationEvaluation.Unavailable("网络开小差了，这次先听听自己的声音吧。")
        val confidence = result.confidence.coerceIn(0f, 1f)
        val hint = if (confidence < 0.65f) {
            "我还没听清，再听一次标准音，慢慢读就好。"
        } else if (result.accuracy >= 80) {
            "读得真清楚，声调也很有精神！"
        } else {
            "再听一次标准音，跟着声音慢慢读。"
        }
        return PronunciationEvaluation.Scored(
            accuracy = result.accuracy.coerceIn(0, 100),
            tone = result.tone?.coerceIn(0, 100),
            confidence = confidence,
            childFriendlyHint = hint,
        )
    }
}
