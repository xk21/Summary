package com.tencent.tmsecure.demo.aresengine.dao;

import java.util.ArrayList;
import java.util.List;

import tmsdk.common.module.aresengine.ContactEntity;
import tmsdk.common.module.aresengine.IContactDao;

/**
 * 黑名单配置 作为DEMO，只是简单采用内存的方式保存数据 实际项目开发当中，应该采用可持久化的数据保存方式，如读取文件或者SQLite等
 * 
 * @author serenazhou
 * 
 */
public class BlackListDao implements IContactDao<ContactEntity> {
	private static List<ContactEntity> mBlackList = new ArrayList<ContactEntity>();
	private static BlackListDao mBlackListDao;
	private static final int NUM_ENTITIES = 5;  
	private static int mEntityIds[] = {1, 2, 3, 6, 9};
	private static String mPhoneNums[] = {"0192345678", "80012345678", "3456789", "+86333344444", "008644562335"};
	private static String mNames[] = {"0192345678", "花心大萝卜", "坏蛋3", "坏蛋4", "坏蛋5"};

	private BlackListDao() {
		DemoDaoHelper.populateStaticData(mBlackList, NUM_ENTITIES, mEntityIds, mPhoneNums, mNames);
	}
	
	protected void initFromStaticData(int numEntities, 
			int entityIds[], String phoneNums[], String names[]) {
		mBlackList.clear();
		for (int i=0; i<numEntities; ++i)
		{
			ContactEntity entity = new ContactEntity();
			entity.id = entityIds[i];
			entity.phonenum = phoneNums[i];
			entity.name = names[i];
			mBlackList.add(entity);
		}
	}

	public static BlackListDao getInstance() {
		if (null == mBlackListDao) {
			synchronized (BlackListDao.class) {
				mBlackListDao = new BlackListDao();
			}
		}
		return mBlackListDao;
	}

	@Override
	public boolean contains(String phonenum, int flags) {
		// 不同的比较方式，可以达到不同的效果，比如可以定义前缀匹配等，都可以根据具体的业务需要实现
		// 这里以后8位来匹配
		return DemoDaoHelper.contains(mBlackList, phonenum, flags);
	}
}