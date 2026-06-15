package com.nonzeroapps.whatisnewdialog

import com.nonzeroapps.whatisnewdialog.model.NewFeatureItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class NewFeatureItemTest {
    @Test
    fun defaultsAreEmpty() {
        val item = NewFeatureItem()
        assertNull(item.featureTitle)
        assertNull(item.featureDesc)
        assertNull(item.imageResource)
        assertEquals(0, item.imageDrawableResource)
    }

    @Test
    fun setImageResourceUpdatesDrawableResourceOnly() {
        val item = NewFeatureItem()
        item.setImageResource(1234)
        assertEquals(1234, item.imageDrawableResource)
        assertNull(item.imageResource)
    }

    @Test
    fun survivesParcelRoundTrip() {
        val original =
            NewFeatureItem(
                featureTitle = "Title",
                featureDesc = "Desc",
                imageResource = "https://example.com/a.gif",
                imageDrawableResource = 42,
            )

        val restored = original.parcelRoundTrip()

        assertEquals(original, restored)
    }
}
