package com.myapp.phonesafe.entity;

public class BlackInfo {
	public static final int TYPE_CALL=1;//拦截电话
	public static final int TYPE_SMS=2;//拦截短信
	public static final int TYPE_ALL=3;//拦截全部
	public String number;
	public int type;

}
