package dev.safi.largetext.ui

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import dev.safi.largetext.data.AppFont
import dev.safi.largetext.data.DisplayTheme
import dev.safi.largetext.data.FakeHistoryRepository
import dev.safi.largetext.data.FakeSettingsRepository
import dev.safi.largetext.data.HistoryEntry
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
    private val history = FakeHistoryRepository()

    private fun viewModel(settings: FakeSettingsRepository = repository) = AppViewModel(settings, history, clock = { 42L })

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun selectAllSoTypingReplacesText() {
        val viewModel = viewModel()
        viewModel.changeText(TextFieldValue("hello", TextRange(5)))
        viewModel.selectAll()
        assertEquals(TextRange(0, 5), viewModel.text.selection)
    }

    @Test
    fun loadsSavedSettingsWithTextSelected() {
        val viewModel = viewModel(FakeSettingsRepository(Settings(AppFont.Atkinson, DisplayTheme.Light, RotationMode.Auto, "saved")))
        assertEquals(AppFont.Atkinson, viewModel.font)
        assertEquals(DisplayTheme.Light, viewModel.theme)
        assertEquals(RotationMode.Auto, viewModel.rotation)
        assertEquals(TextFieldValue("saved", TextRange(0, 5)), viewModel.text)
    }

    @Test
    fun togglesAreSaved() {
        val viewModel = viewModel()
        viewModel.toggleFont()
        viewModel.toggleTheme()
        viewModel.cycleRotation()
        assertEquals(Settings(AppFont.Atkinson, DisplayTheme.Light, RotationMode.Landscape, "saved"), repository.settings.value)
    }

    @Test
    fun textIsSaved() {
        val viewModel = viewModel()
        viewModel.changeText(TextFieldValue("new"))
        assertEquals("new", repository.settings.value.lastText)
    }

    @Test
    fun displayAddsTextToHistory() {
        val viewModel = viewModel()
        viewModel.display()
        assertEquals(listOf("saved"), history.entries.value.map { it.text })
        assertEquals(42L, history.entries.value.single().createdAt)
    }

    @Test
    fun blankTextIsNotAddedToHistory() {
        val viewModel = viewModel()
        viewModel.changeText(TextFieldValue("  "))
        viewModel.display()
        assertEquals(emptyList<HistoryEntry>(), history.entries.value)
    }

    @Test
    fun displayingFromHistorySetsTextAndMovesItToTop() {
        history.entries.value = listOf(HistoryEntry("b", "newer", 2), HistoryEntry("a", "older", 1))
        val viewModel = viewModel()
        viewModel.display(history.entries.value[1])
        assertEquals("older", viewModel.text.text)
        assertEquals(listOf("older", "newer"), history.entries.value.map { it.text })
    }

    @Test
    fun pinAndDeleteUpdateHistory() {
        history.entries.value = listOf(HistoryEntry("a", "x", 1), HistoryEntry("b", "y", 2))
        val viewModel = viewModel()
        viewModel.setPinned(setOf("a"), true)
        viewModel.delete(setOf("b"))
        assertEquals(listOf(HistoryEntry("a", "x", 1, pinned = true)), history.entries.value)
    }
}
