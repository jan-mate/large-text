package dev.safi.largetext.fit

import android.icu.text.BreakIterator
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density

class ComposeTextMeasure(
    private val textMeasurer: TextMeasurer,
    private val style: TextStyle,
    private val density: Density,
) : TextMeasure {
    override fun measure(text: String, fontSize: Float, maxWidth: Float): Measurement {
        val layout = textMeasurer.measure(
            text = text,
            style = style.copy(fontSize = with(density) { fontSize.toSp() }),
            constraints = Constraints(maxWidth = maxWidth.toInt()),
            density = density,
        )
        val lines = 0 until layout.lineCount
        val breaks = BreakIterator.getLineInstance().apply { setText(text) }
        return Measurement(
            width = lines.maxOf { layout.getLineRight(it) - layout.getLineLeft(it) },
            height = layout.size.height.toFloat(),
            brokeMidWord = lines.drop(1).any { !breaks.isBoundary(layout.getLineStart(it)) },
        )
    }
}
