package com.myapp.phonesafe.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.myapp.phonesafe.R;

public class DefindSetup1Activity extends BaseDefindSetupActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_defind_setup1);
	}

	@Override
	public void preActivity() {
		finish();
		
	}
	@Override
	public void nextActivity() {
		Intent intent=new Intent(this, DefindSetup2Activity.class);
		startActivity(intent);
		finish();
		/**Activity切换的平移动画 ： 当调用startActivity或者finish之后执行
		 * enterAnim：进入的动画
		 * exitAnim：退出的动画
		 */
		overridePendingTransition(R.anim.next_enter, R.anim.next_exit);
		
	}

}
