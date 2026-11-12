package com.vela.player.library

import android.app.Application
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vela.player.VelaApp
import com.vela.player.data.PlaybackPosition
import com.vela.player.settings.AppSettings
import com.vela.player.settings.SortField
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Всё, что нужно экрану медиатеки. */
data class LibraryUiState(
    val access: MediaAccess = MediaAccess.FULL,
    val loading: Boolean = true,
    val query: String = "",
    val sortField: SortField = SortField.NAME,
    val sortDescending: Boolean = false,
    /** Видимые файлы (без скрытых папок), отсортированные. */
    val files: List<MediaItem> = emptyList(),
    val folders: List<MediaFolder> = emptyList(),
    /** Позиции просмотра по ключу файла. */
    val positions: Map<String, PlaybackPosition> = emptyMap(),
    val continueWatching: PlaybackPosition? = null,
) {
    fun positionOf(item: MediaItem): PlaybackPosition? {
        item.path?.let { p -> positions[p]?.let { return it } }
        val fp = item.identity.fingerprint ?: return null
        return positions[fp] ?: positions.values.firstOrNull { it.fingerprint == fp }
    }
}

class LibraryViewModel(app: Application) : AndroidViewModel(app) {

    private val velaApp = app as VelaApp
    private val mediaStore = MediaStoreRepository(app)

    private val access = MutableStateFlow(MediaPermissions.access(app))
    private val allItems = MutableStateFlow<List<MediaItem>?>(null)
    private val query = MutableStateFlow("")

    val searchQuery: StateFlow<String> = query.asStateFlow()

    val state: StateFlow<LibraryUiState> = combine(
        combine(access, allItems, query, ::Triple),
        velaApp.settingsRepository.settings,
        velaApp.positionRepository.all,
        velaApp.positionRepository.continueWatching,
    ) { (acc, items, q), settings, positions, cont ->
        build(acc, items, q, settings, positions, cont)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LibraryUiState(access = MediaPermissions.access(app)))

    private val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) = scheduleReload()
    }

    init {
        val resolver = app.contentResolver
        resolver.registerContentObserver(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, true, observer)
        resolver.registerContentObserver(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, observer)
        refresh()
    }

    /** Перепроверить разрешения и перечитать файлы (вызывается при возврате на экран). */
    fun refresh() {
        val app = getApplication<Application>()
        access.value = MediaPermissions.access(app)
        if (access.value == MediaAccess.NONE) {
            allItems.value = emptyList()
            return
        }
        viewModelScope.launch { allItems.value = mediaStore.loadAll() }
    }

    private var reloadJob: Job? = null
    private fun scheduleReload() {
        reloadJob?.cancel()
        reloadJob = viewModelScope.launch {
            delay(800) // при копировании многих файлов изменения идут пачкой
            refresh()
        }
    }

    fun setQuery(value: String) {
        query.value = value
    }

    fun setSort(field: SortField, descending: Boolean) {
        viewModelScope.launch { velaApp.settingsRepository.setSort(field, descending) }
    }

    override fun onCleared() {
        getApplication<Application>().contentResolver.unregisterContentObserver(observer)
    }

    private fun build(
        acc: MediaAccess,
        items: List<MediaItem>?,
        q: String,
        settings: AppSettings,
        positions: List<PlaybackPosition>,
        cont: PlaybackPosition?,
    ): LibraryUiState {
        val visible = (items ?: emptyList()).filter { it.folderPath !in settings.hiddenFolders }
        val needle = q.trim()
        val filtered = if (needle.isEmpty()) visible else visible.filter {
            it.name.contains(needle, ignoreCase = true) || it.folderName.contains(needle, ignoreCase = true)
        }
        val comparator: Comparator<MediaItem> = when (settings.sortField) {
            SortField.NAME -> compareBy(String.CASE_INSENSITIVE_ORDER) { it.name }
            SortField.DATE -> compareBy { it.dateModified }
            SortField.SIZE -> compareBy { it.size }
            SortField.DURATION -> compareBy { it.durationMs }
        }
        val sorted = filtered.sortedWith(if (settings.sortDescending) comparator.reversed() else comparator)

        val folders = filtered.groupBy { it.folderPath }.map { (path, list) ->
            MediaFolder(path, list.first().folderName, list.size, list.any { it.isVideo })
        }.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })

        val map = HashMap<String, PlaybackPosition>(positions.size * 2)
        positions.forEach { p ->
            map[p.key] = p
            p.fingerprint?.let { map.putIfAbsent(it, p) }
        }
        return LibraryUiState(
            access = acc,
            loading = items == null,
            query = q,
            sortField = settings.sortField,
            sortDescending = settings.sortDescending,
            files = sorted,
            folders = folders,
            positions = map,
            continueWatching = cont,
        )
    }
}
