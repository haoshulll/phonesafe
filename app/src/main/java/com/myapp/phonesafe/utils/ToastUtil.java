package com.myapp.phonesafe.utils;

import android.content.Context;
import android.widget.Toast;

/**吐司工具类
 * 
 *
 */
public class ToastUtil {
	public static void show(Context context,String text){
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

}
