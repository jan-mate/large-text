package dev.safi.largetext.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import dev.safi.largetext.fit.BinarySearchTextFitter
import dev.safi.largetext.fit.CachedTextFitter
import dev.safi.largetext.fit.ComposeTextMeasure

private const val PADDING_FRACTION = 0.04f

@Composable
fun FittedBox(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    content: @Composable (fittedStyle: TextStyle, textModifier: Modifier) -> Unit,
) {
    BoxWithConstraints(modifier, contentAlignment = Alignment.Center) {
        val density = LocalDensity.current
        val textMeasurer = rememberTextMeasurer(cacheSize = 0)
        val fitter = remember(style, density) {
            CachedTextFitter(BinarySearchTextFitter(ComposeTextMeasure(textMeasurer, style, density)))
        }
        val padding = minOf(constraints.maxWidth, constraints.maxHeight) * PADDING_FRACTION
        val width = constraints.maxWidth - 2 * padding
        val height = constraints.maxHeight - 2 * padding
        val fontSize = fitter.fit(text, width, height)
        with(density) {
            content(style.copy(fontSize = fontSize.toSp()), Modifier.width(width.toInt().toDp()))
        }
    }
}
