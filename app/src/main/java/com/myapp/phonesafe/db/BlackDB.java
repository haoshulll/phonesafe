package com.myapp.phonesafe.db;
/**与blackdb数据库关联的常量等
 * 
 */
public interface BlackDB {
	String DB_NAME="black.db";//数据库的名称
	int VERSION=1;//版本号
	// 针对t_black 表的常量的描述
	public interface BlackList{
		String COLUMN_ID="_id";
		String COLUMN_NUMBER="number";
		String COLUMN_TYPE="type";
		String TABLE_NAME="t_black";
		String SQL_CREATE_TABLE="create table "+TABLE_NAME+"("+COLUMN_ID+" integer primary key autoincrement,"
				+COLUMN_NUMBER+" text unique,"+COLUMN_TYPE+" integer)";
	}

}
