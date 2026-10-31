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

    private val wholeWordsOnly = BinarySearchTextFitter(measure, minGainToBreakWords = Float.POSITIVE_INFINITY)
    private val breaksAlways = BinarySearchTextFitter(measure, minGainToBreakWords = 0f)

    @Test
    fun resultAlwaysFits() = runBlocking<Unit> {
        checkAll(200, texts, sides, sides) { text, width, height ->
            val size = fitter.fit(text, width, height)
            assertTrue(fits(text, size, width, height, allowBreak = true))
        }
    }

    @Test
    fun breaksWordsOnlyWhenTextGetsMuchLarger() = runBlocking<Unit> {
        checkAll(200, texts, sides, sides) { text, width, height ->
            val whole = wholeWordsOnly.fit(text, width, height)
            val broken = breaksAlways.fit(text, width, height)
            val longestWord = text.split(" ", "\n").maxOf { it.length }
            val expected = if (longestWord >= 8 && broken >= whole * 2f) broken else whole
            assertEquals(expected, fitter.fit(text, width, height), 0f)
        }
    }

    @Test
    fun wholeWordResultIsTheLargestSizeThatFits() = runBlocking<Unit> {
        checkAll(200, texts, sides, sides) { text, width, height ->
            val size = wholeWordsOnly.fit(text, width, height)
            assertTrue(fits(text, size, width, height, allowBreak = false))
            assertFalse(fits(text, size + 0.5f, width, height, allowBreak = false))
        }
    }

    @Test
    fun longWordInAWideBoxIsBroken() {
        val word = "donaudampfschifffahrtsgesellschaft"
        val size = fitter.fit(word, 1000f, 500f)
        assertFalse(fits(word, size, 1000f, 500f, allowBreak = false))
    }

    @Test
    fun sentenceKeepsWholeWords() {
        val sentence = "the bus to the central station leaves from platform seven"
        val size = fitter.fit(sentence, 1000f, 500f)
        assertTrue(fits(sentence, size, 1000f, 500f, allowBreak = false))
    }

    @Test
    fun singleWordFillsTheWidth() {
        assertEquals(100f / (0.6f * 5), fitter.fit("hello", 100f, 40f), 0.5f)
    }

    @Test
    fun shortWordInANarrowBoxIsNotBroken() {
        val size = fitter.fit("wow", 100f, 1000f)
        assertTrue(fits("wow", size, 100f, 1000f, allowBreak = false))
    }

    @Test
    fun shortWordInATallBoxIsNotBroken() {
        val size = fitter.fit("hello", 1080f, 2400f)
        assertTrue(fits("hello", size, 1080f, 2400f, allowBreak = false))
    }

    @Test
    fun breaksMidWordWhenNothingElseFits() {
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
