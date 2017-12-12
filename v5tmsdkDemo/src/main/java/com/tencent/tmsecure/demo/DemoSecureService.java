package com.tencent.tmsecure.demo;

import android.content.Intent;

import tmsdk.common.TMSService;

/**
 * 常驻内存的后台服务
 * @author boyliang
 */
public final class DemoSecureService extends TMSService {

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}
	
}
