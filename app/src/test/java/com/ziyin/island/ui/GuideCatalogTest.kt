package com.ziyin.island.ui

import com.ziyin.island.content.Curriculum
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class GuideCatalogTest {
    @Test
    fun everyStandaloneScreenHasAnEntryPrompt() {
        val pinyin = Curriculum.pinyinLessons.first()
        val character = Curriculum.characterLessons.first()
        val story = Curriculum.stories.first()

        AppScreen.entries.forEach { screen ->
            val prompt = GuideCatalog.forScreen(screen, pinyin, character, story, "a")
            if (screen == AppScreen.PINYIN_LESSON) {
                assertNull("lesson uses phase-specific prompts", prompt)
            } else {
                assertFalse("missing entry prompt for $screen", prompt?.text.isNullOrBlank())
            }
        }
    }
}
