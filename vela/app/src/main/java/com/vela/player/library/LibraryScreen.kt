package com.vela.player.library

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vela.player.R
import com.vela.player.data.PlaybackPosition
import com.vela.player.ui.theme.BorderedBlock
import com.vela.player.ui.theme.HandwrittenTitle
import com.vela.player.ui.theme.PlayButton
import com.vela.player.ui.theme.ProgressLine
import com.vela.player.ui.theme.RoundIconButton
import com.vela.player.ui.theme.SectionTitle
import com.vela.player.ui.theme.Vela
import com.vela.player.ui.theme.VelaBackground
import com.vela.player.ui.theme.VelaCard
import com.vela.player.ui.theme.VelaChip
import com.vela.player.ui.theme.VelaDimens
import com.vela.player.ui.theme.VelaIcons
import com.vela.player.ui.theme.VelaText
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Вкладки медиатеки. */
enum class LibraryTab(val label: Int) {
    ALL(R.string.chip_all), VIDEO(R.string.chip_video), AUDIO(R.string.chip_audio), FOLDERS(R.string.chip_folders)
}

/** Сколько папок показывать на вкладке «Все» (остальные — на вкладке «Папки»). */
private const val FOLDERS_PREVIEW = 8

@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel,
    onOpenItem: (MediaItem) -> Unit,
    onOpenContinue: (PlaybackPosition) -> Unit,
    onOpenFolder: (MediaFolder) -> Unit,
    onOpenStream: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val context = LocalContext.current
    val colors = Vela.colors
    val state by viewModel.state.collectAsStateWithLifecycle()
    var tab by rememberSaveable { mutableStateOf(LibraryTab.ALL) }
    var searching by rememberSaveable { mutableStateOf(false) }

    // --- Разрешения ---
    var askedOnce by rememberSaveable { mutableStateOf(false) }
    var deniedForever by rememberSaveable { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        viewModel.refresh()
        val activity = context as? Activity
        deniedForever = MediaPermissions.access(context) == MediaAccess.NONE &&
            activity != null && MediaPermissions.toRequest.none { activity.shouldShowRequestPermissionRationale(it) }
    }
    val requestAccess = {
        if (deniedForever) {
            context.startActivity(
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null)),
            )
        } else {
            permissionLauncher.launch(MediaPermissions.toRequest)
        }
    }
    // При первом запуске сразу спрашиваем доступ к файлам.
    LaunchedEffect(state.access) {
        if (state.access == MediaAccess.NONE && !askedOnce) {
            askedOnce = true
            permissionLauncher.launch(MediaPermissions.toRequest)
        }
    }
    // Вернулись на экран (из настроек, из плеера) — перепроверяем доступ и файлы.
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.refresh() }

    val insets = WindowInsets.safeDrawing.asPaddingValues()
    val dir = LocalLayoutDirection.current
    val filesForTab = when (tab) {
        LibraryTab.VIDEO -> state.files.filter { it.isVideo }
        LibraryTab.AUDIO -> state.files.filter { !it.isVideo }
        else -> state.files
    }

    VelaBackground {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = insets.calculateStartPadding(dir) + VelaDimens.screenPadding,
                end = insets.calculateEndPadding(dir) + VelaDimens.screenPadding,
                top = insets.calculateTopPadding() + 12.dp,
                bottom = insets.calculateBottomPadding() + VelaDimens.screenPadding,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item(key = "header") {
                Header(
                    onSearch = { searching = !searching; if (!searching) viewModel.setQuery("") },
                    onAdd = onOpenStream,
                    onSettings = onOpenSettings,
                )
            }
            if (searching) {
                item(key = "search") {
                    SearchField(
                        value = state.query,
                        onChange = viewModel::setQuery,
                        onClose = { searching = false; viewModel.setQuery("") },
                    )
                }
            }
            item(key = "chips") {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(LibraryTab.entries) { t ->
                        VelaChip(stringResource(t.label), selected = t == tab, onClick = { tab = t })
                    }
                }
            }

            when (state.access) {
                MediaAccess.NONE -> {
                    item(key = "permission") { PermissionCard(deniedForever, requestAccess) }
                    return@LazyColumn
                }
                MediaAccess.PARTIAL -> item(key = "partial") { PartialAccessBanner(requestAccess) }
                MediaAccess.FULL -> Unit
            }

            val cont = state.continueWatching
            if (cont != null && state.query.isEmpty() && (tab == LibraryTab.ALL || tab == LibraryTab.VIDEO)) {
                item(key = "continue") { ContinueBlock(cont) { onOpenContinue(cont) } }
            }

            when (tab) {
                LibraryTab.ALL -> {
                    if (state.folders.isNotEmpty()) {
                        item(key = "folders") {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    SectionTitle(stringResource(R.string.library_folders), Modifier.weight(1f))
                                    if (state.folders.size > FOLDERS_PREVIEW) {
                                        Text(
                                            stringResource(R.string.library_all_folders, state.folders.size),
                                            style = VelaText.chip,
                                            color = colors.textSecondary,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(50))
                                                .clickable { tab = LibraryTab.FOLDERS }
                                                .padding(horizontal = 12.dp, vertical = 14.dp),
                                        )
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                                FolderGrid(state.folders.take(FOLDERS_PREVIEW), onOpenFolder)
                            }
                        }
                    }
                    filesSection(state, filesForTab, viewModel, onOpenItem)
                }
                LibraryTab.VIDEO, LibraryTab.AUDIO -> filesSection(state, filesForTab, viewModel, onOpenItem)
                LibraryTab.FOLDERS -> item(key = "allFolders") {
                    if (state.folders.isEmpty() && !state.loading) EmptyCard(stringResource(R.string.library_no_folders))
                    else FolderGrid(state.folders, onOpenFolder)
                }
            }
        }
    }
}

private fun LazyListScope.filesSection(
    state: LibraryUiState,
    files: List<MediaItem>,
    viewModel: LibraryViewModel,
    onOpenItem: (MediaItem) -> Unit,
) {
    if (state.loading) return
    item(key = "filesHeader") {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SectionTitle(stringResource(R.string.library_files_count, files.size), Modifier.weight(1f))
            SortButton(state.sortField, state.sortDescending, viewModel::setSort)
        }
    }
    if (files.isEmpty() && !state.loading) {
        item(key = "empty") {
            EmptyCard(
                stringResource(if (state.query.isNotEmpty()) R.string.library_nothing_found else R.string.library_no_files),
            )
        }
    }
    items(files, key = { it.uri.toString() }) { item ->
        MediaRow(item, state.positionOf(item), onClick = { onOpenItem(item) })
    }
}

@Composable
private fun Header(onSearch: () -> Unit, onAdd: () -> Unit, onSettings: () -> Unit) {
    val colors = Vela.colors
    Row(verticalAlignment = Alignment.Top) {
        Column(Modifier.weight(1f)) {
            HandwrittenTitle(stringResource(R.string.library_hello), underline = true)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(todayText(), style = VelaText.body, color = colors.textSecondary)
                Spacer(Modifier.width(6.dp))
                Icon(VelaIcons.ArrowRight, null, Modifier.size(16.dp), tint = colors.textSecondary)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
            RoundIconButton(VelaIcons.Search, stringResource(R.string.action_search), onSearch)
            RoundIconButton(VelaIcons.Plus, stringResource(R.string.action_add), onAdd)
            RoundIconButton(VelaIcons.Settings, stringResource(R.string.action_settings), onSettings)
        }
    }
}

@Composable
private fun ContinueBlock(position: PlaybackPosition, onClick: () -> Unit) {
    val colors = Vela.colors
    BorderedBlock(
        Modifier
            .clip(RoundedCornerShape(VelaDimens.blockRadius))
            .clickable(onClick = onClick),
    ) {
        SectionTitle(stringResource(R.string.library_continue))
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            PlayButton(isPlaying = false, contentDescription = stringResource(R.string.action_play), onClick = onClick, size = 52.dp)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(position.title, style = VelaText.title, color = colors.textPrimary, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Text(
                    if (position.durationMs > 0) stringResource(R.string.library_time_left, formatTime(position.durationMs - position.positionMs))
                    else formatTime(position.positionMs),
                    style = VelaText.caption,
                    color = colors.textTertiary,
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        ProgressLine(position.progress)
    }
}

@Composable
private fun PermissionCard(deniedForever: Boolean, onRequest: () -> Unit) {
    val colors = Vela.colors
    BorderedBlock {
        SectionTitle(stringResource(R.string.permission_title))
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(if (deniedForever) R.string.permission_denied_text else R.string.permission_text),
            style = VelaText.body,
            color = colors.textSecondary,
        )
        Spacer(Modifier.height(14.dp))
        PillButton(stringResource(if (deniedForever) R.string.permission_open_settings else R.string.permission_allow), onRequest)
    }
}

@Composable
private fun PartialAccessBanner(onRequest: () -> Unit) {
    val colors = Vela.colors
    VelaCard(Modifier.fillMaxWidth()) {
        Text(stringResource(R.string.partial_title), style = VelaText.title, color = colors.textPrimary)
        Spacer(Modifier.height(4.dp))
        Text(stringResource(R.string.partial_text), style = VelaText.caption, color = colors.textSecondary)
        Spacer(Modifier.height(10.dp))
        PillButton(stringResource(R.string.partial_allow_all), onRequest)
    }
}

/** Тёмная кнопка-«таблетка». */
@Composable
fun PillButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = Vela.colors
    Text(
        text,
        style = VelaText.chip,
        color = colors.playIcon,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(colors.playBackground, RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
    )
}

@Composable
private fun EmptyCard(text: String) {
    VelaCard(Modifier.fillMaxWidth(), contentPadding = 20.dp) {
        Text(
            text,
            style = VelaText.body,
            color = Vela.colors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun todayText(): String = remember {
    LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.forLanguageTag("ru")))
}

