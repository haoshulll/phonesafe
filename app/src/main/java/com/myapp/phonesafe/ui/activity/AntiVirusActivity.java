package com.myapp.phonesafe.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.myapp.phonesafe.R;
import com.myapp.phonesafe.db.VirusDao;
import com.myapp.phonesafe.utils.MD5;

import java.util.ArrayList;
import java.util.List;



public class AntiVirusActivity extends Activity {
	protected static final int MSG_SCANNER = 0;

	private Context context;
	
	private ImageView iv_scanner;
	private TextView tv_scanner;
	private ProgressBar pb_process;
	private LinearLayout ll_container;
	private VirusDao dao;
	private List<String> viruses=new ArrayList<String>();
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			String packageName=(String) msg.obj;
			PackageManager manager = getPackageManager();
			PackageInfo packageInfo;
			try{
				//如果获取到当前程序的签名 PackageManager.GET_SIGNATURES
				packageInfo = manager.getPackageInfo(packageName,
						PackageManager.GET_SIGNATURES);
				Signature[] signatures = packageInfo.signatures;
				String string = signatures[0].toCharsString();
				Log.e(packageInfo.packageName, MD5.getMD5(string));
				boolean isVirus = dao.isVirus(MD5.getMD5(string)); // 查询程序是否是病毒

				String lable = packageInfo.applicationInfo.loadLabel(manager)
						.toString();
				tv_scanner.setText("正在扫描" + lable);
				TextView view = new TextView(getApplicationContext());
				view.setText(lable);

				if (isVirus) {
					view.setTextColor(Color.RED);
					viruses.add(packageName);
				} else {
					view.setTextColor(Color.BLACK);
				}
				ll_container.addView(view, 0); // 添加到最上面
				
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		//12-03 03:10:38.928: E/cn.itcast.switchtoggledemo(2533): 92bd4b64e75910d1f53a2dcaddcc7cc6

		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anti_virus);
		dao = new VirusDao(this);
		context=this;
		//初始化视图
		initView();
		//设置动画
		RotateAnimation rotateAnimation=new RotateAnimation(0,360,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
		rotateAnimation.setDuration(3300);//持续时间
		rotateAnimation.setRepeatCount(Animation.INFINITE);//无限循环
		iv_scanner.startAnimation(rotateAnimation);
		
		//扫描
		scanner();
		
	}
	
	private void scanner() {
		viruses.clear();
		//遍历所有用户程序
		tv_scanner.setText("正在初始化扫描引擎");
		new Thread(){
			public void run() {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				int count=0;
				//包管理器
				PackageManager manager = getPackageManager();
				List<PackageInfo> installedPackages = manager.getInstalledPackages(0);
				pb_process.setMax(installedPackages.size());
				for (PackageInfo info : installedPackages) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String packageName=info.packageName;
					
					Message msg=Message.obtain();
					msg.what=MSG_SCANNER;
					msg.obj=packageName;
					handler.sendMessage(msg);
					count++;
					pb_process.setProgress(count);
					
					
				}
				runOnUiThread(new Runnable(){
					public void run() {
						iv_scanner.clearAnimation();;//移除动画
						if(viruses.size()<1){
							tv_scanner.setText("扫描完成，没有发现病毒！");
						}else{
							tv_scanner.setText("扫描完成！");
							AlertDialog.Builder builder=new AlertDialog.Builder(AntiVirusActivity.this);
							builder.setTitle("发现病毒");
							builder.setMessage("是否卸载病毒");
							builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									for(String packageName:viruses){
										//卸载病毒
										uninstall(packageName);
									}
									
								}} );
							
							
							builder.setNegativeButton("取消", null);
							builder.show();
						}
						
					};
				});
				
				
			};
			
			
		}.start();
		
	}

	//初始化视图
	private void initView() {
		iv_scanner = (ImageView) findViewById(R.id.iv_scanner);
		tv_scanner = (TextView) findViewById(R.id.tv_scanner);
		pb_process = (ProgressBar) findViewById(R.id.pb_process);
		ll_container = (LinearLayout) findViewById(R.id.ll_container);

	}
	//卸载病毒
	private void uninstall(String packageName) {
		// <intent-filter>
		// <action android:name="android.intent.action.VIEW" />
		// <action android:name="android.intent.action.DELETE" />
		// <category android:name="android.intent.category.DEFAULT" />
		// <data android:scheme="package" />
		// </intent-filter>
		Intent intent = new Intent();
		intent.setAction("android.intent.action.DELETE");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("package:" + packageName));
		startActivityForResult(intent, 0);

	}


}
