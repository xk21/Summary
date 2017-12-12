package com.tencent.tmsecure.demo.trafficcorrection;

import java.util.ArrayList;

import tmsdk.bg.creator.ManagerCreatorB;
import tmsdk.bg.module.network.CodeName;
import tmsdk.bg.module.network.ITrafficCorrectionListener;
//import tmsdk.bg.module.network.TrafficCorrectionConfig;
import tmsdk.bg.module.network.TrafficCorrectionManager;
import tmsdk.common.ErrorCode;
import tmsdk.common.IDualPhoneInfoFetcher;
import tmsdk.common.TMSDKContext;
//import tmsdk.common.spi.IDualSimAdpter;
//import tmsdk.common.module.aresengine.SmsEntity;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.tmsecure.demo.DemoApplication;
import com.tencent.tmsecure.demo.R;

public class TrafficCorrectionActivity extends Activity {
	public static final String TAG = "TrafficCorrectionUser";
	String mQueryCode1 = ""; // 卡1
	String mQueryPort1 = "";
	String mTrafficMsg1 = "温馨提示，截至5月3日，您的套餐内本地流量已使用7.02 MB，剩余流量292.98 MB。您已使用0.00 MB套外流量（含国内漫游流量），套外国内流量按0.28元/MB收取。登录联通网上营业厅www.10010.com，查询、交费、充值、方便又实惠！";
	
	String mQueryCode2 = ""; // 卡2
	String mQueryPort2 = "";
	String mTrafficMsg2 = "肯定失败,测试";
		
	/** 测试用例
	 * 
	imsi: 460012625302841
	北京 北京 中国联通 联通3G
	温馨提示，截至5月3日，您的套餐内本地流量已使用7.02 MB，剩余流量292.98 MB。您已使用0.00 MB套外流量（含国内漫游流量），套外国内流量按0.28元/MB收取。登录联通网上营业厅www.10010.com，查询、交费、充值、方便又实惠！

	imsi: 460020071866405
	湖北 武汉 中国移动 神州行
	尊敬的客户，您好！扣除您已产生的消费（37.57元）后，您当前余额41.08元。您各项套餐的准实时消费情况如下:1.湖北神州行本地套餐18元A包含本地主叫本地150分钟，已经使用2分钟，剩余148分钟；2.您套餐内上网流量共1024M，截止14日15时，已使用10.35M，剩余1013.65M。仅供参考，具体以月结账单为准。 优服务，点赞10分。中国移动手机营业厅，一键话费查询，精准流量管理。点击下载：http://www.10086.cn/cmccclient/download 中国移动

	imsi: 460004084284252
	广东 广州 移动
	尊敬的13924107706客户，您当月个人流量套餐内流量共360M，截至06日19时01分，已用37.80M，剩余322.20M，其中：省内4G流量余112.22M，省内通用流量余59.98M，国内通用流量余150M，更多流量详情请登录 wap.gd.10086.cn/cxll 查询或点击 gd.10086.cn/appcx 下载“广东移动10086”掌上服务厅客户端查询。中国移动
	
	*/ 
	
	
	TextView mTVSim1Detail, mTVSim2Detail;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.traffic_correction);
		
		
		mTVSim1Detail = (TextView)findViewById(R.id.sim1_detail);
		mTVSim2Detail = (TextView)findViewById(R.id.sim2_detail);
		
		TrafficCorrectionWrapper.getInstance().init(getApplicationContext());
		TrafficCorrectionWrapper.getInstance().setTrafficCorrectionListener(new ITrafficCorrectionListener() {
			
			@Override
			public void onNeedSmsCorrection(int simIndex, String queryCode, String queryPort) {
				android.util.Log.v(TAG, "onNeedSmsCorrection--simIndex:[" + simIndex + "]--queryCode:[" + 
						queryCode +  "]queryPort:[" + queryPort + "]");
				String strState = "";
				strState += "卡：[" + (simIndex+1) + "]需要发查询短信校正\n";
				
				final int simIndexF = simIndex;
				if(IDualPhoneInfoFetcher.FIRST_SIM_INDEX == simIndexF){
					mQueryCode1 = queryCode;
					mQueryPort1 = queryPort;
					mTVSim1Detail.setText(mTVSim1Detail.getText() + strState);
				}else if(IDualPhoneInfoFetcher.SECOND_SIM_INDEX == simIndexF){
					mQueryCode2 = queryCode;
					mQueryPort2 = queryPort;
					mTVSim2Detail.setText(mTVSim2Detail.getText() + strState);
				}
				
				new Thread(new Runnable(){
					@Override
					public void run() {
						String strDetail = "";
						
						if(IDualPhoneInfoFetcher.FIRST_SIM_INDEX == simIndexF){
							TrafficCorrectionWrapper.getInstance().analysisSMS(simIndexF, 
									mQueryCode1, 
									mQueryPort1, 
									mTrafficMsg1);
							strDetail += "[" + mQueryCode1 + "][" + mQueryPort1 + "]\n[" + 
									mTrafficMsg1 + "]\n";
							
						}else if(IDualPhoneInfoFetcher.SECOND_SIM_INDEX == simIndexF){
							TrafficCorrectionWrapper.getInstance().analysisSMS(simIndexF, 
									mQueryCode2, 
									mQueryPort2, 
									mTrafficMsg2);
							strDetail += "[" + mQueryCode2 + "][" + mQueryPort2 + "]\n[" + 
									mTrafficMsg2 + "]\n";
						}
						Message msg = uiHandler.obtainMessage(MSG_NEED_SEND_MSG,simIndexF,0);
						msg.obj = strDetail;
						msg.sendToTarget();
					}
					
				}).start();
			}
			@Override
			public void onTrafficInfoNotify(int simIndex, int trafficClass, int subClass, int kBytes){
				Message msg = uiHandler.obtainMessage(MSG_TRAFfICT_NOTIFY,simIndex,0);
				msg.obj = logTrafficInfo(simIndex, trafficClass, subClass, kBytes);
				msg.sendToTarget();
				android.util.Log.v(TAG, "onTrafficNotify-" + (String)msg.obj);
			}
			
			@Override
			public void onError(int simIndex, int errorCode) {
				
				String strState = "状态信息：";
				strState += "卡：[" + simIndex + "]校正出错:[" + errorCode + "]";
				
				if(IDualPhoneInfoFetcher.FIRST_SIM_INDEX == simIndex){
					mTVSim1Detail.setText(strState);
				}else if(IDualPhoneInfoFetcher.SECOND_SIM_INDEX == simIndex){
					mTVSim2Detail.setText(strState);
				}
				android.util.Log.v(TAG, "onError--simIndex:[" + simIndex + "]errorCode:[" + errorCode + "]");
			}

			@Override
			public void onCorrectionResult(int simIndex, int retCode) {
				String retStr = "-1";
				if(retCode == 0){
					retStr = "校正成功";
				}else if(retCode == -1){
					retStr = "校正失败";
				}else{
					retStr = "校正未知";
				}
				android.util.Log.v(TAG, "onCorrectionResult, simindex:["+simIndex+"], retCode:["+retCode+"]");
			}
		});
		
		
		// 卡槽1设置
		Button bt_sim0_setting = (Button) findViewById(R.id.sim1_setting);
		bt_sim0_setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LocationSetDlg locationSetDlg = new LocationSetDlg(
						TrafficCorrectionActivity.this,
						IDualPhoneInfoFetcher.FIRST_SIM_INDEX);
				locationSetDlg.startDlg();
			}
		});
		// 卡槽1校正
		Button bt_sim0_correction = (Button) findViewById(R.id.sim1_correction);
		bt_sim0_correction.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
				mTVSim1Detail.setText("卡1校正...\n");
				
				int retCode = TrafficCorrectionWrapper.getInstance().startCorrection(IDualPhoneInfoFetcher.FIRST_SIM_INDEX);
				if(retCode != ErrorCode.ERR_NONE){
					mTVSim1Detail.setText("卡1校正出错终止\n");
				}
			}
		});
		
		// 卡槽2设置
		Button bt_sim1_setting = (Button) findViewById(R.id.sim2_setting);
		bt_sim1_setting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LocationSetDlg locationSetDlg = new LocationSetDlg(
						TrafficCorrectionActivity.this, 
						IDualPhoneInfoFetcher.SECOND_SIM_INDEX);
				locationSetDlg.startDlg();
			}
		});
		// 卡槽2校正
		Button bt_sim1_correction = (Button) findViewById(R.id.sim2_correction);
		bt_sim1_correction.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
				mTVSim2Detail.setText("卡2校正...\n");
				int retCode = TrafficCorrectionWrapper.getInstance().startCorrection(IDualPhoneInfoFetcher.SECOND_SIM_INDEX);
				if(retCode != ErrorCode.ERR_NONE){
					mTVSim2Detail.setText("卡2校正出错终止\n");
				}
			}
		});

		// 卡槽2设置
		Button bt_imsi_change = (Button) findViewById(R.id.imsi_change);
		bt_imsi_change.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				android.util.Log.i("TrafficCorrection","onImsiChanged");
				TMSDKContext.onImsiChanged();
			}
		});
		
		String logTemp = "";
		int simIndex = IDualPhoneInfoFetcher.FIRST_SIM_INDEX;
		int retTrafficInfo[] = TrafficCorrectionWrapper.getInstance().getTrafficInfo(simIndex);
		logTemp += "常规-剩余[" + retTrafficInfo[0] + "]已用[" + retTrafficInfo[1] + "]总量[" +retTrafficInfo[2] +"]\n";
		logTemp += "闲时-剩余[" + retTrafficInfo[3] + "]已用[" + retTrafficInfo[4] + "]总量[" +retTrafficInfo[5] +"]\n";
		logTemp += "4G-剩余[" + retTrafficInfo[6] + "]已用[" + retTrafficInfo[7] + "]总量[" +retTrafficInfo[8] +"]\n";
		mTVSim1Detail.setText(logTemp);
		
		logTemp = "";
		simIndex = IDualPhoneInfoFetcher.SECOND_SIM_INDEX;
		retTrafficInfo = TrafficCorrectionWrapper.getInstance().getTrafficInfo(simIndex);
		logTemp += "常规-剩余[" + retTrafficInfo[0] + "]已用[" + retTrafficInfo[1] + "]总量[" +retTrafficInfo[2] +"]\n";
		logTemp += "闲时-剩余[" + retTrafficInfo[3] + "]已用[" + retTrafficInfo[4] + "]总量[" +retTrafficInfo[5] +"]\n";
		logTemp += "4G-剩余[" + retTrafficInfo[6] + "]已用[" + retTrafficInfo[7] + "]总量[" +retTrafficInfo[8] +"]\n";
		mTVSim2Detail.setText(logTemp);
		
		
		android.util.Log.v(TAG, "onTrafficNotify-" + logTemp);
		
		
	}
	
	protected void onDestroy (){		
		TrafficCorrectionWrapper.getInstance().setTrafficCorrectionListener(null);
		super.onDestroy();
	}
	
	void showArrayList(ArrayList<CodeName> list) {
		StringBuilder sb = new StringBuilder();
		for (CodeName cn: list) {
			sb.append("(" + cn.mCode + "," + cn.mName + ")");
		}
		showLogToastNote(sb.toString());
	}
	
	void showToastNote(String msg) {
		Toast a = Toast.makeText(TrafficCorrectionActivity.this, msg, Toast.LENGTH_SHORT);
		a.show();
	}
	
	void showLogToastNote(String msg) {
		Toast a = Toast.makeText(TrafficCorrectionActivity.this, msg, Toast.LENGTH_LONG);
		a.show();
	}
	
	String logTrafficInfo(int simIndex, int trafficClass, int subClass, int kBytes){
		String logTemp = "";
		
		if(trafficClass == ITrafficCorrectionListener.TC_TrafficCommon){
			logTemp += "--常规";
		}else if(trafficClass == ITrafficCorrectionListener.TC_TrafficFree){
			logTemp += "--闲时";
		}else if(trafficClass == ITrafficCorrectionListener.TC_Traffic4G){
			logTemp += "--4G";
		}
		
		if(subClass == ITrafficCorrectionListener.TSC_LeftKByte){
			logTemp = logTemp + "-剩余:[" + kBytes + "]";
		}else if(subClass == ITrafficCorrectionListener.TSC_UsedKBytes){
			logTemp = logTemp + "-已用:[" + kBytes + "]";
		}else if(subClass == ITrafficCorrectionListener.TSC_TotalKBytes){
			logTemp = logTemp + "-总额:[" + kBytes + "]";
		}
		logTemp += "\n";
		return logTemp;
	}
	

	/**
	 * 校正成功提示
	 */
	private final int MSG_TRAFfICT_NOTIFY = 0x1a;
	
	/***
	 * 需要发送短信
	 */
	private final int MSG_NEED_SEND_MSG = 0x1b;
	Handler uiHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			
			case MSG_TRAFfICT_NOTIFY:
				String logTemp = (String) msg.obj;
				if(IDualPhoneInfoFetcher.FIRST_SIM_INDEX == msg.arg1){
					mTVSim1Detail.setText(mTVSim1Detail.getText() + logTemp);
				}else if(IDualPhoneInfoFetcher.SECOND_SIM_INDEX == msg.arg1){
					mTVSim2Detail.setText(mTVSim2Detail.getText() + logTemp);
				}
				break;
			case MSG_NEED_SEND_MSG:
				if(IDualPhoneInfoFetcher.FIRST_SIM_INDEX == msg.arg1){
					mTVSim1Detail.setText(mTVSim1Detail.getText() + (String) msg.obj);
				}else if(IDualPhoneInfoFetcher.SECOND_SIM_INDEX == msg.arg1){
					mTVSim2Detail.setText(mTVSim2Detail.getText() + (String) msg.obj);
				}
				break;
			}
		}
	};
	
}
