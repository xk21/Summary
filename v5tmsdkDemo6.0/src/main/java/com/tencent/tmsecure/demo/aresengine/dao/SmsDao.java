package com.tencent.tmsecure.demo.aresengine.dao;

import java.util.ArrayList;
import java.util.List;

import tmsdk.common.module.aresengine.FilterResult;
import tmsdk.common.module.aresengine.ISmsDao;
import tmsdk.common.module.aresengine.SmsEntity;

/**
 * �������ؼ�¼����
 * ��ΪDEMO��ֻ�Ǽ򵥲����ڴ�ķ�ʽ��������
 * ʵ����Ŀ�������У�Ӧ�ò��ÿɳ־û������ݱ��淽ʽ�����ȡ�ļ�����SQLite��
 * @author serenazhou
 *
 */
public class SmsDao implements ISmsDao<SmsEntity> {
	private static List<SmsEntity> mSmsList = new ArrayList<SmsEntity>();	
	private static SmsDao mSmsDao;
	
	private SmsDao() {
	}
	
	// ��ȡ�������ؼ�¼����ʵ��
	public static SmsDao getInstance() {
		if (null == mSmsDao) {
			synchronized (SmsDao.class) {
				mSmsDao = new SmsDao();
			}
		}
		return mSmsDao;
	}
	
	// �Ƴ��������ؼ�¼
	public boolean delete(SmsEntity entity) {
		mSmsList.remove(entity);
		return true;
	}

	// �����µĶ������ؼ�¼
	@Override
	public long insert(SmsEntity entity, FilterResult result) {
		mSmsList.add(entity);
		return mSmsList.size() - 1;
	}

	// ���¶������ؼ�¼�б���ƥ�䵽��ȷ�Ķ������ؼ�¼��ɾ�����еģ������¼���һ��
	public boolean update(SmsEntity entity) {
		int size = mSmsList.size();
		SmsEntity tempEntity;
		for(int i = 0; i < size; i++) {
			tempEntity = mSmsList.get(i);
			if(tempEntity.phonenum.equals(entity.phonenum)){
				mSmsList.remove(tempEntity);
				mSmsList.add(i,tempEntity);
			}
		}
		return true;
	}
}
