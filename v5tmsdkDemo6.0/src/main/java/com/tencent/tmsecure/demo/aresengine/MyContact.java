package com.tencent.tmsecure.demo.aresengine;

import tmsdk.common.module.aresengine.ContactEntity;

/**
 * 项目可以根据需要重写联系人实体，扩充一些字段，demo中只简单加了一个字段extend
 * @author serenazhou
 */
public class MyContact extends ContactEntity {
	public int extended = 0;  // this is an Extended attr
	
	public MyContact() {
		
	}
	
	public MyContact(ContactEntity other){
		super(other);
	}
	
	public MyContact(String name, String number, int type) {
		this.name = name;
		this.phonenum = number;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getExtended() {
		return extended;
	}
	
	public void setExtended(int extended) {
		this.extended = extended;
	}
	
	@Override
	public MyContact clone() {
		MyContact other = new MyContact();
		other.id = id;
		other.name = name;
		other.phonenum = phonenum;
		other.extended = extended;
		return other;
	}
	
	
}
