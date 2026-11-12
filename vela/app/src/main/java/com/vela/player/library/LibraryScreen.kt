package com.vela.player.library

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vela.player.R
import com.vela.player.ui.theme.BorderedBlock
import com.vela.player.ui.theme.HandwrittenTitle
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

/**
 * Медиатека. Этап 1 — каркас с дизайном; реальные файлы из MediaStore появятся на этапе 2.
 * Временный блок «Папки» ведёт на другие экраны, чтобы проверить навигацию.
 */
@Composable
fun LibraryScreen(
    onOpenVideo: () -> Unit,
    onOpenAudio: () -> Unit,
    onOpenStream: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val context = LocalContext.current
    val colors = Vela.colors
    var tab by rememberSaveable { mutableStateOf(LibraryTab.ALL) }
    val soon = stringResource(R.string.coming_soon, 2)

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
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Шапка: «привет», дата, кнопки.
            item {
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
                        RoundIconButton(VelaIcons.Search, stringResource(R.string.action_search), {
                            Toast.makeText(context, soon, Toast.LENGTH_SHORT).show()
                        })
                        RoundIconButton(VelaIcons.Plus, stringResource(R.string.action_add), onOpenStream)
                        RoundIconButton(VelaIcons.Settings, stringResource(R.string.action_settings), onOpenSettings)
                    }
                }
            }

            // Чипы вкладок.
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(LibraryTab.entries) { t ->
                        VelaChip(stringResource(t.label), selected = t == tab, onClick = { tab = t })
                    }
                }
            }

            // Продолжить просмотр.
            item {
                BorderedBlock {
                    SectionTitle(stringResource(R.string.library_continue))
                    Spacer(Modifier.height(10.dp))
                    Text(stringResource(R.string.library_continue_empty), style = VelaText.caption, color = colors.textTertiary)
                    Spacer(Modifier.height(12.dp))
                    ProgressLine(0.35f)
                }
            }

            // Папки: сетка 4 колонки. Пока — временные ссылки на экраны.
            item {
                Column {
                    SectionTitle(stringResource(R.string.library_folders))
                    Spacer(Modifier.height(12.dp))
                    val tiles = listOf(
                        Tile(VelaIcons.Video, stringResource(R.string.library_open_video), onOpenVideo),
                        Tile(VelaIcons.Music, stringResource(R.string.library_open_audio), onOpenAudio),
                        Tile(VelaIcons.Stream, stringResource(R.string.library_open_stream), onOpenStream),
                        Tile(VelaIcons.Folder, stringResource(R.string.action_settings), onOpenSettings),
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        tiles.forEach { FolderTile(it, Modifier.weight(1f)) }
                    }
                }
            }

            // Файлы.
            item {
                Column {
                    SectionTitle(stringResource(R.string.library_files))
                    Spacer(Modifier.height(12.dp))
                    VelaCard(Modifier.fillMaxWidth(), contentPadding = 20.dp) {
                        Text(
                            stringResource(R.string.library_empty),
                            style = VelaText.body,
                            color = colors.textSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

private data class Tile(val icon: ImageVector, val label: String, val onClick: () -> Unit)

@Composable
private fun FolderTile(tile: Tile, modifier: Modifier = Modifier) {
    val colors = Vela.colors
    VelaCard(modifier, onClick = tile.onClick, contentPadding = 10.dp) {
        Icon(tile.icon, null, Modifier.size(26.dp).align(Alignment.CenterHorizontally), tint = colors.textPrimary)
        Spacer(Modifier.height(8.dp))
        Text(
            tile.label,
            style = VelaText.caption.copy(fontSize = 10.sp, lineHeight = 13.sp),
            color = colors.textSecondary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun todayText(): String = remember {
    val ru = Locale.forLanguageTag("ru")
    LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM", ru))
}
