package com.myapp.phonesafe.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

public class VirusDao {
	// private XXXOpenHelper helper;
	private Context context;

	public VirusDao(Context context) {
		// helper = new XXXOpenHelper(context);
		this.context = context;
	}
	/**
	 * @param md5
	 * @return
	 */
	public boolean isVirus(String md5){
		boolean flag=false;
		File file = new File(context.getFilesDir(), "antivirus.db");
		SQLiteDatabase sql = SQLiteDatabase.openDatabase(
				file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
		Cursor query = sql.query("datable", new String[]{"desc"}, "md5=?", new String[]{md5}, null, null, null);
		if(query.moveToNext()){
			flag=true;
			
		}
		query.close();
		sql.close();
		return flag;
		
	}
}
