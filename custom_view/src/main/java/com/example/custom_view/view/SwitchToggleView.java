package com.example.custom_view.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.custom_view.R;

/**
 * Created by chenmingying on 2017/4/11.
 */

public class SwitchToggleView extends View {

    private  Paint mPaint;
    private Bitmap mSwitchBackground;
    private Bitmap mSwitchButton;
    private boolean isClose;
    private OnSwitchChangeListener mOnSwitchChangeListener;

    public SwitchToggleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mSwitchBackground = BitmapFactory.decodeResource(getResources(), R.mipmap.switch_background);
        mSwitchButton = BitmapFactory.decodeResource(getResources(), R.mipmap.switch_button);
        setOnClickListener(new OnClickListener() {



            @Override
            public void onClick(View v) {
                //改变开关的状态
                isClose = !isClose;
                //刷新ui
                invalidate();//--》onDraw

                if (mOnSwitchChangeListener != null) {
                    mOnSwitchChangeListener.onSwitch(isClose);
                }
            }
        });
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //设置成背景图片的宽高
        int measuredWidth = mSwitchBackground.getWidth();
        int measuredHeight = mSwitchBackground.getHeight();
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画背景图片
        canvas.drawBitmap(mSwitchBackground, 0, 0, mPaint);
        //画滑块
        if (isClose) {
            canvas.drawBitmap(mSwitchButton, 0, 0, mPaint);
        } else {
            //滑块开
            int left = mSwitchBackground.getWidth() - mSwitchButton.getWidth();
            canvas.drawBitmap(mSwitchButton, left, 0, mPaint);
        }
    }


    public interface OnSwitchChangeListener {
        //什么事件声明什么方法
        void onSwitch(boolean isClose);
    }

    public void setOnSwitchChangeListener(OnSwitchChangeListener l) {
        mOnSwitchChangeListener = l;
    }
}
