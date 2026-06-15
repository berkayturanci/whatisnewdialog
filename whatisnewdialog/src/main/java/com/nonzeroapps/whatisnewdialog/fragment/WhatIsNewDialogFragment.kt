package com.nonzeroapps.whatisnewdialog.fragment

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.viewpager.widget.ViewPager
import com.nonzeroapps.whatisnewdialog.R
import com.nonzeroapps.whatisnewdialog.adapter.ImageViewPagerAdapter
import com.nonzeroapps.whatisnewdialog.`object`.DialogSettings
import com.nonzeroapps.whatisnewdialog.`object`.NewFeatureItem
import com.nonzeroapps.whatisnewdialog.util.ParallaxPagerTransformer
import com.nonzeroapps.whatisnewdialog.util.SharedPrefHelper
import com.nonzeroapps.whatisnewdialog.view.InkPageIndicator

class WhatIsNewDialogFragment : DialogFragment() {

    private lateinit var mImageViewPager: ViewPager
    private lateinit var mInkPageIndicator: InkPageIndicator
    private var mNewFeatureItemArrayList: ArrayList<NewFeatureItem>? = null
    private var mPositiveButtonListener: DialogInterface.OnClickListener? = null
    private var mNeutralButtonListener: DialogInterface.OnClickListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()
        val view = requireActivity().layoutInflater.inflate(R.layout.newfeaturedialog, null)

        @Suppress("DEPRECATION")
        mNewFeatureItemArrayList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelableArrayList(NEW_FEATURE_ITEM_LIST, NewFeatureItem::class.java)
        } else {
            arguments?.getParcelableArrayList(NEW_FEATURE_ITEM_LIST)
        }

        @Suppress("DEPRECATION")
        val dialogSettings = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(DIALOG_SETTINGS, DialogSettings::class.java)
        } else {
            arguments?.getParcelable(DIALOG_SETTINGS)
        }

        mImageViewPager = view.findViewById(R.id.viewPager)
        mInkPageIndicator = view.findViewById(R.id.indicator)
        mImageViewPager.setPageTransformer(false, ParallaxPagerTransformer(R.id.imageView))

        dialogSettings?.let { initPage(it) }

        val builder = AlertDialog.Builder(context).setView(view)

        dialogSettings?.let { settings ->
            if (settings.isShowTitle) {
                builder.setTitle(settings.getTitleText(context))
            }

            if (settings.isShowPositiveButton) {
                builder.setPositiveButton(settings.getPositiveText(context)) { dialog, which ->
                    SharedPrefHelper.setSeenBefore(context, settings.getVersionName(context), true)
                    mPositiveButtonListener?.onClick(dialog, which)
                }
            }

            if (settings.isShowNeutralButton) {
                builder.setNeutralButton(settings.getNeutralText(context)) { dialog, which ->
                    mNeutralButtonListener?.onClick(dialog, which)
                }
            }

            builder.setCancelable(settings.isCancelable)
        }

        return builder.create()
    }

    private fun initPage(dialogSettings: DialogSettings) {
        val adapter = ImageViewPagerAdapter(
            requireContext(), mNewFeatureItemArrayList ?: ArrayList(),
            dialogSettings.usePaletteForDescBackground,
            dialogSettings.usePaletteForImageBackground
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

        @JvmStatic
        fun newInstance(
            newFeatureItemArrayList: ArrayList<NewFeatureItem>?,
            dialogSettings: DialogSettings?,
            positiveButtonListener: DialogInterface.OnClickListener?,
            neutralButtonListener: DialogInterface.OnClickListener?
        ): WhatIsNewDialogFragment {
            val fragment = WhatIsNewDialogFragment()
            fragment.setPositiveButtonListener(positiveButtonListener)
            fragment.setNeutralButtonListener(neutralButtonListener)

            val args = Bundle()
            args.putParcelableArrayList(NEW_FEATURE_ITEM_LIST, newFeatureItemArrayList)
            args.putParcelable(DIALOG_SETTINGS, dialogSettings)
            fragment.arguments = args

            return fragment
        }
    }
}
