package com.myapp.phonesafe.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**常用号码查询的工具类
 * 
 */
public class CommonNumDao {
    // 取得组的显示的文本
	public static String getGroupText(Context context,int groupPosition) {
		String groupText=null;
		/**
		 * 查询数据库
		 * 数据库的位置： /data/data/cn.itcast.phonesafe/databases/commmonnum.db    
		 */
		String path=context.getFilesDir().getParentFile().getAbsolutePath()+"/databases/commonnum.db";
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		String sql="select name from classlist where idx=?";
		Cursor c = db.rawQuery(sql, new String[]{(groupPosition+1)+""});
		if(c!=null){
			if(c.moveToNext()){
				groupText=c.getString(0);
			}
			c.close();
		}
		db.close();
		return groupText;
		
	}
     //取得组的数量
	public static int getGroupCount(Context context) {
		int count=0;
		String path=context.getFilesDir().getParentFile().getAbsolutePath()+"/databases/commonnum.db";
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		String sql="select  count(*)  from classlist ";
		Cursor c = db.rawQuery(sql,null);
		if(c!=null){
			if(c.moveToNext()){
				count=c.getInt(0);
			}
			c.close();
		}
		db.close();
		return count;
	}
	
	// 取得组的子列表项的数量
	public static int getChildrenCount(Context context, int groupPosition) {
		int count=0;
		String path=context.getFilesDir().getParentFile().getAbsolutePath()+"/databases/commonnum.db";
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		String sql="select  count(*)  from table"+(groupPosition+1);
		Cursor c = db.rawQuery(sql,null);
		if(c!=null){
			if(c.moveToNext()){
				count=c.getInt(0);
			}
			c.close();
		}
		db.close();
		return count;
	}
	
	//取得组的子列表项的电话和名称
	public static String[] getChildrenTexts(Context context, int groupPosition,
			int childPosition) {
		String[] childrenTexts=null;
		String path=context.getFilesDir().getParentFile().getAbsolutePath()+"/databases/commonnum.db";
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		String sql="select number,name from table"+(groupPosition+1)+" where _id=?";
		Cursor c = db.rawQuery(sql, new String[]{(childPosition+1)+""});
		if(c!=null){
			if(c.moveToNext()){
				childrenTexts=new String[2];
				childrenTexts[0]=c.getString(0);
				childrenTexts[1]=c.getString(1);
			}
			c.close();
		}
		db.close();
		return childrenTexts;
	}

}
