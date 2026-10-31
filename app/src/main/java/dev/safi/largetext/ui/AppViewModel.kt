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
import dev.safi.largetext.data.RotationMode
import dev.safi.largetext.data.Settings
import dev.safi.largetext.data.SettingsRepository
import dev.safi.largetext.data.next
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AppViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {
    var text by mutableStateOf(TextFieldValue(""))
        private set
    var font by mutableStateOf(AppFont.Safi)
        private set
    var theme by mutableStateOf(DisplayTheme.Dark)
        private set
    var rotation by mutableStateOf(RotationMode.Landscape)
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

    private fun save() {
        val settings = Settings(font, theme, rotation, text.text)
        viewModelScope.launch { settingsRepository.save(settings) }
    }
}
