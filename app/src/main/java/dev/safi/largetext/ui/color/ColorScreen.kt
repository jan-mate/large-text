package dev.safi.largetext.ui.color

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Colorize
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.safi.largetext.data.AppFont
import dev.safi.largetext.data.ColorPreset
import dev.safi.largetext.data.DisplayColors
import dev.safi.largetext.ui.BarButton
import dev.safi.largetext.ui.OutlineGrey
import dev.safi.largetext.ui.PreviewCell
import dev.safi.largetext.ui.SelectionBar
import dev.safi.largetext.ui.display.DisplayContent
import dev.safi.largetext.ui.rememberDragOrder
import dev.safi.largetext.ui.toggled
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState

private val swatches = listOf(
    0xFFFFFFFF, 0xFF000000, 0xFF808080, 0xFFE53935, 0xFFFB8C00, 0xFFFFEB3B,
    0xFF43A047, 0xFF00BCD4, 0xFF1E88E5, 0xFF8E24AA, 0xFFEC407A,
).map { it.toInt() }

private const val SWATCHES_PER_ROW = 6

private enum class Target { Text, Background }

@Composable
fun ColorScreen(
    text: String,
    font: AppFont,
    colors: DisplayColors,
    presets: List<ColorPreset>,
    displaySize: IntSize,
    onTextColor: (Int) -> Unit,
    onBackgroundColor: (Int) -> Unit,
    onInvert: () -> Unit,
    onApply: (DisplayColors) -> Unit,
    onPin: (DisplayColors) -> Unit,
    onDelete: (Set<String>) -> Unit,
    onMove: (fromId: String, toId: String) -> Unit,
) {
    var selected by rememberSaveable { mutableStateOf(emptySet<String>()) }
    var picking by remember { mutableStateOf<Target?>(null) }
    val selecting = selected.isNotEmpty()
    val presetsById = presets.associateBy { it.id }
    val dragOrder = rememberDragOrder(presets.map { it.id })
    val gridState = rememberLazyGridState()
    val reorderState = rememberReorderableLazyGridState(gridState) { from, to -> dragOrder.move(from.key, to.key) }
    val previewText = text.ifBlank { "Large Text" }

    BackHandler(enabled = selecting) { selected = emptySet() }
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(Modifier.safeDrawingPadding()) {
            if (selecting) {
                Box(Modifier.fillMaxWidth().height(88.dp).padding(horizontal = 12.dp), contentAlignment = Alignment.Center) {
                    SelectionBar(selected.size, onClear = { selected = emptySet() }) {
                        BarButton(Icons.Outlined.Delete, "Delete") {
                            onDelete(selected)
                            selected = emptySet()
                        }
                    }
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                state = gridState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(12.dp),
            ) {
                fullWidth("preview") {
                    PreviewCell(displaySize, selected = false) { DisplayContent(previewText, font, colors) }
                }
                fullWidth("text") {
                    SwatchRow("Text color", colors.text, onTextColor) { picking = Target.Text }
                }
                fullWidth("background") {
                    SwatchRow("Background color", colors.background, onBackgroundColor) { picking = Target.Background }
                }
                fullWidth("actions") {
                    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                        BarButton(Icons.Outlined.SwapVert, "Inverse", onInvert)
                        BarButton(Icons.Outlined.PushPin, "Pin current") { onPin(colors) }
                    }
                }
                fullWidth("pinned") {
                    Text("Pinned", fontSize = 20.sp, modifier = Modifier.padding(8.dp))
                }
                items(dragOrder.ids, key = { it }) { id ->
                    val preset = presetsById.getValue(id)
                    ReorderableItem(reorderState, key = id) {
                        PreviewCell(
                            displaySize = displaySize,
                            selected = id in selected,
                            modifier = Modifier
                                .clickable { if (selecting) selected = selected.toggled(id) else onApply(preset.colors) }
                                .longPressDraggableHandle(
                                    onDragStarted = { selected = selected + id },
                                    onDragStopped = {
                                        val target = dragOrder.targetOf(id)
                                        if (target != id) onMove(id, target)
                                    },
                                ),
                        ) {
                            DisplayContent(previewText, font, preset.colors)
                        }
                    }
                }
            }
        }
    }
    when (picking) {
        Target.Text -> ColorPickerDialog(colors.text, onPick = { onTextColor(it); picking = null }, onDismiss = { picking = null })
        Target.Background -> ColorPickerDialog(colors.background, onPick = { onBackgroundColor(it); picking = null }, onDismiss = { picking = null })
        null -> Unit
    }
}

private fun LazyGridScope.fullWidth(key: String, content: @Composable () -> Unit) {
    item(key = key, span = { GridItemSpan(maxLineSpan) }) { content() }
}

@Composable
private fun SwatchRow(title: String, current: Int, onChoose: (Int) -> Unit, onCustom: () -> Unit) {
    val cells: List<Int?> = swatches + null
    Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, fontSize = 20.sp)
        cells.chunked(SWATCHES_PER_ROW).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { swatch ->
                    val cell = Modifier.weight(1f).aspectRatio(1f)
                    if (swatch == null) CustomColorButton(title, onCustom, cell)
                    else Swatch(Color(swatch), chosen = swatch == current, cell) { onChoose(swatch) }
                }
            }
        }
    }
}

@Composable
private fun CustomColorButton(title: String, onClick: () -> Unit, modifier: Modifier) {
    Box(
        modifier.padding(5.dp).border(1.dp, OutlineGrey, CircleShape).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(Icons.Outlined.Colorize, contentDescription = "Custom $title")
    }
}

@Composable
private fun Swatch(color: Color, chosen: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier
            .border(2.dp, if (chosen) Color.White else Color.Transparent, CircleShape)
            .padding(5.dp)
            .background(color, CircleShape)
            .border(1.dp, OutlineGrey, CircleShape)
            .clickable(onClick = onClick),
    )
}
