package com.nonzeroapps.whatisnewdialog

import com.nonzeroapps.whatisnewdialog.util.SharedPrefHelper
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SharedPrefHelperTest {
    private val context = RuntimeEnvironment.getApplication()

    @Before
    fun setUp() {
        SharedPrefHelper.clearSharedPreferences(context)
    }

    @Test
    fun unseenByDefault() {
        assertFalse(SharedPrefHelper.isSeenBefore(context, "1.0.0"))
    }

    @Test
    fun marksVersionAsSeen() {
        SharedPrefHelper.setSeenBefore(context, "1.0.0", true)
        assertTrue(SharedPrefHelper.isSeenBefore(context, "1.0.0"))
    }

    @Test
    fun versionsAreTrackedIndependently() {
        SharedPrefHelper.setSeenBefore(context, "1.0.0", true)
        assertTrue(SharedPrefHelper.isSeenBefore(context, "1.0.0"))
        assertFalse(SharedPrefHelper.isSeenBefore(context, "2.0.0"))
    }

    @Test
    fun clearResetsSeenState() {
        SharedPrefHelper.setSeenBefore(context, "1.0.0", true)
        SharedPrefHelper.clearSharedPreferences(context)
        assertFalse(SharedPrefHelper.isSeenBefore(context, "1.0.0"))
    }
}
