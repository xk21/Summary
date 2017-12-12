package com.tencent.tmsecure.demo.update;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.tmsecure.demo.R;

import tmsdk.common.creator.ManagerCreatorC;
import tmsdk.common.module.update.CheckResult;
import tmsdk.common.module.update.ICheckListener;
import tmsdk.common.module.update.IUpdateListener;
import tmsdk.common.module.update.UpdateConfig;
import tmsdk.common.module.update.UpdateInfo;
import tmsdk.common.module.update.UpdateManager;

/**
 * 更新模块DEMO，涉及所有功能模块的更新逻辑
 * @author boyliang
 */
public final class UpdateActivity extends Activity{
	private TextView mContextShower;
	private Button mCheckButton;
	private Button mUpdateButton;
	
	private UpdateManager mUpdateManager;
	private CheckResult mCheckResults;
	private ProgressDialog mProgressDialog;
	
 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//更新模块窗口
		setContentView(R.layout.update_activity);
		
		mUpdateManager = ManagerCreatorC.getManager(UpdateManager.class);
		//更新模块显示
		mContextShower = (TextView) findViewById(R.id.content_shower);
		//检查按钮
		mCheckButton = (Button) findViewById(R.id.check_btn);
		//更新按钮
		mUpdateButton = (Button) findViewById(R.id.update_btn);
		
 
		
		mProgressDialog = new ProgressDialog(this);
	    mProgressDialog.setCancelable(false);
	    
 
 
		//检查更新
		mCheckButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mProgressDialog.setMessage("正在检查更新，请稍后...");
				mProgressDialog.setIndeterminate(true);
				showDialog(0);
				mContextShower.setText(" 正在4检查更新，请稍后... ");
				new Thread(new Runnable() {	
					@Override
					
					
					public void run() {
						long flags =  
								  UpdateConfig.UPDATA_FLAG_NUM_MARK//号码标记模块
//								| UpdateConfig.UPDATE_FLAG_BLACKLIST_PROCESS //优化模块的加速功能
//								| UpdateConfig.UPDATE_FLAG_NOTKILLLIST_KILL_PROCESSES //优化模块的加速功能
								| UpdateConfig.UPDATE_FLAG_VIRUS_BASE//病毒扫描模块
								| UpdateConfig.UPDATE_FLAG_VIRUSKILLER_CLOUDSCAN_WHITE//病毒扫描模块
								| UpdateConfig.UPDATE_FLAG_TRAFFIC_MONITOR_CONFIG//流量监控
//								| UpdateConfig.UPDATE_FLAG_LOCATION//归属地模块
								| UpdateConfig.UPDATE_FLAG_PROCESSMANAGER_WHITE_LIST// 瘦身大文件模块
								| UpdateConfig.UPDATE_FLAG_POSEIDONV2 //智能拦截
								| UpdateConfig.UPDATE_FLAG_YELLOW_PAGEV2_Large//黄页大库
								| UpdateConfig.UPDATE_FLAG_DEEP_CLEAN_APPGROUP_DESC;//垃圾清理分组信息
						mUpdateManager.check(flags, new ICheckListener() {
							@Override
							//检查网络，如果网络失败则回调
							public void onCheckEvent(int arg0) {
								//检查网络状态，如果网络失败则不能更新
								Message msg = Message.obtain(mHandler, MSG_NETWORK_ERROR);
								msg.arg1 = arg0;
								msg.sendToTarget();
							}
							
							@Override
							public void onCheckStarted() {
								
							}
							
							@Override
							public void onCheckCanceled() {
								
							}
							@Override
							public void onCheckFinished(CheckResult result) {
								mCheckResults = result;			
								
								//修改数据显示时机，在数据check出后，再发送通知进行显示
								mHandler.sendEmptyMessage(MSG_HIDE_CHECK_PROGRESS);
								if(result != null) {
									for(UpdateInfo info:result.mUpdateInfoList) {
										Log.v("demo", "updateinfo:"+info.url);
									}
								}
							}
						});	
						//mHandler.sendEmptyMessage(MSG_HIDE_CHECK_PROGRESS);
					}
				}).start();
			}
		});
		//更新
		mUpdateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mCheckResults != null && mCheckResults.mUpdateInfoList != null 
						&& mCheckResults.mUpdateInfoList.size() > 0) {
					mProgressDialog.setMessage("正在更新数据，请稍后...");
					mProgressDialog.setIndeterminate(false);
					showDialog(0);
					new Thread(new Runnable() {
						@Override
						public void run() {
							if(null == mCheckResults) return;
							mUpdateManager.update(mCheckResults.mUpdateInfoList, new IUpdateListener() {
								@Override
								//更新
								public void onProgressChanged(UpdateInfo arg0, int arg1) {
									Message msg = Message.obtain(mHandler, MSG_UPDATE_PROGRESS);
									msg.obj = arg0;
									msg.arg1 = arg1;
									msg.sendToTarget();
								}
								@Override
								//更新中检查网络
								public void onUpdateEvent(UpdateInfo arg0, int arg1) {
									Message msg = Message.obtain(mHandler, MSG_NETWORK_ERROR);
									msg.arg1 = arg1;
									msg.sendToTarget();
								}
								
								@Override
								public void onUpdateFinished() {
									
								}
								
								@Override
								public void onUpdateStarted() {
									
								}
								@SuppressWarnings("unused")
								public void onUpdateCanceled() {
		
								}
							});
							//发出通知更新
							mHandler.sendEmptyMessage(MSG_HIDE_UPDATE_PROGRESS);
						}
					}).start();
				}
			}
		});
	}
	
	private static final int MSG_HIDE_CHECK_PROGRESS = 0;//发出检查通知
	private static final int MSG_HIDE_UPDATE_PROGRESS = 1;//发出更新通知
    private static final int MSG_UPDATE_PROGRESS = 2;//更新
    private static final int MSG_NETWORK_ERROR = 3;//网络失败
    private Handler mHandler = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
    		case MSG_HIDE_CHECK_PROGRESS:
    			dismissDialog(0);
    			showCheckResult();
    			break;
    		case MSG_HIDE_UPDATE_PROGRESS:
    			dismissDialog(0);
    			break;
    		case MSG_UPDATE_PROGRESS:
    			UpdateInfo updateInfo = (UpdateInfo) msg.obj;
    			mProgressDialog.setProgress(msg.arg1);
    			mProgressDialog.setMessage(updateInfo.fileName);
    			addUpdateResult(updateInfo.fileName);
    			break;
    		case MSG_NETWORK_ERROR:
    			Toast.makeText(UpdateActivity.this, "网络错误，错误码：" + msg.arg1, Toast.LENGTH_LONG).show();
    			break;
    		}
    	}
    };
	
	
	@SuppressLint("Override")
	protected Dialog onCreateDialog(int id, Bundle args) {
	    return mProgressDialog;
	}
   //检查结果显示
	private void showCheckResult() {
		StringBuilder sb = new StringBuilder();
		
		if (null == mCheckResults || null == mCheckResults.mUpdateInfoList) {
			sb.append("check result: mUpdateInfoList = null").append("\n");
			mContextShower.setText(sb.toString());
			return;
		} 
		for (UpdateInfo info : mCheckResults.mUpdateInfoList) {
			sb.append("check result: ").append("\n");
			sb.append("need to update: ").append(info.fileName).append("\n");
		}
		mContextShower.setText(sb.toString());
	}
 //更新结果显示
	private void addUpdateResult(String fileName) {
		StringBuilder sb = new StringBuilder(mContextShower.getText());
		sb.append("update file -> ").append(fileName).append("\n");
		mContextShower.setText(sb.toString());
	}
}
