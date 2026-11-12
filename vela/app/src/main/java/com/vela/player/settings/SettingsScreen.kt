package com.vela.player.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.vela.player.R
import com.vela.player.ui.theme.BorderedBlock
import com.vela.player.ui.theme.ScreenHeader
import com.vela.player.ui.theme.SectionTitle
import com.vela.player.ui.theme.Vela
import com.vela.player.ui.theme.VelaChip
import com.vela.player.ui.theme.VelaScreen
import com.vela.player.ui.theme.VelaText
import kotlinx.coroutines.launch

/** Настройки. На этапе 7 сюда соберутся все настройки прошлых этапов. */
@Composable
fun SettingsScreen(settings: AppSettings, repository: SettingsRepository, onBack: () -> Unit) {
    val colors = Vela.colors
    val scope = rememberCoroutineScope()

    VelaScreen {
        ScreenHeader(stringResource(R.string.settings_title), onBack, stringResource(R.string.action_back))

        BorderedBlock {
            SectionTitle(stringResource(R.string.settings_theme))
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    ThemeMode.LIGHT to R.string.theme_light,
                    ThemeMode.DARK to R.string.theme_dark,
                    ThemeMode.SYSTEM to R.string.theme_system,
                ).forEach { (mode, label) ->
                    VelaChip(stringResource(label), settings.themeMode == mode, { scope.launch { repository.setThemeMode(mode) } })
                }
            }
        }

        BorderedBlock {
            SectionTitle(stringResource(R.string.settings_decoder))
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    DecoderMode.HW to R.string.decoder_hw,
                    DecoderMode.HW_PLUS to R.string.decoder_hw_plus,
                    DecoderMode.SW to R.string.decoder_sw,
                ).forEach { (mode, label) ->
                    VelaChip(stringResource(label), settings.decoder == mode, { scope.launch { repository.setDecoder(mode) } })
                }
            }
        }

        BorderedBlock {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable(role = Role.Switch) { scope.launch { repository.setAutoPlayNext(!settings.autoPlayNext) } },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    SectionTitle(stringResource(R.string.settings_auto_next))
                    Spacer(Modifier.height(4.dp))
                    Text(stringResource(R.string.settings_auto_next_hint), style = VelaText.caption, color = colors.textTertiary)
                }
                Spacer(Modifier.width(12.dp))
                Switch(
                    checked = settings.autoPlayNext,
                    onCheckedChange = { scope.launch { repository.setAutoPlayNext(it) } },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colors.playIcon,
                        checkedTrackColor = colors.playBackground,
                        uncheckedThumbColor = colors.textTertiary,
                        uncheckedTrackColor = colors.roundButton,
                        uncheckedBorderColor = colors.border,
                    ),
                )
            }
        }

        Text(stringResource(R.string.settings_more_later), style = VelaText.caption, color = colors.textTertiary)
    }
}
