package com.tencent.tmsecure.demo.ivvi;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.tencent.tmsecure.demo.R;


public class SpaceClenActivity extends Activity  {
	
	private LinearLayout mPathsLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_space_clen_activity);
		SpaceClenView viewById = (SpaceClenView) findViewById(R.id.SpaceClenView1);
		viewById.setRightText("asdfsadfs");
		viewById.setLeftTitleVisibility(View.GONE);
		
	}
 
}
