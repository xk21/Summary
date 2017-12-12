
package com.example.custom_view.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.custom_view.R;

public class TextSummaryWithImgCheckBox extends LinearLayout implements OnClickListener {

    /* yulong begin, modify */
    /* modify for findbugs, sunguofeng, 2016.02.26 */
    private LinearLayout parent = null;
    /* yulong end */

    private TextView tv_title, tv_summary;
    // private ImageView arrow;
    private ImageView leftImg,switcher;
    private int layoutrightarrow;

    private OnImgCheckedBoxChangeListener mListener = null;
    private boolean mSwitchOn;

    public TextSummaryWithImgCheckBox(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        initView();
    }

    public TextSummaryWithImgCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        LayoutInflater.from(context).inflate(R.layout.security_textsummarywithimgcheckbox, this, true);
        tv_title = (TextView) findViewById(R.id.title);
        tv_summary = (TextView) findViewById(R.id.summary);
        // arrow = (ImageView)findViewById(R.id.arrow);
        leftImg=(ImageView) findViewById(R.id.left_img);
        switcher = (ImageView) findViewById(R.id.switcher);
        initView();
        /*
         * TypedArray a =
         * context.obtainStyledAttributes(attrs,R.styleable.TextSummaryWithImage
         * ); layoutrightarrow =
         * a.getInt(R.styleable.TextSummaryWithImage_layoutrightarrow, 14);
         */

    }

    public void initView() {
        if(mSwitchOn)
        {
            switcher.setImageResource(R.mipmap.security_widget_checkbox_on);
        }else{
            switcher.setImageResource(R.mipmap.security_widget_checkbox_off);
        }
        setOnClickListener(this);
    }

    public void setTitleTextView(int id) {
        tv_title.setText(id);
    }

    public void setTitleTextView(String text) {
        tv_title.setText(text);
    }

    public void setSummaryTextView(int id) {
        tv_summary.setText(id);
    }

    public void setSummaryTextView(String text) {
        tv_summary.setText(text);
    }

    // public void setImageResource(int resId){
    // arrow.setImageResource(resId);
    // }
    //
    
    public void setLeftImageResource(int resId)
    {
        leftImg.setImageResource(resId);
    }
    public void setClickListener(OnClickListener l) {
        /*yulong begin, modify for findbugs, xiaoming1_2016-03-02*/
        //parent.setOnClickListener(l);
        /*yuong end.*/
    }

    public void setSummaryInvisible() {
        tv_summary.setVisibility(View.GONE);
    }

    // public void setCursorInvisible(){
    // arrow.setVisibility(View.GONE);
    // }
    public void setLeftImageVisible()
    {
        leftImg.setVisibility(View.VISIBLE);
    }
    // public void setSwitchButtonInvisible(){
    // switcher.setVisibility(View.GONE);
    // }
    //
    // public YLSwitchButton getSwitchButtonObject(){
    // return switcher;
    // }

    public void setImageCheckBoxInvisible()
    {
        switcher.setVisibility(View.GONE);
    }

    public ImageView getCheckBoxObject() {
        return switcher;
    }

    public TextView getTitltTextViewObject() {
        return tv_title;
    }

    public TextView getSummaryTextViewObject() {
        return tv_summary;
    }

    public void setOnImgCheckedBoxChangeListener(OnImgCheckedBoxChangeListener listener) {
        mListener = listener;
    }

    public interface OnImgCheckedBoxChangeListener {
        public void onImgCheckedBoxChange(boolean state);
    }

    // public void setCheckedBox(ImageView img,boolean value)
    // {
    // if(mSwitchOn!=value)
    // {
    // if(mlistener != null)
    // {
    // mlistener.onCheckedBoxChangeListener(img,value);
    // }
    // }
    // }
    public boolean getCheckedBox() {
        return mSwitchOn;
    }

    public void setChecked(boolean value) {
        if (mSwitchOn != value) {
            if (mListener != null) {
                mListener.onImgCheckedBoxChange(value);
            }
        }
        mSwitchOn = value;
        if (mSwitchOn)
        {
            switcher.setImageResource(R.mipmap.security_widget_checkbox_on);
        } else {
            switcher.setImageResource(R.mipmap.security_widget_checkbox_off);
        }
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        mSwitchOn = !mSwitchOn;
        if (mListener != null) {
            mListener.onImgCheckedBoxChange(mSwitchOn);
        }
        if (mSwitchOn)
        {
            switcher.setImageResource(R.mipmap.security_widget_checkbox_on);
        } else {
            switcher.setImageResource(R.mipmap.security_widget_checkbox_off);
        }
    }

    public void setCheckedBoxType(boolean value)
    {
        mSwitchOn = value;
        if (mSwitchOn)
        {
            switcher.setImageResource(R.mipmap.security_widget_checkbox_on);
        } else {
            switcher.setImageResource(R.mipmap.security_widget_checkbox_off);
        }
    }
}
