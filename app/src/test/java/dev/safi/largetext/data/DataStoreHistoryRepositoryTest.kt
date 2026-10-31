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

class DataStoreHistoryRepositoryTest {
    @get:Rule
    val folder = TemporaryFolder()

    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private val repository by lazy {
        DataStoreHistoryRepository(DataStoreFactory.create(HistorySerializer, scope = scope) { folder.root.resolve("history.json") })
    }

    @After
    fun tearDown() = scope.cancel()

    @Test
    fun emptyWhenNothingSaved() = runBlocking {
        assertEquals(emptyList<HistoryEntry>(), repository.entries.first())
    }

    @Test
    fun updatesAreReadBack() = runBlocking {
        val entries = listOf(HistoryEntry("a", "hej æøå 🐱\nline", 1, pinned = true), HistoryEntry("b", "", 2))
        repository.update { entries }
        assertEquals(entries, repository.entries.first())
    }
}
