package dev.safi.largetext.ui.display

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import dev.safi.largetext.data.AppFont
import dev.safi.largetext.data.DisplayTheme
import dev.safi.largetext.ui.FittedBox
import dev.safi.largetext.ui.fontFamily

val DisplayTheme.foreground: Color
    get() = if (this == DisplayTheme.Dark) Color.White else Color.Black

val DisplayTheme.background: Color
    get() = if (this == DisplayTheme.Dark) Color.Black else Color.White

@Composable
fun DisplaySurface(
    text: String,
    font: AppFont,
    theme: DisplayTheme,
    modifier: Modifier = Modifier,
    content: @Composable (fittedStyle: TextStyle, textModifier: Modifier) -> Unit,
) {
    FittedBox(
        text = text,
        style = TextStyle(fontFamily = font.fontFamily, textAlign = TextAlign.Center, color = theme.foreground),
        modifier = modifier.fillMaxSize().background(theme.background),
        content = content,
    )
}

@Composable
fun DisplayContent(text: String, font: AppFont, theme: DisplayTheme, modifier: Modifier = Modifier) {
    DisplaySurface(text, font, theme, modifier) { style, textModifier ->
        Text(text, style = style, modifier = textModifier)
    }
}

@Composable
fun ScaledToDisplay(displaySize: IntSize, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    BoxWithConstraints(modifier) {
        val scale = constraints.maxWidth.toFloat() / displaySize.width
        with(LocalDensity.current) {
            Box(
                Modifier
                    .requiredSize(displaySize.width.toDp(), displaySize.height.toDp())
                    .graphicsLayer(scaleX = scale, scaleY = scale),
            ) {
                content()
            }
        }
    }
}
