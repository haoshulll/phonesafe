package com.myapp.phonesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 缓存配置的数据的工具类 用SharedPreferences缓存
 * 
 * 
 */
public class CacheConfigUtils {
	private static final String CONFIG = "config";// SharedPreferences关联的xml文件，config.xml
	public static final String IS_UPDATE_VERSION = "is_update_version";// 是否要自动更新apk版本的key
	public static final String DEFIND_PASSWORD = "defind_password";// 手机访问的密码的key
	public static final String DEFIND_IS_SETUP="defind_is_setup";//手机防盗是否设置
	public static final String DEFIND_SIM_BIND = "defind_sim_bind";//sim卡是否绑定
	public static final String DEFIND_IS_PROTECED = "defind_is_proteced";//是否开启手机防护
	public static final String DEFIND_SAFE_NUMBER = "defind_safe_number";//手机防护的安全号码
	public static final String IS_BLACK_INTERCEPT = "is_black_intercept";//拦截黑名单的设置
	public static final String SHOW_ADDRESS = "show_address";//是否显示归属地
	public static final String ADDRESS_STYLE = "address_style";//号码归属地显示的样式
	public static final String SHOW_SYSTEM_PROCESS = "show_system_process";//是否显示系统进程
	public static final String IS_AUTO_CLEAR_PROCESS = "is_auto_clear_process";//是否自动清除进程： 锁屏清除进程
	public static final String IS_WATCH_APP = "is_watch_app";//是否开启电子狗服务
	
	private static SharedPreferences mSp;

	private static SharedPreferences getPreference(Context context) {
		if (mSp == null) {
			mSp = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		}
		return mSp;

	}

	// 存布尔数据
	public static void putBoolean(Context context, String key, boolean value) {
		SharedPreferences sp = getPreference(context);
		sp.edit().putBoolean(key, value).commit();
	}

	// 取布尔数据,默认是false
	public static boolean getBoolean(Context context, String key) {
		SharedPreferences sp = getPreference(context);
		return sp.getBoolean(key, false);
	}

	// 取布尔数据,可以设置缺省值
	public static boolean getBoolean(Context context, String key,
			boolean defvalue) {
		SharedPreferences sp = getPreference(context);
		return sp.getBoolean(key, defvalue);
	}

	// 存字符串数据
	public static void putString(Context context, String key, String value) {
		SharedPreferences sp = getPreference(context);
		sp.edit().putString(key, value).commit();
	}

	// 取字符串数据,默认是null
	public static String getString(Context context, String key) {
		SharedPreferences sp = getPreference(context);
		return sp.getString(key, null);
	}

	// 取字符串数据,可以设置缺省值
	public static String getString(Context context, String key,
			String defvalue) {
		SharedPreferences sp = getPreference(context);
		return sp.getString(key, defvalue);
	}
	
	
	
	// 存字整数 
	public static void putInt(Context context, String key, int value) {
		SharedPreferences sp = getPreference(context);
		sp.edit().putInt(key, value).commit();
	}
	
	// 取整数,默认是0
	public static int getInt(Context context, String key) {
		SharedPreferences sp = getPreference(context);
		return sp.getInt(key, 0);
	}
	
	// 取整数,可以设置缺省值
	public static int getInt(Context context, String key,
			int defvalue) {
		SharedPreferences sp = getPreference(context);
		return sp.getInt(key, defvalue);
	}

}
