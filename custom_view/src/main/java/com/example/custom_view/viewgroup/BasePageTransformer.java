package com.example.custom_view.viewgroup;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by chenmingying on 2017/4/19.
 */

public abstract class BasePageTransformer implements ViewPager.PageTransformer {
    protected ViewPager.PageTransformer mPageTransformer = NonPageTransformer.INSTANCE;
    public static final float DEFAULT_CENTER = 0.5f;
    @Override
    public void transformPage(View page, float position) {
        if (null != mPageTransformer){
            mPageTransformer.transformPage(page,position);
        }
        pageTransform(page,position);
    }

    protected abstract void pageTransform(View page, float position);


}
