package dev.safi.largetext.data

import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream

open class JsonListSerializer<T>(
    itemSerializer: KSerializer<T>,
    override val defaultValue: List<T>,
) : Serializer<List<T>> {
    private val serializer = ListSerializer(itemSerializer)
    override suspend fun readFrom(input: InputStream): List<T> = Json.decodeFromStream(serializer, input)
    override suspend fun writeTo(t: List<T>, output: OutputStream) = Json.encodeToStream(serializer, t, output)
}

object HistorySerializer : JsonListSerializer<HistoryEntry>(HistoryEntry.serializer(), emptyList())

object ColorPresetSerializer : JsonListSerializer<ColorPreset>(
    ColorPreset.serializer(),
    listOf(ColorPreset("white-on-black", DisplayColors.WhiteOnBlack), ColorPreset("black-on-white", DisplayColors.BlackOnWhite)),
)

class DataStoreListRepository<T>(private val dataStore: DataStore<List<T>>) : ListRepository<T> {
    override val items: Flow<List<T>> = dataStore.data

    override suspend fun update(transform: (List<T>) -> List<T>) {
        dataStore.updateData(transform)
    }
}
