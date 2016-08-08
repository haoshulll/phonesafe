package com.myapp.phonesafe.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import com.myapp.phonesafe.utils.CacheConfigUtils;

// 接受系统引导成功的接收器
public class BootCompleteReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		// 1.取得保存的sim卡的序列号
		String storeSimNumber= CacheConfigUtils.getString(context, CacheConfigUtils.DEFIND_SIM_BIND);
		//2. 取得当前的sim卡的序列号
		TelephonyManager tm=(TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String simSerialNumber = tm.getSimSerialNumber();
		if(!storeSimNumber.equals(simSerialNumber)){//假如sim卡有变动，则发短信给安全号码
			String safeNum=CacheConfigUtils.getString(context, CacheConfigUtils.DEFIND_SAFE_NUMBER);
			
			// 发短信： 取得短信管理器
			SmsManager smsManager=SmsManager.getDefault(); 
			//deliveryIntent: 分销意图
			smsManager.sendTextMessage(safeNum, null, "Boss,I lost,Help me!", null	, null);
			//发短信要钱，要权限
			
		}

	}

}
