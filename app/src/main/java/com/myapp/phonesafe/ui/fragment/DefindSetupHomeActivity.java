package com.myapp.phonesafe.ui.fragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.lidroid.xutils.ViewUtils;
import com.myapp.phonesafe.R;

//向下支持到V4  

//public class DefindSetupHomeActivity extends FragmentActivity {
	public class DefindSetupHomeActivity extends Activity {


	private FragmentManager mFragmentManager;//片段管理器
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_defind_setup_home);
		ViewUtils.inject(this);
		
		mFragmentManager = getFragmentManager();
		
		//取得向下支持的片段的管理器
//		getSupportFragmentManager();//
		FragmentTransaction bt = mFragmentManager.beginTransaction();
		Setup1Fragment setup1Fragment=new Setup1Fragment();
		bt.add(R.id.content, setup1Fragment).commit();
		
	}

}

