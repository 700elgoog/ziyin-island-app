package com.ziyin.island.ui

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VoiceUiContractTest {
    @Test
    fun manifestDeclaresAndroidTextToSpeechService() {
        val manifest = File("src/main/AndroidManifest.xml").readText()
        assertTrue(manifest.contains("android.intent.action.TTS_SERVICE"))
    }

    @Test
    fun appHasNoGlobalBottomVoiceBar() {
        val app = File("src/main/kotlin/com/ziyin/island/ui/ZiyinApp.kt").readText()
        val guideUi = File("src/main/kotlin/com/ziyin/island/ui/GuideUi.kt").readText()
        assertFalse(app.contains("bottomBar ="))
        assertFalse(app.contains("GuideBar("))
        assertFalse(guideUi.contains("fun GuideBar("))
    }

    @Test
    fun coreLearningElementsOwnTheirVoiceClicks() {
        val learning = File("src/main/kotlin/com/ziyin/island/ui/LearningScreens.kt").readText()
        assertTrue(learning.contains("onMouthClick"))
        assertTrue(learning.contains("onSymbolClick"))
        assertTrue(learning.contains("clickable(onClick = onListen)"))
        assertTrue(learning.contains("character-core-"))
    }
}
