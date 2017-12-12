package com.tencent.tmsecure.demo.aresengine;

import tmsdk.bg.module.aresengine.AresEngineFactor;
import tmsdk.bg.module.aresengine.PhoneDeviceController;
import tmsdk.common.module.aresengine.AbsSysDao;
import tmsdk.common.module.aresengine.CallLogEntity;
import tmsdk.common.module.aresengine.ContactEntity;
import tmsdk.common.module.aresengine.DefaultSysDao;
import tmsdk.common.module.aresengine.ICallLogDao;
import tmsdk.common.module.aresengine.IContactDao;
import tmsdk.common.module.aresengine.IEntityConverter;
import tmsdk.common.module.aresengine.IKeyWordDao;
import tmsdk.common.module.aresengine.ILastCallLogDao;
import tmsdk.common.module.aresengine.ISmsDao;
import tmsdk.common.module.aresengine.SmsEntity;
import android.content.Context;

import com.tencent.tmsecure.demo.aresengine.dao.BlackListDao;
import com.tencent.tmsecure.demo.aresengine.dao.CallLogDao;
import com.tencent.tmsecure.demo.aresengine.dao.KeyWordDao;
import com.tencent.tmsecure.demo.aresengine.dao.LastCallLogDao;
import com.tencent.tmsecure.demo.aresengine.dao.PrivateCallLogDao;
import com.tencent.tmsecure.demo.aresengine.dao.PrivateListDao;
import com.tencent.tmsecure.demo.aresengine.dao.PrivateSmsDao;
import com.tencent.tmsecure.demo.aresengine.dao.SmsDao;
import com.tencent.tmsecure.demo.aresengine.dao.WhiteListDao;

/**
 * ��ս������Ĺ�����, �ṩ�����ع��� ��Ŀ��Ҫ�ṩ���ع��ܣ�Ҫʵ�ִ����е����з���
 * 
 * @author serenazhou
 * 
 */
public final class DemoAresEngineFactor extends AresEngineFactor {
	private Context mContext;

	public DemoAresEngineFactor(Context context) {
		mContext = context;
	}

	@Override
	public IContactDao<? extends ContactEntity> getBlackListDao() {
		return BlackListDao.getInstance();
	}

	@Override
	public ICallLogDao<? extends CallLogEntity> getCallLogDao() {
		return CallLogDao.getInstance();
	}

	@Override
	public IEntityConverter getEntityConverter() {
		return new EntityConvert();
	}

	@Override
	public IKeyWordDao getKeyWordDao() {
		return KeyWordDao.getInstance();
	}

	@Override
	public ILastCallLogDao getLastCallLogDao() {
		return LastCallLogDao.getInstance();
	}

	@Override
	public ICallLogDao<? extends CallLogEntity> getPrivateCallLogDao() {
		return PrivateCallLogDao.getInstance();
	}

	@Override
	public IContactDao<? extends ContactEntity> getPrivateListDao() {
		return PrivateListDao.getInstance();
	}

	@Override
	public ISmsDao<? extends SmsEntity> getPrivateSmsDao() {
		return PrivateSmsDao.getInstance();
	}

	@Override
	public ISmsDao<? extends SmsEntity> getSmsDao() {
		return SmsDao.getInstance();
	}

	@Override
	public IContactDao<? extends ContactEntity> getWhiteListDao() {
		return WhiteListDao.getInstance();
	}
	
	@Override
	public PhoneDeviceController getPhoneDeviceController() {
		return super.getPhoneDeviceController();
	}

	@Override
	public AbsSysDao getSysDao() {
		return DefaultSysDao.getInstance(mContext);
	}
}
