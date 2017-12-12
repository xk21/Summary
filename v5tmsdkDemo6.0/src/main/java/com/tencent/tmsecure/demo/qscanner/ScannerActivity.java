package com.tencent.tmsecure.demo.qscanner;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.tencent.tmsecure.demo.R;

import java.util.List;

import tmsdk.common.module.qscanner.QScanConfig;
import tmsdk.common.module.qscanner.QScanListener;
import tmsdk.common.module.qscanner.QScanResultEntity;
import tmsdk.common.module.qscanner.QScanResultPluginEntity;
import tmsdk.common.module.qscanner.QScannerManagerV2;
import tmsdk.fg.creator.ManagerCreatorF;

//import tmsdk.bg.module.network.NetworkManager;

public class ScannerActivity extends Activity implements OnClickListener {
	static final String TAG = QScannerManagerV2.LOG_TAG + "-UI";
	private TextView mTVScanResultState = null;//扫描结果
	
	private ProgressBar mProgressbar;//扫描进度条
	private TextView mTVScanDetail;// 扫描详细信息
	private StringBuilder mScanDetailInfo;
	private StringBuilder mResultInfo;
	private ScrollView mScrollView;
	
	private Button mBtnLocalScanInstalledPackages, mBtnCloudScanInstalledPackages;
	private Button mBtnLocalScanUnstalledApks, mBtnCloudScanUnstalledApks;
	private Button mBtnPauseContinueScan, mBtnCancelScan;
	
	boolean mPause = false;
	
	private QScannerManagerV2 mQScannerMananger;//病毒扫描功能接口
	HandlerThread mMainJobThread = null;
	Handler mMainJobHandler = null;
	Handler mUpdateUIHandler = null;
	
	static final int MSG_INIT = 0x100; 	//
	static final int MSG_LOCAL_SCAN_INSTALLED_PKGS = 0x101; 	// 本地扫描已安装包
	static final int MSG_CLOUD_SCAN_INSTALLED_PKGS = 0x102; 	// 云扫描已安装包
	static final int MSG_LOCAL_SCAN_UNSTALLED_APKS = 0x103; 	// 本地扫描未安装apk文件
	static final int MSG_CLOUD_SCAN_UNSTALLED_APKS = 0x104; 	// 云扫描未安装apk文件
	static final int MSG_FREE = 0x105; 	//
	
	
	static final int MSG_UPDATE_UI_BTN = 0x201; 	// 刷新按钮
	static final int MSG_UPDATE_UI_DETAIL = 0x202; 	// 刷新扫描详细信息
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qscanner_activity);
		
		mTVScanResultState = (TextView) findViewById(R.id.text_scan_status);
		mProgressbar = (ProgressBar) findViewById(R.id.progress_bar);
		mTVScanDetail = (TextView) findViewById(R.id.text_scan_detail);
		mScrollView = (ScrollView) findViewById(R.id.scroll_layout);
		mBtnLocalScanInstalledPackages = (Button) findViewById(R.id.btn_local_scan_installed_packages);
		mBtnLocalScanInstalledPackages.setOnClickListener(this);
		mBtnCloudScanInstalledPackages = (Button) findViewById(R.id.btn_cloud_scan_installed_packages);
		mBtnCloudScanInstalledPackages.setOnClickListener(this);
		mBtnLocalScanUnstalledApks = (Button) findViewById(R.id.btn_local_scan_unstalled_apks);
		mBtnLocalScanUnstalledApks.setOnClickListener(this);
		mBtnCloudScanUnstalledApks = (Button) findViewById(R.id.btn_cloud_scan_unstalled_apks);
		mBtnCloudScanUnstalledApks.setOnClickListener(this);
		mBtnPauseContinueScan = (Button) findViewById(R.id.btn_pause_continue_scan);
		mBtnPauseContinueScan.setOnClickListener(this);
		mBtnCancelScan = (Button) findViewById(R.id.btn_cancel_scan);
		mBtnCancelScan.setOnClickListener(this);
		updateBtn(false);
		
		mQScannerMananger = ManagerCreatorF.getManager(QScannerManagerV2.class);
		Log.v(TAG, "VirusBaseVersion：" + mQScannerMananger.getVirusBaseVersion());
		
		mMainJobThread = new HandlerThread("qscan");
		mMainJobThread.start();
		mMainJobHandler = new Handler(mMainJobThread.getLooper()){
			@Override
			public void handleMessage(Message msg) {
//				ArrayList<String> apkPath = new ArrayList<String>();
//				apkPath.add("/storage/emulated/0/test/notOfficial1.apk"); // 招行
//				apkPath.add("/storage/emulated/0/test/notOfficial2.apk"); // 美团
//				apkPath.add("/storage/emulated/0/test/payRisk1.apk"); // 伪淘宝
//				apkPath.add("/storage/emulated/0/test/plugin1.apk"); // 空气污染指数_com.freshideas.airindex_4

//				apkPath.add("/storage/emulated/0/test/stealAccountRisk1.apk");
//				apkPath.add("/storage/emulated/0/test/stealAccountRisk2.apk");
//				apkPath.add("/storage/emulated/0/test/stealAccountRisk3.apk");
//				apkPath.add("/storage/emulated/0/test/stealAccountRisk4.apk");
//				apkPath.add("/storage/emulated/0/test/stealAccountRisk5.apk");
				
				if(msg.what == MSG_INIT){
					int nRet = mQScannerMananger.initScanner();
					if(nRet != QScanConfig.S_OK){
						Log.w(TAG, "initScanner error:[" + nRet + "]");
						finish();
					}
				}else if(msg.what == MSG_LOCAL_SCAN_INSTALLED_PKGS){ // 本地扫描已安装包
					mScanDetailInfo = new StringBuilder();
					mResultInfo = new StringBuilder();
					mQScannerMananger.scanInstalledPackages(QScanConfig.SCAN_LOCAL, null, new QScanListenerUI(),
							QScanConfig.ERT_FAST, 10*1000);
					Message msgUI = mUpdateUIHandler.obtainMessage(MSG_UPDATE_UI_BTN, 0, 0);
					mUpdateUIHandler.sendMessage(msgUI);
				}else if(msg.what == MSG_CLOUD_SCAN_INSTALLED_PKGS){ // 云扫描已安装包
					mScanDetailInfo = new StringBuilder();
					mResultInfo = new StringBuilder();
					mQScannerMananger.scanInstalledPackages(QScanConfig.SCAN_LOCAL | QScanConfig.SCAN_CLOUD, null,
							new QScanListenerUI(), QScanConfig.ERT_FAST, 0);
					Message msgUI = mUpdateUIHandler.obtainMessage(MSG_UPDATE_UI_BTN, 0, 0);
					mUpdateUIHandler.sendMessage(msgUI);
				}else if(msg.what == MSG_LOCAL_SCAN_UNSTALLED_APKS){ // 本地扫描未安装apk文件
					mScanDetailInfo = new StringBuilder();
					mResultInfo = new StringBuilder();
					mQScannerMananger.scanUninstallApks(QScanConfig.SCAN_LOCAL, null,
							new QScanListenerUI(), 0);
					Message msgUI = mUpdateUIHandler.obtainMessage(MSG_UPDATE_UI_BTN, 0, 0);
					mUpdateUIHandler.sendMessage(msgUI);
				}else if(msg.what == MSG_CLOUD_SCAN_UNSTALLED_APKS){ // 云扫描未安装apk文件
					mScanDetailInfo = new StringBuilder();
					mResultInfo = new StringBuilder();
					mQScannerMananger.scanUninstallApks(QScanConfig.SCAN_LOCAL | QScanConfig.SCAN_CLOUD, null,
							new QScanListenerUI(), 0);
					Message msgUI = mUpdateUIHandler.obtainMessage(MSG_UPDATE_UI_BTN, 0, 0);
					mUpdateUIHandler.sendMessage(msgUI);
				}else if(msg.what == MSG_FREE){
					mQScannerMananger.cancelScan();
					mQScannerMananger.freeScanner();
					mMainJobThread.quit();
				}
			}
		};
		mUpdateUIHandler = new Handler(this.getMainLooper()){
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == MSG_UPDATE_UI_BTN){
					updateBtn((msg.arg1 == 0) ? false : true);
				}else if(msg.what == MSG_UPDATE_UI_DETAIL){
					mTVScanDetail.setText(mScanDetailInfo.toString());
					mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
					mTVScanResultState.setText(mResultInfo.toString());
					
				}
			}
		};
		
		mMainJobHandler.sendEmptyMessage(MSG_INIT);
	}
	@Override
	protected void onDestroy() {
		mMainJobHandler.sendEmptyMessage(MSG_FREE);
		
		super.onDestroy();
	}
	@Override
	public void onClick(View v) {
		
		if(v == mBtnLocalScanInstalledPackages){ // 本地扫描已安装包
			
			mMainJobHandler.sendEmptyMessage(MSG_LOCAL_SCAN_INSTALLED_PKGS);
			
			Message msg = mUpdateUIHandler.obtainMessage(MSG_UPDATE_UI_BTN, 1, 1);
			mUpdateUIHandler.sendMessage(msg);
			
		}else if(v == mBtnCloudScanInstalledPackages){ // 云扫描已安装包
			
			mMainJobHandler.sendEmptyMessage(MSG_CLOUD_SCAN_INSTALLED_PKGS);
			Message msg = mUpdateUIHandler.obtainMessage(MSG_UPDATE_UI_BTN, 1, 1);
			mUpdateUIHandler.sendMessage(msg);
			
		}else if(v == mBtnLocalScanUnstalledApks){ // 本地扫描未安装apk文件
			
			mMainJobHandler.sendEmptyMessage(MSG_LOCAL_SCAN_UNSTALLED_APKS);
			Message msg = mUpdateUIHandler.obtainMessage(MSG_UPDATE_UI_BTN, 1, 1);
			mUpdateUIHandler.sendMessage(msg);
			
		}else if(v == mBtnCloudScanUnstalledApks){ // 云扫描未安装apk文件
			
			mMainJobHandler.sendEmptyMessage(MSG_CLOUD_SCAN_UNSTALLED_APKS);
			Message msg = mUpdateUIHandler.obtainMessage(MSG_UPDATE_UI_BTN, 1, 1);
			mUpdateUIHandler.sendMessage(msg);
			
		}else if(v == mBtnPauseContinueScan){ // 暂停/继续扫描
			if(!mPause){
				Log.v(TAG, "pauseScan");
				mQScannerMananger.pauseScan();
				mPause = true;
			}else{
				Log.v(TAG, "continueScan");
				mQScannerMananger.continueScan();
				mPause = false;
			}
			
		}else if(v == mBtnCancelScan){ // 取消扫描
			Log.v(TAG, "cancelScan");
			mQScannerMananger.cancelScan();
			Message msg = mUpdateUIHandler.obtainMessage(MSG_UPDATE_UI_BTN, 0, 0);
			mUpdateUIHandler.sendMessage(msg);
		}
	}
	private void updateBtn(boolean bScan){
		if(bScan){
			mBtnLocalScanInstalledPackages.setEnabled(false);
			mBtnCloudScanInstalledPackages.setEnabled(false);
			mBtnLocalScanUnstalledApks.setEnabled(false);
			mBtnCloudScanUnstalledApks.setEnabled(false);
			mBtnPauseContinueScan.setEnabled(true);
			mBtnCancelScan.setEnabled(true);
		}else{
			mBtnLocalScanInstalledPackages.setEnabled(true);
			mBtnCloudScanInstalledPackages.setEnabled(true);
			mBtnLocalScanUnstalledApks.setEnabled(true);
			mBtnCloudScanUnstalledApks.setEnabled(true);
			mBtnPauseContinueScan.setEnabled(false);
			mBtnCancelScan.setEnabled(false);
		}
	}
	
	
	
	private class QScanListenerUI extends QScanListener {
		
		public void onScanStarted(int scanType) {
			Log.v(TAG, "onScanStarted, scanType:[" + scanType + "]");
			
			mScanDetailInfo.append("开始扫描:[" + scanType + "]\n");
			
			mUpdateUIHandler.sendEmptyMessage(MSG_UPDATE_UI_DETAIL);
		}
		
		
		public void onScanProgress(int scanType, int curr, int total, QScanResultEntity result) {
			Log.v(TAG, "onScanProgress11, scanType:[" + scanType +
					"]curr:[" + curr + "]total:[" + total + "]");
			Log.v(TAG, "onScanProgress22,[" + result.packageName + "][" +
					result.softName + "]");
			mScanDetailInfo.append("正在扫描:[" + result.packageName + "][" + result.softName + "]\n");
			
			mUpdateUIHandler.sendEmptyMessage(MSG_UPDATE_UI_DETAIL);
		}
		
		
		public void onScanError(int scanType, int errCode) {
			Log.v(TAG, "onScanError, scanType:[" + scanType + "]errCode:[" + errCode + "]");
			mScanDetailInfo.append("扫描出错:[" + scanType + "]errCode:[" + errCode + "]\n");
			
			mUpdateUIHandler.sendEmptyMessage(MSG_UPDATE_UI_DETAIL);
		}
		
		
		public void onScanPaused(int scanType) {
			Log.v(TAG, "onScanPaused, scanType:[" + scanType + "]");
			mScanDetailInfo.append("暂停扫描:[" + scanType + "]\n");
			
			mUpdateUIHandler.sendEmptyMessage(MSG_UPDATE_UI_DETAIL);
		}
		
		
		public void onScanContinue(int scanType) {
			Log.v(TAG, "onScanContinue, scanType:[" + scanType + "]");
			mScanDetailInfo.append("继续扫描:[" + scanType + "]\n");
			
			mUpdateUIHandler.sendEmptyMessage(MSG_UPDATE_UI_DETAIL);
		}
		
		
		public void onScanCanceled(int scanType) {
			Log.v(TAG, "onScanCanceled, scanType:[" + scanType + "]");
			mScanDetailInfo.append("取消扫描:[" + scanType + "]\n");
			
			mUpdateUIHandler.sendEmptyMessage(MSG_UPDATE_UI_DETAIL);
		}
		
		
		public void onScanFinished(int scanType, List<QScanResultEntity> results) {
			Log.v(TAG, "onScanFinished, scanType:[" + scanType + "]");
			mScanDetailInfo.append("扫描结束:[" + scanType + "]\n");
			mScanDetailInfo.append("---------------\n");
			// 安全
			// 恶意软件(病毒)
			// 支付风险
			// 账号风险
			// 其他隐患软件
			// 非官方
			// 广告
			int nSafe = 0, nViruses = 0,nPayRisks = 0, nStealaccountRisks = 0;
			int nOtherRisk = 0, nNotOfficial = 0, nPlugin = 0;
			
			StringBuilder virusesRet = new StringBuilder();
			virusesRet.append("[Viruses]....\n");
			virusesRet.append("--------------------\n");
			
			StringBuilder payRisksRet = new StringBuilder();
			payRisksRet.append("[PayRisks]....\n");
			payRisksRet.append("--------------------\n");
			
			StringBuilder stealaccountRisksRet = new StringBuilder();
			stealaccountRisksRet.append("[StealaccountRisks]....\n");
			stealaccountRisksRet.append("--------------------\n");
			
			StringBuilder otherRiskRet = new StringBuilder();
			otherRiskRet.append("[OtherRisk]....\n");
			otherRiskRet.append("--------------------\n");
			
			StringBuilder notOfficialRet = new StringBuilder();
			notOfficialRet.append("[NotOfficial]....\n");
			notOfficialRet.append("--------------------\n");
			
			StringBuilder pluginRet = new StringBuilder();
			pluginRet.append("[Plugin]....\n");
			pluginRet.append("--------------------\n");
			for(QScanResultEntity entity : results){
				Log.v(TAG, "[onScanFinished]" +
						"]packageName[" + entity.packageName +
						"softName[" + entity.softName +
						"]version[" + entity.version +
						"]versionCode[" + entity.versionCode +
						"]path[" + entity.path +
						"]scanResult[" + entity.scanResult +
						"]discription[" + entity.virusDiscription + "]");
				Log.i("MMXXQQ", "[" + entity.softName + "]desp:[" + entity.virusDiscription + "]");
				
				if(entity.scanResult == QScanConfig.RET_VIRUSES){
					nViruses++;
					virusesRet.append("[" + entity.scanResult + "][" + entity.packageName + "][" + entity.softName + "]\n");
					virusesRet.append("[" + entity.virusDiscription + "]\n");
				}else if(entity.scanResult == QScanConfig.RET_PAY_RISKS){
					nPayRisks++;
					payRisksRet.append("[" + entity.scanResult + "][" + entity.packageName + "][" + entity.softName + "]\n");
					payRisksRet.append("[" + entity.virusDiscription + "]\n");
				}else if(entity.scanResult == QScanConfig.RET_STEALACCOUNT_RISKS){
					nStealaccountRisks++;
					stealaccountRisksRet.append("[" + entity.scanResult + "][" + entity.packageName + "][" + entity.softName + "]\n");
					stealaccountRisksRet.append("[" + entity.virusDiscription + "]\n");
				}else if(entity.scanResult == QScanConfig.RET_OTHER_RISKS){
					nOtherRisk++;
					otherRiskRet.append("[" + entity.scanResult + "][" + entity.packageName + "][" + entity.softName + "]\n");
					otherRiskRet.append("[" + entity.virusDiscription + "]\n");
				}else if(entity.scanResult == QScanConfig.RET_NOT_OFFICIAL){
					nNotOfficial++;
					notOfficialRet.append("[" + entity.scanResult + "][" + entity.packageName + "][" + entity.softName + "]\n");
					notOfficialRet.append("[" + entity.virusDiscription + "]\n");
				}else if(entity.plugins.size() > 0){
					nPlugin++;
					pluginRet.append("[" + entity.scanResult + "][" + entity.packageName + "][" + entity.softName +  "][" + entity.virusDiscription + "]\n");
					for(QScanResultPluginEntity pluginItem : entity.plugins){
						pluginRet.append("[" + pluginItem.name + "][" + pluginItem.type + "]\n");
					}
				}else if(entity.scanResult == QScanConfig.RET_SAFE){
					nSafe++;
				}
			}
			
			mScanDetailInfo.append(virusesRet.toString());
			mScanDetailInfo.append(payRisksRet.toString());
			mScanDetailInfo.append(stealaccountRisksRet.toString());
			mScanDetailInfo.append(otherRiskRet.toString());
			mScanDetailInfo.append(notOfficialRet.toString());
			mScanDetailInfo.append(pluginRet.toString());
			
			mResultInfo.append("[" + results.size() + "]");
			mResultInfo.append("safe:[" + nSafe + "]");
			mResultInfo.append("viruses:[" + nViruses + "]");
			mResultInfo.append("payRisks:[" + nPayRisks + "]");
			mResultInfo.append("stealaccountRisks:[" + nStealaccountRisks + "]");
			mResultInfo.append("otherRisk:[" + nOtherRisk + "]");
			mResultInfo.append("notOfficial:[" + nNotOfficial + "]");
			mResultInfo.append("plugin:[" + nPlugin + "]");
			
			mUpdateUIHandler.sendEmptyMessage(MSG_UPDATE_UI_DETAIL);
		}
		
	}
	
	
	
	
}
