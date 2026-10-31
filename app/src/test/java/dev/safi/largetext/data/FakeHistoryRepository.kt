package dev.safi.largetext.data

import kotlinx.coroutines.flow.MutableStateFlow

class FakeHistoryRepository(initial: List<HistoryEntry> = emptyList()) : HistoryRepository {
    override val entries = MutableStateFlow(initial)
    override suspend fun update(transform: (List<HistoryEntry>) -> List<HistoryEntry>) {
        entries.value = transform(entries.value)
    }
}
