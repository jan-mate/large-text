package dev.safi.largetext.ui.history

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.safi.largetext.data.AppFont
import dev.safi.largetext.data.DisplayTheme
import dev.safi.largetext.data.HistoryEntry
import dev.safi.largetext.ui.display.DisplayContent
import dev.safi.largetext.ui.display.ScaledToDisplay

private val Grey = Color(0xFF555555)

@Composable
fun HistoryScreen(
    entries: List<HistoryEntry>,
    font: AppFont,
    theme: DisplayTheme,
    displaySize: IntSize,
    onOpen: (HistoryEntry) -> Unit,
    onTogglePin: (String) -> Unit,
    onDelete: (String) -> Unit,
) {
    val (pinned, recent) = entries.partition { it.pinned }
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.safeDrawingPadding(),
            contentPadding = PaddingValues(12.dp),
        ) {
            section("Pinned", pinned, font, theme, displaySize, onOpen, onTogglePin, onDelete)
            section("Recent", recent, font, theme, displaySize, onOpen, onTogglePin, onDelete)
        }
    }
}

private fun LazyGridScope.section(
    title: String,
    entries: List<HistoryEntry>,
    font: AppFont,
    theme: DisplayTheme,
    displaySize: IntSize,
    onOpen: (HistoryEntry) -> Unit,
    onTogglePin: (String) -> Unit,
    onDelete: (String) -> Unit,
) {
    if (entries.isEmpty()) return
    item(span = { GridItemSpan(maxLineSpan) }) {
        Text(title, fontSize = 20.sp, modifier = Modifier.padding(8.dp))
    }
    items(entries, key = { it.id }) { entry ->
        HistoryCell(entry, font, theme, displaySize, onOpen, onTogglePin, onDelete)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HistoryCell(
    entry: HistoryEntry,
    font: AppFont,
    theme: DisplayTheme,
    displaySize: IntSize,
    onOpen: (HistoryEntry) -> Unit,
    onTogglePin: (String) -> Unit,
    onDelete: (String) -> Unit,
) {
    var menuOpen by remember { mutableStateOf(false) }
    Box(Modifier.padding(4.dp)) {
        ScaledToDisplay(
            displaySize = displaySize,
            modifier = Modifier
                .aspectRatio(displaySize.width.toFloat() / displaySize.height)
                .border(1.dp, Grey)
                .combinedClickable(onClick = { onOpen(entry) }, onLongClick = { menuOpen = true }),
        ) {
            DisplayContent(entry.text, font, theme)
        }
        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
            DropdownMenuItem(
                text = { Text(if (entry.pinned) "Unpin" else "Pin") },
                onClick = {
                    menuOpen = false
                    onTogglePin(entry.id)
                },
            )
            DropdownMenuItem(
                text = { Text("Delete") },
                onClick = {
                    menuOpen = false
                    onDelete(entry.id)
                },
            )
        }
    }
}
