package dev.safi.largetext.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
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

class DataStoreSettingsRepositoryTest {
    @get:Rule
    val folder = TemporaryFolder()

    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private val repository by lazy {
        DataStoreSettingsRepository(
            PreferenceDataStoreFactory.create(scope = scope) { folder.root.resolve("settings.preferences_pb") },
        )
    }

    @After
    fun tearDown() = scope.cancel()

    @Test
    fun defaultsWhenNothingSaved() = runBlocking {
        assertEquals(Settings(), repository.settings.first())
    }

    @Test
    fun savedSettingsAreReadBack() = runBlocking {
        val settings = Settings(AppFont.Caveat, DisplayColors(0xFFFFFF00.toInt(), 0xFF0000FF.toInt()), RotationMode.Portrait, "hej æøå 🐱", displayCount = 7)
        repository.save(settings)
        assertEquals(settings, repository.settings.first())
    }
}
