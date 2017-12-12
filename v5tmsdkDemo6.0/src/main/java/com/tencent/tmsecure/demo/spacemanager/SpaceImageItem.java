package com.tencent.tmsecure.demo.spacemanager;

import com.tencent.tmsecure.demo.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import tmsdk.fg.module.spacemanager.PhotoSimilarResult.PhotoSimilarBucketItem;

public class SpaceImageItem extends RelativeLayout {
	
	public ImageView mImageView;
	public CheckBox mCheckBox;
	public PhotoSimilarBucketItem mData;

	public SpaceImageItem(Context context,final PhotoSimilarBucketItem data) {
		super(context);
		// TODO Auto-generated constructor stub
		LayoutInflater.from(context).inflate(R.layout.space_image_item, this, true);
		mImageView = (ImageView) findViewById(R.id.space_image_imageview);
		mCheckBox = (CheckBox)findViewById(R.id.space_image_checkbox);
		mImageView.setImageResource(R.drawable.ic_launcher);
		mData = data;
		mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					mImageView.setAlpha(0.5f);
					mData.mSelected=true;
				}else{
					mImageView.setAlpha(1.0f);
					mData.mSelected=false;
				}
			}
		});
	}

	public SpaceImageItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	

}
