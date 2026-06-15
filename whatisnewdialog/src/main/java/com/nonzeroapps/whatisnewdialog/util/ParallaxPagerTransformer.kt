package com.nonzeroapps.whatisnewdialog.util

import android.view.View
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs

class ParallaxPagerTransformer(
    private val id: Int,
) : ViewPager.PageTransformer {
    private val border = 0
    private val speed = 0.2f

    override fun transformPage(
        view: View,
        position: Float,
    ) {
        val parallaxView = view.findViewById<View>(id)

        if (parallaxView != null) {
            if (position > -1 && position < 1) {
                val width = parallaxView.width.toFloat()
                parallaxView.translationX = -(position * width * speed)
                val sc = (view.width.toFloat() - border) / view.width.toFloat()
                if (position == 0f) {
                    view.scaleX = 1f
                    view.scaleY = 1f
                } else {
                    view.scaleX = 1 - abs(position) * (1 - sc)
                    view.scaleY = 1 - abs(position) * (1 - sc)
                }
            }
        }
    }
}
