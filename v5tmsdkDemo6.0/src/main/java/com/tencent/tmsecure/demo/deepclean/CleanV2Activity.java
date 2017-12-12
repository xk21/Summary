package com.tencent.tmsecure.demo.deepclean;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import com.tencent.tmsecure.demo.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import tmsdk.fg.creator.ManagerCreatorF;
import tmsdk.fg.module.cleanV2.AppGroupDesc;
import tmsdk.fg.module.cleanV2.CleanManager;
import tmsdk.fg.module.cleanV2.ICleanTaskCallBack;
import tmsdk.fg.module.cleanV2.IScanTaskCallBack;
import tmsdk.fg.module.cleanV2.IUpdateCallBack;
import tmsdk.fg.module.cleanV2.RubbishEntity;
import tmsdk.fg.module.cleanV2.RubbishHolder;
 

public class CleanV2Activity extends Activity {
	
	protected static final int MSG_SCANPROCESS = 10;//扫描进程
	protected static final int MSG_ENABLESTARTSCAN = 11;//开始扫描
	protected static final int MSG_ENABLESTARTCLEAN = 12;//清除垃圾

	private static String TAG = "demo";
	
	RubbishHolder mCurrentRubbish;
	private ProgressBar mProgressbar;
	private TextView mScanResultStateView;
	private Button mStopScan, mStopQuickScan,mSdcardScan, mClean,mStopCleanlean,mUpdateRubbishData,mScanPkgButton,mQuickScanButton;
	
	private EditText mInputPkgName;
	/** 垃圾列表item的信息 */
	
	/** 我是来找垃圾的*/
	CleanManager mCleanV2Manager;
 
	/** 刷新占用详情部分UI */
	private final int MSG_REFRESH_SPACE_DETAIL = 0x08;
	/**缓存头部进程*/
	private final int MSG_REFRESH_HEAD_PROGRESS = 0x15;
	/**
	 * 垃圾扫描结束
	 */
	private final int MSG_SDSCANNER_END = 0x19;
 
	
	private final int MSG_SDCLEAN_END = 0x23;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.deepclean_activity);
		mProgressbar = (ProgressBar) findViewById(R.id.progress_bar);
		//显示界面信息
		mScanResultStateView = (TextView) findViewById(R.id.ResultStateText);
		//显示扫描按钮
		mSdcardScan = (Button) findViewById(R.id.sdcard_scan);
		//显示停止扫描按钮
		mStopScan = (Button) findViewById(R.id.stop_scan);
		//显示清除按钮
		mClean = (Button) findViewById(R.id.clean);
		mStopCleanlean = (Button) findViewById(R.id.stop_clean);
		mQuickScanButton =  (Button) findViewById(R.id.quick_scan);
		
		mStopScan.setEnabled(false);
		mStopCleanlean.setEnabled(false);
		mClean.setEnabled(false);
		mUpdateRubbishData = (Button) findViewById(R.id.update_rubbish_data);
		mInputPkgName = (EditText)findViewById(R.id.pkg_name);
		mScanPkgButton =(Button) findViewById(R.id.scan_pkg_button);
		mStopQuickScan = (Button) findViewById(R.id.stop_quick_scan);
		mStopQuickScan.setEnabled(false);
		
		mInputPkgName.setText("com.tencent.mm");
		mTips = new StringBuffer();
		mCleanV2Manager = ManagerCreatorF.getManager(CleanManager.class);
 
		
		mUpdateRubbishData.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				
				//此接口不允许多用户同一时间更新，需要把时间打散，把随机种子时间作为参数传入，如果没有定时更新，传-1
				long randomTime = -1;//务必重视次参数，否则影响使用
				mCleanV2Manager.updateRule(mIUpdateCallBack,randomTime);


			}
		});
 
		//SD卡扫描
		mSdcardScan.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mCleanV2Manager.scanDisk (mScanTaskCallBack, null)){
					mSdcardScan.setEnabled(false);
					mQuickScanButton.setEnabled(false);
					mStopScan.setEnabled(true);
				}
			}
		});
		//停止扫描
		mStopScan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//只要停一个扫描任务，调其中一个就可以了。如果扫描任务没有启动会返回false
				if( mCleanV2Manager.cancelScan(CleanManager.DISK_SCAN_TAG)){
					mStopScan.setEnabled(false);
					mSdcardScan.setEnabled(true);
					mQuickScanButton.setEnabled(true);
				}
			}
		});
		//SD卡扫描
		mQuickScanButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mCleanV2Manager.easyScan(mScanTaskCallBack, null)) {
					mQuickScanButton.setEnabled(false);
					mStopQuickScan.setEnabled(true);
					mSdcardScan.setEnabled(false);
				}
			}
		});
		
		mStopQuickScan.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if( mCleanV2Manager.cancelScan(CleanManager.EASY_SCAN_TAG) ){
					mStopQuickScan.setEnabled(false);
					mQuickScanButton.setEnabled(true);
					mSdcardScan.setEnabled(true);
				}
			}
		});
		 
		

		//清除垃圾
		mClean.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(null!=mCurrentRubbish){
					/***
					 * 以下代码演示了，如何根据特定条件去选对清理或不清理那些垃圾。 
					 */
					Map<String,RubbishEntity> _rubbishes = mCurrentRubbish.getmInstallRubbishes();
					if(null !=_rubbishes){
						for(RubbishEntity _aRubbish :_rubbishes.values() ){
							if(_aRubbish.getPackageName().equals("com.tencent.qq")){
								//这里是用于演示改变清理选中状态。 将当前垃圾，设定为选择清除。 在后面的清理过程中，该垃圾会被删除。
								_aRubbish.setStatus(RubbishEntity.MODEL_TYPE_SELECTED);
							}
						}
					}
//					/**
//					 * 注意：开始清理全部垃圾,只清理model.getStatus() == RubbishType.MODEL_TYPE_SELECTED 的垃圾model
//					 * 如果需要自行清理每一项的垃圾，可分别对每一项结果中的files自己实现delete，注意软件垃圾的files中保存的是目录。 
//					 */
					if( mCleanV2Manager.cleanRubbish(mCurrentRubbish, mCleanCallback)){
						mClean.setEnabled(false);
						mStopCleanlean.setEnabled(true); 
					}
				}
			}
		});
		
		mStopCleanlean.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCleanV2Manager.cancelClean();
				mStopCleanlean.setEnabled(false);
			}
		});
		
		mScanPkgButton.setOnClickListener(new  OnClickListener(){
			public void onClick(View v){
				final String _inputText = mInputPkgName.getText().toString();
				if(TextUtils.isEmpty(_inputText)){
					AlertDialog.Builder builder = new Builder(CleanV2Activity.this); 
					builder.setMessage("请先输入包名"); 
					builder.setTitle("提示");
					DialogInterface.OnClickListener _confirmButton = new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}};
					builder.setPositiveButton("确认",_confirmButton   );
					
					DialogInterface.OnClickListener _cancelButton = new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}};
					builder.setNegativeButton("取消",_cancelButton );
								builder.create().show();
					return;
				} 
	 
				mCleanV2Manager.scan4app(_inputText, mScanTaskCallBack);
				
				//记录某软件被卸载。
				mCleanV2Manager.addUninstallPkg("com.tencent.qq");
				//改变软件被卸载的状态。如果软件再次被安装刚需要调用此接口。
				mCleanV2Manager.delUninstallPkg("com.tencent.mm");
 
			}
		});
		
		
	}
	StringBuffer mTips = new StringBuffer();
	ScanTaskCallBack mScanTaskCallBack = new ScanTaskCallBack();
	class ScanTaskCallBack implements  IScanTaskCallBack{

		@Override
		public void onScanStarted() {
			
		}

 
		public void onRubbishFound(RubbishEntity aRubbish) {
			
//			if((System.currentTimeMillis()-previousRefreshTime)<1000){
//				return ;
//			}
//			previousRefreshTime =System.currentTimeMillis();
//			StringBuffer sbtips = new StringBuffer();
//			sbtips.append("垃圾大小：").append(transformShortType(aRubbish.getSize(), true)).append("\n");
//			sbtips.append("所属应用：").append(aRubbish.getAppName()).append("\n");
//			sbtips.append("应用包名：").append(aRubbish.getPackageName()).append("\n");
//			if(RubbishEntity.INDEX_APK==aRubbish.getRubbishType()){
//				sbtips.append("垃圾描述：").append("APK　安装包\n");
//			}else{
//				sbtips.append("垃圾描述：").append(aRubbish.getDescription()).append("\n");
//			}
//			List<String>  _rubbishkeys = aRubbish.getRubbishKey();
//			if(null!=_rubbishkeys){
//				sbtips.append("file cnt ::"+ _rubbishkeys.size()  +" :: "+_rubbishkeys.get(0));
//				sbtips.append("\n");
//			}
//			mRubbishTips = sbtips.toString();
//			Message msg = mUIHandler.obtainMessage(MSG_REFRESH_HEAD_PROGRESS);
//			msg.sendToTarget();
		}

		@Override
		public void onScanCanceled(RubbishHolder aRubbishHolder) {
			
		}

		@Override
		public void onScanFinished(RubbishHolder aRubbishHolder ) {
			Log.i(TAG,"onScanFinished : "  );
			mCurrentRubbish = aRubbishHolder;
			mUIHandler.sendEmptyMessage(MSG_SDSCANNER_END);
	 
			File _tmpFile = new File("/sdcard/x_v2_demo_all_rubbish.txt");
			_tmpFile.deleteOnExit();
			new File("/sdcard/x_v2_files_demo_all_rubbish.txt");
			_tmpFile.deleteOnExit();
			sbtips = new StringBuffer();
			if(null!=aRubbishHolder){
				if(null!=aRubbishHolder.getmApkRubbishes()){
					sbtips.append("——————————————————————【APK】——————————————————————\n" );
					for(RubbishEntity aRubbish:aRubbishHolder.getmApkRubbishes()){
						writeRubbishEntity2Log(aRubbish);
					}
				}
				if(null!=aRubbishHolder.getmSystemRubbishes()){
					sbtips.append("——————————————————————【系统垃圾】——————————————————————\n " );
					for(Map.Entry<String, RubbishEntity> entry:aRubbishHolder.getmSystemRubbishes().entrySet()){
						writeRubbishEntity2Log(entry.getValue());
					} 
				}
				if(null!=aRubbishHolder.getmInstallRubbishes()){
					sbtips.append("——————————————————————【软件缓存】——————————————————————\n " );
					for(Map.Entry<String, RubbishEntity> entry:aRubbishHolder.getmInstallRubbishes().entrySet()){
						writeRubbishEntity2Log(entry.getValue());
					} 
				}
				if(null!=aRubbishHolder.getmUnInstallRubbishes()){
					sbtips.append("——————————————————————【卸载残余】——————————————————————\n " );
					for(Map.Entry<String, RubbishEntity> entry:aRubbishHolder.getmUnInstallRubbishes().entrySet()){
						writeRubbishEntity2Log(entry.getValue());
					} 
				}
				sbtips.append("——————————————————————【汇总】 ——————————————————————\n ");
				sbtips.append("总的垃圾大小 ：："+ aRubbishHolder.getAllRubbishFileSize()).append("\n");
				sbtips.append("建议删除大小 ：："+ aRubbishHolder.getSuggetRubbishFileSize()).append("\n");
				sbtips.append("垃圾文件数 ：：").append(_rubbishFileCnt).append(" 要删除的文件数 ：：").append( _rubbishSelectedFileCnt).append("\n");
				writeRubbishLog("/sdcard/x_v2_demo_all_rubbish.txt", sbtips.toString());
			}
		
		}

		@Override
		public void onScanError(int error,RubbishHolder aRubbishHolder) {
			Log.i(TAG,"onScanError : " +error );
			mUIHandler.sendEmptyMessage(MSG_SDSCANNER_END);			
		}


		@Override
		public void onDirectoryChange(String dirPath, int fileCnt) {
			Message msg = mUIHandler.obtainMessage(MSG_REFRESH_HEAD_PROGRESS);
			msg.sendToTarget();
			mTips.setLength(0);
			mPathTips = mTips.append(" 扫过文件数：").append(fileCnt).append("\n正在扫：").append(dirPath).toString();
		}
		
	}
	CleanCallback mCleanCallback = new CleanCallback();
	class CleanCallback implements ICleanTaskCallBack {

		@Override
		public void onCleanStarted() {
			Log.i(TAG,"onCleanStarted : "  );
		}

		@Override
		public void onCleanProcessChange(int nowPercent, String aCleanPath) {
			String scantype = "垃圾清理过程：";
			Message msg = mUIHandler.obtainMessage(MSG_REFRESH_HEAD_PROGRESS);
			msg.obj = scantype+aCleanPath;
			msg.arg1 = nowPercent;
			mPercentTip= nowPercent;
			msg.sendToTarget();
			Log.i(TAG,"onCleanProcessChange : "+nowPercent+ "% ::" +aCleanPath );
		}

		@Override
		public void onCleanCanceled() {
			Message msg = mUIHandler.obtainMessage(MSG_SDCLEAN_END);
			msg.sendToTarget();
			Log.i(TAG,"onCleanCanceled : "  );
		}

		@Override
		public void onCleanFinished() {
			Message msg = mUIHandler.obtainMessage(MSG_SDCLEAN_END);
			msg.sendToTarget();
			Log.i(TAG,"onCleanFinish : "  );
		}

		@Override
		public void onCleanError(int error) {
			Message msg = mUIHandler.obtainMessage(MSG_SDCLEAN_END);
			msg.sendToTarget();
			Log.i(TAG,"onCleanError : "  );
		}
		
	}
	
	UpdateCallBack mIUpdateCallBack = new UpdateCallBack();
	class UpdateCallBack implements IUpdateCallBack{
		@Override
		public void updateEnd(int aCode) {
			Log.i("DeepcleanActivity", "updateFinished   "  );
		}
	}
	//销毁进程
    @Override
	protected void onDestroy() {
    	super.onDestroy();
		/**
		 * 请关注！！ 
		 */
 		if(null!=mCleanV2Manager) {
			mCleanV2Manager.onDestroy(); 
		}
 		mCleanV2Manager=null;
		
	}

	/** 主线程的handler，你懂的	 */
	private Handler mUIHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SDSCANNER_END:
				mClean.setEnabled(true);
				mSdcardScan.setEnabled(false);
				mStopScan.setEnabled(false);
				mQuickScanButton.setEnabled(false);
				mStopQuickScan.setEnabled(false);
				mProgressbar.setProgress(0);
				break;
			case MSG_REFRESH_HEAD_PROGRESS:
				mScanResultStateView.setText("pathtips::"+mPathTips+"\n"+mRubbishTips);
				mProgressbar.setProgress(mPercentTip);
				break;
			case MSG_REFRESH_SPACE_DETAIL:
				break;
			case MSG_SDCLEAN_END:
				mStopCleanlean.setEnabled(false);
				break;
			}
		}
	
	};
	
 
	int mPercentTip=0; 
	String mRubbishTips="";
	String mPathTips="";
	//任务进程开启
 

	private void writeRubbishLog(String aLogPath,String aLog){
		FileWriter fw = null;
	try {
		File logFile = new File(aLogPath); 
		try {
			if(!logFile.exists()) {
				logFile.createNewFile();
			}
			fw = new FileWriter(logFile,true); 
			}catch(Exception e ){
			}
		fw.write(  aLog +"\n");
		fw.flush();
		} catch(Exception e){
		} finally {
			if(fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static final long ONE_KB = 1024L;
	public static final long ONE_MB = ONE_KB * 1024L;
	public static final long ONE_GB = ONE_MB * 1024L;
	public static final long ONE_TB = ONE_GB * 1024L;
	public String transformShortType(long bytes, boolean isShortType) {
		long currenUnit = ONE_KB;
		int unitLevel = 0;
		boolean isNegative = false;
		if (bytes < 0) {
			isNegative = true;
			bytes = (-1) * bytes;
		}

		while ((bytes / currenUnit) > 0) {
			unitLevel++;
			currenUnit *= ONE_KB;
		}

		String result_text = null;
		double currenResult = 0;
//		int skipLevel = 1000;//如果大于等于1000就用更大一级单位显示
		switch (unitLevel) {
		case 0:
			result_text = "0K";
			break;
		case 1:
			currenResult = bytes / ONE_KB;
//			if (currenResult < skipLevel) {
				result_text = getFloatValue(currenResult, 1) + "K";
//			} else {
//				result_text = getFloatValue(bytes * 1.0 / ONE_MB) + "M";
//			}
			break;
		case 2:
			currenResult = bytes * 1.0 / ONE_MB;
//			if (currenResult < skipLevel) {
				result_text = getFloatValue(currenResult, 1) + "M";
//			} else {
//				result_text = getFloatValue(bytes * 1.0 / ONE_GB) + "G";
//			}
			break;
		case 3:
			currenResult = bytes * 1.0 / ONE_GB;
//			if (currenResult < skipLevel) {
				result_text = getFloatValue(currenResult, 2) + "G";
//			} else {
//				result_text = getFloatValue(bytes * 1.0 / ONE_TB) + "T";
//			}
			break;
		case 4:
			result_text = getFloatValue(bytes * 1.0 / ONE_TB, 2) + "T";
		}

		if (isNegative) {
			result_text = "-" + result_text;
		}
		return result_text;
	}
	
	private String getFloatValue(double oldValue, int decimalCount){
		if (oldValue >= 1000) {//大于四位整数  不出现小数部分
			decimalCount = 0;
		}else if(oldValue >= 100){
			decimalCount = 1;
		}
		
		BigDecimal b = new BigDecimal(oldValue);
		try {
			if (decimalCount <= 0) {
				oldValue = b.setScale(0, BigDecimal.ROUND_DOWN).floatValue(); //ROUND_DOWN 表示舍弃末尾
			}else{
				oldValue = b.setScale(decimalCount, BigDecimal.ROUND_DOWN).floatValue(); //ROUND_DOWN 表示舍弃末尾,decimalCount 位小数保留
			}
		} catch (ArithmeticException e) {
			Log.w("Unit.getFloatValue", e.getMessage());
		}
		String decimalStr = "";
		if (decimalCount <= 0) {
			decimalStr = "#";
		} else {
			for (int i = 0; i < decimalCount; i++) {
				decimalStr += "#";
			}
		}
		// decimalCount 位小数保留
		DecimalFormat format = new DecimalFormat("###." + decimalStr);
		return  format.format(oldValue);
	}


 

 

	
//	被选择要删除的文件总数
	long _rubbishSelectedFileCnt = 0;
	//一共扫出的文件总数
	long _rubbishFileCnt = 0;
	StringBuffer sbtips ;
	private void writeRubbishEntity2Log(RubbishEntity aRubbish){
		StringBuffer _xx =  new StringBuffer();
		sbtips.append("垃圾大小：").append(transformShortType(aRubbish.getSize(), true)).append("\n");
		sbtips.append("垃圾描述：").append(aRubbish.getDescription()).append(" ");
		if(aRubbish.isSuggest()){
			sbtips.append("是否建议：").append(aRubbish.isSuggest()).append(" TO_DEL");
			_rubbishSelectedFileCnt +=aRubbish.getRubbishKey().size(); 
		}else{
			sbtips.append("是否建议：").append(aRubbish.isSuggest()).append(" NOT_DEL");
		}
		
		sbtips.append("所属应用：").append(aRubbish.getAppName()).append(" ");
		sbtips.append("应用包名：").append(aRubbish.getPackageName()).append("\n");
		List<String>  _rubbishkeys = aRubbish.getRubbishKey();
		if(null!=_rubbishkeys){
			for(String _p : _rubbishkeys){
				sbtips.append( _p +" \n ");
				_xx.append(_p).append("\n");
			}
		}
		_rubbishFileCnt += _rubbishkeys.size();
		Integer[] _groupInfo = aRubbish.getmGroupIds();
		if((null!=_groupInfo)&&(null!=mCleanV2Manager)){
			AppGroupDesc _groupDesc = mCleanV2Manager.getGroupInfo(aRubbish.getmGroupIds()[0]);
			sbtips.append("group_info_ok title:").append(_groupDesc.mTitle).append("group desc:").append(_groupDesc.mDesc).append("\n");
		}else{
			sbtips.append("group_info_no null\n");
		}
		 
		writeRubbishLog("/sdcard/x_v2_demo_all_rubbish.txt",sbtips.toString());
		writeRubbishLog("/sdcard/x_v2_files_demo_all_rubbish.txt",_xx.toString());
		sbtips.setLength(0);
	}

 
}

