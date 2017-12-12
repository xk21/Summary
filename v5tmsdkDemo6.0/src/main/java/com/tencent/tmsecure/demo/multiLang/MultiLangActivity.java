package com.tencent.tmsecure.demo.multiLang;

import tmsdk.common.creator.ManagerCreatorC;
import tmsdk.common.module.lang.ILangDef;
import tmsdk.common.module.lang.MultiLangManager;

import com.tencent.tmsecure.demo.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MultiLangActivity extends Activity{

	private MultiLangManager mMultiLangManager;
	private TextView mCurrentLang;
	private Button 	 mBtCHS, mBtENG;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.multilang_activity);
		mMultiLangManager = ManagerCreatorC.getManager(MultiLangManager.class);
		mCurrentLang = (TextView) findViewById(R.id.current_language);
		mBtCHS = (Button) findViewById(R.id.bt_chs);
		mBtENG = (Button) findViewById(R.id.bt_eng);
		showLang();
		mBtCHS.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mMultiLangManager.onCurrentLangNotify(ILangDef.ELANG_CHS);
				showLang();
			}
		});
		mBtENG.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mMultiLangManager.onCurrentLangNotify(ILangDef.ELANG_ENG);
				showLang();
			}
		});
		
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
	}
	
	void showLang(){
		if(mMultiLangManager.getCurrentLang() == ILangDef.ELANG_CHS){
			mCurrentLang.setText("中文");
		}else if(mMultiLangManager.getCurrentLang() == ILangDef.ELANG_ENG){
			mCurrentLang.setText("英文");
		}
	}
}
