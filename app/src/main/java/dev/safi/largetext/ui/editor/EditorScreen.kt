package dev.safi.largetext.ui.editor

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import dev.safi.largetext.data.AppFont
import dev.safi.largetext.data.DisplayColors
import dev.safi.largetext.data.RotationMode
import dev.safi.largetext.ui.display.DisplaySurface
import dev.safi.largetext.ui.display.ScaledToDisplay
import dev.safi.largetext.ui.display.rememberDisplaySize
import dev.safi.largetext.ui.display.textColor
import dev.safi.largetext.ui.OutlineGrey

private const val PLACEHOLDER = "Type here"

@Composable
fun EditorScreen(
    text: TextFieldValue,
    font: AppFont,
    colors: DisplayColors,
    rotation: RotationMode,
    onTextChange: (TextFieldValue) -> Unit,
    onDisplay: () -> Unit,
    onResume: () -> Unit,
    controls: @Composable () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        focusRequester.requestFocus()
        keyboard?.show()
        onResume()
    }
    val displaySize = rememberDisplaySize(rotation)

    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            Modifier.safeDrawingPadding().imePadding().padding(start = 12.dp, top = 12.dp, end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                ScaledToDisplay(
                    displaySize = displaySize,
                    modifier = Modifier
                        .aspectRatio(displaySize.width.toFloat() / displaySize.height)
                        .border(1.dp, OutlineGrey),
                ) {
                    val selectionColors = TextSelectionColors(colors.textColor, colors.textColor.copy(alpha = 0.3f))
                    CompositionLocalProvider(LocalTextSelectionColors provides selectionColors) {
                        DisplaySurface(text.text.ifEmpty { PLACEHOLDER }, font, colors) { style, textModifier ->
                            BasicTextField(
                                value = text,
                                onValueChange = onTextChange,
                                textStyle = style,
                                cursorBrush = SolidColor(colors.textColor),
                                decorationBox = { field ->
                                    Box(contentAlignment = Alignment.Center) {
                                        if (text.text.isEmpty()) Text(PLACEHOLDER, style = style.copy(color = colors.textColor.copy(alpha = 0.3f)))
                                        field()
                                    }
                                },
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
            }
            Row(Modifier.fillMaxWidth().height(72.dp), verticalAlignment = Alignment.CenterVertically) {
                Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) { controls() }
                TextButton(
                    onClick = onDisplay,
                    shape = RectangleShape,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White),
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                ) {
                    Text("Display", fontSize = 40.sp)
                }
            }
        }
    }
}
