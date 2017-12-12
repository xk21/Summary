package com.example.custom_view.viewgroup;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by chenmingying on 2017/4/19.
 */

public class RotateDownPageTransformer extends BasePageTransformer {
    private static final float DEFAULT_MAX_ROTATE = 20.0f;
    private float mMaxRotate = DEFAULT_MAX_ROTATE;

    public RotateDownPageTransformer() {
    }

    public RotateDownPageTransformer(float maxRotate) {
        this(maxRotate, NonPageTransformer.INSTANCE);
    }

    public RotateDownPageTransformer(ViewPager.PageTransformer pageTransformer) {
        this(DEFAULT_MAX_ROTATE, pageTransformer);
    }

    public RotateDownPageTransformer(float maxRotate, ViewPager.PageTransformer pageTransformer) {
        mPageTransformer = pageTransformer;
        mMaxRotate = maxRotate;
    }

    @Override
    protected void pageTransform(View page, float position) {
        if (position < -1) {
            page.setRotation(mMaxRotate * -1);
            page.setPivotX(page.getWidth());
            page.setPivotY(page.getHeight());
        } else if (position <= 1) {
            if (position<0){
                page.setPivotX(page.getWidth()*(DEFAULT_CENTER+DEFAULT_CENTER*(-position)));
                page.setPivotY(page.getHeight());
                page.setRotation(mMaxRotate *position);
            }else {
                page.setPivotX(page.getWidth()*DEFAULT_CENTER*(1-position));
                page.setPivotY(page.getHeight());
                page.setRotation(mMaxRotate *position);
            }

        } else {
            page.setRotation(mMaxRotate * 1);
            page.setPivotX(0);
            page.setPivotY(page.getHeight());
        }
    }
}
