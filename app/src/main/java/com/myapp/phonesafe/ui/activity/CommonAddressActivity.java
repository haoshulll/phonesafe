package com.myapp.phonesafe.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.myapp.phonesafe.R;
import com.myapp.phonesafe.db.AddressDao;


public class CommonAddressActivity extends Activity {
	@ViewInject(R.id.tools_address_number_et)
	private EditText mNumberEt;
	
	@ViewInject(R.id.tools_number_tv)
	private TextView mAddressTv;

	@OnClick(R.id.tools_address_query_btn)
	public void queryAddress(View v){
		String number = mNumberEt.getText().toString().trim();
		String findAddress = AddressDao.findAddress(this, number);
		mAddressTv.setText(findAddress);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_address);
		ViewUtils.inject(this);
		
		// 对文本编辑框添加文本改变的监听 
		mNumberEt.addTextChangedListener(new TextWatcher() {
			// 文本改变时回调
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				System.out.println("onTextChanged:"+s.toString());
				//取得改变的文本
				String changeText=s.toString();
				//查询数据库
				String findAddress = AddressDao.findAddress(getApplicationContext(), changeText);
				mAddressTv.setText(findAddress);
			}
			//文本改变之前
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				System.out.println("beforeTextChanged");
				
			}
			//文本改变之后
			@Override
			public void afterTextChanged(Editable s) {
				System.out.println("afterTextChanged");
				
			}
		});
	}


}
