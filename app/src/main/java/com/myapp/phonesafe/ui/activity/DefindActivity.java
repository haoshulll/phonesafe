package com.myapp.phonesafe.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.myapp.phonesafe.R;
import com.myapp.phonesafe.utils.CacheConfigUtils;


public class DefindActivity extends Activity {
	@ViewInject(R.id.defind_number_tv)
	private TextView numberTextView;
	
	@ViewInject(R.id.defind_lock_iv)
	private ImageView lockImageView;

	//绑定点击监听事件
	@OnClick(R.id.defind_resetup_rly)
	public void resetup(View v){
		//从新进入到设置界面
		Intent intent=new Intent(this, DefindSetup1Activity.class);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_defind);
		ViewUtils.inject(this);
		
		//显示设置了的安全号码
		String safeNumber= CacheConfigUtils.getString(this, CacheConfigUtils.DEFIND_SAFE_NUMBER);
		numberTextView.setText(safeNumber);
		
		//是否开启了手机防护
		boolean isProtected=CacheConfigUtils.getBoolean(this, CacheConfigUtils.DEFIND_IS_PROTECED);
		if(isProtected){
			lockImageView.setImageResource(R.drawable.lock);
		}else{
			lockImageView.setImageResource(R.drawable.unlock);
		}
	}


}
