package com.myapp.phonesafe.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.myapp.phonesafe.utils.LogUtil;

public class BlackDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "BlackDBHelper";

	/**通过构造函数来创建或者打开数据库
     * 
     * @param context
     */
	public BlackDBHelper(Context context) {
		super(context, BlackDB.DB_NAME, null, BlackDB.VERSION);
	}
    //当数据库初始化时调用，即数据库创建的时候调用，一般做创建表的操作
	@Override
	public void onCreate(SQLiteDatabase db) {
      db.execSQL(BlackDB.BlackList.SQL_CREATE_TABLE);
      LogUtil.d(TAG, BlackDB.BlackList.SQL_CREATE_TABLE);
	}
    //版本更新时调用
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		 LogUtil.d(TAG, "onUpgrade");
	}

}
