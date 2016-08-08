package com.myapp.phonesafe.db;
/**与appLockdb数据库关联的常量等
 * 
 */
public interface AppLockDB {
	String DB_NAME="appLock.db";//数据库的名称
	int VERSION=1;//版本号
	// 针对t_black 表的常量的描述
	public interface AppLock{
		String COLUMN_ID="_id";
		String COLUMN_PACKAGE_NAME="packagname";//包名
		String TABLE_NAME="t_applock";//表名
		String SQL_CREATE_TABLE="create table "+TABLE_NAME+"("+COLUMN_ID+" integer primary key autoincrement,"
				+COLUMN_PACKAGE_NAME+" text unique)";
	}

}
