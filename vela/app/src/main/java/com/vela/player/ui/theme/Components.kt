package com.vela.player.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Фон экрана с пастельным градиентом на весь экран (в том числе под системными панелями). */
@Composable
fun VelaBackground(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    val colors = Vela.colors
    val brush = remember(colors) { colors.backgroundBrush() }
    Box(modifier.fillMaxSize().background(brush), content = content)
}

/** Блок в тонкой тёмной рамке (скругление 24dp). */
@Composable
fun BorderedBlock(
    modifier: Modifier = Modifier,
    contentPadding: Dp = 18.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier
            .fillMaxWidth()
            .border(VelaDimens.borderWidth, Vela.colors.border, RoundedCornerShape(VelaDimens.blockRadius))
            .padding(contentPadding),
        content = content,
    )
}

/** Серая полупрозрачная карточка (скругление 20dp). */
@Composable
fun VelaCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    contentPadding: Dp = 14.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(VelaDimens.cardRadius)
    Column(
        modifier
            .clip(shape)
            .background(Vela.colors.card, shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(contentPadding),
        content = content,
    )
}

/** Круглая кнопка с иконкой на полупрозрачной подложке. */
@Composable
fun RoundIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = VelaDimens.roundButton,
    iconSize: Dp = 22.dp,
    enabled: Boolean = true,
    background: Color = Vela.colors.roundButton,
    tint: Color = Vela.colors.textPrimary,
) {
    Box(
        modifier
            .size(size)
            .clip(CircleShape)
            .background(background)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription, Modifier.size(iconSize), tint = if (enabled) tint else tint.copy(alpha = 0.35f))
    }
}

/** Главная круглая тёмная кнопка Play/Пауза. */
@Composable
fun PlayButton(
    isPlaying: Boolean,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = VelaDimens.playButton,
) {
    val colors = Vela.colors
    RoundIconButton(
        icon = if (isPlaying) VelaIcons.Pause else VelaIcons.Play,
        contentDescription = contentDescription,
        onClick = onClick,
        modifier = modifier,
        size = size,
        iconSize = size * 0.4f,
        background = colors.playBackground,
        tint = colors.playIcon,
    )
}

/** Плашка формата: MP4, FLAC 24/96 и т.п. */
@Composable
fun FormatBadge(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = VelaText.badge,
        color = Vela.colors.textPrimary,
        maxLines = 1,
        modifier = modifier
            .background(Vela.colors.formatBadge, CircleShape)
            .padding(horizontal = 10.dp, vertical = 5.dp),
    )
}

/** Круглый чип-переключатель. Видимая высота 36dp, зона нажатия — 44dp. */
@Composable
fun VelaChip(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = Vela.colors
    Box(
        modifier
            .heightIn(min = VelaDimens.minTouch)
            .clip(CircleShape)
            .clickable(role = Role.Tab, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        val pill = Modifier
            .height(36.dp)
            .then(
                if (selected) Modifier.background(colors.playBackground, CircleShape)
                else Modifier.border(VelaDimens.borderWidth, colors.border, CircleShape),
            )
            .padding(horizontal = 16.dp)
        Box(pill, contentAlignment = Alignment.Center) {
            Text(text, style = VelaText.chip, color = if (selected) colors.playIcon else colors.textPrimary)
        }
    }
}

/**
 * Рукописный заголовок. С [underline] — голубое «от руки» подчёркивание и акцентная точка.
 */
@Composable
fun HandwrittenTitle(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = VelaText.handwrittenLarge,
    underline: Boolean = false,
) {
    val colors = Vela.colors
    Row(modifier, verticalAlignment = Alignment.Bottom) {
        Text(
            text = text,
            style = style,
            color = colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = if (!underline) Modifier else Modifier.drawBehind {
                val y = size.height - 6.dp.toPx()
                val path = Path().apply {
                    moveTo(2.dp.toPx(), y)
                    quadraticTo(size.width * 0.5f, y + 5.dp.toPx(), size.width - 2.dp.toPx(), y - 2.dp.toPx())
                }
                drawPath(path, colors.accent, style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round))
            },
        )
        if (underline) {
            Spacer(Modifier.width(4.dp))
            Box(
                Modifier
                    .padding(bottom = 16.dp)
                    .size(9.dp)
                    .drawBehind { drawCircle(colors.accent, radius = size.minDimension / 2, center = Offset(size.width / 2, size.height / 2)) },
            )
        }
    }
}

/** Заголовок экрана: кнопка «назад» + рукописное название. */
@Composable
fun ScreenHeader(title: String, onBack: () -> Unit, backDescription: String, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        RoundIconButton(VelaIcons.Back, backDescription, onBack)
        HandwrittenTitle(title, style = VelaText.handwrittenMedium)
    }
}

/** Подзаголовок секции (Montserrat SemiBold). */
@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(text, style = VelaText.title, color = Vela.colors.textPrimary, modifier = modifier)
}

/** Тонкая полоска прогресса. */
@Composable
fun ProgressLine(progress: Float, modifier: Modifier = Modifier) {
    val colors = Vela.colors
    Box(
        modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(CircleShape)
            .background(colors.textPrimary.copy(alpha = 0.12f)),
    ) {
        Box(
            Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(4.dp)
                .background(colors.accent, CircleShape),
        )
    }
}

/** Каркас простого экрана: градиентный фон, отступы от системных панелей, прокрутка. */
@Composable
fun VelaScreen(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    VelaBackground(modifier) {
        Column(
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = VelaDimens.screenPadding)
                .padding(top = 12.dp, bottom = VelaDimens.screenPadding),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            content = content,
        )
    }
}
