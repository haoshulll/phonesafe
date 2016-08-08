package com.myapp.phonesafe.entity;

import android.graphics.drawable.Drawable;

/**应用程序信息
 * 
 */
public class AppInfo {  
	public String name;//应用程序名 ,label
	public String pacakgeName;//包名
	public Drawable icon;//应用的图标
	public boolean isInstallSdcard=false;//是否安装在外部存储
	public boolean isSystem=false;//是否为系统程序，默认是假
	public long size;//程序的大小，安装包的大小
	@Override
	public String toString() {
		return "AppInfo [name=" + name + ", pacakgeName=" + pacakgeName
				+ ", icon=" + icon + ", isInstallSdcard=" + isInstallSdcard
				+ ", isSystem=" + isSystem + ", size=" + size + "]";
	}
	

}
