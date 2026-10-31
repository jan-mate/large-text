package dev.safi.largetext.fit

import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.createFontFamilyResolver
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36])
class ComposeTextMeasureTest {
    private val density = Density(1f)
    private val measure = ComposeTextMeasure(
        TextMeasurer(createFontFamilyResolver(ApplicationProvider.getApplicationContext()), density, LayoutDirection.Ltr),
        TextStyle(),
        density,
    )
    private val fitter = BinarySearchTextFitter(measure)

    private fun assertFits(text: String, width: Float, height: Float) {
        val size = fitter.fit(text, width, height)
        val result = measure.measure(text, size, width)
        assertTrue(size > 1f)
        assertTrue(result.width <= width && result.height <= height)
    }

    @Test
    fun wrappingAtSpacesIsNotAMidWordBreak() {
        assertFalse(measure.measure("hello world again", 40f, 150f).brokeMidWord)
    }

    @Test
    fun wordWiderThanTheBoxIsAMidWordBreak() {
        assertTrue(measure.measure("abcdefghijklmnopqrstuvwxyz", 40f, 150f).brokeMidWord)
    }

    @Test
    fun wrappingBetweenIdeographsIsNotAMidWordBreak() {
        assertFalse(measure.measure("日本語の文章です日本語の文章です", 40f, 150f).brokeMidWord)
    }

    @Test
    fun shortWordGetsBiggerThanLongSentence() {
        assertTrue(fitter.fit("Hi", 1000f, 500f) > fitter.fit("This is a much longer sentence to show", 1000f, 500f))
    }

    @Test
    fun explicitNewlinesAreKept() {
        assertTrue(measure.measure("a\nb\nc", 40f, 1000f).height > measure.measure("a b c", 40f, 1000f).height)
    }

    @Test
    fun variousScriptsFit() {
        listOf("", "Hi", "Hello æøå ÆØÅ ñ ß", "Привет", "日本語", "مرحبا بالعالم", "🐱👍🏽🇩🇰 ‽★", "line one\nline two")
            .forEach { assertFits(it, 1000f, 500f) }
    }
}
