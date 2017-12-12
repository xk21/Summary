package com.tencent.tmsecure.demo.optimus;

import com.tencent.tmsecure.demo.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import tmsdk.common.creator.ManagerCreatorC;
import tmsdk.common.SmsEntity;
//import tmsdk.common.module.optimus.BsFakeType;
import tmsdk.common.module.optimus.BsFakeType;
import tmsdk.common.module.optimus.IFakeBaseStationListener;
import tmsdk.common.module.optimus.OptimusManager;
import tmsdk.common.module.optimus.SMSCheckerResult;

public class OptimusActivity extends Activity implements OnClickListener, IFakeBaseStationListener {
	OptimusManager mOptimusManager;
	TextView mTextView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.optimus_activity);
		
		findViewById(R.id.btn1).setOnClickListener(this);
		findViewById(R.id.btn2).setOnClickListener(this);
		
		mTextView = (TextView) findViewById(R.id.content_shower);
		mTextView.setText("请选择按钮测试");
		mOptimusManager = ManagerCreatorC.getManager(OptimusManager.class);
		boolean isOK = mOptimusManager.start();
		if(!isOK) {
			this.finish();//初始化失败，退出
		}

		//需要在OptimusManager的start方法之后调用
		mOptimusManager.setFakeBsListener(this);
		
//		OptimusTest.getInstance(this).monitorBS_start();//test
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mOptimusManager.stop();
		mOptimusManager = null;
//		OptimusTest.getInstance(this).monitorBS_stop();//test
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.btn1:
			checkSms();
			break;
		case R.id.btn2:
			getFakeBSTime();
			break;
		}
	}
	
	
	private void checkSms() {
		/**
		 * 检测是否伪基站短信
		 * 因为没有内建线程，支持云查时，联网操作可能会比较慢，所以请在非UI主线程中调用。
		 * 只有非2G数据网络才支持云查。
		 */
		new Thread() {

			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				SmsEntity sms  = new SmsEntity();
				sms.phonenum = "10086";
				sms.body = "尊敬的客户：您的话费积分已符合兑换498.00元现金条件，请用手机点击登陆 www.10086swe.com 根据提示激活领取【中国移动】";//当前版本跟短消息关系比较小，主要还是基站信息
				sb.append("sms num:");
				sb.append(sms.phonenum);
				sb.append("\n  body:");
				sb.append(sms.body);
				Log.v("demo", "mOptimusManager.checkSms(sms, false)");
				SMSCheckerResult result = mOptimusManager.checkSms(sms, false);
				sb.append("\n  mOptimusManager.checkSms(sms, false)");
				displayResult(result, sb);
				Log.v("demo", "mOptimusManager.checkSms(sms, true)");
				result = mOptimusManager.checkSms(sms, true);
				sb.append("\n  mOptimusManager.checkSms(sms, true)");
				displayResult(result, sb);
				Log.v("demo", sb.toString());
				myHandler.obtainMessage(102, sb.toString()).sendToTarget();
			}
			
		}.start();
	}
	
	private void displayResult(SMSCheckerResult result, StringBuffer sb) {
		if(result == null) {
			Log.v("demo", "SMSCheckerResult is null");
			sb.append("\n  SMSCheckerResult is null");
			return;
		}
		
		sb.append("\n  isCoundCheckresult ="+result.isCloudCheck);
		sb.append("result.mType = "+result.mType.toString());
//		switch(result.mType) {
//		case SAFE:
//		case UNKNOW://UNKNOW 等同SAFE
//			break;
//		case FAKE:
//		break;
//		case RIST:
//		break;
//		}
	}
	
	private void getFakeBSTime() {
		mTextView.setText("\n   getFakeBSLastTime = " + mOptimusManager.getFakeBSLastTime());
	}

	private Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 102) {
				mTextView.setText((String)msg.obj);
			}
			super.handleMessage(msg);
		}
		
	};


	@Override
	public void onFakeNotify(BsFakeType bsFakeType) {
		//bsFakeType应为BsFakeType.FAKE
	}
}
