package com.example.custom_view.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by chenmingying on 2017/4/11.
 */

public class ProgressBarView extends View {
    private Paint mPaint;
    private Bitmap mBitmap;
    private Path mPath;
    private RectF mOval;

    private int mProgress = 0; //0-100;
    public ProgressBarView(Context context) {
        super(context);
    }

    public ProgressBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);//去锯齿
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(3);
        mPaint.setColor(Color.BLUE);

      /*  mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.haha);

        mPath = new Path();
        //初始化三角形路径
        int x1 = 100, y1 = 5;
        int x2 = 180, y2 = 180;
        int x3 = 5, y3 = 180;

        mPath.moveTo(x1, y1);//移动到第一点
        //链接第一个点和第二个点
        mPath.lineTo(x2, y2);
        mPath.lineTo(x3, y3);
        mPath.lineTo(x1, y1);*/

        mOval = new RectF(5, 5, 195, 165);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }


    /**
     * 不要在onDraw方法里面去创建对象，因为界面频繁刷新，反复的调用onDraw方法
     */
    @Override
    protected void onDraw(Canvas canvas) {

        int startAngle= -90;
//		int sweepAngle = 30;
        int sweepAngle = (int) ((mProgress / 100f) * 360);
        boolean useCenter = false;//是否画出扇形的边线
        canvas.drawArc(mOval, startAngle, sweepAngle, useCenter, mPaint);
    }

    /**
     * 更新进度
     * @param i
     */
    public void updateProgress(int i) {
        mProgress = i;
//		onDraw(canvas)
//		invalidate();//触发view的重新绘制 -》 onDraw, 不会触发测量 布局
        postInvalidate();//申请在主线程刷新ui -》invalidate -》 onDraw
    }
}
