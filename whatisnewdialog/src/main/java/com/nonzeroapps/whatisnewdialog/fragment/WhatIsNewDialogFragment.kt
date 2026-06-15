package com.nonzeroapps.whatisnewdialog.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.viewpager.widget.ViewPager
import com.nonzeroapps.whatisnewdialog.R
import com.nonzeroapps.whatisnewdialog.adapter.ImageViewPagerAdapter
import com.nonzeroapps.whatisnewdialog.model.DialogSettings
import com.nonzeroapps.whatisnewdialog.model.NewFeatureItem
import com.nonzeroapps.whatisnewdialog.util.ParallaxPagerTransformer
import com.nonzeroapps.whatisnewdialog.util.SharedPrefHelper
import com.nonzeroapps.whatisnewdialog.view.InkPageIndicator
import java.util.ArrayList

class WhatIsNewDialogFragment : DialogFragment() {
    private lateinit var mImageViewPager: ViewPager
    private lateinit var mInkPageIndicator: InkPageIndicator
    private var mNewFeatureItemArrayList: ArrayList<NewFeatureItem>? = null
    private var mPositiveButtonListener: DialogInterface.OnClickListener? = null
    private var mNeutralButtonListener: DialogInterface.OnClickListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()

        val view = requireActivity().layoutInflater.inflate(R.layout.newfeaturedialog, null)

        mNewFeatureItemArrayList =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                arguments?.getParcelableArrayList(NEW_FEATURE_ITEM_LIST, NewFeatureItem::class.java)
            } else {
                @Suppress("DEPRECATION")
                arguments?.getParcelableArrayList(NEW_FEATURE_ITEM_LIST)
            }
        val dialogSettings =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                arguments?.getParcelable(DIALOG_SETTINGS, DialogSettings::class.java)
            } else {
                @Suppress("DEPRECATION")
                arguments?.getParcelable(DIALOG_SETTINGS)
            }

        mImageViewPager = view.findViewById(R.id.viewPager)
        mInkPageIndicator = view.findViewById(R.id.indicator)
        mImageViewPager.setPageTransformer(false, ParallaxPagerTransformer(R.id.imageView))

        if (dialogSettings != null) {
            initPage(dialogSettings)
        }

        val builder = AlertDialog.Builder(context).setView(view)

        if (dialogSettings != null) {
            if (dialogSettings.isShowTitle) {
                builder.setTitle(dialogSettings.getTitleText(context))
            }

            if (dialogSettings.isShowPositiveButton) {
                builder.setPositiveButton(dialogSettings.getPositiveText(context)) { dialog, which ->
                    SharedPrefHelper.setSeenBefore(context, dialogSettings.getVersionName(context), true)
                    mPositiveButtonListener?.onClick(dialog, which)
                }
            }

            if (dialogSettings.isShowNeutralButton) {
                builder.setNeutralButton(dialogSettings.getNeutralText(context)) { dialog, which ->
                    mNeutralButtonListener?.onClick(dialog, which)
                }
            }

            builder.setCancelable(dialogSettings.isCancelable)
        }

        return builder.create()
    }

    private fun initPage(dialogSettings: DialogSettings) {
        val adapter =
            ImageViewPagerAdapter(
                requireContext(),
                mNewFeatureItemArrayList ?: ArrayList(),
                dialogSettings.isUsePaletteForDescBackground,
                dialogSettings.isUsePaletteForImageBackground,
            )

        mImageViewPager.adapter = adapter
        mInkPageIndicator.setViewPager(mImageViewPager)
    }

    fun setPositiveButtonListener(positiveButtonListener: DialogInterface.OnClickListener?) {
        mPositiveButtonListener = positiveButtonListener
    }

    fun setNeutralButtonListener(neutralButtonListener: DialogInterface.OnClickListener?) {
        mNeutralButtonListener = neutralButtonListener
    }

    companion object {
        private const val NEW_FEATURE_ITEM_LIST = "newFeatureItemList"
        private const val DIALOG_SETTINGS = "dialogSettings"

        fun newInstance(
            newFeatureItemArrayList: ArrayList<NewFeatureItem>?,
            dialogSettings: DialogSettings,
            positiveButtonListener: DialogInterface.OnClickListener?,
            neutralButtonListener: DialogInterface.OnClickListener?,
        ): WhatIsNewDialogFragment {
            val whatIsNewDialogFragment = WhatIsNewDialogFragment()

            whatIsNewDialogFragment.setPositiveButtonListener(positiveButtonListener)
            whatIsNewDialogFragment.setNeutralButtonListener(neutralButtonListener)

            val args = Bundle()
            args.putParcelableArrayList(NEW_FEATURE_ITEM_LIST, newFeatureItemArrayList)
            args.putParcelable(DIALOG_SETTINGS, dialogSettings)
            whatIsNewDialogFragment.arguments = args

            return whatIsNewDialogFragment
        }
    }
}
