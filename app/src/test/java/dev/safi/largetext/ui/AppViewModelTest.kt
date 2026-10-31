package dev.safi.largetext.ui

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import org.junit.Assert.assertEquals
import org.junit.Test

class AppViewModelTest {
    @Test
    fun selectAllSoTypingReplacesText() {
        val viewModel = AppViewModel()
        viewModel.changeText(TextFieldValue("hello", TextRange(5)))
        viewModel.selectAll()
        assertEquals(TextRange(0, 5), viewModel.text.selection)
    }
}
