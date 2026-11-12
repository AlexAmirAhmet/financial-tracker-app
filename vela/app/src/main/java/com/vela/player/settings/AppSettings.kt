package com.vela.player.settings

/** Тема оформления. */
enum class ThemeMode { LIGHT, DARK, SYSTEM }

/** Декодер видео по умолчанию (опции libVLC, этап 4). */
enum class DecoderMode { HW, HW_PLUS, SW }

/** Сортировка медиатеки. */
enum class SortField { NAME, DATE, SIZE, DURATION }

/** Все настройки приложения. Значения по умолчанию — здесь. */
data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val decoder: DecoderMode = DecoderMode.HW_PLUS,
    val autoPlayNext: Boolean = true,
    /** Размер субтитров в процентах от стандартного (этап 5). */
    val subtitleScale: Int = 100,
    /** Цвет текста субтитров, ARGB. */
    val subtitleColor: Long = 0xFFFFFFFF,
    /** Полупрозрачная подложка под текстом субтитров. */
    val subtitleBackground: Boolean = false,
    /** Кодировка субтитров; пустая строка — автоопределение. */
    val subtitleEncoding: String = "",
    /** Папки, скрытые из медиатеки (относительные пути). */
    val hiddenFolders: Set<String> = emptySet(),
    val sortField: SortField = SortField.NAME,
    val sortDescending: Boolean = false,
)
