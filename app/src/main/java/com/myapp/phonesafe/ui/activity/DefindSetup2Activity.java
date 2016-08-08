package com.myapp.phonesafe.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.myapp.phonesafe.R;
import com.myapp.phonesafe.utils.CacheConfigUtils;
import com.myapp.phonesafe.utils.ToastUtil;


public class DefindSetup2Activity extends BaseDefindSetupActivity {
	private RelativeLayout bindRelativeLayout;
	private ImageView lockImageView;
	private TelephonyManager tm;
	private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_defind_setup2);
		context=this;
		tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		lockImageView=(ImageView) findViewById(R.id.setup_lock_iv);
		String sim= CacheConfigUtils.getString(this, CacheConfigUtils.DEFIND_SIM_BIND);
		//初始化锁的状态
		if(TextUtils.isEmpty(sim)){
			lockImageView.setImageResource(R.drawable.unlock);
		}else{
			lockImageView.setImageResource(R.drawable.lock);
		}
		bindRelativeLayout=(RelativeLayout) findViewById(R.id.setup_bind_rly);
		
		//点击绑定或者解绑sim卡
		bindRelativeLayout.setOnClickListener(new View.OnClickListener() {
			

			@Override
			public void onClick(View v) {
				//取得sim卡序列号
				String sim=CacheConfigUtils.getString(context, CacheConfigUtils.DEFIND_SIM_BIND);
				if(TextUtils.isEmpty(sim)){
					//绑定sim卡
					lockImageView.setImageResource(R.drawable.lock);
					String simSerialNumber = tm.getSimSerialNumber();
					CacheConfigUtils.putString(context, CacheConfigUtils.DEFIND_SIM_BIND, simSerialNumber);
					ToastUtil.show(context, simSerialNumber);
				}else{
					//解绑sim卡
					CacheConfigUtils.putString(context, CacheConfigUtils.DEFIND_SIM_BIND, "");
					lockImageView.setImageResource(R.drawable.unlock);
				}
				
			}
		});
	}
	@Override
	public void preActivity() {
		Intent intent=new Intent(this, DefindSetup1Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.pre_enter, R.anim.pre_exit);
		
	}
	@Override
	public void nextActivity() {
		String sim=CacheConfigUtils.getString(this, CacheConfigUtils.DEFIND_SIM_BIND);
		//初始化锁的状态
		if(TextUtils.isEmpty(sim)){
			ToastUtil.show(context, "需要绑定SIM卡");
			return;
		}
		
		Intent intent=new Intent(this, DefindSetup3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.next_enter, R.anim.next_exit);
	}

}
