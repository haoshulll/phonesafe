package com.myapp.phonesafe.entity;

import android.graphics.drawable.Drawable;

/**
 * 流量统计的信息
 *
 */
public class TrafficInfo {
	public String name;
	public Drawable icon;
	public String packageName;
	public int uid;//用户id
	public long rcv;//接收数据的流量大小
	public long snd;//发送数据的流量大小
	@Override
	public String toString() {
		return "TrafficInfo [name=" + name + ", packageName=" + packageName
				+ ", uid=" + uid + ", rcv=" + rcv + ", snd=" + snd + "]";
	}
	

}
