package com.myapp.phonesafe.ui.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.myapp.phonesafe.R;
import com.myapp.phonesafe.receiver.MyDeviceAdminReceiver;
import com.myapp.phonesafe.utils.ToastUtil;

public class DefindSetup4Activity extends BaseDefindSetupActivity {

	private static final int REQUEST_CODE_ENABLE_ADMIN = 6;//请求激活设备管理对象的请求码
	private DevicePolicyManager mDpm;//设备方案管理对象
	private ComponentName who;// 组件

	@OnClick(R.id.setup_admin_rly)
	public void activiteAdmin(View v){
		// to do  : 激活或者取消设置管理者 对象
		if(mDpm.isAdminActive(who)){
			mDpm.resetPassword("", 0);
			mDpm.removeActiveAdmin(who);//移除激活的设备管理对象
			//设置图片
			mAmdinImageView.setImageResource(R.drawable.admin_inactivated);
		}else{
			//激活设备管理对象
			Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, who);
			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
					"手机安全卫士");
			startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);

//			mAmdinImageView.setImageResource(R.drawable.admin_activated);
		}

	}
	@ViewInject(R.id.setup_admin_iv)
	private ImageView mAmdinImageView;//显示的是否激活的图标
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_defind_setup4);
		ViewUtils.inject(this);

		mDpm=(DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		who=new ComponentName(this, MyDeviceAdminReceiver.class);
		//初始化激活设置管理 状态
		if(mDpm.isAdminActive(who)){
			//设置图片
			mAmdinImageView.setImageResource(R.drawable.admin_activated);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(REQUEST_CODE_ENABLE_ADMIN==requestCode){
			if(RESULT_OK==resultCode){
				mAmdinImageView.setImageResource(R.drawable.admin_activated);
			}
		}
	}
	@Override
	public void preActivity() {
		Intent intent=new Intent(this, DefindSetup3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.pre_enter, R.anim.pre_exit);

	}
	@Override
	public void nextActivity() {
		if(!mDpm.isAdminActive(who)){//没有激活设备管理对象，则不能进入下一个设置
			ToastUtil.show(this, "只有激活设置管理者,才能保护您的手机");
			return;
		}
		Intent intent=new Intent(this, DefindSetup5Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.next_enter, R.anim.next_exit);

	}

}