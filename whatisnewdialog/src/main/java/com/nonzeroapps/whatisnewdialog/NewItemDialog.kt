package com.nonzeroapps.whatisnewdialog

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import com.nonzeroapps.whatisnewdialog.fragment.WhatIsNewDialogFragment
import com.nonzeroapps.whatisnewdialog.model.DialogSettings
import com.nonzeroapps.whatisnewdialog.model.NewFeatureItem
import com.nonzeroapps.whatisnewdialog.util.SharedPrefHelper
import java.util.ArrayList

class NewItemDialog private constructor(
    private val mContext: Context,
) {
    private var mNewFeatureItemArrayList: ArrayList<NewFeatureItem>? = null
    private val mDialogSettings = DialogSettings()
    private var mPositiveButtonListener: DialogInterface.OnClickListener? = null
    private var mNegativeButtonListener: DialogInterface.OnClickListener? = null

    fun setDialogTitle(dialogTitle: String): NewItemDialog {
        mDialogSettings.titleText = dialogTitle
        return this
    }

    fun setPositiveButtonTitle(positiveButtonTitle: String): NewItemDialog {
        mDialogSettings.positiveText = positiveButtonTitle
        return this
    }

    fun setUsePaletteForDescBackground(usePaletteForDescBackground: Boolean): NewItemDialog {
        mDialogSettings.isUsePaletteForDescBackground = usePaletteForDescBackground
        return this
    }

    fun setUsePaletteForImageBackground(usePaletteForImageBackground: Boolean): NewItemDialog {
        mDialogSettings.isUsePaletteForImageBackground = usePaletteForImageBackground
        return this
    }

    fun setNeutralButtonTitle(neutralButtonTitle: String): NewItemDialog {
        mDialogSettings.neutralText = neutralButtonTitle
        return this
    }

    fun setShowLaterButton(isShowNeutralButton: Boolean): NewItemDialog {
        mDialogSettings.isShowNeutralButton = isShowNeutralButton
        return this
    }

    fun setCancelButton(isShowCancelButton: Boolean): NewItemDialog {
        mDialogSettings.isShowPositiveButton = isShowCancelButton
        return this
    }

    fun setCancelable(cancelable: Boolean): NewItemDialog {
        mDialogSettings.isCancelable = cancelable
        return this
    }

    fun setVersionName(versionName: String): NewItemDialog {
        mDialogSettings.versionName = versionName
        return this
    }

    fun setCancelButtonListener(cancelButtonListener: DialogInterface.OnClickListener): NewItemDialog {
        this.mPositiveButtonListener = cancelButtonListener
        return this
    }

    fun setShowLaterButtonListener(showLaterButtonListener: DialogInterface.OnClickListener): NewItemDialog {
        this.mNegativeButtonListener = showLaterButtonListener
        return this
    }

    fun setItems(newFeatureItemArraylist: ArrayList<NewFeatureItem>): NewItemDialog {
        mNewFeatureItemArrayList = newFeatureItemArraylist
        return this
    }

    fun clearSharedPref() {
        SharedPrefHelper.clearSharedPreferences(mContext)
    }

    fun isConditionsSuitable(): Boolean = !SharedPrefHelper.isSeenBefore(mContext, mDialogSettings.getVersionName(mContext))

    fun showDialogIfConditionsSuitable(activity: AppCompatActivity) {
        if (activity.isFinishing) {
            return
        }

        if (isConditionsSuitable()) {
            showDialog(activity)
        }
    }

    fun showDialog(activity: AppCompatActivity) {
        try {
            if (activity.supportFragmentManager.findFragmentByTag(DIALOG_TAG) == null) {
                val newFragment =
                    WhatIsNewDialogFragment.newInstance(
                        mNewFeatureItemArrayList,
                        mDialogSettings,
                        mPositiveButtonListener,
                        mNegativeButtonListener,
                    )

                val transaction = activity.supportFragmentManager.beginTransaction()
                transaction.add(newFragment, DIALOG_TAG)
                transaction.commitAllowingStateLoss()
            }
        } catch (ex: IllegalStateException) {
            // Ignore
        }
    }

    companion object {
        private const val DIALOG_TAG = "whatIsNewDialogFragment"

        @Volatile
        private var mNewItemDialog: NewItemDialog? = null

        @JvmStatic
        fun init(context: Context): NewItemDialog =
            mNewItemDialog ?: synchronized(this) {
                mNewItemDialog ?: NewItemDialog(context.applicationContext).also { mNewItemDialog = it }
            }
    }
}
