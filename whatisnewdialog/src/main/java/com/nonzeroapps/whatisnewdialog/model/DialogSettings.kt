package com.nonzeroapps.whatisnewdialog.model

import android.content.Context
import android.os.Parcelable
import androidx.annotation.StringRes
import com.nonzeroapps.whatisnewdialog.R
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class DialogSettings(
    var versionNameId: Int = R.string.version_name,
    var titleResId: Int = R.string.new_features_dialog_title,
    var textPositiveResId: Int = R.string.close,
    var textNeutralResId: Int = R.string.remind_me_later,
    var isUsePaletteForDescBackground: Boolean = true,
    var isUsePaletteForImageBackground: Boolean = true,
    var isShowNeutralButton: Boolean = true,
    var isShowPositiveButton: Boolean = true,
    var isShowTitle: Boolean = true,
    var isCancelable: Boolean = true,
    var versionName: String? = null,
    var titleText: String? = null,
    var positiveText: String? = null,
    var neutralText: String? = null
) : Parcelable {

    fun getTitleText(context: Context): String {
        if (titleText == null) {
            titleText = context.getString(titleResId)
        }
        return titleText!!
    }

    fun getPositiveText(context: Context): String {
        if (positiveText == null) {
            positiveText = context.getString(textPositiveResId)
        }
        return positiveText!!
    }

    fun getNeutralText(context: Context): String {
        if (neutralText == null) {
            neutralText = context.getString(textNeutralResId)
        }
        return neutralText!!
    }

    fun getVersionName(context: Context): String {
        if (versionName == null) {
            versionName = context.getString(versionNameId)
        }
        return versionName!!
    }
}
