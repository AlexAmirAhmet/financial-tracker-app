package com.vela.player.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/** Хранилище настроек на DataStore. Читать — через [settings], менять — через set-функции. */
class SettingsRepository(context: Context) {

    private val store = context.applicationContext.settingsDataStore

    private object Keys {
        val THEME = stringPreferencesKey("theme_mode")
        val DECODER = stringPreferencesKey("decoder")
        val AUTO_NEXT = booleanPreferencesKey("auto_play_next")
        val SUB_SCALE = intPreferencesKey("subtitle_scale")
        val SUB_COLOR = longPreferencesKey("subtitle_color")
        val SUB_BG = booleanPreferencesKey("subtitle_background")
        val SUB_ENCODING = stringPreferencesKey("subtitle_encoding")
        val HIDDEN_FOLDERS = stringSetPreferencesKey("hidden_folders")
        val SORT_FIELD = stringPreferencesKey("sort_field")
        val SORT_DESC = booleanPreferencesKey("sort_descending")
    }

    val settings: Flow<AppSettings> = store.data.map { p ->
        val d = AppSettings()
        AppSettings(
            themeMode = p[Keys.THEME].toEnum(d.themeMode),
            decoder = p[Keys.DECODER].toEnum(d.decoder),
            autoPlayNext = p[Keys.AUTO_NEXT] ?: d.autoPlayNext,
            subtitleScale = p[Keys.SUB_SCALE] ?: d.subtitleScale,
            subtitleColor = p[Keys.SUB_COLOR] ?: d.subtitleColor,
            subtitleBackground = p[Keys.SUB_BG] ?: d.subtitleBackground,
            subtitleEncoding = p[Keys.SUB_ENCODING] ?: d.subtitleEncoding,
            hiddenFolders = p[Keys.HIDDEN_FOLDERS] ?: d.hiddenFolders,
            sortField = p[Keys.SORT_FIELD].toEnum(d.sortField),
            sortDescending = p[Keys.SORT_DESC] ?: d.sortDescending,
        )
    }

    suspend fun setThemeMode(value: ThemeMode) = store.edit { it[Keys.THEME] = value.name }
    suspend fun setDecoder(value: DecoderMode) = store.edit { it[Keys.DECODER] = value.name }
    suspend fun setAutoPlayNext(value: Boolean) = store.edit { it[Keys.AUTO_NEXT] = value }
    suspend fun setSubtitleScale(value: Int) = store.edit { it[Keys.SUB_SCALE] = value }
    suspend fun setSubtitleColor(value: Long) = store.edit { it[Keys.SUB_COLOR] = value }
    suspend fun setSubtitleBackground(value: Boolean) = store.edit { it[Keys.SUB_BG] = value }
    suspend fun setSubtitleEncoding(value: String) = store.edit { it[Keys.SUB_ENCODING] = value }
    suspend fun setHiddenFolders(value: Set<String>) = store.edit { it[Keys.HIDDEN_FOLDERS] = value }
    suspend fun setSort(field: SortField, descending: Boolean) = store.edit {
        it[Keys.SORT_FIELD] = field.name
        it[Keys.SORT_DESC] = descending
    }
}

private inline fun <reified T : Enum<T>> String?.toEnum(default: T): T =
    this?.let { name -> enumValues<T>().firstOrNull { it.name == name } } ?: default
