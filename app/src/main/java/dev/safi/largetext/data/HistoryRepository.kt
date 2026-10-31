package dev.safi.largetext.data

import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    val entries: Flow<List<HistoryEntry>>
    suspend fun update(transform: (List<HistoryEntry>) -> List<HistoryEntry>)
}
