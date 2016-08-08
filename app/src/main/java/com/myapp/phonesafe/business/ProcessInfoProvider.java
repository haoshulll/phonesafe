package com.myapp.phonesafe.business;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Debug.MemoryInfo;

import com.myapp.phonesafe.entity.ProcessInfo;
import com.myapp.phonesafe.entity.ProcessManager;

import java.util.ArrayList;
import java.util.List;


/**
 * 取得进程信息的业务列
 *
 */
public class ProcessInfoProvider {
	public static List<ProcessInfo> getAppProcesses(Context context){
		List<ProcessInfo> data=new ArrayList<ProcessInfo>();
		//活动管理器 ， 指处于活跃状态的组件的信息 
		ActivityManager am=(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//包管理器
		PackageManager pm = context.getPackageManager();
		List<ProcessManager.Process> runningAppProcesses = ProcessManager.getRunningProcesses();
		
		//遍历
		for (ProcessManager.Process runProcess: runningAppProcesses) {
			ProcessInfo processInfo=new ProcessInfo();
			String packageName = null;
			if (runProcess.getPackageName()!=null){
				packageName = runProcess.getPackageName();//进程的包名
				processInfo.packageName=packageName;//包名
			}
			//取得进行运行的内存
			MemoryInfo[] memoryInfos = am.getProcessMemoryInfo(new int[]{runProcess.pid});
			int totalPss = memoryInfos[0].getTotalPss()*1024;//转为 byte=8bit 字节数  位  ：bit b/s
			processInfo.memory=totalPss;
			
			try {
				ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
				processInfo.name=applicationInfo.loadLabel(pm).toString();//进程的名称 ，label
				processInfo.icon=applicationInfo.loadIcon(pm);//进程图标

				//判断系统进程还是用户进程
				if((applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)==ApplicationInfo.FLAG_SYSTEM){
					processInfo.isSystem=true;
				}
				
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
  			data.add(processInfo);
		}
		System.out.println(data.toString());
		return data;
	}
    //杀死进程
	public static void killProcess(Context context,String packageName) {
		ActivityManager am=(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		am.killBackgroundProcesses(packageName);

	}
}
