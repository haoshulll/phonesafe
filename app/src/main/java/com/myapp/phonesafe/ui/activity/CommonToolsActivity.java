package com.myapp.phonesafe.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.myapp.phonesafe.R;
import com.myapp.phonesafe.service.WatchDogService;
import com.myapp.phonesafe.utils.CacheConfigUtils;
import com.myapp.phonesafe.utils.ServiceStateUtil;
import com.myapp.phonesafe.view.SettingItemView;

public class CommonToolsActivity extends Activity {

	// 电话号码归属地查询点击监听
	@OnClick(R.id.tools_number_address_sv)
	public void enterNumberQuery(View v){
		//测试gzip压缩
/*		File srcFile=new File(Environment.getExternalStorageDirectory(), "address.db");
		File zipFile=new File(Environment.getExternalStorageDirectory(), "address.zip");
		try {
			GZipUtils.zip(srcFile, zipFile);
		} catch (IOException e) {
			e.printStackTrace();
		}*/

		//测试解压gzip格式
/*		File zipFile=new File(Environment.getExternalStorageDirectory(), "address.zip");
		File targetFile=new File(Environment.getExternalStorageDirectory(),"myaddress.db");
		try {
			GZipUtils.unzip(zipFile, targetFile);
		} catch (IOException e) {
			e.printStackTrace();
		}*/


		Intent intent=new Intent(getApplicationContext(), CommonAddressActivity.class);
		startActivity(intent);

	}
	// 常用号码查看点击监听
	@OnClick(R.id.tools_common_number_sv)
	public void enterCommonNumber(View v){
		//进入常用号码的查询界面

		Intent intent=new Intent(getApplicationContext(), CommonNumberActivity.class);
		startActivity(intent);

	}
	@OnClick(R.id.tools_applock_sv)
	public void enterAppLock(View v){
		//进入软件锁管理界面 todo
		Intent intent=new Intent(getApplicationContext(), AppLockActivity.class);
		startActivity(intent);
	}

	@ViewInject(R.id.tools_watchdog_sv)
	private SettingItemView mWatchDogSv;//电子狗服务的设置
	private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_tools);
		ViewUtils.inject(this);
		context=this;

		//点击监听事件
		mWatchDogSv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//点击服务开启或者关闭操作
				boolean isWatching = ServiceStateUtil.isRunning(context, WatchDogService.class);
				Intent service=new Intent(context,WatchDogService.class);
				if(isWatching){
					stopService(service);
					mWatchDogSv.setChecked(false);
					//把设置状态持久化
					CacheConfigUtils.putBoolean(context, CacheConfigUtils.IS_WATCH_APP, false);

				}else{
					startService(service);
					mWatchDogSv.setChecked(true);
					CacheConfigUtils.putBoolean(context, CacheConfigUtils.IS_WATCH_APP, true);
				}
			}
		});

	}
	@Override
	protected void onStart() {
		//电子狗服务正在运行
		boolean isWatching = ServiceStateUtil.isRunning(context, WatchDogService.class);
		if(isWatching){
			mWatchDogSv.setChecked(true);
		}else{
			mWatchDogSv.setChecked(false);
		}
		super.onStart();
	}

}
