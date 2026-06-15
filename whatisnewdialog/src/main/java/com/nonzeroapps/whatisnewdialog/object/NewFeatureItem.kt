package com.nonzeroapps.whatisnewdialog.`object`

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewFeatureItem(
    var featureTitle: String? = null,
    var featureDesc: String? = null,
    var imageResource: String? = null,
    @DrawableRes var imageDrawableResource: Int = 0
) : Parcelable {

    /**
     * Backward-compatible overload that mirrors the original Java API, where
     * [setImageResource] accepted a drawable resource id. Stores the id in
     * [imageDrawableResource] so existing callers keep compiling and working.
     */
    fun setImageResource(@DrawableRes imageResource: Int) {
        imageDrawableResource = imageResource
    }
}
