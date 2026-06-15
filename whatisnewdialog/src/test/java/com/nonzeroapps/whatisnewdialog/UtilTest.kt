package com.nonzeroapps.whatisnewdialog

import android.graphics.Color
import com.nonzeroapps.whatisnewdialog.util.Util
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class UtilTest {

    @Test
    fun getContrastColor_returnsWhiteForDarkColor() {
        val darkColor = Color.rgb(0, 0, 0)
        val contrastColor = Util.getContrastColor(darkColor)
        assertEquals(Color.WHITE, contrastColor)
    }

    @Test
    fun getContrastColor_returnsBlackForLightColor() {
        val lightColor = Color.rgb(255, 255, 255)
        val contrastColor = Util.getContrastColor(lightColor)
        assertEquals(Color.BLACK, contrastColor)
    }
}
