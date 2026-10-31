package dev.safi.largetext.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.safi.largetext.data.AppFont
import dev.safi.largetext.data.DisplayColors
import dev.safi.largetext.data.HistoryEntry
import dev.safi.largetext.data.ListRepository
import dev.safi.largetext.data.moved
import dev.safi.largetext.data.RotationMode
import dev.safi.largetext.data.Settings
import dev.safi.largetext.data.SettingsRepository
import dev.safi.largetext.data.next
import dev.safi.largetext.data.withDisplayed
import dev.safi.largetext.data.withPinned
import dev.safi.largetext.data.without
import java.util.UUID
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AppViewModel(
    private val settingsRepository: SettingsRepository,
    private val historyRepository: ListRepository<HistoryEntry>,
    private val clock: () -> Long = System::currentTimeMillis,
) : ViewModel() {
    val history: StateFlow<List<HistoryEntry>> =
        historyRepository.items.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var text by mutableStateOf(TextFieldValue(""))
        private set
    var font by mutableStateOf(Settings().font)
        private set
    var colors by mutableStateOf(Settings().colors)
        private set
    var rotation by mutableStateOf(Settings().rotation)
        private set

    init {
        viewModelScope.launch {
            val settings = settingsRepository.settings.first()
            font = settings.font
            colors = settings.colors
            rotation = settings.rotation
            text = TextFieldValue(settings.lastText, TextRange(0, settings.lastText.length))
        }
    }

    fun changeText(value: TextFieldValue) {
        text = value
        save()
    }

    fun selectAll() {
        text = text.copy(selection = TextRange(0, text.text.length))
    }

    fun chooseFont(value: AppFont) {
        font = value
        save()
    }

    fun setTextColor(color: Int) = applyColors(colors.copy(text = color))

    fun setBackgroundColor(color: Int) = applyColors(colors.copy(background = color))

    fun invertColors() = applyColors(colors.inverted())

    fun applyColors(value: DisplayColors) {
        colors = value
        save()
    }

    fun cycleRotation() {
        rotation = rotation.next(RotationMode.entries.toTypedArray())
        save()
    }

    fun display() {
        val displayed = text.text
        if (displayed.isBlank()) return
        val displayedFont = font
        val displayedColors = colors
        viewModelScope.launch {
            historyRepository.update {
                it.withDisplayed(displayed, displayedFont, displayedColors, clock(), { UUID.randomUUID().toString() })
            }
        }
    }

    fun display(entry: HistoryEntry) {
        font = entry.font
        colors = entry.colors
        changeText(TextFieldValue(entry.text, TextRange(0, entry.text.length)))
        display()
    }

    fun setPinned(ids: Set<String>, pinned: Boolean) {
        viewModelScope.launch { historyRepository.update { it.withPinned(ids, pinned) } }
    }

    fun moveHistory(fromId: String, toId: String) {
        viewModelScope.launch { historyRepository.update { it.moved(fromId, toId) } }
    }

    fun delete(ids: Set<String>) {
        viewModelScope.launch { historyRepository.update { it.without(ids) } }
    }

    private fun save() {
        val settings = Settings(font, colors, rotation, text.text)
        viewModelScope.launch { settingsRepository.save(settings) }
    }
}
