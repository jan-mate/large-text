package dev.safi.largetext.ui

import dev.safi.largetext.data.ColorPreset
import dev.safi.largetext.data.DisplayColors
import dev.safi.largetext.data.FakeListRepository
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
class ColorPresetsViewModelTest {
    private val yellowOnBlue = DisplayColors(0xFFFFFF00.toInt(), 0xFF0000FF.toInt())
    private val repository = FakeListRepository(
        listOf(ColorPreset("w", DisplayColors.WhiteOnBlack), ColorPreset("b", DisplayColors.BlackOnWhite)),
    )
    private val viewModel by lazy { ColorPresetsViewModel(repository, newId = { "new" }) }

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun pinAddsCurrentColors() {
        viewModel.pin(yellowOnBlue)
        assertEquals(ColorPreset("new", yellowOnBlue), repository.items.value.last())
    }

    @Test
    fun deleteAndMove() {
        viewModel.move("w", "b")
        assertEquals(listOf("b", "w"), repository.items.value.map { it.id })
        viewModel.delete(setOf("w"))
        assertEquals(listOf("b"), repository.items.value.map { it.id })
    }
}
