package dev.safi.largetext.ui.color

import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HexColorTest {
    @Test
    fun opaqueColorsRoundTrip() = runBlocking<Unit> {
        checkAll(300, Arb.int(0..0xFFFFFF)) { rgb ->
            val color = rgb or 0xFF000000.toInt()
            assertEquals(color, parseHexColor(color.toHex()))
        }
    }

    @Test
    fun formatsAsHashAndSixDigits() {
        assertEquals("#FF8800", 0xFFFF8800.toInt().toHex())
    }

    @Test
    fun parsesWithOrWithoutHashAndAnyCase() {
        assertEquals(0xFFFF8800.toInt(), parseHexColor("#ff8800"))
        assertEquals(0xFFFF8800.toInt(), parseHexColor("FF8800"))
    }

    @Test
    fun rejectsInvalidText() {
        assertNull(parseHexColor("#FF88"))
        assertNull(parseHexColor("#GG8800"))
        assertNull(parseHexColor(""))
    }
}
