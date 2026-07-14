package com.ziyin.island.content

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test

class PronunciationGuidanceTest {
    @Test
    fun everyCurriculumSymbolHasSpokenArticulationGuidance() {
        Curriculum.pinyinLessons.flatMap { it.symbols }.distinct().forEach { symbol ->
            val guide = PronunciationGuidance.forSymbol(symbol)
            assertFalse("missing child prompt for $symbol", guide.childPrompt.isBlank())
            assertFalse("missing mouth cue for $symbol", guide.mouthCue.isBlank())
            assertFalse("missing tongue cue for $symbol", guide.tongueCue.isBlank())
            assertFalse("missing airflow cue for $symbol", guide.airflowCue.isBlank())
            assertNotNull(guide.visual)
        }
    }

    @Test
    fun sixCoreVowelsHaveDedicatedMouthVisuals() {
        val vowels = listOf("a", "o", "e", "i", "u", "ü")
        val visuals = vowels.map { PronunciationGuidance.forSymbol(it).visual }
        assertFalse(visuals.any { it == MouthVisual.GENERIC })
        assertFalse(visuals.distinct().size != vowels.size)
    }
}
