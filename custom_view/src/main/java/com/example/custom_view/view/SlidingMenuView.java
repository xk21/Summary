package com.example.custom_view.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Scroller;

import com.example.custom_view.R;

/**
 * Created by chenmingying on 2017/4/13.
 */

public class SlidingMenuView extends ViewGroup {
    private View mRightChild;
    private View mLeftChild;
    private float mDownX;

    private Scroller mScroller;

    private ImageView mBack;

    private boolean isOpen = false;
    private float mDownY;

    public SlidingMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
    }


    @Override
    public void onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach();
    }

    /**
     * 当所有的孩子都解析完成之后， 再去findviewbyid
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mBack = (ImageView) findViewById(R.id.back);
        mBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                toggle();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //测量孩子
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 布局左边孩子
        mLeftChild = getChildAt(0);
        mLeftChild.layout(-mLeftChild.getMeasuredWidth(), 0, 0, mLeftChild.getMeasuredHeight());
        //布局右边孩子
        mRightChild = getChildAt(1);
        mRightChild.layout(0, 0, mRightChild.getMeasuredWidth(), mRightChild.getMeasuredHeight());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                //2. 拖动右边内容区域，拉出菜单列表
                float moveX = event.getX();
                float diffX = mDownX - moveX;//滚动偏移量
                //滚动偏移量的范围0到负左边孩子的宽度
                //拿到将要滚动到的位置的滚动偏移量,如果发生越界，就要做处理
                //getScrollX()获取当前所在位置的滚动偏移量mScrollX
                float finalScrollX = getScrollX() + diffX;//最终要滚动到位置的滚动偏移量
                if (finalScrollX > 0) {
                    scrollTo(0, 0);//如果滚动偏移量大于最大值，就滚动到偏移量最大值的地方
                    return true;

                } else if (finalScrollX < -mLeftChild.getMeasuredWidth()) {
                    //如果滚动偏移量小于最小值，就滚动到偏移量最小值的地方
                    scrollTo(-mLeftChild.getMeasuredWidth(), 0);
                    return true;
                }

                scrollBy((int) diffX, 0);

                //更新down的位置
                mDownX = moveX;
                break;
            case MotionEvent.ACTION_UP:
                //中线临界点滚动偏移量
                int threshold = - mLeftChild.getMeasuredWidth() / 2;
                //如果当前的滚动偏移量小于threshold
                if (getScrollX() < threshold) {
//				scrollTo(-mLeftChild.getMeasuredWidth(), 0);

/*				int startX = getScrollX();
				int startY = 0;
				int endX = -mLeftChild.getMeasuredWidth();
				int dx = endX - startX;
				int dy = 0;
				int duration = 500;
				mScroller.startScroll(startX, startY, dx, dy, duration);
				//触发重新绘制
				invalidate();*/
//				smoothScrollTo(-mLeftChild.getMeasuredWidth());
                    open();

                } else {
//				scrollTo(0, 0);

                    close();
                }
                break;
        }
        return true;//消费事件
    }

    /**
     * 关闭侧滑菜单
     */
    private void close() {
        smoothScrollTo(0);
        isOpen = false;
    }

    /**
     * 打开侧滑菜单
     */
    public void open() {
        smoothScrollTo(-mLeftChild.getMeasuredWidth());
        isOpen = true;
    }

    /**
     * 打开或者关闭侧滑菜单
     */
    public void toggle() {
        if (isOpen) {
            close();
        } else {
            open();
        }
    }

    private void smoothScrollTo(int endX) {
        int startY = 0;
        int startX = getScrollX();
        int dx = endX - startX;
        int dy = 0;
        int duration = 500;
        mScroller.startScroll(startX, startY, dx, dy, duration);
        //触发重新绘制
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            invalidate();
        }
    }


    /**
     * 有条件的去拦截事件
     * 只拦截左右滑动的事件
     * 判断x轴的变化量与y轴上的变化量 dx > dy，表示左右滑动
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getY();

                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = ev.getX();
                float moveY = ev.getY();
                float dx = Math.abs(moveX - mDownX);
                float dy = Math.abs(moveY - mDownY);
                if (dx > dy) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
