package dev.safi.largetext.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.safi.largetext.data.AppFont
import dev.safi.largetext.data.DisplayTheme
import dev.safi.largetext.data.HistoryEntry
import dev.safi.largetext.data.HistoryRepository
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
    private val historyRepository: HistoryRepository,
    private val clock: () -> Long = System::currentTimeMillis,
) : ViewModel() {
    val history: StateFlow<List<HistoryEntry>> =
        historyRepository.entries.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var text by mutableStateOf(TextFieldValue(""))
        private set
    var font by mutableStateOf(Settings().font)
        private set
    var theme by mutableStateOf(Settings().theme)
        private set
    var rotation by mutableStateOf(Settings().rotation)
        private set

    init {
        viewModelScope.launch {
            val settings = settingsRepository.settings.first()
            font = settings.font
            theme = settings.theme
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

    fun toggleFont() {
        font = font.next(AppFont.entries.toTypedArray())
        save()
    }

    fun toggleTheme() {
        theme = theme.next(DisplayTheme.entries.toTypedArray())
        save()
    }

    fun cycleRotation() {
        rotation = rotation.next(RotationMode.entries.toTypedArray())
        save()
    }

    fun display() {
        val displayed = text.text
        if (displayed.isBlank()) return
        viewModelScope.launch {
            historyRepository.update { it.withDisplayed(displayed, clock(), { UUID.randomUUID().toString() }) }
        }
    }

    fun display(entry: HistoryEntry) {
        changeText(TextFieldValue(entry.text, TextRange(0, entry.text.length)))
        display()
    }

    fun setPinned(ids: Set<String>, pinned: Boolean) {
        viewModelScope.launch { historyRepository.update { it.withPinned(ids, pinned) } }
    }

    fun delete(ids: Set<String>) {
        viewModelScope.launch { historyRepository.update { it.without(ids) } }
    }

    private fun save() {
        val settings = Settings(font, theme, rotation, text.text)
        viewModelScope.launch { settingsRepository.save(settings) }
    }
}
