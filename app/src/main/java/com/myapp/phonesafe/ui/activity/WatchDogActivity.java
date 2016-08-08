package com.myapp.phonesafe.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapp.phonesafe.R;
import com.myapp.phonesafe.service.WatchDogService;
import com.myapp.phonesafe.utils.CacheConfigUtils;
import com.myapp.phonesafe.utils.MD5;
import com.myapp.phonesafe.utils.ToastUtil;


public class WatchDogActivity extends Activity {
	private EditText pwdEt;
	private Context context;
	private String packageName;
	private int taskId;
	
	private ImageView mIconIv;
	private TextView mNameTv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_watch_dog);
		packageName = getIntent().getStringExtra(WatchDogService.PACKNAME);
		taskId = getIntent().getIntExtra(WatchDogService.TASK_ID, 0);
		context=this;
		pwdEt = (EditText) findViewById(R.id.watchdog_authrity_et);
		mIconIv=(ImageView)findViewById(R.id.watchdog_icon_iv);
		mNameTv=(TextView)findViewById(R.id.watchdog_name_tv);
	}
	public void authority(View v) {
		String pwd = pwdEt.getText().toString().trim();
		//保存的密码
		
		String password= CacheConfigUtils.getString(context,CacheConfigUtils.DEFIND_PASSWORD).trim();
		Log.e("pppppppppppppp",password);
		if(TextUtils.isEmpty(password)){
			ToastUtil.show(context, "密码为空");
		}
		if (password.equals(MD5.getMD5(pwd))) {
			//临时解锁 ，把解锁的包和任务id告诉服务
			Intent intent=new Intent();
			intent.setAction("cn.itcast.phonesafe.UNLOCK");//发临时解锁的广播
			intent.putExtra(WatchDogService.PACKNAME, packageName);
			intent.putExtra(WatchDogService.TASK_ID, taskId);
			
           sendBroadcast(intent);
			finish();

		}else{
			ToastUtil.show(context, "验证不正确");
			pwdEt.setText(null);
			pwdEt.requestFocus();
		}
	}
	
	@Override
	protected void onStart() {
		//依据包名获取应用程序的图标，应用程序的名称等
//	    ActivityManager am=(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = getPackageManager();
		try {
			PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
			Drawable icon=packageInfo.applicationInfo.loadIcon(pm);//取得要解锁的包的图标
			String name=packageInfo.applicationInfo.loadLabel(pm).toString();//取得要解锁的包的应用名称
			mIconIv.setImageDrawable(icon);
			mNameTv.setText(name);
			
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	    
		super.onStart();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){//按下返回键
		//进入到Home界面
		Intent intent=new Intent();
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);//要访问的Activity组件的类别 
		startActivity(intent);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	

}
