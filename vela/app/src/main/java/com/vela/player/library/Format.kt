package com.vela.player.library

import android.content.Context
import android.text.format.Formatter

/** 4523000 → «1:15:23», 83000 → «1:23». */
fun formatTime(ms: Long): String {
    val total = (ms.coerceAtLeast(0) / 1000)
    val h = total / 3600
    val m = (total % 3600) / 60
    val s = total % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
}

/** Размер файла по-русски: «1,4 ГБ». */
fun formatSize(context: Context, bytes: Long): String = Formatter.formatShortFileSize(context, bytes)
