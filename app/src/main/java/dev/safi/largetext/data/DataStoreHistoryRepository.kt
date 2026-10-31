package dev.safi.largetext.data

import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream

object HistorySerializer : Serializer<List<HistoryEntry>> {
    override val defaultValue: List<HistoryEntry> = emptyList()
    override suspend fun readFrom(input: InputStream): List<HistoryEntry> = Json.decodeFromStream(input)
    override suspend fun writeTo(t: List<HistoryEntry>, output: OutputStream) = Json.encodeToStream(t, output)
}

class DataStoreHistoryRepository(private val dataStore: DataStore<List<HistoryEntry>>) : HistoryRepository {
    override val entries: Flow<List<HistoryEntry>> = dataStore.data

    override suspend fun update(transform: (List<HistoryEntry>) -> List<HistoryEntry>) {
        dataStore.updateData(transform)
    }
}
