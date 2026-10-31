package dev.safi.largetext.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import dev.safi.largetext.data.AppFont
import dev.safi.largetext.data.DisplayTheme
import dev.safi.largetext.data.RotationMode

class AppViewModel : ViewModel() {
    var text by mutableStateOf(TextFieldValue(""))
        private set
    var font by mutableStateOf(AppFont.Safi)
        private set
    var theme by mutableStateOf(DisplayTheme.Dark)
        private set
    var rotation by mutableStateOf(RotationMode.Landscape)
        private set

    fun changeText(value: TextFieldValue) {
        text = value
    }

    fun selectAll() {
        text = text.copy(selection = TextRange(0, text.text.length))
    }
}
