package com.vela.player.stream

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.vela.player.R
import com.vela.player.ui.theme.PlayButton
import com.vela.player.ui.theme.ScreenHeader
import com.vela.player.ui.theme.SectionTitle
import com.vela.player.ui.theme.Vela
import com.vela.player.ui.theme.VelaCard
import com.vela.player.ui.theme.VelaDimens
import com.vela.player.ui.theme.VelaScreen
import com.vela.player.ui.theme.VelaText

/** Сетевой поток. Этап 1 — макет; открытие ссылок и история — этап 7. */
@Composable
fun StreamScreen(onBack: () -> Unit) {
    val colors = Vela.colors
    var url by rememberSaveable { mutableStateOf("") }

    VelaScreen {
        ScreenHeader(stringResource(R.string.stream_title), onBack, stringResource(R.string.action_back))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .weight(1f)
                    .heightIn(min = 56.dp)
                    .border(VelaDimens.borderWidth, colors.border, CircleShape)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (url.isEmpty()) {
                    Text(stringResource(R.string.stream_hint), style = VelaText.body, color = colors.textTertiary)
                }
                BasicTextField(
                    value = url,
                    onValueChange = { url = it },
                    singleLine = true,
                    textStyle = VelaText.body.copy(color = colors.textPrimary),
                    cursorBrush = SolidColor(colors.textPrimary),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Go),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Spacer(Modifier.width(12.dp))
            PlayButton(isPlaying = false, contentDescription = stringResource(R.string.action_open), onClick = {}, size = 56.dp)
        }

        SectionTitle(stringResource(R.string.stream_history))
        VelaCard(Modifier.fillMaxWidth(), contentPadding = 20.dp) {
            Text(stringResource(R.string.stream_history_empty), style = VelaText.body, color = colors.textSecondary)
            Spacer(Modifier.height(4.dp))
            Text(stringResource(R.string.coming_soon, 7), style = VelaText.caption, color = colors.textTertiary)
        }
    }
}
