package com.myapp.phonesafe.receiver;


import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import com.myapp.phonesafe.R;
import com.myapp.phonesafe.service.GPSService;

public class SmsReceiver extends BroadcastReceiver {

	private DevicePolicyManager mdpm;//设备方案管理对象
	private ComponentName who;// 组件 

	@Override
	public void onReceive(Context context, Intent intent) {
		mdpm = (DevicePolicyManager)
				context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		who = new ComponentName(context, MyDeviceAdminReceiver.class);
		//获取传递过来的数据
		Object[] objs=(Object[]) intent.getExtras().get("pdus");//传输数据单元，国际标准 ：  70多个汉字 
		//遍历数组
		for(Object obj:objs){
			byte[] pdu=(byte[]) obj;
			//创建短信
			SmsMessage smsMessage=SmsMessage.createFromPdu(pdu);
			//取得发送者的电话号码
//			smsMessage.getDisplayOriginatingAddress();
			String number = smsMessage.getOriginatingAddress();
			//取得短信内容
			String body=smsMessage.getMessageBody();
			System.out.println("电话号码"+number+" body:"+body);
			if(!TextUtils.isEmpty(body)){  
				//依据短信指令执行相应的防盗行为，并且该条短信不能被系统的短信接收器接收到 
				if("#*location*#".equals(body)){//位置跟踪
					//todo
					//通过服务来不断获取被盗手机的位置信息
					Intent service=new Intent();
					service.setClass(context, GPSService.class);
			         context.startService(service);
					abortBroadcast();//拦截广播事件，截断广播事件
				}else if("#*alarm*#".equals(body)){//播放报警音乐
					MediaPlayer mediaPlayer=MediaPlayer.create(context, R.raw.guoge);
					mediaPlayer.start();
					abortBroadcast();//拦截广播事件，截断广播事件
				} else if("#*wipedata*#".equals(body)){//远程擦除数据
					
					if(mdpm.isAdminActive(who)){
						mdpm.wipeData(0);//擦除数据
					}
					abortBroadcast();//拦截广播事件，截断广播事件
					
				}else if("#*lockscreen*#".equals(body)){//远程锁屏

					if(mdpm.isAdminActive(who)){
						//重置密码
						mdpm.resetPassword("123", 0);
						mdpm.lockNow();//锁屏
					}

					abortBroadcast();//拦截广播事件，截断广播事件
				}
			}
			
			
			
		}
    
	}

}
