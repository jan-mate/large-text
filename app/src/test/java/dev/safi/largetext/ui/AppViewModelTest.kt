package dev.safi.largetext.ui

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import dev.safi.largetext.data.AppFont
import dev.safi.largetext.data.DisplayTheme
import dev.safi.largetext.data.FakeSettingsRepository
import dev.safi.largetext.data.RotationMode
import dev.safi.largetext.data.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {
    private val repository = FakeSettingsRepository(Settings(lastText = "saved"))

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun selectAllSoTypingReplacesText() {
        val viewModel = AppViewModel(repository)
        viewModel.changeText(TextFieldValue("hello", TextRange(5)))
        viewModel.selectAll()
        assertEquals(TextRange(0, 5), viewModel.text.selection)
    }

    @Test
    fun loadsSavedSettingsWithTextSelected() {
        val viewModel = AppViewModel(FakeSettingsRepository(Settings(AppFont.Atkinson, DisplayTheme.Light, RotationMode.Auto, "saved")))
        assertEquals(AppFont.Atkinson, viewModel.font)
        assertEquals(DisplayTheme.Light, viewModel.theme)
        assertEquals(RotationMode.Auto, viewModel.rotation)
        assertEquals(TextFieldValue("saved", TextRange(0, 5)), viewModel.text)
    }

    @Test
    fun togglesAreSaved() {
        val viewModel = AppViewModel(repository)
        viewModel.toggleFont()
        viewModel.toggleTheme()
        viewModel.cycleRotation()
        assertEquals(Settings(AppFont.Atkinson, DisplayTheme.Light, RotationMode.Portrait, "saved"), repository.settings.value)
    }

    @Test
    fun textIsSaved() {
        val viewModel = AppViewModel(repository)
        viewModel.changeText(TextFieldValue("new"))
        assertEquals("new", repository.settings.value.lastText)
    }
}
