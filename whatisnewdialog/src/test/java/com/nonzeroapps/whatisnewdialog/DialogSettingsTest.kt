package com.nonzeroapps.whatisnewdialog

import com.nonzeroapps.whatisnewdialog.model.DialogSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DialogSettingsTest {
    private val context = RuntimeEnvironment.getApplication()

    @Test
    fun defaultTextsResolveFromResources() {
        val settings = DialogSettings()
        assertEquals("What Is New In This Version!", settings.getTitleText(context))
        assertEquals("Close", settings.getPositiveText(context))
        assertEquals("Remind Me Later", settings.getNeutralText(context))
        assertEquals("1.0.0", settings.getVersionName(context))
    }

    @Test
    fun explicitTextsOverrideResources() {
        val settings = DialogSettings()
        settings.titleText = "Custom Title"
        settings.positiveText = "Got it"
        settings.neutralText = "Later"
        settings.versionName = "9.9.9"

        assertEquals("Custom Title", settings.getTitleText(context))
        assertEquals("Got it", settings.getPositiveText(context))
        assertEquals("Later", settings.getNeutralText(context))
        assertEquals("9.9.9", settings.getVersionName(context))
    }

    @Test
    fun defaultFlagsAreEnabled() {
        val settings = DialogSettings()
        assertTrue(settings.isShowTitle)
        assertTrue(settings.isShowPositiveButton)
        assertTrue(settings.isShowNeutralButton)
        assertTrue(settings.isCancelable)
        assertTrue(settings.isUsePaletteForDescBackground)
        assertTrue(settings.isUsePaletteForImageBackground)
    }

    @Test
    fun survivesParcelRoundTrip() {
        val original = DialogSettings()
        original.titleText = "Title"
        original.positiveText = "OK"
        original.neutralText = "Later"
        original.versionName = "2.0.0"
        original.isShowNeutralButton = false
        original.isCancelable = false

        val restored = original.parcelRoundTrip()

        assertEquals("Title", restored.getTitleText(context))
        assertEquals("OK", restored.getPositiveText(context))
        assertEquals("Later", restored.getNeutralText(context))
        assertEquals("2.0.0", restored.getVersionName(context))
        assertFalse(restored.isShowNeutralButton)
        assertFalse(restored.isCancelable)
    }
}
