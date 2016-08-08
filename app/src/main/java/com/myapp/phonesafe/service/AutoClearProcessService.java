package com.myapp.phonesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.myapp.phonesafe.business.ProcessInfoProvider;
import com.myapp.phonesafe.entity.ProcessInfo;

import java.util.List;

public class AutoClearProcessService extends Service {
	private BroadcastReceiver receiver=new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			//查询目前所有的进程
			List<ProcessInfo> appProcesses = ProcessInfoProvider.getAppProcesses(context);
			for(ProcessInfo temp:appProcesses){
				if(!temp.packageName.equals(context.getPackageName())){
//					System.out.println("AutoClearProcessService---onReceive");
					ProcessInfoProvider.killProcess(context, temp.packageName);
				}
				
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate() {
		System.out.println("AutoClearProcessService-create");
		IntentFilter filter=new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);//锁屏
		registerReceiver(receiver, filter);
		
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public void onDestroy() {
		System.out.println("AutoClearProcessService-onDestroy");
		unregisterReceiver(receiver);//注销
		super.onDestroy();
	}
	
	

}
