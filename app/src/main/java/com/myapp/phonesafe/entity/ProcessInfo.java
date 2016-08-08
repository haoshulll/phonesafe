package com.myapp.phonesafe.entity;

import android.graphics.drawable.Drawable;

/**进程信息
 * 
 */
public class ProcessInfo {  
	public String name;//进程名 ,label 
	public String packageName;//包名
	public Drawable icon;//进程的图标,应用的图标
	public boolean isSystem=false;//是否为系统程序，默认是假
	public long memory;//占用内存大小
	public boolean isChecked=false;//是否被选中  ，默认false
	@Override
	public String toString() {
//		return "ProcessInfo [name=" + name + ", pacakgeName=" + pacakgeName
//				+ ", icon=" + icon + ", isSystem=" + isSystem + ", memory="
//				+ memory + "]";
		return "{name:" + name +",pacakgeName:"+packageName+ "}"; //返回json格式
	}
	


}
