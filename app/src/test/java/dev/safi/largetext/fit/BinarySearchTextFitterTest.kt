package dev.safi.largetext.fit

import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.float
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BinarySearchTextFitterTest {
    private val measure = MonospaceMeasure()
    private val fitter = BinarySearchTextFitter(measure)

    private val words = Arb.list(Arb.string(1..12, "abcdefghijklmnopqrstuvwxyz"), 1..15)
    private val separators = Arb.list(Arb.element(" ", "\n"), 15..15)
    private val texts = Arb.bind(words, separators) { ws: List<String>, ss: List<String> ->
        ws.mapIndexed { i, w -> if (i == 0) w else ss[i] + w }.joinToString("")
    }
    private val sides = Arb.float(50f..2000f, includeNaNs = false)

    private fun fits(text: String, size: Float, width: Float, height: Float, allowBreak: Boolean): Boolean {
        val result = measure.measure(text, size, width)
        return result.width <= width && result.height <= height && (allowBreak || !result.brokeMidWord)
    }

    @Test
    fun resultAlwaysFitsWithoutBreakingWords() = runBlocking<Unit> {
        checkAll(200, texts, sides, sides) { text, width, height ->
            val size = fitter.fit(text, width, height)
            assertTrue(fits(text, size, width, height, allowBreak = false))
        }
    }

    @Test
    fun resultIsTheLargestSizeThatFits() = runBlocking<Unit> {
        checkAll(200, texts, sides, sides) { text, width, height ->
            val size = fitter.fit(text, width, height)
            assertFalse(fits(text, size + 0.5f, width, height, allowBreak = false))
        }
    }

    @Test
    fun singleWordFillsTheWidth() {
        assertEquals(100f / (0.6f * 5), fitter.fit("hello", 100f, 1000f), 0.5f)
    }

    @Test
    fun breaksMidWordOnlyWhenNothingElseFits() {
        val tiny = BinarySearchTextFitter(measure, minFontSize = 10f)
        val size = tiny.fit("abcdefghijklmnopqrstuvwxyz", 60f, 1000f)
        assertTrue(fits("abcdefghijklmnopqrstuvwxyz", size, 60f, 1000f, allowBreak = true))
        assertTrue(size >= 10f)
    }

    @Test
    fun emptyTextGetsAPositiveSize() {
        assertTrue(fitter.fit("", 100f, 100f) > 0f)
    }
}
