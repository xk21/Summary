package com.tencent.tmsecure.demo.aresengine;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.tmsecure.demo.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import tmsdk.bg.creator.ManagerCreatorB;
import tmsdk.bg.module.aresengine.AresEngineManager;
import tmsdk.bg.module.aresengine.DataFilter;
import tmsdk.bg.module.aresengine.DataHandler;
import tmsdk.bg.module.aresengine.DataInterceptor;
import tmsdk.bg.module.aresengine.DataInterceptorBuilder;
import tmsdk.bg.module.aresengine.ISmsReportCallBack;
import tmsdk.bg.module.aresengine.IncomingCallFilter;
import tmsdk.bg.module.aresengine.IncomingSmsFilter;
import tmsdk.common.ErrorCode;
import tmsdk.common.module.aresengine.FilterConfig;
import tmsdk.common.module.aresengine.SmsEntity;
import tmsdk.common.module.aresengine.TelephonyEntity;
import tmsdk.common.module.intelli_sms.IntelliSmsCheckResult;

/**
 * 说明：实际项目中以下数据应该是由用户自定义的，为了测试设置了一些默认的数据 黑名单：135号码段，020和1065开始的号码作为demo中的黑名单
 * 白名单：15931256489号码作为demo中的白名单 关键字：“花费”作为demo中的关键字
 * 私密：“15912351868”号码作为demo中的私密联系人号码
 *
 * @author boyliang
 *
 */
public class FilterActivity extends Activity implements OnClickListener {
	private AresEngineManager mAresEnginManager;
	private TextView mContextShower;
	private ArrayList<String> mLines = new ArrayList<String>();
	private DataHandlerCallback mIncomingcall;
	private DataHandlerCallback mSyscalllog;
	private DataHandlerCallback mMTSMSCallBack;
	private int mReportCount = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filter_activity);
		mContextShower = (TextView) findViewById(R.id.content_shower);
		findViewById(R.id.btn1).setOnClickListener(this);
		findViewById(R.id.btn2).setOnClickListener(this);
//		findViewById(R.id.btn3).setOnClickListener(this);
//		findViewById(R.id.btn4).setOnClickListener(this);
		findViewById(R.id.btn3).setVisibility(View.INVISIBLE);
		findViewById(R.id.btn4).setVisibility(View.INVISIBLE);
		mReportCount = 0;
		// 设置配置因子
		mAresEnginManager = ManagerCreatorB.getManager(AresEngineManager.class);
		mAresEnginManager.setAresEngineFactor(new DemoAresEngineFactor(getApplicationContext()));
		
		// //////////////////////////来去电拦截使用示例//////////////////////////////////////////////
		DataInterceptorBuilder<?> builder = null;
		// 每一种类型的拦截器只能调用addIntercepter方法添加一次，之后添加的不会生效并且会导致程序异常。
		// 实际使用过程中推荐在Application启动时添加拦截器， 这里捕获异常只是为了方便demo的可测试性
		try {
			// 添加来电拦截器
			builder = DataInterceptorBuilder.createInComingCallInterceptorBuilder();
			mAresEnginManager.addInterceptor(builder);
			
			// 添加通话记录拦截器
			builder = DataInterceptorBuilder.createSystemCallLogInterceptorBuilder();
			mAresEnginManager.addInterceptor(builder);
			
			// 添加来短信拦截器
			builder = DataInterceptorBuilder.createInComingSmsInterceptorBuilder();
			mAresEnginManager.addInterceptor(builder);
			
			// 配置拦截模式，如只“白名单和系统联系人通过”
			DataInterceptor<?> intercepter = mAresEnginManager.findInterceptor(DataInterceptorBuilder.TYPE_INCOMING_CALL);
			DataFilter<?> filter = intercepter.dataFilter();
			FilterConfig config = new FilterConfig();
			config.set(IncomingCallFilter.PRIVATE_CALL, FilterConfig.STATE_DISABLE);
			config.set(IncomingCallFilter.BLACK_LIST, FilterConfig.STATE_DISABLE);
			config.set(IncomingCallFilter.WHITE_LIST, FilterConfig.STATE_ACCEPTABLE);
			config.set(IncomingCallFilter.SYS_CONTACT, FilterConfig.STATE_ACCEPTABLE);
			// ...
			filter.setConfig(config);
			//配置来短信拦截器
			DataInterceptor<?> intercepter1 = mAresEnginManager.findInterceptor(DataInterceptorBuilder.TYPE_INCOMING_SMS);
			DataFilter<?> filter1 = intercepter1.dataFilter();
			FilterConfig config1 = new FilterConfig();
			config1.set(IncomingSmsFilter.SPECIAL_SMS, 		FilterConfig.STATE_DISABLE);
			config1.set(IncomingSmsFilter.REMOVE_PRIVATE_SMS, 	FilterConfig.STATE_DISABLE);
			config1.set(IncomingSmsFilter.WHITE_LIST, 			FilterConfig.STATE_DISABLE);
			config1.set(IncomingSmsFilter.BLACK_LIST, 			FilterConfig.STATE_REJECTABLE);
			config1.set(IncomingSmsFilter.SYS_CONTACT, 		FilterConfig.STATE_DISABLE);
			config1.set(IncomingSmsFilter.LAST_CALLS, 			FilterConfig.STATE_DISABLE);
			config1.set(IncomingSmsFilter.KEY_WORKDS, 			FilterConfig.STATE_DISABLE);
			config1.set(IncomingSmsFilter.INTELLIGENT_CHECKING,FilterConfig.STATE_DISABLE);
			config1.set(IncomingSmsFilter.STRANGER_SMS, 		FilterConfig.STATE_REJECTABLE);
			filter1.setConfig(config1);
			
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		
		// 回调来电功能对象
		mIncomingcall = new DataHandlerCallback("来电");
		registerCallback(DataInterceptorBuilder.TYPE_INCOMING_CALL, mIncomingcall);
		
		// 回调通话记录功能对象
		mSyscalllog = new DataHandlerCallback("通话记录");
		registerCallback(DataInterceptorBuilder.TYPE_SYSTEM_CALL, mSyscalllog);
		
		//来短信
		mMTSMSCallBack = new DataHandlerCallback("来短信");
		registerCallback(DataInterceptorBuilder.TYPE_INCOMING_SMS, mMTSMSCallBack);
		
	}
	
	// 添加回调需要使用的对象
	private void registerCallback(String type, DataHandler.DataHandlerCallback callback) {
		mAresEnginManager.findInterceptor(type).dataHandler().addCallback(callback);
	}
	
	// 注销被回调的功能对象
	private void unregisterCallback(String type, DataHandler.DataHandlerCallback callback) {
		mAresEnginManager.findInterceptor(type).dataHandler().removeCallback(callback);
	}
	
	@Override
	protected void onDestroy() {
		
		// 该功能模块程序退出时注销被回调功能对象
		unregisterCallback(DataInterceptorBuilder.TYPE_INCOMING_CALL, mIncomingcall);
		unregisterCallback(DataInterceptorBuilder.TYPE_INCOMING_SMS, mMTSMSCallBack);
		unregisterCallback(DataInterceptorBuilder.TYPE_SYSTEM_CALL, mSyscalllog);
		super.onDestroy();
	}
	
	private static String msgFilterState(boolean isSms, int filter, int state, Object[] args) {
		
		// 判断是否为短信拦截
		if (isSms) {
			final String[] FILTER_NAME = { "private", "white list", "black list", "system contacts", "recent call log", "keywords", "intelli engine", "stranger sms" };
			int indexOfFilterName = Integer.numberOfTrailingZeros(filter);
			
			//判断拦截内容
			switch (filter) {
				case IncomingSmsFilter.REMOVE_PRIVATE_SMS:
					//判断拦截器是否启动
					if (state == FilterConfig.STATE_ENABLE)
						return "MOVED to private";
					break;
				
				case IncomingSmsFilter.WHITE_LIST:
				case IncomingSmsFilter.BLACK_LIST:
				case IncomingSmsFilter.SYS_CONTACT:
				case IncomingSmsFilter.LAST_CALLS:
				case IncomingSmsFilter.KEY_WORKDS:
				case IncomingSmsFilter.STRANGER_SMS:
					
					//判断权限是否为可通过
					if (state == FilterConfig.STATE_ACCEPTABLE)
						return "PASSED by " + FILTER_NAME[indexOfFilterName];
					else if (state == FilterConfig.STATE_REJECTABLE)
						return "BLOCKED by " + FILTER_NAME[indexOfFilterName];
					break;
				
				case IncomingSmsFilter.INTELLIGENT_CHECKING:
					//判断拦截器是否启动
					if (state == FilterConfig.STATE_ENABLE) {
						IntelliSmsCheckResult checkResult = (IntelliSmsCheckResult) (args[1]);
						
						// 判断处理建议，suggestion 取值范围｛-1，1，2，3，4｝
						switch (checkResult.suggestion) {
							case IntelliSmsCheckResult.SUGGESTION_PASS:
								return "PASSED by " + FILTER_NAME[indexOfFilterName];
							
							case IntelliSmsCheckResult.SUGGESTION_INTERCEPT:
							case IntelliSmsCheckResult.SUGGESTION_DOUBT:
								return "BLOCKED by " + FILTER_NAME[indexOfFilterName];
							
							default:
								return "WTF? by " + FILTER_NAME[indexOfFilterName];
						}
					}
					break;
			}
		} else {
			final String[] FILTER_NAME = { "private", "white list", "black list", "system contacts", "last call", "stranger call" };
			int indexOfFilterName = Integer.numberOfTrailingZeros(filter);
			
			//判断是否有可通过权限
			if (state == FilterConfig.STATE_ACCEPTABLE)
				return "PASSED by " + FILTER_NAME[indexOfFilterName];
			else if (state == FilterConfig.STATE_REJECTABLE)
				return "BLOCKED by " + FILTER_NAME[indexOfFilterName];
		}
		
		return "PASSED filter=" + filter + "state=" + state;
	}
	
	private final class DataHandlerCallback implements DataHandler.DataHandlerCallback {
		
		private String mTag;
		
		public DataHandlerCallback(String tag) {
			mTag = tag;
		}
		
		@Override
		public void onCallback(TelephonyEntity data, int filter, int state, Object... datas) {
			synchronized (mLines) {
				if (mLines.size() >= 20) {
					mLines.remove(0);
				}
				String body = (data instanceof SmsEntity) ? (data.phonenum + " " + ((SmsEntity) data).body) : data.phonenum;
				String line = String.format("%s: %s %s", mTag, body, msgFilterState(data instanceof SmsEntity, filter, state, datas));
				mLines.add(line);
				
				Log.i("demo", line);
				final StringBuffer content = new StringBuffer();
				for (String l : mLines) {
					content.append(l).append("\n");
				}
				
				mContextShower.post(new Runnable() {
					
					@Override
					public void run() {
						mContextShower.setText(content.toString());
					}
				});
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.btn1:
			{
				final List<SmsEntity> smslist = new ArrayList<SmsEntity>();
				SmsEntity en = new SmsEntity();
				en.phonenum = "1900000000"+mReportCount;
				en.body="测试可以短信上报用短息内容" + mReportCount ;
				en.protocolType = SmsEntity.PROTOCOL_TYPE_SMS;
				smslist.add(en);
				mReportCount++;
				new Thread() {
					@Override
					public void run() {
						boolean result = mAresEnginManager.reportSms(smslist);
						if(result) {
							myHandler.obtainMessage(MSG_ID_REPORTSMS, 1, 0).sendToTarget();
						} else {
							myHandler.obtainMessage(MSG_ID_REPORTSMS, 0, 0).sendToTarget();
						}
					}
				}.start();
				break;
			}
			case R.id.btn2:
			{
				LinkedHashMap<SmsEntity, Integer> smsMap = new LinkedHashMap<SmsEntity, Integer>();
				for(int i = 0; i < 3; i++) {
					SmsEntity en = new SmsEntity();
					en.phonenum = "1900000000"+mReportCount;
					en.body="测试可以短信上报用短息内容" + mReportCount ;
					en.protocolType = SmsEntity.PROTOCOL_TYPE_SMS;
					smsMap.put(en, Integer.valueOf(mReportCount%2==0?0:1));
					mReportCount++;
				}
				mAresEnginManager.reportRecoverSms(smsMap, new ISmsReportCallBack() {
					
					@Override
					public void onReprotFinish(int retCode) {
						if(retCode == ErrorCode.ERR_NONE) {
							myHandler.obtainMessage(MSG_ID_REPORTRECOVERSMS, 1, 0).sendToTarget();
						} else {
							myHandler.obtainMessage(MSG_ID_REPORTRECOVERSMS, 0, 0).sendToTarget();
						}
					}
				});
				
				break;
			}
//		case R.id.btn3:
//		{
//			SmsCheckTest.testSms(mAresEnginManager);
//			break;
//		}
//		case R.id.btn4:
//		{
//			SmsCheckTest.testPaySms(mAresEnginManager);
//			break;
//		}
			default:
				break;
		}
	}
	
	protected static final int MSG_ID_REPORTSMS = 11;
	protected static final int MSG_ID_REPORTRECOVERSMS = 12;
	private Handler myHandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
				case MSG_ID_REPORTSMS:
					if(msg.arg1 == 1) {
						Toast.makeText(FilterActivity.this, "reportSms success!", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(FilterActivity.this, "reportSms failed!", Toast.LENGTH_SHORT).show();
					}
					break;
				case MSG_ID_REPORTRECOVERSMS:
					if(msg.arg1 == 1) {
						Toast.makeText(FilterActivity.this, "reportRecoverSms success!", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(FilterActivity.this, "reportRecoverSms failed!", Toast.LENGTH_SHORT).show();
					}
					break;
			}
		}
		
	};
}
