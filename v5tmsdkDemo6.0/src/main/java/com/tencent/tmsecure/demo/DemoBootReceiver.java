package com.tencent.tmsecure.demo;

import android.content.Context;
import android.content.Intent;

import tmsdk.common.TMSBootReceiver;

/**
 * 开机事件监听
 * @author boyliang
 */
public final class DemoBootReceiver extends TMSBootReceiver {
	
	@Override
	public void doOnRecv(final Context context, Intent intent) {
		super.doOnRecv(context, intent);
	}
	
}
