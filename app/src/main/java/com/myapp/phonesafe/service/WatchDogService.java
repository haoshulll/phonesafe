package com.myapp.phonesafe.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;

import com.myapp.phonesafe.db.AppLockDao;
import com.myapp.phonesafe.ui.activity.WatchDogActivity;
import com.myapp.phonesafe.utils.LogUtil;

import java.util.List;

public class WatchDogService extends Service {

	private static final String TAG = "WatchDogService";
	public static final String PACKNAME = "packagename";//包名
	public static final String TASK_ID = "taskId";//当前任务栈的id
	private AppLockDao mAppLockDao;
	private ActivityManager mActivityManager;
	private boolean isWatch=true;
	
	private String unlockPackage="";//临时解锁的包
	private int unlockTaskid=0;//临时解锁的任务栈的id
	
	
	private BroadcastReceiver receiver=new BroadcastReceiver() {
		


		@Override
		public void onReceive(Context context, Intent intent) {
			unlockPackage = intent.getStringExtra(PACKNAME);
			unlockTaskid = intent.getIntExtra(TASK_ID, 0);
			
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		LogUtil.d(TAG, "onCreate");
		//动态注册广播接收器，解锁临时解锁的广播事件
		IntentFilter filter=new IntentFilter();
		filter.addAction("cn.itcast.phonesafe.UNLOCK");
		registerReceiver(receiver, filter);
		
		mAppLockDao = new AppLockDao(this);
		mActivityManager = (ActivityManager) this
				.getSystemService(Context.ACTIVITY_SERVICE);
		new Thread(){
			public void run() {
				
				while (isWatch) {
					List<RunningTaskInfo> runningTasks = mActivityManager.getRunningTasks(1);// 取得运行的任务，每个应用都应该有任务栈，即应该有Activity
					for (RunningTaskInfo runningTaskInfo : runningTasks) {
						String baseActivityName = runningTaskInfo.baseActivity
								.toShortString();
						String topActivityName = runningTaskInfo.topActivity
								.toShortString();
//						LogUtil.d(TAG, "栈底" + baseActivityName);
						LogUtil.d(TAG, "栈顶" + topActivityName);
						int taskId=runningTaskInfo.id;//任务栈的id
						System.out.println("新的任务栈id"+taskId+" 已经解锁的任务栈id"+unlockTaskid);
						
						// 取得最近打开正在运行的任务栈 的包名
						String packageName = runningTaskInfo.baseActivity
								.getPackageName();
						System.out.println("新打开的包"+packageName+" 已经解锁的包"+unlockPackage);
						boolean isLock = mAppLockDao.isLock(packageName);
						if (isLock) {
							if(!(unlockPackage.equals(packageName)&&unlockTaskid==taskId)){
								// 进入到WatchDogActivity 界面，输入验证码
								Intent intent = new Intent();
								intent.setClass(getApplicationContext(),
										WatchDogActivity.class);
								//附加包名数据
								intent.putExtra(PACKNAME, packageName);
								intent.putExtra(TASK_ID, taskId);
								//从服务启动Activity 需要设置标记： 新的任务栈的标记
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(intent);
							}
							
						}
					}
					SystemClock.sleep(1000);//休眠一秒
				}
				
			};
		}.start();
	}

	@Override
	public void onDestroy() {
		LogUtil.d(TAG, "onDestroy");
		unregisterReceiver(receiver);//注销广播接收器
		isWatch=false;
		super.onDestroy();
	}

}
