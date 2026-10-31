package dev.safi.largetext.data

import kotlinx.coroutines.flow.MutableStateFlow

class FakeListRepository<T>(initial: List<T> = emptyList()) : ListRepository<T> {
    override val items = MutableStateFlow(initial)
    override suspend fun update(transform: (List<T>) -> List<T>) {
        items.value = transform(items.value)
    }
}
