package dev.safi.largetext.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.safi.largetext.data.ColorPreset
import dev.safi.largetext.data.DisplayColors
import dev.safi.largetext.data.ListRepository
import dev.safi.largetext.data.moved
import dev.safi.largetext.data.withPinned
import dev.safi.largetext.data.without
import java.util.UUID
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ColorPresetsViewModel(
    private val repository: ListRepository<ColorPreset>,
    private val newId: () -> String = { UUID.randomUUID().toString() },
) : ViewModel() {
    val presets: StateFlow<List<ColorPreset>> =
        repository.items.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun pin(colors: DisplayColors) = update { it.withPinned(colors, newId) }

    fun delete(ids: Set<String>) = update { it.without(ids) }

    fun move(fromId: String, toId: String) = update { it.moved(fromId, toId) }

    private fun update(transform: (List<ColorPreset>) -> List<ColorPreset>) {
        viewModelScope.launch { repository.update(transform) }
    }
}
