package com.tencent.tmsecure.demo.ivvi;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.tmsecure.demo.R;


public class SpaceClenView extends LinearLayout {
    protected static final String TAG = "CleanAccelerate";
    public ImageView leftImage, rightImage,middleImage;
    public TextView titleText, abstractText, rightText;
    private int mLeftTitleColor,mAbstractTextColor,mRightTextColor;
    private int mLeftImgeIcon,mMiddleImgeIcon,mRightImgeIcon,mRightTextSize;
    private String mLeftTitleText,mAbstractText,mRightText;
    /**  */
    
    public SpaceClenView(Context context) {
        this(context,null);
    }
    
    public SpaceClenView(Context context, AttributeSet attrs) {
       super(context,attrs);
        LayoutInflater.from(context).inflate(R.layout.lite_space__clen_view, this, true);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SpaceClenView);
        Log.i("tagg","typedArray:"+typedArray);
        mLeftImgeIcon =typedArray.getResourceId(R.styleable.SpaceClenView_leftImgeIcon,0);
        mMiddleImgeIcon = typedArray.getResourceId(R.styleable.SpaceClenView_middleImgeIcon,0);
    
        mLeftTitleText =typedArray.getString(R.styleable.SpaceClenView_leftTitleText);
        mLeftTitleColor = typedArray.getColor(R.styleable.SpaceClenView_leftTitleColor, Color.BLACK);
    
        mAbstractText=typedArray.getString(R.styleable.SpaceClenView_abstractText);
        mAbstractTextColor =typedArray.getColor(R.styleable.SpaceClenView_abstractTextColor,Color.CYAN);
    
        mRightText = typedArray.getString(R.styleable.SpaceClenView_rightText);
        mRightTextSize = (int) typedArray.getDimension(R.styleable.SpaceClenView_rightTextSize,14);
        mRightTextColor = typedArray.getColor(R.styleable.SpaceClenView_rightTextColor,Color.CYAN);
    
        mRightImgeIcon=typedArray.getResourceId(R.styleable.SpaceClenView_rightImgeIcon,0);
    
        //initView(context);
        
        middleImage = (ImageView)
                findViewById(R.id.clen_middle_image);
        leftImage = (ImageView)
                findViewById(R.id.clen_lift_image);
        rightImage = (ImageView)
                findViewById(R.id.clen_right_image);
        titleText = (TextView) findViewById(R.id.clen_left_title);
        abstractText = (TextView)
                findViewById(R.id.clen_abstract_text);
        rightText = (TextView)
                findViewById(R.id.clen_right_text);
        
        middleImage.setImageResource(mMiddleImgeIcon);
        leftImage.setImageResource(mLeftImgeIcon);
        rightImage.setImageResource(mRightImgeIcon);
        titleText.setText(mLeftTitleText);
        titleText.setTextColor(mLeftTitleColor);
        abstractText.setText(mAbstractText);
        Log.i("tagg","mAbstractText:"+mAbstractText);
        Log.i("tagg","mMiddleImgeIcon:"+mMiddleImgeIcon);
        abstractText.setTextColor(mAbstractTextColor);
        rightText.setText(mRightText);
        rightText.setTextColor(mRightTextColor);
        rightText.setTextSize(mRightTextSize);
        typedArray.recycle();
    }
    
    public void setLeftTitleVisibility(int visbility) {
        titleText.setVisibility(visbility);
    }
    public void setLeftTitleText(String text) {
        titleText.setText(text);
    }
    public void setLeftTitleColor(int color) {
        titleText.setTextColor(mLeftTitleColor);
    }
    public void setAbstractVisibility(int visbility) {
        abstractText.setVisibility(visbility);
    }
    public void setAbstractText(String text) {
        abstractText.setText(text);
    }
    public void setAbstractTextColor(int color) {
        abstractText.setTextColor(mAbstractTextColor);
    }
    
    public void setRightText(String text) {
        rightText.setText(text);
    }
    public void setRightTextColor(int color) {
        rightText.setTextColor(color);
    }
    public void setRightTextSize(int size) {
        abstractText.setTextSize(size);
    }
   

    

  

    
   
   
            

   
}
