package com.nonzeroapps.whatisnewdialog.util;

import android.view.View;
import androidx.viewpager.widget.ViewPager;

public class ParallaxPagerTransformer implements ViewPager.PageTransformer {

    private int id;
    private float speed = 0.5f;

    public ParallaxPagerTransformer(int id) {
        this.id = id;
    }

    @Override
    public void transformPage(View view, float position) {
        View parallaxView = view.findViewById(id);

        if (parallaxView != null) {
            if (position > -1 && position < 1) {
                float width = parallaxView.getWidth();
                parallaxView.setTranslationX(-(position * width * speed));
            }
        }
    }
}
