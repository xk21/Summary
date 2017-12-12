package com.tencent.tmsecure.demo.wifidetect;

import java.util.List;

import tmsdk.bg.creator.ManagerCreatorB;
import tmsdk.bg.module.wifidetect.IWifiDetectListener;
import tmsdk.bg.module.wifidetect.WifiDetectManager;
import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class WifiDetectActivity extends Activity implements OnClickListener{
	static final String TAG = "user-WifiDetectManager";
	//检查号码
	private static final int BUTTON_ARP = 0x10;
	private static final int BUTTON_DNS_PHISHING = 0x11;
	private static final int BUTTON_SECURITY = 0x12;
	private static final int BUTTON_STATE_APPROVE = 0x13;
	
	private EditText mEditTextSSID, mEditTextBSSID;
	private TextView mTextView;
	private Handler mMainThreadHandler;
	
	WifiManager mWifiMgr = null;
	private WifiDetectManager mWifiDetectManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mWifiMgr = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		initView();
		mWifiDetectManager = ManagerCreatorB.getManager(WifiDetectManager.class);
		mWifiDetectManager.init();
		
		mMainThreadHandler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				mTextView.setText(mTextView.getText() + (String)msg.obj);
			}
		};
	}
	private void initView() {
		RelativeLayout rootView = new RelativeLayout(this);
		LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		rootView.setLayoutParams(lp);

		LinearLayout linearLayout = new LinearLayout(this);
		lp = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		linearLayout.setLayoutParams(lp);

		
		Button buttonApprove = new Button(this);
		buttonApprove.setId(BUTTON_STATE_APPROVE);
		buttonApprove.setText("状态认证");
		buttonApprove.setOnClickListener(this);
		
		Button buttonDNS = new Button(this);
		buttonDNS.setId(BUTTON_DNS_PHISHING);
		buttonDNS.setText("DNS钓鱼");
		buttonDNS.setOnClickListener(this);
		
		Button buttonARP = new Button(this);
		buttonARP.setId(BUTTON_ARP);
		buttonARP.setText("ARP检查");
		buttonARP.setOnClickListener(this);
		
		Button buttonSecurity = new Button(this);
		buttonSecurity.setId(BUTTON_SECURITY);
		buttonSecurity.setText("加密检查");
		buttonSecurity.setOnClickListener(this);
		LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(0,
				LayoutParams.WRAP_CONTENT);
		llp.weight = 1;
		buttonApprove.setLayoutParams(llp);
		buttonDNS.setLayoutParams(llp);
		buttonARP.setLayoutParams(llp);
		buttonSecurity.setLayoutParams(llp);

		linearLayout.addView(buttonApprove);
		linearLayout.addView(buttonDNS);
		linearLayout.addView(buttonARP);
		linearLayout.addView(buttonSecurity);

		mEditTextSSID = new EditText(this);
		mEditTextSSID.setId(0x10000001);
		lp = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		mEditTextSSID.setLayoutParams(lp);
		
		
		mEditTextBSSID = new EditText(this);
		mEditTextBSSID.setId(0x10000002);
		lp = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.BELOW, mEditTextSSID.getId());
		mEditTextBSSID.setLayoutParams(lp);
		


		mTextView = new TextView(this);
		lp = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.BELOW, mEditTextBSSID.getId());
		mTextView.setLayoutParams(lp);
		
		rootView.addView(linearLayout);
		rootView.addView(mEditTextSSID);
		rootView.addView(mEditTextBSSID);
		rootView.addView(mTextView);

		setContentView(rootView);
	}
	@Override
	public void onClick(View v) {
		int id = v.getId();
		Thread td = null;
		switch (id) {
		case BUTTON_STATE_APPROVE:
			mTextView.setText("检查网络状态-detectNetworkState\n");
			td = new Thread(new Runnable() {
				@Override
				public void run() {
					String strRet = "";
					Log.v(TAG, "[Beg]detectNetworkState");
					int nRet = mWifiDetectManager.detectNetworkState();
					if(nRet == WifiDetectManager.NETWORK_AVILABLE){
						strRet = "网络可用";
						Log.v(TAG, "网络可用");
					}else if(nRet == WifiDetectManager.NETWORK_NOTAVILABLE){
						strRet = "网络未链接";
						Log.v(TAG, "网络未链接");
					}else if(nRet == WifiDetectManager.NETWORK_NOTAVILABLE_APPROVE){
						strRet = "网络不可用，需认证";
						Log.v(TAG, "网络不可用，需认证");
					}
					Log.v(TAG, "[End]detectNetworkState");
					
					Message msg = mMainThreadHandler.obtainMessage(0);
					msg.obj = strRet;
					mMainThreadHandler.sendMessage(msg);
					
				}
			});
			td.setName("user-detectNetworkState");
			td.start();
			break;
		case BUTTON_DNS_PHISHING:
			mTextView.setText("检查DNS与钓鱼-detectDnsAndPhishing\n");
			td = new Thread(new Runnable() {
				@Override
				public void run() {
					Log.v(TAG, "[Beg]detectDnsAndPhishing");
//					int nRet = mWifiDetectManager.detectDnsAndPhishing(mEditTextSSID.getText().toString(),
//							mEditTextBSSID.getText().toString());
					int nRet = mWifiDetectManager.detectDnsAndPhishing(new IWifiDetectListener(){

						@Override
						public void onResult(int arg0) {
							if(arg0 == WifiDetectManager.CLOUND_CHECK_NETWORK_ERROR){
								mTextView.setText(mTextView.getText() + "联网异常");
							}else if(arg0 == WifiDetectManager.CLOUND_CHECK_NO_FAKE){
								mTextView.setText(mTextView.getText() + "正常");
							}else if(arg0 == WifiDetectManager.CLOUND_CHECK_DNS_FAKE){
								mTextView.setText(mTextView.getText() + "DNS劫持");
							}else if(arg0 == WifiDetectManager.CLOUND_CHECK_PHISHING_FAKE){
								mTextView.setText(mTextView.getText() + "虚假钓鱼WiFi");
							}
						}
						
					});
					Log.v(TAG, "[End]detectDnsAndPhishing:[" +  nRet + "]");
				}
			});
			td.setName("user-detectDnsAndPhishing");
			td.start();
			break;
		case BUTTON_ARP:
			mTextView.setText("检查ARP-detectARP...\n");
			td = new Thread(new Runnable() {
				@Override
				public void run() {
					Log.v(TAG, "[Beg]detectARP");
					int nRet = mWifiDetectManager.detectARP("mdetector");
					Log.v(TAG, "[End]detectARP:[" + nRet + "]");
					
					Message msg = mMainThreadHandler.obtainMessage(0);
					if(nRet == WifiDetectManager.ARP_OK){
						msg.obj = "ARP检测OK";
					}else if(nRet == WifiDetectManager.ARP_FAKE){
						msg.obj = "ARP攻击";
					}else if(nRet < 0){
						msg.obj = "ARP检查异常:[" + nRet + "]";
					}
					mMainThreadHandler.sendMessage(msg);
				}
			});
			td.setName("user-detectARP");
			td.start();
			break;
		case BUTTON_SECURITY:
			mTextView.setText("检查加密-detectSecurity...\n");
			td = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						WifiInfo currWifiInfo = mWifiMgr.getConnectionInfo();
						List<ScanResult> scanList = mWifiMgr.getScanResults();
						StringBuilder sb = new StringBuilder();						
						Log.v(TAG, "[Beg]detectSecurity");
						for(ScanResult item : scanList){
							if(currWifiInfo.getBSSID().compareTo(item.BSSID) == 0){
								sb.append("item:[" + item + "]\n");
								int nRet = mWifiDetectManager.detectSecurity(item);
								Log.v(TAG, "ScanResult-item:[" + item + "]nRet:[" + nRet + "]");
								if(nRet == WifiDetectManager.SECURITY_NONE){
									sb.append("结果:[无加密]\n");
								}else if(nRet == WifiDetectManager.SECURITY_WEP){
									sb.append("结果:[EAP认证与可选的WEP]\n");
								}else if(nRet == WifiDetectManager.SECURITY_PSK){
									sb.append("结果:[WPA pre-shared key]\n");
								}else if(nRet == WifiDetectManager.SECURITY_EAP){
									sb.append("结果:[WPA使用EAP认证]\n");
								}
								
								break;
							}
						}
						Log.v(TAG, "[End]detectSecurity");
						Message msg = mMainThreadHandler.obtainMessage(0);
						msg.obj = sb.toString();
						mMainThreadHandler.sendMessage(msg);
					} catch (Exception e) {
					}
				}
			});
			td.setName("user-detectSecurity");
			td.start();
			break;
		}
	}
	@Override
	protected void onDestroy() {
		mWifiDetectManager.free();
		super.onDestroy();
	}
}