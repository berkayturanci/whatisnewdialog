package com.nonzeroapps.whatisnewdialog

import com.nonzeroapps.whatisnewdialog.util.SharedPrefHelper
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class NewItemDialogTest {
    private val context = RuntimeEnvironment.getApplication()

    @Before
    fun setUp() {
        // Reset the singleton so each test rebuilds it with the current Robolectric context.
        NewItemDialog::class.java.getDeclaredField("mNewItemDialog").apply {
            isAccessible = true
            set(null, null)
        }
        SharedPrefHelper.clearSharedPreferences(context)
    }

    @Test
    fun initReturnsSingletonInstance() {
        val first = NewItemDialog.init(context)
        val second = NewItemDialog.init(context)
        assertSame(first, second)
    }

    @Test
    fun buildersAreFluent() {
        val dialog = NewItemDialog.init(context)
        assertSame(dialog, dialog.setVersionName("1.0.0"))
        assertSame(dialog, dialog.setDialogTitle("Title"))
        assertSame(dialog, dialog.setPositiveButtonTitle("Close"))
        assertSame(dialog, dialog.setNeutralButtonTitle("Later"))
        assertSame(dialog, dialog.setCancelable(false))
    }

    @Test
    fun conditionsSuitableWhenVersionNotSeen() {
        val dialog = NewItemDialog.init(context).setVersionName("3.1.4")
        assertTrue(dialog.isConditionsSuitable())
    }

    @Test
    fun conditionsNotSuitableAfterVersionSeen() {
        val dialog = NewItemDialog.init(context).setVersionName("3.1.4")
        SharedPrefHelper.setSeenBefore(context, "3.1.4", true)
        assertFalse(dialog.isConditionsSuitable())
    }

    @Test
    fun clearSharedPrefMakesConditionsSuitableAgain() {
        val dialog = NewItemDialog.init(context).setVersionName("3.1.4")
        SharedPrefHelper.setSeenBefore(context, "3.1.4", true)
        assertFalse(dialog.isConditionsSuitable())

        dialog.clearSharedPref()
        assertTrue(dialog.isConditionsSuitable())
    }
}
