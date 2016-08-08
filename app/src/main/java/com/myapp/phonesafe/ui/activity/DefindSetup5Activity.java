package com.myapp.phonesafe.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnCompoundButtonCheckedChange;
import com.myapp.phonesafe.R;
import com.myapp.phonesafe.utils.CacheConfigUtils;
import com.myapp.phonesafe.utils.ToastUtil;


public class DefindSetup5Activity extends BaseDefindSetupActivity {
	@ViewInject(R.id.setup_defind_chb)
	private CheckBox defindCheckBox;


	//通过ViewUtils 设置 事件绑定  ,状态改变的监听
	@OnCompoundButtonCheckedChange(R.id.setup_defind_chb)
	public void checkChange(CompoundButton buttonView, boolean isChecked){
//		   ToastUtil.show(getApplicationContext(), isChecked?"保护":"不保护");
		CacheConfigUtils.putBoolean(DefindSetup5Activity.this, CacheConfigUtils.DEFIND_IS_PROTECED, isChecked);
	}


	//绑定点击监听
	@OnClick(R.id.setup_defind_chb)
	public void testClic(View v){
		ToastUtil.show(getApplicationContext(), "点击");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_defind_setup5);
		ViewUtils.inject(this);
//		defindCheckBox=(CheckBox) findViewById(R.id.setup_defind_chb);
		// 初始化复选框
		boolean flag=CacheConfigUtils.getBoolean(this, CacheConfigUtils.DEFIND_IS_PROTECED);
		if(flag){
			defindCheckBox.setChecked(true);
		}

		//设置复选框的状态改变的监听
//		defindCheckBox.setOnCheckedChangeListener(null);

	}
	@Override
	public void preActivity() {
		Intent intent=new Intent(this, DefindSetup4Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.pre_enter, R.anim.pre_exit);
	}
	//设置完成
	@Override
	public void nextActivity() {
		//设置完成，必须要 开启保护
		boolean flag=CacheConfigUtils.getBoolean(this, CacheConfigUtils.DEFIND_IS_PROTECED);
		if(!flag){
			ToastUtil.show(getApplicationContext(), "必须开启手机保护,才能设置完成");
			return;
		}

		//设置完成， 进入到手机防护状态显示界面
		CacheConfigUtils.putBoolean(this, CacheConfigUtils.DEFIND_IS_SETUP, true);
		Intent intent=new Intent();
		intent.setClass(this, DefindActivity.class);
		startActivity(intent);
		finish();
	}

}