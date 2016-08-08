package com.myapp.phonesafe.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.myapp.phonesafe.R;
import com.myapp.phonesafe.utils.CacheConfigUtils;
import com.myapp.phonesafe.utils.ToastUtil;


public class DefindSetup3Activity extends BaseDefindSetupActivity {
	private static final int CONTACT_REQUEST_CODE = 1;//获取联系人的请求码
	//注解式声明， 描述该变量是通过注入的方式来赋值
	@ViewInject(R.id.setup_safe_number_et)
	private EditText safeNumberEditText;//手机安全号码的编辑框
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_defind_setup3);
		//注入Activity关联的View对象
		ViewUtils.inject(this);
//		ViewUtils.inject(view);
//		ViewUtils.inject(handler, view);//handler：处理的对象 ， view 要注入的所有的View的控件对象
//		ViewUtils.inject(fragment, view);//在片段中使用

//		safeNumberEditText=(EditText) findViewById(R.id.setup_safe_number_et);

		String safeNumber=CacheConfigUtils.getString(this, CacheConfigUtils.DEFIND_SAFE_NUMBER);
		safeNumberEditText.setText(safeNumber);//假如为空，会显示hint信息

	}
	@Override
	public void preActivity() {
		Intent intent=new Intent(this, DefindSetup2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.pre_enter, R.anim.pre_exit);

	}
	@Override
	public void nextActivity() {
		String safeNumber=safeNumberEditText.getText().toString().trim();
		if(TextUtils.isEmpty(safeNumber)){
			ToastUtil.show(this, "安全号码必须绑定");
			return;
		}

		//保存安全号码到配置文件中 config.xml
		CacheConfigUtils.putString(this, CacheConfigUtils.DEFIND_SAFE_NUMBER, safeNumber);
		Intent intent=new Intent(this, DefindSetup4Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.next_enter, R.anim.next_exit);

	}
	//点击获取联系人按钮
	public void selectContact(View v){
		Intent intent=new Intent();
//		intent.setClass(this, ContactActivity.class);

		//通过游标来展示联系人的数据
		intent.setClass(this, ContactCursorActivity.class);
		startActivityForResult(intent, CONTACT_REQUEST_CODE);
	}
	// 取得被调用者返回的数据
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(CONTACT_REQUEST_CODE==requestCode&&RESULT_OK==resultCode){
			//todo
			safeNumberEditText.setText(data.getStringExtra("tel"));

		}
	}

}
