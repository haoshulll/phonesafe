package com.myapp.phonesafe.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Context;

import java.util.List;

/**服务状态检测的工具类
 * 
 */
public class ServiceStateUtil {
	private static final String TAG="ServiceStateUtil";
	public static boolean isRunning(Context context,Class<? extends Service> clazz ){
	
		//取得活动的组件的状态
		ActivityManager am=(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//取得运行的服务
		List<RunningServiceInfo> runningServices = am.getRunningServices(100);// maxNum,取得服务的最大值 
//		am.getRunningTasks(maxNum);//取得Activity ，任务栈
		for(RunningServiceInfo serviceInfo:runningServices){
			//取得类名
			String className = serviceInfo.service.getClassName();
//			LogUtil.d(TAG, className);
			if(className.equals(clazz.getName())){
				return true;
			}
		}
		return false;
	}

}
