package com.tencent.tmsecure.demo.network;

import tmsdk.bg.creator.ManagerCreatorB;
import tmsdk.bg.module.network.INetworkChangeCallBack;
import tmsdk.bg.module.network.NetworkManager;
import tmsdk.common.module.network.TrafficEntity;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import com.tencent.tmsecure.demo.R;

public class NetworkActivity extends Activity implements OnClickListener {
	private Button 	 mStartStopButton;
	private TextView mContentShower;
	private NetworkManager mNetworkManager;
	private INetworkChangeCallBack mCallbackForMobile;
	private INetworkChangeCallBack mCallbackForWIFI;
	
	private AppTrafficTest mAppTrafficTest;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.network_activity);
		//启动&停止按钮
		mStartStopButton = (Button) findViewById(R.id.star_stop_btn);
		mContentShower = (TextView) findViewById(R.id.content_shower);
		mStartStopButton.setOnClickListener(this);
		findViewById(R.id.btn1).setOnClickListener(this);
	
		mNetworkManager = ManagerCreatorB.getManager(NetworkManager.class);
		//INTERVAL_FOR_REALTIME = 2;最短时间的刷新，适配一些极端情况
		mNetworkManager.setInterval(NetworkManager.INTERVAL_FOR_REALTIME);
		mStartStopButton.setText(mNetworkManager.isEnable() ? "停止" : "启动");
		//添加默认的Mobile监控器和WIFI监控器
		mNetworkManager.addDefaultMobileMonitor("mobile", NetworkInfoDao.getInstance("mobile"));
		mNetworkManager.addDefaultWifiMonitor("WIFI", NetworkInfoDao.getInstance("WIFI"));
		
		mCallbackForMobile = new NetworkChangeCallBack("mobile", mContentShower);
		mCallbackForWIFI = new NetworkChangeCallBack("WIFI", mContentShower);
		//寻找流量监控器,添加毁掉
		mNetworkManager.findMonitor("mobile").addCallback(mCallbackForMobile);
		mNetworkManager.findMonitor("WIFI").addCallback(mCallbackForWIFI);
		
		
		
//		mStartStopButton.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				//监控服务是否开启
//				boolean enable = mNetworkManager.isEnable();
//				//设置监控服务开关状态
//				mNetworkManager.setEnable(!enable);
//				mStartStopButton.setText(!enable ? "停止" : "启动");
//			}
//		});
		mAppTrafficTest = new AppTrafficTest();
		mAppTrafficTest.start(mNetworkManager, this);
	}
	
	@Override
	protected void onDestroy() {
		//寻找流量监控器,删除毁掉
		mNetworkManager.findMonitor("mobile").removeCallback(mCallbackForMobile);
		mNetworkManager.findMonitor("WIFI").removeCallback(mCallbackForWIFI);
		mAppTrafficTest.destory();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.star_stop_btn:
		{
			//监控服务是否开启
			boolean enable = mNetworkManager.isEnable();
			//设置监控服务开关状态
			mNetworkManager.setEnable(!enable);
			mStartStopButton.setText(!enable ? "停止" : "启动");
			break;
		}
		case R.id.btn1:
			ArrayList<TrafficEntity> datas = mAppTrafficTest.getLastData();
			if(datas != null && datas.size() > 0) {
				mContentShower.setText(datas.get(0).toString());
			}
			break;
		}
	}
}
