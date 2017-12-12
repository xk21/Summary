/**
 * 
 */
package com.tencent.tmsecure.demo.intelli_sms;

import com.tencent.tmsecure.demo.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import tmsdk.common.creator.ManagerCreatorC;
import tmsdk.common.module.intelli_sms.IntelliSmsManager;

/**
 * @author jiangweiti
 *
 */
public class IntelliSmsActivity extends Activity implements OnClickListener {
	IntelliSmsManager mIntelliSmsManager;
	private TextView mContextShower;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intelli_sms_activity);
		mContextShower = (TextView) findViewById(R.id.content_shower);
		findViewById(R.id.btn1).setOnClickListener(this);
		findViewById(R.id.btn2).setOnClickListener(this);
		
		mContextShower.setVisibility(View.INVISIBLE);
		//获取manager
		mIntelliSmsManager = ManagerCreatorC.getManager(IntelliSmsManager.class);
		
		//必须初始化,与destroy（）一一对应
		mIntelliSmsManager.init();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mIntelliSmsManager.destroy();//必须销毁
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.btn1://智能短信检测
		{
			SmsCheckTest.testSms(mIntelliSmsManager);
			break;
		}
		case R.id.btn2://支付短信检测
		{
			SmsCheckTest.testPaySms(mIntelliSmsManager);
			break;
		}
		default:
		}
	}
}
