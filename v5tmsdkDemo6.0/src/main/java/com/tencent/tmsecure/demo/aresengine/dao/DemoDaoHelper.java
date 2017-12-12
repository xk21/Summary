package com.tencent.tmsecure.demo.aresengine.dao;

import java.util.List;

import tmsdk.common.module.aresengine.ContactEntity;

public class DemoDaoHelper {

	public static void populateStaticData(List<ContactEntity> contactList,
			int numEntities, 
			int entityIds[], String phoneNums[], String names[]) {
		contactList.clear();
		for (int i=0; i<numEntities; ++i)
		{
			ContactEntity entity = new ContactEntity();
			entity.id = entityIds[i];
			entity.phonenum = phoneNums[i];
			entity.name = names[i];
			contactList.add(entity);
		}
	}
	
	public static boolean contains(List<ContactEntity> contactList, String phonenum, int callfrom) {
		for (ContactEntity entity : contactList) {
			String pattern = entity.phonenum;
			if (pattern.length()>8)
				pattern = pattern.substring(pattern.length()-8);
			if (phonenum.endsWith(pattern)) {
				return true;
			}
		}
		return false;
	}
}
