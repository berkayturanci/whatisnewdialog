package com.nonzeroapps.whatisnewdialog.adapter

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

abstract class ViewPagerAdapter : PagerAdapter() {

    abstract fun getItem(position: Int): View

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val mItemView = getItem(position)
        container.addView(mItemView)
        return mItemView
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}
