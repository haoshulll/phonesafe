package com.myapp.phonesafe.ui.activity;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;

public  abstract class BaseDefindSetupActivity extends Activity {
	
	public void next(View v){
		nextActivity();
	}
	public void pre(View v){
		preActivity();
	}
	//设置一个抽象的方法，由子类去实现
	public abstract void preActivity();
	public abstract void nextActivity();
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){ 
			preActivity();
			return true;
		}
		return super.onKeyDown(keyCode, event);
		
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

}
