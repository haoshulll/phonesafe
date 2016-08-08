package com.myapp.phonesafe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**gzip压缩与解压工具列
 * 
 */
public class GZipUtils {
	/**压缩文件为gzip格式
	 * srcFile: 要压缩的文件
	 * zipfile:压缩后的文件
	 * @throws IOException 
	 */
	public static void zip(File srcFile,File zipFile) throws IOException{
		FileInputStream fis=null;
		GZIPOutputStream gos=null;
		try{
		//输入流，读文件
		 fis=new FileInputStream(srcFile);
		//输出流，写文件
		FileOutputStream fos=new FileOutputStream(zipFile);
		// gzip压缩格式的输出流
	      gos=new GZIPOutputStream(fos);
	     byte[] buffer =new byte[1024];
	     int len=-1;
	     while((len=fis.read(buffer))!=-1){//读到文件的尾部
	    	 gos.write(buffer, 0, len);
	     }
		}finally{
			//关闭流
			gos.close();
			fis.close();
		}
	}
	/**解压gzip格式的文件
	 * srcFile: 要压解压的gzip文件
	 * targetfile:压缩后的文件
	 * @throws IOException 
	 */
	public static void unzip(File zipFile,File targetFile) throws IOException{
		FileOutputStream fos=null;
		GZIPInputStream gis=null;
		try{
			//输入流，读gzip格式的文件
			gis=new GZIPInputStream(new FileInputStream(zipFile));
			//输出流，写文件
			 fos=new FileOutputStream(targetFile);
			byte[] buffer =new byte[1024];
			int len=-1;
			while((len=gis.read(buffer))!=-1){//读到文件的尾部
				fos.write(buffer, 0, len);
			}
		}finally{
			//关闭流
			fos.close();
			gis.close();
		}
	}
	/**解压gzip格式的文件 
	 * is: 要压解压的gzip文件的输入流
	 * os:压缩后的文件的输出流
	 * @throws IOException 
	 */
	public static void unzip(InputStream is,OutputStream os) throws IOException{
		GZIPInputStream gis=null;
		try{
			//输入流，读gzip格式的文件
			gis=new GZIPInputStream(is);
			byte[] buffer =new byte[1024];
			int len=-1;
			while((len=gis.read(buffer))!=-1){//读到文件的尾部
				os.write(buffer, 0, len);
			}
		}finally{
			//关闭流
			os.close();
			gis.close();
		}
	}

}
