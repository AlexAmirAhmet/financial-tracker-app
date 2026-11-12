package com.vela.player.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.border
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size
import com.vela.player.R
import com.vela.player.data.PlaybackPosition
import com.vela.player.settings.SortField
import com.vela.player.ui.theme.FormatBadge
import com.vela.player.ui.theme.ProgressLine
import com.vela.player.ui.theme.RoundIconButton
import com.vela.player.ui.theme.Vela
import com.vela.player.ui.theme.VelaCard
import com.vela.player.ui.theme.VelaDimens
import com.vela.player.ui.theme.VelaIcons
import com.vela.player.ui.theme.VelaText

/** Строка файла: превью или плашка формата, название, длительность, размер, прогресс. */
@Composable
fun MediaRow(item: MediaItem, position: PlaybackPosition?, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = Vela.colors
    val context = LocalContext.current
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(VelaDimens.cardRadius))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Thumbnail(item, Modifier.size(width = 96.dp, height = 60.dp))
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(item.name, style = VelaText.title, color = colors.textPrimary, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(4.dp))
            val details = buildList {
                if (item.durationMs > 0) add(formatTime(item.durationMs))
                if (item.size > 0) add(formatSize(context, item.size))
            }.joinToString("  ·  ")
            Text(details, style = VelaText.caption, color = colors.textTertiary, maxLines = 1)
            if (position != null && position.positionMs > 0) {
                Spacer(Modifier.height(6.dp))
                ProgressLine(if (position.isFinished) 1f else position.progress)
            }
        }
    }
}

/** Превью кадра для видео; для аудио и при ошибке — плашка формата. */
@Composable
fun Thumbnail(item: MediaItem, modifier: Modifier = Modifier) {
    val colors = Vela.colors
    val shape = RoundedCornerShape(14.dp)
    Box(modifier.clip(shape).background(colors.card, shape), contentAlignment = Alignment.Center) {
        val badge = @Composable { FormatBadge(item.extension) }
        if (item.isVideo) {
            val context = LocalContext.current
            SubcomposeAsyncImage(
                model = remember(item.uri) {
                    ImageRequest.Builder(context).data(item.uri).size(Size(320, 200)).crossfade(true).build()
                },
                contentDescription = null,
                contentScale = ContentScale.Crop,
                loading = { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { badge() } },
                error = { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { badge() } },
                success = {
                    val scope = this
                    Box(Modifier.fillMaxSize()) {
                        scope.SubcomposeAsyncImageContent()
                        // Небольшая плашка формата поверх кадра.
                        Text(
                            item.extension,
                            style = VelaText.badge.copy(fontSize = 8.sp),
                            color = colors.textPrimary,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(5.dp)
                                .background(colors.formatBadge, CircleShape)
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                        )
                    }
                },
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(VelaIcons.Music, null, Modifier.size(20.dp), tint = colors.textSecondary)
                Spacer(Modifier.height(4.dp))
                badge()
            }
        }
    }
}

/** Плитка папки: линейная иконка, название, количество файлов. */
@Composable
fun FolderTile(folder: MediaFolder, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = Vela.colors
    VelaCard(modifier, onClick = onClick, contentPadding = 10.dp) {
        Icon(
            if (folder.hasVideo) VelaIcons.Folder else VelaIcons.Music,
            null,
            Modifier.size(26.dp).align(Alignment.CenterHorizontally),
            tint = colors.textPrimary,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            folder.name,
            style = VelaText.caption.copy(fontSize = 10.sp, lineHeight = 13.sp),
            color = colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            folder.count.toString(),
            style = VelaText.caption.copy(fontSize = 10.sp, lineHeight = 13.sp),
            color = colors.textTertiary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/** Сетка папок в 4 колонки. */
@Composable
fun FolderGrid(folders: List<MediaFolder>, onOpen: (MediaFolder) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        folders.chunked(4).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { FolderTile(it, { onOpen(it) }, Modifier.weight(1f)) }
                repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

/** Кнопка сортировки с выпадающим меню. Повторный выбор того же поля меняет направление. */
@Composable
fun SortButton(field: SortField, descending: Boolean, onChange: (SortField, Boolean) -> Unit) {
    val colors = Vela.colors
    var open by remember { mutableStateOf(false) }
    val labels = mapOf(
        SortField.NAME to R.string.sort_name,
        SortField.DATE to R.string.sort_date,
        SortField.SIZE to R.string.sort_size,
        SortField.DURATION to R.string.sort_duration,
    )
    Box {
        Row(
            Modifier
                .heightIn(min = VelaDimens.minTouch)
                .clip(CircleShape)
                .clickable { open = true }
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(stringResource(labels.getValue(field)), style = VelaText.chip, color = colors.textSecondary)
            Spacer(Modifier.width(4.dp))
            Text(if (descending) "↓" else "↑", style = VelaText.chip, color = colors.textSecondary)
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            SortField.entries.forEach { f ->
                DropdownMenuItem(
                    text = {
                        Text(
                            stringResource(labels.getValue(f)) + if (f == field) (if (descending) "  ↓" else "  ↑") else "",
                            style = VelaText.body,
                        )
                    },
                    onClick = {
                        open = false
                        if (f == field) onChange(f, !descending) else onChange(f, f != SortField.NAME)
                    },
                )
            }
        }
    }
}

/** Поле поиска-«таблетка». */
@Composable
fun SearchField(value: String, onChange: (String) -> Unit, onClose: () -> Unit, modifier: Modifier = Modifier) {
    val colors = Vela.colors
    val focus = remember { FocusRequester() }
    LaunchedEffect(Unit) { focus.requestFocus() }
    Row(
        modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .border(VelaDimens.borderWidth, colors.border, CircleShape)
            .padding(start = 18.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(VelaIcons.Search, null, Modifier.size(18.dp), tint = colors.textTertiary)
        Spacer(Modifier.width(10.dp))
        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (value.isEmpty()) Text(stringResource(R.string.search_hint), style = VelaText.body, color = colors.textTertiary)
            BasicTextField(
                value = value,
                onValueChange = onChange,
                singleLine = true,
                textStyle = VelaText.body.copy(color = colors.textPrimary),
                cursorBrush = SolidColor(colors.textPrimary),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                modifier = Modifier.fillMaxWidth().focusRequester(focus),
            )
        }
        RoundIconButton(VelaIcons.Close, stringResource(R.string.action_close), onClose, size = 44.dp, iconSize = 18.dp)
    }
}
