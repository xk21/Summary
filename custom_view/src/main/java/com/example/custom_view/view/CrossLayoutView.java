package com.example.custom_view.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by chenmingying on 2017/4/11.
 */

public class CrossLayoutView extends ViewGroup {

    private boolean isStartLeft=false;

    public CrossLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //测量孩子
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //把孩子全部布局到左上角
        int top = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int left = 0;
            if (isStartLeft) {
                //奇数摆右边，偶数摆左边
                if (i % 2 == 0) {
                    left = 0;
                } else {
                    left = getMeasuredWidth() - child.getMeasuredWidth();
                }
            } else {
                //奇数摆左边，偶数摆右边
                if (i % 2 == 0) {
                    left = getMeasuredWidth() - child.getMeasuredWidth();
                } else {
                    left = 0;
                }
            }

            int right = left + child.getMeasuredWidth();
            int bottom = top + child.getMeasuredHeight();
            child.layout(left, top, right, bottom);
            //展开孩子
            top += child.getMeasuredHeight();
        }
    }
    public void revert() {
        //改变左右的状态
        isStartLeft = !isStartLeft;
        requestLayout();//请求重新布局--》onLayout 测量和布局 不会触发绘制
    }

}
