package com.vela.player.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vela.player.R
import com.vela.player.ui.theme.ScreenHeader
import com.vela.player.ui.theme.SectionTitle
import com.vela.player.ui.theme.Vela
import com.vela.player.ui.theme.VelaBackground
import com.vela.player.ui.theme.VelaDimens
import com.vela.player.ui.theme.VelaText

/** Содержимое одной папки. */
@Composable
fun FolderScreen(
    viewModel: LibraryViewModel,
    folderPath: String,
    onBack: () -> Unit,
    onOpenItem: (MediaItem) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val files = state.files.filter { it.folderPath == folderPath }
    val name = folderPath.trimEnd('/').substringAfterLast('/').ifEmpty { "/" }
    val insets = WindowInsets.safeDrawing.asPaddingValues()
    val dir = LocalLayoutDirection.current

    VelaBackground {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = insets.calculateStartPadding(dir) + VelaDimens.screenPadding,
                end = insets.calculateEndPadding(dir) + VelaDimens.screenPadding,
                top = insets.calculateTopPadding() + 12.dp,
                bottom = insets.calculateBottomPadding() + VelaDimens.screenPadding,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { ScreenHeader(name, onBack, stringResource(R.string.action_back)) }
            item {
                Text(folderPath, style = VelaText.caption, color = Vela.colors.textTertiary)
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SectionTitle(stringResource(R.string.library_files_count, files.size), Modifier.weight(1f))
                    SortButton(state.sortField, state.sortDescending, viewModel::setSort)
                }
            }
            items(files, key = { it.uri.toString() }) { item ->
                MediaRow(item, state.positionOf(item), onClick = { onOpenItem(item) })
            }
        }
    }
}
