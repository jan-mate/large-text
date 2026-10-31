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
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import dev.safi.largetext.data.AppFont
import dev.safi.largetext.data.DisplayColors
import dev.safi.largetext.data.RotationMode
import dev.safi.largetext.ui.FittedBox
import dev.safi.largetext.ui.fontFamily

val DisplayColors.textColor: Color
    get() = Color(text)

val DisplayColors.backgroundColor: Color
    get() = Color(background)

@Composable
fun DisplaySurface(
    text: String,
    font: AppFont,
    colors: DisplayColors,
    modifier: Modifier = Modifier,
    content: @Composable (fittedStyle: TextStyle, textModifier: Modifier) -> Unit,
) {
    FittedBox(
        text = text,
        style = TextStyle(fontFamily = font.fontFamily, textAlign = TextAlign.Center, color = colors.textColor),
        modifier = modifier.fillMaxSize().background(colors.backgroundColor),
        content = content,
    )
}

@Composable
fun DisplayContent(text: String, font: AppFont, colors: DisplayColors, modifier: Modifier = Modifier) {
    DisplaySurface(text, font, colors, modifier) { style, textModifier ->
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

@Composable
fun rememberDisplaySize(rotation: RotationMode): IntSize {
    val windowSize = LocalWindowInfo.current.containerSize
    val long = maxOf(windowSize.width, windowSize.height)
    val short = minOf(windowSize.width, windowSize.height)
    return when (rotation) {
        RotationMode.Portrait -> IntSize(short, long)
        RotationMode.Landscape, RotationMode.Auto -> IntSize(long, short)
    }
}
