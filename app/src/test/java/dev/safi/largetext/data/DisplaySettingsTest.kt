package dev.safi.largetext.data

import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class DisplaySettingsTest {
    @Test
    fun rotationCyclesAutoLandscapePortrait() {
        val cycle = generateSequence(RotationMode.Auto) { it.next(RotationMode.entries.toTypedArray()) }.take(4).toList()
        assertEquals(listOf(RotationMode.Auto, RotationMode.Landscape, RotationMode.Portrait, RotationMode.Auto), cycle)
    }

    @Test
    fun defaultsAreAutoRotationSafiAndWhiteOnBlack() {
        assertEquals(RotationMode.Auto, Settings().rotation)
        assertEquals(AppFont.Safi, Settings().font)
        assertEquals(DisplayColors(0xFFFFFFFF.toInt(), 0xFF000000.toInt()), Settings().colors)
    }

    @Test
    fun invertingSwapsAndTwiceIsTheSame() = runBlocking<Unit> {
        checkAll(100, Arb.int(), Arb.int()) { text, background ->
            val colors = DisplayColors(text, background)
            assertEquals(DisplayColors(background, text), colors.inverted())
            assertEquals(colors, colors.inverted().inverted())
        }
    }

    @Test
    fun blackOnWhiteIsInvertedDefault() {
        assertEquals(DisplayColors.WhiteOnBlack.inverted(), DisplayColors.BlackOnWhite)
    }
}
