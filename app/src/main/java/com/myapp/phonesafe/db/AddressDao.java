package com.myapp.phonesafe.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

/**
 * 手机号码归属地查询的工具类
 */
public class AddressDao {
	public static String findAddress(Context context,String number){
		String address=null;
		// SqliteOpenHelper 打开数据库
		/**打开数据库
		 * path：要打开的数据库的路径
		 * factory：游标工厂
		 * flags： 模式，只读，还是写
		 */
		String path=context.getFilesDir().getAbsolutePath()+"/address.db";
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
//		context.openOrCreateDatabase(name, mode, factory)// 
		/**号码的类型：
		 *  1. 手机号码  11位   通过正则表达式来描述手机号码：13660559129
		 *   手机号码的正则表达式  match： ^1[34578]\\d{9}$   
		 *  2. 3位号码 ;110  120 119 等 ： 特殊号码 
		 *  3. 4位号码： 虚拟号码   5556  5554 等
		 *  4： 5位号码： 10086  10000  ， 95553 等
		 *  5. 7位号码、8位号码： 本地号码   ： 6345322  、 32326878 
		 *  6： 10位、11位、12位 ： 长途号码  02012345678  0294587234  075565567892 
		 *     开始的字符是以 0开头
		 *  7： 其他情况：
		 *     未知号码
		 * 
		 */
		//针对手机号码
		boolean isPhone = number.matches("^1[34578]\\d{9}$");
		if(isPhone){//手机号码
			String prefix=number.substring(0, 7);//截取前7位 
			String sql="select cardtype from info where mobileprefix=?";
			Cursor c = db.rawQuery(sql, new String[]{prefix});
			if(c!=null){
				if(c.moveToNext()){
					address=c.getString(0);
				}
				c.close();
			}
		}else{//不是手机号码
			int length=number.length();
			switch (length) {
			case 3:
				address="紧急号码";
				break;
			case 4:
				address="虚拟号码";
				break;
			case 5:
				address="服务号码";
				break;
			case 7:
			case 8:
				address="本地号码";
				break;
			case 10:
			case 11:
			case 12:
				if(number.startsWith("0")){//长途号码必须以0开始
					String area=number.substring(0, 3);// 区号是3位的    020-23453457
					String sql="select city from info where area=?";
					Cursor c = db.rawQuery(sql, new String[]{area});
					if(c!=null){
						if(c.moveToNext()){
							address=c.getString(0);// 获取长途号码的归属地
						}
						c.close();
					}
					
					// 
					if(TextUtils.isEmpty(address)){ 
						 area=number.substring(0,4);// 区号是4位的    0755-23453457
						 c=db.rawQuery(sql, new String[]{area});
						 if(c!=null){
								if(c.moveToNext()){
									address=c.getString(0);// 获取长途号码的归属地
								}
								c.close();
							}
						 
					}
					
					if(TextUtils.isEmpty(address)){
						address="未知号码";
					}
					
					
				}else{
					address="未知号码";
				}
				break;

			default:
				address="未知号码";
				break;
			}
			
		}
		
		db.close();//关闭数据库
		return address;
	}

}
