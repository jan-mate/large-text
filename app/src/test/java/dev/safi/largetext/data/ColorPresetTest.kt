package dev.safi.largetext.data

import org.junit.Assert.assertEquals
import org.junit.Test

class ColorPresetTest {
    private val yellowOnBlue = DisplayColors(0xFFFFFF00.toInt(), 0xFF0000FF.toInt())
    private val defaults = listOf(ColorPreset("w", DisplayColors.WhiteOnBlack), ColorPreset("b", DisplayColors.BlackOnWhite))

    @Test
    fun pinningAddsAtTheEnd() {
        assertEquals(defaults + ColorPreset("new", yellowOnBlue), defaults.withPinned(yellowOnBlue) { "new" })
    }

    @Test
    fun pinningExistingColorsChangesNothing() {
        assertEquals(defaults, defaults.withPinned(DisplayColors.BlackOnWhite) { "new" })
    }

    @Test
    fun deleteAndMoveById() {
        assertEquals(listOf(defaults[1]), defaults.without(setOf("w")))
        assertEquals(listOf(defaults[1], defaults[0]), defaults.moved("w", "b"))
    }
}
