package dev.safi.largetext.data

import androidx.datastore.core.DataStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class DataStoreListRepositoryTest {
    @get:Rule
    val folder = TemporaryFolder()

    private val scope = CoroutineScope(Dispatchers.IO + Job())

    private fun <T> repository(serializer: JsonListSerializer<T>, file: String) =
        DataStoreListRepository(DataStoreFactory.create(serializer, scope = scope) { folder.root.resolve(file) })

    private val history by lazy { repository(HistorySerializer, "history.json") }
    private val presets by lazy { repository(ColorPresetSerializer, "color_presets.json") }

    @After
    fun tearDown() = scope.cancel()

    @Test
    fun historyIsEmptyWhenNothingSaved() = runBlocking {
        assertEquals(emptyList<HistoryEntry>(), history.items.first())
    }

    @Test
    fun historyUpdatesAreReadBack() = runBlocking {
        val entries = listOf(HistoryEntry("a", "hej æøå 🐱\nline", 1, pinned = true), HistoryEntry("b", "", 2))
        history.update { entries }
        assertEquals(entries, history.items.first())
    }

    @Test
    fun presetsStartWithWhiteOnBlackAndBlackOnWhite() = runBlocking {
        assertEquals(listOf(DisplayColors.WhiteOnBlack, DisplayColors.BlackOnWhite), presets.items.first().map { it.colors })
    }

    @Test
    fun presetUpdatesAreReadBack() = runBlocking {
        val saved = listOf(ColorPreset("x", DisplayColors(0xFFFFFF00.toInt(), 0xFF0000FF.toInt())))
        presets.update { saved }
        assertEquals(saved, presets.items.first())
    }
}
