package com.nonzeroapps.whatisnewdialog.util

import android.graphics.Color
import androidx.annotation.ColorInt

object Util {
    @ColorInt
    @JvmStatic
    fun getContrastColor(@ColorInt color: Int): Int {
        // Counting the perceptive luminance - human eye favors green color...
        val a = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return if (a < 0.5) Color.BLACK else Color.WHITE
    }
}
