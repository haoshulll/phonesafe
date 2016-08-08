package com.myapp.phonesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.phonesafe.R;
import com.myapp.phonesafe.db.AddressDao;
import com.myapp.phonesafe.utils.CacheConfigUtils;
import com.myapp.phonesafe.utils.Constants;
import com.myapp.phonesafe.utils.LogUtil;

public class AddressShowService extends Service {

	private static final String TAG = "AddressShowService";
	private TelephonyManager mTm;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		LogUtil.d(TAG, "onCreate");
		mTm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		//监听电话的状态
		mTm.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);//监听通话状态
		
		if(Constants.IS_CALL_ADDRESS){
			// 2. 呼叫显示号码归属地  依据配置情况决定是否显示呼叫的号码归属地显示  
			// 通过一个广播接收器 ，接受对外拨号的事件，来显示号码归属地
			IntentFilter filter=new IntentFilter();
			//订阅广播事件  ,对外呼叫的广播事件
			filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
			registerReceiver(receiver, filter);
		}

	}
	@Override
	public void onDestroy() {
		LogUtil.d(TAG, "onDestroy");
		mTm.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		if(Constants.IS_CALL_ADDRESS){
			unregisterReceiver(receiver);
		}
		
		super.onDestroy();
	}
	
	// 监听电话状态
	private PhoneStateListener myPhoneStateListener =new PhoneStateListener(){

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE://空闲状态
				
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK://通话状态
				
				break;
			case TelephonyManager.CALL_STATE_RINGING://响铃状态
				// 通过Toast来显示
				showToast(incomingNumber);
				
				break;

			default:
				break;
			}
		}
	};
    // 通过Toast 显示号码归属地
	protected void showToast(String incomingNumber) {
		//自定义Toast
		Toast toast=new Toast(this);
		View view = View.inflate(this, R.layout.layout_address_show, null);
		TextView addressTv=(TextView) view.findViewById(R.id.address_tv);
		String findAddress = AddressDao.findAddress(this, incomingNumber);
		addressTv.setText(findAddress);
		toast.setView(view);
		// 设置toast背景样式
		int style = CacheConfigUtils.getInt(this, CacheConfigUtils.ADDRESS_STYLE);
		view.setBackgroundResource(styleImageRes[style]);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.show();
	}
	// 接受对外呼叫的广播事件
	private BroadcastReceiver receiver=new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// 获取到呼叫的电话号码
			String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			
			showToast(number);
		}
	};
	private int[] styleImageRes = { R.drawable.address_style_nomal,
			R.drawable.address_style_orange, R.drawable.address_style_blue,
			R.drawable.address_style_gray, R.drawable.address_style_green };

}
