package com.myapp.phonesafe.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**软件锁数据库的工具类
 * 
 */
public class AppLockDao {
	private AppLockDBHelper mHelper;//数据库的辅助类，通过它可以获取SqliteDatabase 类
	public AppLockDao(Context context){
		mHelper=new AppLockDBHelper(context);
	}
	// 添加加锁的应用
	public boolean  addLockedApp(String packageName){
		SQLiteDatabase db=mHelper.getWritableDatabase();
	   ContentValues values=new ContentValues();
	   values.put(AppLockDB.AppLock.COLUMN_PACKAGE_NAME, packageName);
		long insert = db.insert(AppLockDB.AppLock.TABLE_NAME, null, values);
		db.close();
		return insert!=-1;
	}
	// 移除操作 ：解锁： 移除已加锁的应用 
	public boolean removeApp(String packageName){
		SQLiteDatabase db=mHelper.getWritableDatabase();
		String whereClause=AppLockDB.AppLock.COLUMN_PACKAGE_NAME+"=?";
		String[] whereArgs={packageName};
		int delete = db.delete(AppLockDB.AppLock.TABLE_NAME, whereClause, whereArgs);
		db.close();
		return delete==1;
	
	}
	
	
	//查询某个包是否加锁
	public boolean isLock(String packageName){
		boolean isLock=false;
		SQLiteDatabase db=mHelper.getReadableDatabase();
		Cursor c = db.query(AppLockDB.AppLock.TABLE_NAME, null, 
				AppLockDB.AppLock.COLUMN_PACKAGE_NAME+"=?", new String[]{packageName}, null, null, null);
		if(c!=null){
			if(c.moveToNext()){
				isLock=true;
				c.close();
			}
		}
		db.close();
		return isLock;
	}
	
	// 查询所有已经加锁的包或者应用
	public List<String> findAllLockApp(){
		List<String> data=null;
		SQLiteDatabase db=mHelper.getReadableDatabase();
		Cursor c = db.query(AppLockDB.AppLock.TABLE_NAME, new String[]{AppLockDB.AppLock.COLUMN_PACKAGE_NAME}, 
				null, null, null, null, null);
		if(c!=null){
			data=new ArrayList<String>();
			while(c.moveToNext()){
				String packageName=c.getString(0);
				data.add(packageName);
			}
			
		}
		c.close();
		db.close();
		return data;
		
	}
	
	
}
