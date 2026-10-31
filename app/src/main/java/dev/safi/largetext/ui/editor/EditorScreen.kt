package dev.safi.largetext.ui.editor

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import dev.safi.largetext.data.AppFont
import dev.safi.largetext.data.DisplayTheme
import dev.safi.largetext.data.RotationMode
import dev.safi.largetext.ui.display.DisplaySurface
import dev.safi.largetext.ui.display.ScaledToDisplay
import dev.safi.largetext.ui.display.foreground

private val Grey = Color(0xFF555555)

@Composable
fun EditorScreen(
    text: TextFieldValue,
    font: AppFont,
    theme: DisplayTheme,
    rotation: RotationMode,
    onTextChange: (TextFieldValue) -> Unit,
    onDisplay: () -> Unit,
    onResume: () -> Unit,
    topBar: @Composable () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        focusRequester.requestFocus()
        keyboard?.show()
        onResume()
    }
    val displaySize = previewDisplaySize(LocalWindowInfo.current.containerSize, rotation)

    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            Modifier.safeDrawingPadding().imePadding().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) { topBar() }
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                ScaledToDisplay(
                    displaySize = displaySize,
                    modifier = Modifier
                        .aspectRatio(displaySize.width.toFloat() / displaySize.height)
                        .border(1.dp, Grey),
                ) {
                    DisplaySurface(text.text, font, theme) { style, textModifier ->
                        BasicTextField(
                            value = text,
                            onValueChange = onTextChange,
                            textStyle = style,
                            cursorBrush = SolidColor(theme.foreground),
                            modifier = textModifier
                                .focusRequester(focusRequester)
                                .onPreviewKeyEvent {
                                    val shiftEnter = it.key == Key.Enter && it.isShiftPressed
                                    if (shiftEnter && it.type == KeyEventType.KeyDown) onDisplay()
                                    shiftEnter
                                },
                        )
                    }
                }
            }
            Button(
                onClick = onDisplay,
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Grey, contentColor = Color.White),
                modifier = Modifier.fillMaxWidth().height(88.dp),
            ) {
                Text("Display", fontSize = 32.sp)
            }
        }
    }
}

private fun previewDisplaySize(windowSize: IntSize, rotation: RotationMode): IntSize {
    val long = maxOf(windowSize.width, windowSize.height)
    val short = minOf(windowSize.width, windowSize.height)
    return when (rotation) {
        RotationMode.Portrait -> IntSize(short, long)
        RotationMode.Landscape, RotationMode.Auto -> IntSize(long, short)
    }
}
