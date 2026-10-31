package dev.safi.largetext.ui.history

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.safi.largetext.data.AppFont
import dev.safi.largetext.data.DisplayTheme
import dev.safi.largetext.data.HistoryEntry
import dev.safi.largetext.data.matching
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
    onSetPinned: (Set<String>, Boolean) -> Unit,
    onDelete: (Set<String>) -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var selected by rememberSaveable { mutableStateOf(emptySet<String>()) }
    val selecting = selected.isNotEmpty()
    val (pinned, recent) = entries.matching(query).partition { it.pinned }
    val cell = CellActions(
        selected = selected,
        onClick = { entry -> if (selecting) selected = selected.toggled(entry.id) else onOpen(entry) },
        onLongClick = { entry -> selected = selected.toggled(entry.id) },
    )
    BackHandler(enabled = selecting) { selected = emptySet() }
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(Modifier.safeDrawingPadding().imePadding()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(12.dp),
            ) {
                section("Pinned", pinned, font, theme, displaySize, cell)
                section("Recent", recent, font, theme, displaySize, cell)
            }
            Box(Modifier.fillMaxWidth().height(88.dp).padding(horizontal = 12.dp), contentAlignment = Alignment.Center) {
                if (selecting) {
                    val allPinned = entries.filter { it.id in selected }.all { it.pinned }
                    SelectionBar(
                        count = selected.size,
                        allPinned = allPinned,
                        onClear = { selected = emptySet() },
                        onPin = {
                            onSetPinned(selected, !allPinned)
                            selected = emptySet()
                        },
                        onDelete = {
                            onDelete(selected)
                            selected = emptySet()
                        },
                    )
                } else {
                    SearchBar(query, onQueryChange = { query = it })
                }
            }
        }
    }
}

private fun Set<String>.toggled(id: String) = if (id in this) this - id else this + id

private class CellActions(
    val selected: Set<String>,
    val onClick: (HistoryEntry) -> Unit,
    val onLongClick: (HistoryEntry) -> Unit,
)

@Composable
private fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Search") },
        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Outlined.Close, contentDescription = "Clear search")
                }
            }
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Grey,
            cursorColor = Color.White,
        ),
    )
}

@Composable
private fun SelectionBar(count: Int, allPinned: Boolean, onClear: () -> Unit, onPin: () -> Unit, onDelete: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onClear) {
            Icon(Icons.Outlined.Close, contentDescription = "Cancel selection")
        }
        Text("$count selected", fontSize = 20.sp, modifier = Modifier.weight(1f))
        TextButton(onClick = onPin) {
            Icon(Icons.Outlined.PushPin, contentDescription = null, tint = Color.White)
            Text(if (allPinned) " Unpin" else " Pin", color = Color.White, fontSize = 18.sp)
        }
        TextButton(onClick = onDelete) {
            Icon(Icons.Outlined.Delete, contentDescription = null, tint = Color.White)
            Text(" Delete", color = Color.White, fontSize = 18.sp)
        }
    }
}

private fun LazyGridScope.section(
    title: String,
    entries: List<HistoryEntry>,
    font: AppFont,
    theme: DisplayTheme,
    displaySize: IntSize,
    cell: CellActions,
) {
    if (entries.isEmpty()) return
    item(span = { GridItemSpan(maxLineSpan) }) {
        Text(title, fontSize = 20.sp, modifier = Modifier.padding(8.dp))
    }
    items(entries, key = { it.id }) { entry ->
        HistoryCell(entry, entry.id in cell.selected, font, theme, displaySize, cell)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HistoryCell(
    entry: HistoryEntry,
    isSelected: Boolean,
    font: AppFont,
    theme: DisplayTheme,
    displaySize: IntSize,
    cell: CellActions,
) {
    Box(Modifier.padding(4.dp)) {
        ScaledToDisplay(
            displaySize = displaySize,
            modifier = Modifier
                .aspectRatio(displaySize.width.toFloat() / displaySize.height)
                .border(if (isSelected) 3.dp else 1.dp, if (isSelected) Color.White else Grey)
                .combinedClickable(onClick = { cell.onClick(entry) }, onLongClick = { cell.onLongClick(entry) }),
        ) {
            DisplayContent(entry.text, font, theme)
        }
        if (isSelected) {
            Icon(
                Icons.Filled.CheckCircle,
                contentDescription = "Selected",
                tint = Color.White,
                modifier = Modifier.align(Alignment.TopEnd).padding(6.dp).background(Color.Black, CircleShape),
            )
        }
    }
}
