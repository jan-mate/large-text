package dev.safi.largetext.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreSettingsRepository(private val dataStore: DataStore<Preferences>) : SettingsRepository {
    private val fontKey = stringPreferencesKey("font")
    private val themeKey = stringPreferencesKey("theme")
    private val rotationKey = stringPreferencesKey("rotation")
    private val lastTextKey = stringPreferencesKey("last_text")

    override val settings: Flow<Settings> = dataStore.data.map { preferences ->
        val defaults = Settings()
        Settings(
            font = preferences[fontKey]?.let(AppFont::valueOf) ?: defaults.font,
            theme = preferences[themeKey]?.let(DisplayTheme::valueOf) ?: defaults.theme,
            rotation = preferences[rotationKey]?.let(RotationMode::valueOf) ?: defaults.rotation,
            lastText = preferences[lastTextKey] ?: defaults.lastText,
        )
    }

    override suspend fun save(settings: Settings) {
        dataStore.edit { preferences ->
            preferences[fontKey] = settings.font.name
            preferences[themeKey] = settings.theme.name
            preferences[rotationKey] = settings.rotation.name
            preferences[lastTextKey] = settings.lastText
        }
    }
}
