package dev.safi.largetext.data

import kotlinx.coroutines.flow.MutableStateFlow

class FakeSettingsRepository(initial: Settings = Settings()) : SettingsRepository {
    override val settings = MutableStateFlow(initial)
    override suspend fun save(settings: Settings) {
        this.settings.value = settings
    }
}
