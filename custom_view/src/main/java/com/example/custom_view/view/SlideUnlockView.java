package com.example.custom_view.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import com.example.custom_view.R;

/**
 * Created by chenmingying on 2017/4/13.
 */

public class SlideUnlockView extends View {
    private Bitmap mSwitchButton;
    private Paint mPaint;
    private float mMax;

    private Scroller mScroller;//实现平滑滚动的工具类
    private OnUnlockListener mOnUnlockListener;
    public SlideUnlockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mSwitchButton = BitmapFactory.decodeResource(getResources(), R.mipmap.switch_button);
        mScroller = new Scroller(context);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //宽度可以是用期望
        //高度设置滑块图片的高度
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measuredHeight = mSwitchButton.getHeight();
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mSwitchButton, 0, 0, mPaint);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //2. 点击滑块外部，不做处理
                if (event.getX() > mSwitchButton.getWidth()) {
                    return false;
                }

                //1. 点击滑块，滑块中线会滚动鼠标下面
                float x1 = event.getX() - mSwitchButton.getWidth() / 2;
                //滚动偏移量
                float mScrollX1 = - x1;
                //如果滚动偏移量是正数，会向左边滚出界
                if (mScrollX1 > 0) {
                    mScrollX1 = 0;
                }
                scrollTo((int) mScrollX1, 0);
                break;

            case MotionEvent.ACTION_MOVE:
                float x2 = event.getX() - mSwitchButton.getWidth() / 2;

                //x2的取值范围是0到max
                if (x2 < 0) {
                    x2 = 0;
                } else if (x2 > mMax) {
                    x2 = mMax;
                }

                float mScrollX2 = -x2;//滚动偏移量
                scrollTo((int) mScrollX2, 0);
                break;
            case MotionEvent.ACTION_UP:
                //4. 松开滑块时，如果滑块没有滚动到最右边，则会滚到最左边，如果滑块滚动到最右边，滑动解锁
                float x3 = event.getX() - mSwitchButton.getWidth() / 2;
                if (x3 < mMax) {
                    //滚到左边
//				scrollTo(0, 0);
                    int startX = getScrollX();//开始滚动时滚动的偏移量
                    int startY = 0;
                    int endX = 0;//最终位置的滚动偏移量
                    int dx = endX - startX;//滚动偏移量的变化量
                    int dy = 0;
                    int duration = 1000;
                    mScroller.startScroll(startX, startY, dx, dy, duration);
                    //触发重新绘制
                    invalidate();
                } else {
                    //解锁
                    if (mOnUnlockListener != null) {
                        mOnUnlockListener.onUnlock();
                    }
                }
                break;
        }
        return true;
    }

    /**
     * 布局完成之后的调用
     * layout->setFrame->sizeChange->onSizeChange
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mMax = w - mSwitchButton.getWidth();
    }

    /**
     * 计算当前的滚动偏移量
     */
    @Override
    public void computeScroll() {
        //computeScrollOffset根据已经过去的事件，来计算当前应该在滚动偏移量mCurrX
        //如果computeScrollOffset返回true，表示滚动还没有结束
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            invalidate();//触发重新绘制
        }
    }

    public interface OnUnlockListener {
        void onUnlock();
    }

    public void setOnUnlockListener(OnUnlockListener l) {
        mOnUnlockListener = l;
    }

}
