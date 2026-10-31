package dev.safi.largetext.ui.history

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
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
import dev.safi.largetext.data.HistoryEntry
import dev.safi.largetext.data.matching
import dev.safi.largetext.ui.BarButton
import dev.safi.largetext.ui.OutlineGrey
import dev.safi.largetext.ui.PreviewCell
import dev.safi.largetext.ui.SelectionBar
import dev.safi.largetext.ui.display.DisplayContent
import dev.safi.largetext.ui.rememberDragOrder
import dev.safi.largetext.ui.toggled
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState

@Composable
fun HistoryScreen(
    entries: List<HistoryEntry>,
    displaySize: IntSize,
    onOpen: (HistoryEntry) -> Unit,
    onSetPinned: (Set<String>, Boolean) -> Unit,
    onDelete: (Set<String>) -> Unit,
    onMove: (fromId: String, toId: String) -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var selected by rememberSaveable { mutableStateOf(emptySet<String>()) }
    val selecting = selected.isNotEmpty()
    val (pinned, recent) = entries.matching(query).partition { it.pinned }
    val pinnedById = pinned.associateBy { it.id }
    val dragOrder = rememberDragOrder(pinned.map { it.id })
    val gridState = rememberLazyGridState()
    val reorderState = rememberReorderableLazyGridState(gridState) { from, to -> dragOrder.move(from.key, to.key) }
    val onClick = { entry: HistoryEntry -> if (selecting) selected = selected.toggled(entry.id) else onOpen(entry) }

    BackHandler(enabled = selecting) { selected = emptySet() }
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(Modifier.safeDrawingPadding().imePadding()) {
            Box(Modifier.fillMaxWidth().height(88.dp).padding(horizontal = 12.dp), contentAlignment = Alignment.Center) {
                if (selecting) {
                    val allPinned = entries.filter { it.id in selected }.all { it.pinned }
                    SelectionBar(selected.size, onClear = { selected = emptySet() }) {
                        BarButton(Icons.Outlined.PushPin, if (allPinned) "Unpin" else "Pin") {
                            onSetPinned(selected, !allPinned)
                            selected = emptySet()
                        }
                        BarButton(Icons.Outlined.Delete, "Delete") {
                            onDelete(selected)
                            selected = emptySet()
                        }
                    }
                } else {
                    SearchBar(query, onQueryChange = { query = it })
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                state = gridState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(12.dp),
            ) {
                if (pinned.isNotEmpty()) header("Pinned")
                items(dragOrder.ids, key = { it }) { id ->
                    val entry = pinnedById.getValue(id)
                    ReorderableItem(reorderState, key = id) {
                        PreviewCell(
                            displaySize = displaySize,
                            selected = id in selected,
                            modifier = Modifier
                                .clickable { onClick(entry) }
                                .longPressDraggableHandle(
                                    onDragStarted = { selected = selected + id },
                                    onDragStopped = {
                                        val target = dragOrder.targetOf(id)
                                        if (target != id) onMove(id, target)
                                    },
                                ),
                        ) {
                            DisplayContent(entry.text, entry.font, entry.colors)
                        }
                    }
                }
                if (recent.isNotEmpty()) header("Recent")
                items(recent, key = { it.id }) { entry ->
                    RecentCell(entry, entry.id in selected, displaySize, onClick) {
                        selected = selected.toggled(entry.id)
                    }
                }
            }
        }
    }
}

private fun LazyGridScope.header(title: String) {
    item(key = title, span = { GridItemSpan(maxLineSpan) }) {
        Text(title, fontSize = 20.sp, modifier = Modifier.padding(8.dp))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RecentCell(
    entry: HistoryEntry,
    isSelected: Boolean,
    displaySize: IntSize,
    onClick: (HistoryEntry) -> Unit,
    onLongClick: () -> Unit,
) {
    PreviewCell(
        displaySize = displaySize,
        selected = isSelected,
        modifier = Modifier.combinedClickable(onClick = { onClick(entry) }, onLongClick = onLongClick),
    ) {
        DisplayContent(entry.text, entry.font, entry.colors)
    }
}

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
            unfocusedBorderColor = OutlineGrey,
            cursorColor = Color.White,
        ),
    )
}
