package dev.safi.largetext.ui.color

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import dev.safi.largetext.ui.OutlineGrey

private val hueColors = (0..6).map { Color.hsv(it * 60f % 360f, 1f, 1f) }

@Composable
fun ColorPickerDialog(initial: Int, onPick: (Int) -> Unit, onDismiss: () -> Unit) {
    val hsv = remember { FloatArray(3).also { android.graphics.Color.colorToHSV(initial, it) } }
    var hue by remember { mutableFloatStateOf(hsv[0]) }
    var saturation by remember { mutableFloatStateOf(hsv[1]) }
    var value by remember { mutableFloatStateOf(hsv[2]) }
    var hexText by remember { mutableStateOf(initial.toHex()) }
    val color = Color.hsv(hue, saturation, value)

    fun showHex() {
        hexText = Color.hsv(hue, saturation, value).toArgb().toHex()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = { onPick(color.toArgb()) }) { Text("OK", color = Color.White) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = Color.White) } },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Brush.horizontalGradient(listOf(Color.White, Color.hsv(hue, 1f, 1f))))
                        .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black)))
                        .positionInput { position, size ->
                            saturation = (position.x / size.width).coerceIn(0f, 1f)
                            value = 1f - (position.y / size.height).coerceIn(0f, 1f)
                            showHex()
                        },
                ) {
                    Canvas(Modifier.matchParentSize()) {
                        drawCircle(Color.White, 12.dp.toPx(), Offset(saturation * size.width, (1 - value) * size.height), style = Stroke(3.dp.toPx()))
                    }
                }
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                        .background(Brush.horizontalGradient(hueColors))
                        .positionInput { position, size ->
                            hue = (position.x / size.width).coerceIn(0f, 1f) * 360f
                            showHex()
                        },
                ) {
                    Canvas(Modifier.matchParentSize()) {
                        val x = hue / 360f * size.width
                        drawLine(Color.White, Offset(x, 0f), Offset(x, size.height), 4.dp.toPx())
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.size(48.dp).background(color).border(1.dp, OutlineGrey))
                    OutlinedTextField(
                        value = hexText,
                        onValueChange = { text ->
                            hexText = text
                            parseHexColor(text)?.let { parsed ->
                                android.graphics.Color.colorToHSV(parsed, hsv)
                                hue = hsv[0]
                                saturation = hsv[1]
                                value = hsv[2]
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.White, unfocusedBorderColor = OutlineGrey),
                    )
                }
            }
        },
    )
}

private fun Modifier.positionInput(onPosition: (Offset, IntSize) -> Unit) = pointerInput(Unit) {
    awaitEachGesture {
        val down = awaitFirstDown()
        onPosition(down.position, size)
        drag(down.id) { change ->
            change.consume()
            onPosition(change.position, size)
        }
    }
}
