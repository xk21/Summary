package com.tencent.tmsecure.demo.deepclean;

import java.util.ArrayList;
import java.util.List;

import com.tencent.tmsecure.demo.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CustomSdcardRootsActivity extends Activity{
	
	public static class Parameters{
		public static final String SD_ROOTS = "sd_roots";
		public static final String WHITE_PATHS = "white_paths";
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.deepclean_custom_sdcard_roots);
		
		findViewById(R.id.add_new_sds).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutInflater inflater = getLayoutInflater();
				View item = inflater.inflate(R.layout.deepclean_custom_sdcard_roots_item, null);
				item.findViewById(R.id.delete).setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						((LinearLayout)findViewById(R.id.sds_container)).removeView((View) v.getParent());
					}
					
				});
				
				((LinearLayout)findViewById(R.id.sds_container)).addView(item);
			}
			
		});
		
		findViewById(R.id.add_sds).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ArrayList<String> sds = new ArrayList<String>();
				ArrayList<String> white = new ArrayList<String>();
				LinearLayout ll = ((LinearLayout)findViewById(R.id.sds_container));
				int itemCnt = ll.getChildCount();
				boolean isValid = true;
				for(int i=0; i<itemCnt; i++){
					String sdroot = ((EditText)ll.getChildAt(i).findViewById(R.id.sd_path)).getText().toString();					
					String wp = ((EditText)ll.getChildAt(i).findViewById(R.id.white_path)).getText().toString();
					if(sdroot.equals("")||sdroot.equals("")){
						Toast.makeText(CustomSdcardRootsActivity.this, "虚拟sd卡路径或其对应的白名单不可为空", Toast.LENGTH_SHORT).show();
						isValid = false;
					}
					if(sds.contains(sdroot)){
						Toast.makeText(CustomSdcardRootsActivity.this, "不可有重复的虚拟sd卡路径", Toast.LENGTH_SHORT).show();
						isValid = false;
					}
					else{
						sds.add(sdroot);
					}
					if(!white.contains(wp)){
						white.add(wp);
					}				
				
				}
				if(!isValid)
					return;
				Intent intent = new Intent();
				intent.putStringArrayListExtra(Parameters.SD_ROOTS, sds);
				intent.putStringArrayListExtra(Parameters.WHITE_PATHS, white);
				//intent.putExtra(Parameters.SD_ROOTS, sds.toArray());
				//intent.putExtra(Parameters.WHITE_PATHS, white.toArray());
				setResult(RESULT_OK, intent);
				finish();
			}
			
		});
	}
}
