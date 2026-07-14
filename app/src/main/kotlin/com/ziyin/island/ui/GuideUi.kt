package com.ziyin.island.ui

import androidx.compose.runtime.compositionLocalOf
import com.ziyin.island.content.CharacterLesson
import com.ziyin.island.content.PinyinLesson
import com.ziyin.island.content.StoryLesson

interface GuideSpeaker {
    fun speak(text: String, key: String = text)
    fun replay()
    fun stop()
}

val LocalGuideSpeaker = compositionLocalOf<GuideSpeaker> {
    error("GuideSpeaker is not provided")
}

data class GuidePrompt(val key: String, val text: String)

object GuideCatalog {
    fun forScreen(
        screen: AppScreen,
        pinyin: PinyinLesson,
        character: CharacterLesson,
        story: StoryLesson,
        tracingSymbol: String,
    ): GuidePrompt? = when (screen) {
        AppScreen.ONBOARDING -> prompt("onboarding", "小朋友，欢迎来到字音岛。点一点小芽鸟，和它一起出发吧。")
        AppScreen.HOME -> prompt("home", "这里是字音岛。点一点声音森林、拼音工坊、汉字小镇或故事剧场，选择今天的学习。")
        AppScreen.SOUND_FOREST -> prompt("sound-forest", "小朋友，点一首儿歌，注意听一听，再跟着中央的小鼓拍拍手。")
        AppScreen.PINYIN_MAP -> prompt("pinyin-map", "小朋友，点一张课程卡，听清今天要学习的声音。")
        AppScreen.PINYIN_LESSON -> null
        AppScreen.TONE_GAME -> prompt("tone-${pinyin.id}", "来玩声调过山车。我说第几声，你就点那一个。第一声平，第二声扬，第三声拐弯，第四声降。")
        AppScreen.TRACING -> prompt("trace-$tracingSymbol", "小朋友，用手指沿着浅色的 $tracingSymbol 慢慢画。画错没关系，可以重新画。")
        AppScreen.LITERACY_TOWN -> prompt("literacy-town", "欢迎来到汉字小镇。点一张汉字卡，听听它叫什么。")
        AppScreen.CHARACTER_LESSON -> prompt("character-${character.character}", "小朋友，点一点${character.character}，听一听。再点图片、拼音和词语，认识这个汉字。")
        AppScreen.REVIEW_GAMES -> prompt("review", "这里是复习游乐场。先选一个小游戏，听清问题，再点你的答案。")
        AppScreen.STORY_THEATER -> prompt("story-theater", "欢迎来到故事剧场。点一张故事海报，小芽鸟就会给你讲故事。")
        AppScreen.STORY_READER -> prompt("story-${story.id}", "故事《${story.title}》开始啦。点一点击故事正文，可以再听一遍。")
        AppScreen.ACHIEVEMENTS -> prompt("achievements", "这里是成长花园。点一张拼音、汉字或故事成长卡，听听你的进步。")
        AppScreen.PARENT -> prompt("parent", "这里是家长中心，需要请爸爸妈妈来操作。小朋友可以先回到字音岛。")
    }

    private fun prompt(key: String, text: String) = GuidePrompt(key, text)
}
