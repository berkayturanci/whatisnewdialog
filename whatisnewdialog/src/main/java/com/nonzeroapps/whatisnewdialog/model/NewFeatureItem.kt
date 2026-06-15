package com.nonzeroapps.whatisnewdialog.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewFeatureItem(
    var featureTitle: String? = null,
    var featureDesc: String? = null,
    var imageResource: String? = null,
    var imageDrawableResource: Int = 0
) : Parcelable {
    fun setImageResource(@DrawableRes imageResource: Int) {
        this.imageDrawableResource = imageResource
    }
}
