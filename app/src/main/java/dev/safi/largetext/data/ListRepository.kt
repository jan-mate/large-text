package dev.safi.largetext.data

import kotlinx.coroutines.flow.Flow

interface ListRepository<T> {
    val items: Flow<List<T>>
    suspend fun update(transform: (List<T>) -> List<T>)
}
