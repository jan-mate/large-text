package dev.safi.largetext.data

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<Settings>
    suspend fun save(settings: Settings)
}
