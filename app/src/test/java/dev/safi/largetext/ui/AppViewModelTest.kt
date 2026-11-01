package dev.safi.largetext.ui

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import dev.safi.largetext.data.AppFont
import dev.safi.largetext.data.DisplayColors
import dev.safi.largetext.data.FakeListRepository
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
    private val history = FakeListRepository<HistoryEntry>()

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
        val viewModel = viewModel(FakeSettingsRepository(Settings(AppFont.Atkinson, DisplayColors.BlackOnWhite, RotationMode.Auto, "saved")))
        assertEquals(AppFont.Atkinson, viewModel.font)
        assertEquals(DisplayColors.BlackOnWhite, viewModel.colors)
        assertEquals(RotationMode.Auto, viewModel.rotation)
        assertEquals(TextFieldValue("saved", TextRange(0, 5)), viewModel.text)
    }

    @Test
    fun choicesAreSaved() {
        val viewModel = viewModel()
        viewModel.chooseFont(AppFont.Lora)
        viewModel.cycleRotation()
        viewModel.setTextColor(0xFFFFFF00.toInt())
        viewModel.setBackgroundColor(0xFF0000FF.toInt())
        val yellowOnBlue = DisplayColors(0xFFFFFF00.toInt(), 0xFF0000FF.toInt())
        assertEquals(Settings(AppFont.Lora, yellowOnBlue, RotationMode.Landscape, "saved"), repository.settings.value)
    }

    @Test
    fun invertAndApplyColorsAreSaved() {
        val viewModel = viewModel()
        viewModel.invertColors()
        assertEquals(DisplayColors.BlackOnWhite, repository.settings.value.colors)
        viewModel.applyColors(DisplayColors.WhiteOnBlack)
        assertEquals(DisplayColors.WhiteOnBlack, repository.settings.value.colors)
    }

    @Test
    fun movingPinnedHistory() {
        history.items.value = listOf(HistoryEntry("a", "x", 1, true), HistoryEntry("b", "y", 2, true))
        val viewModel = viewModel()
        viewModel.moveHistory("a", "b")
        assertEquals(listOf("b", "a"), history.items.value.map { it.id })
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
        assertEquals(listOf("saved"), history.items.value.map { it.text })
        assertEquals(42L, history.items.value.single().createdAt)
    }

    @Test
    fun displayRemembersFontAndColors() {
        val viewModel = viewModel()
        viewModel.chooseFont(AppFont.Lora)
        viewModel.invertColors()
        viewModel.display()
        val entry = history.items.value.single()
        assertEquals(AppFont.Lora, entry.font)
        assertEquals(DisplayColors.BlackOnWhite, entry.colors)
    }

    @Test
    fun displayingFromHistoryUsesItsFontAndColors() {
        history.items.value = listOf(HistoryEntry("a", "old", 1, false, AppFont.Caveat, DisplayColors.BlackOnWhite))
        val viewModel = viewModel()
        viewModel.display(history.items.value[0])
        assertEquals(AppFont.Caveat, viewModel.font)
        assertEquals(DisplayColors.BlackOnWhite, viewModel.colors)
        assertEquals(AppFont.Caveat, repository.settings.value.font)
    }

    @Test
    fun blankTextIsNotAddedToHistory() {
        val viewModel = viewModel()
        viewModel.changeText(TextFieldValue("  "))
        viewModel.display()
        assertEquals(emptyList<HistoryEntry>(), history.items.value)
    }

    @Test
    fun displayingFromHistorySetsTextAndMovesItToTop() {
        history.items.value = listOf(HistoryEntry("b", "newer", 2), HistoryEntry("a", "older", 1))
        val viewModel = viewModel()
        viewModel.display(history.items.value[1])
        assertEquals("older", viewModel.text.text)
        assertEquals(listOf("older", "newer"), history.items.value.map { it.text })
    }

    @Test
    fun pinAndDeleteUpdateHistory() {
        history.items.value = listOf(HistoryEntry("a", "x", 1), HistoryEntry("b", "y", 2))
        val viewModel = viewModel()
        viewModel.setPinned(setOf("a"), true)
        viewModel.delete(setOf("b"))
        assertEquals(listOf(HistoryEntry("a", "x", 1, pinned = true)), history.items.value)
    }

    @Test
    fun exitHintShowsOnTheFirstThreeDisplaysOnly() {
        val viewModel = viewModel()
        val shown = (1..5).map {
            viewModel.display()
            viewModel.showExitHint
        }
        assertEquals(listOf(true, true, true, false, false), shown)
        assertEquals(5, repository.settings.value.displayCount)
    }

    @Test
    fun displayCountIsLoaded() {
        val viewModel = viewModel(FakeSettingsRepository(Settings(lastText = "saved", displayCount = 3)))
        viewModel.display()
        assertEquals(false, viewModel.showExitHint)
    }
}
