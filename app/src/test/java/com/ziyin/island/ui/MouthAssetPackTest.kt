package com.ziyin.island.ui

import java.io.File
import javax.imageio.ImageIO
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MouthAssetPackTest {
    @Test
    fun sixImage2MouthCardsAreSquareAndPresent() {
        listOf("a", "o", "e", "i", "u", "v").forEach { vowel ->
            val file = File("src/main/res/drawable-nodpi/mouth_${vowel}_v1.png")
            assertTrue("missing mouth asset: ${file.path}", file.isFile)
            val image = ImageIO.read(file)
            assertEquals("mouth asset must be square: ${file.name}", image.width, image.height)
            assertTrue("mouth asset is unexpectedly small: ${file.name}", image.width >= 1024)
        }
    }
}
