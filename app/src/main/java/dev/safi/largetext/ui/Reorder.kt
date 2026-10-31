package dev.safi.largetext.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.safi.largetext.data.moved

class DragOrder(private val original: List<String>) {
    var ids by mutableStateOf(original)
        private set

    fun move(fromKey: Any, toKey: Any) {
        val from = ids.indexOf(fromKey)
        val to = ids.indexOf(toKey)
        if (from >= 0 && to >= 0) ids = ids.moved(from, to)
    }

    fun targetOf(id: String): String = original[ids.indexOf(id)]
}

@Composable
fun rememberDragOrder(ids: List<String>) = remember(ids) { DragOrder(ids) }
