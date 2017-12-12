package com.tencent.tmsecure.demo.ivvi;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.tmsecure.demo.R;

public class LevelMenuItem extends LinearLayout {
    private TextView mTextView = null;
    private ImageView mImageView = null;
    
    public LevelMenuItem(Context context) {
        super(context);
    }
    public LevelMenuItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        LayoutInflater layoutInflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.level_menu_item, this);
        
        TypedArray typedArray = context.obtainStyledAttributes(attrs
                ,R.styleable.LevelMenuItem);
        
        initWidget(typedArray);
    }
    private void initWidget(TypedArray typedArray)
    {
        mTextView = (TextView)findViewById(R.id.tv_item);
        String textString = typedArray.getString(R.styleable.LevelMenuItem_text);
        Log.i("tagg", "textString: "+textString);
        int textColor = typedArray.getColor(R.styleable.LevelMenuItem_text_color,
                0xffffffff);
        float textSize = typedArray.getDimension(R.styleable.LevelMenuItem_text_size,
                20);
        mTextView.setText(textString);
        mTextView.setTextColor(textColor);
        mTextView.setTextSize(textSize);
        
        mImageView = (ImageView)findViewById(R.id.image_item);
        int imageHeight = (int) typedArray.getDimension(R.styleable.LevelMenuItem_image_height, 25);
        int imageWidth = (int) typedArray.getDimension(R.styleable.LevelMenuItem_image_width, 25);
        int imageSrc = typedArray.getResourceId(R.styleable.LevelMenuItem_image_src, 0);
        int imageBg = typedArray.getResourceId(R.styleable.LevelMenuItem_image_bg, 0);
        int imageAlpha = typedArray.getInt(R.styleable.LevelMenuItem_image_alpha, 255);
        mImageView.setAlpha(imageAlpha);
        mImageView.setImageResource(imageSrc);
        mImageView.setBackgroundResource(imageBg);
        LayoutParams layoutParams = new LayoutParams(imageWidth, imageHeight);
        mImageView.setLayoutParams(layoutParams);
        
        typedArray.recycle();
    }
    /**
     * 设置此控件的文本
     * @param text
     */
    public void setText(String text)
    {
        mTextView.setText(text);
    }
    /**
     * 设置文字颜色
     * @param textColor
     */
    public void setTextColor(int textColor)
    {
        mTextView.setTextColor(textColor);
    }
    /**
     * 设置字体大小
     * @param textSize
     */
    public void setTextSize(int textSize)
    {
        mTextView.setTextSize(textSize);
    }
    /**
     * 设置图片
     * @param resId
     */
    public void setImageResource(int resId)
    {
        mImageView.setImageResource(resId);
    }
    /**
     * 设置图片背景
     */
    public void setBackgroundResource(int resId)
    {
        mImageView.setBackgroundResource(resId);
    }
    /**
     * 设置图片的不透名度
     * @param alpha
     */
    public void setImageAlpha(int alpha)
    {
        mImageView.setAlpha(alpha);
    }
    /**
     * 设置图片的大小
     * 这里面需要使用LayoutParams这个布局参数来设置
     * @param width
     * @param height
     */
    public void setImageSize(int width,int height)
    {
        LayoutParams layoutParams = new LayoutParams(width, height);
        mImageView.setLayoutParams(layoutParams);
    }
    /**
     * image点击事件的回调
     * @param listener
     */
   /* public void setOnClickListener(OnItemClickListener listener)
    {
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onImageClick();
            }
        });
    }*/
    /**
     * 点击事件接口
     * @author linc
     *
     */
    public interface OnItemClickListener
    {
        public void onImageClick();
    }
}


