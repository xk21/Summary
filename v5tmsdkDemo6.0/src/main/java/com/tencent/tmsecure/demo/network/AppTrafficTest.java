/**
 * 
 */
package com.tencent.tmsecure.demo.network;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import tmsdk.bg.module.network.NetworkManager;
import tmsdk.common.module.network.TrafficEntity;

/**
 * 这个demo仅仅是个例子，所以没有本地存储数据。
 * 
 * @author jiangweiti
 *
 */
public class AppTrafficTest {
	private static final String PKGNAME = "com.tencent.mtt";
	
	private NetworkManager mManager;
	private Context mContext;
	private ArrayList<TrafficEntity> mDatas;
	/**
	 * 开始检测
	 * @param pkg 检测的应用包名
	 * @param manager 
	 * @return 是否成功
	 */
	public boolean  start(NetworkManager manager, Context context) {
		mManager = manager;
		mContext = context;
		String[] pkgs = new String[]{PKGNAME};
		/**
		 * clearTrafficInfo()接口用于初始化应用在TMSDK中的数据。
		 */
		manager.clearTrafficInfo(pkgs);
		/**
		 * 这个refreshTrafficInfo()接口，适用于开始检测流量数据。
		 */
		mDatas = manager.refreshTrafficInfo(pkgs, true);
		if(mDatas != null && mDatas.size() > 0) {
			registerNetStateReceiver();
			return true;
		}
		return false;
	}
	
	/**
	 * 结束检测
	 */
	public void destory() {
		if(mReceiver != null) {
			mContext.unregisterReceiver(mReceiver);
		}
		mDatas = null;
	}
	/**
	 * 获取最新数据
	 * @return
	 */
	public ArrayList<TrafficEntity> getLastData() {
		mManager.refreshTrafficInfo(mDatas);
		Log.v("demo", "mDatas = " + mDatas.get(0).toString());
		return mDatas;
	}
	
	/* 
	 * 手动注册网络状态变化，或在清单文件配置。 
	 */  
	private BroadcastReceiver mReceiver;
	private void registerNetStateReceiver() {
		IntentFilter mfilter = new IntentFilter(); 
		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();  
				ConnectivityManager connectivityManager = (ConnectivityManager) mContext  
		                .getSystemService(Context.CONNECTIVITY_SERVICE);  
				NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
				Log.v("demo", "网络状态改变action="+action+"  && networkInfo =" + networkInfo!=null?networkInfo.getTypeName():"null");
				
				
				/**
				 * 每次网络变化需要刷新检测数据
				 */
				mManager.refreshTrafficInfo(mDatas);
				Log.v("demo", "mDatas = " + mDatas.get(0).toString());
			}
		};
	    mfilter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);  
	    mContext.registerReceiver(mReceiver, mfilter);
	}  
	
	
}
