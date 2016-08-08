package com.myapp.phonesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.myapp.phonesafe.db.BlackDao;
import com.myapp.phonesafe.entity.BlackInfo;
import com.myapp.phonesafe.utils.LogUtil;
import com.myapp.phonesafe.utils.ToastUtil;

import java.lang.reflect.Method;

public class BlackIntercepterService extends Service {
 //拦截短信 ，通过短信接收器来实现，并且在服务创建的时候注册，在服务销毁的时候注销
 // 拦截电话 ，通过一个监听器监听电话状态， 当响铃的时候，则挂断电话
	private static final String TAG = "BlackIntercepter";
	private BlackDao mBlackDao;
	
	private TelephonyManager mTm;//电话管理器

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate() {
		LogUtil.d(TAG, "onCreate");
		super.onCreate();
		mBlackDao=new BlackDao(this);
		IntentFilter filter=new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");//拦截短信的广播事件
		// 短信广播是有优先级
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);//设置最高的优先级
		
		
		mTm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		//通过电话管理器来监听电话(呼叫)的状态
		mTm.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		//注册广播接收器
		registerReceiver(mySmsIntercepReceiver, filter);
	}
	@Override
	public void onDestroy() {
		LogUtil.d(TAG, "onDestroy");
		//注销广播接收器
		unregisterReceiver(mySmsIntercepReceiver);
		//注销电话的监听,不监听任何状态
		mTm.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		super.onDestroy();
	}
	
	//自定义短信接收器
	private BroadcastReceiver mySmsIntercepReceiver =new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// 取得发短信的电话号码
			Object[] objs=(Object[]) intent.getExtras().get("pdus");
			for(Object obj:objs){
				byte[] pdu=(byte[]) obj;
				SmsMessage smsMessage=SmsMessage.createFromPdu(pdu);//创建短信
				//取电话号码
				String number=smsMessage.getOriginatingAddress();//取得电话号码
				System.out.println("号码"+number);
				String newNumber=number;
				// 假如前缀是+86情况
				if(number.startsWith("+86")){
					newNumber=number.substring(3);// +8613865748356
				}
				if(number.startsWith("1555521")){
					newNumber=number.substring(7);// 15555215554
				}
				
				// 与数据库保存的黑名单 比较
				int type = mBlackDao.getType(newNumber);
				if(BlackInfo.TYPE_ALL==type||BlackInfo.TYPE_SMS==type){
					ToastUtil.show(context, "被拦截的电话号码:"+number+" 拦截的类型"+type);
					abortBroadcast();//截断短信,在android 4.4 版本 以后，要把短信的功能全部实现，比如彩信，多媒体短信等，并且优先级最高，才能拦截
				}
				
			}
			
		}
	};
	
	//自定义电话状态的监听器
	private PhoneStateListener myPhoneStateListener =new PhoneStateListener(){
         /**电话状态改变时回调
          * state：电话状态
          * incomingNumber：来电号码
          */
		@Override
		public void onCallStateChanged(int state, final String incomingNumber) {
			//todo
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE://空闲
				
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK://通话
				
				break;
			case TelephonyManager.CALL_STATE_RINGING://响铃
				/**响铃挂断电话：
				 * 1. 比较来电电话是否为黑名单拦截电话或者全部类型的电话 
				 * 2. 挂断电话
				 */
				// 1.  比较来电电话是否为黑名单拦截电话或者全部类型的电话 
				
				String newNumber=incomingNumber;
				// 假如前缀是+86情况
				if(incomingNumber.startsWith("+86")){
					newNumber=incomingNumber.substring(3);// +8613865748356
				}
				if(incomingNumber.startsWith("1555521")){
					newNumber=incomingNumber.substring(7);// 15555215554
				}
				
				//取得拦截的类型 ,从数据库中找不到电话号码，返回的是-1
				int type = mBlackDao.getType(newNumber);
				if(BlackInfo.TYPE_ALL==type||BlackInfo.TYPE_CALL==type){
					/**
					 *  2. 挂断电话  TelephoneManager  
					 *   在 android 10  版本以后， TelephoneManager就不再提供直接挂断电话的方法：endCall()
					 */
				     endTel(incomingNumber);
				     
				     // 删除通话记录  ，通过内容提供者来实现  ,系统通过开辟子线程添加通话记录 ，假如要及时删除通话记录，开辟子线程删除，或者休眠一点时间
				   new Thread(){
					   public void run() {
						   SystemClock.sleep(1000);
						     Uri uri=Uri.parse("content://call_log/calls");
						     getContentResolver().delete(uri, " number=? ", new String[]{incomingNumber});
					   };
					   
				   }.start();
				    
					
				}
				
				break;

			default:
				break;
			}
		}
		
	};
    //挂断电话的方法
	protected void endTel(String incomingNumber) {
		//1. 通过java的反射机制 调用 对象的方法
		try {
			// 1.1  取得类的字节码
		Class<?> clazz = Class.forName("android.os.ServiceManager");
//		clazz.newInstance();//new对象
		/**1.2 通过类的字节码获取方法
		 *  获取类中声明的方法
		 *  name：方法名
		 */
		Method declaredMethod = clazz.getDeclaredMethod("getService", String.class);
		
		// 执行方法  receiver: 执行对象
		IBinder binder=(IBinder) declaredMethod.invoke(null, Context.TELEPHONY_SERVICE);
		
		// 2. 通过AIDL实现ipc的通信，获取ITelephony的代理对象，通过代理对象来挂断电话
//		 ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
		ITelephony iTelephony = ITelephony.Stub.asInterface(binder);
			iTelephony.endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}//挂断电话
		
	}
	
		
	}

