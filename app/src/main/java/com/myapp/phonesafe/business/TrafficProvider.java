package com.myapp.phonesafe.business;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.myapp.phonesafe.entity.TrafficInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 流量统计的业务类
 *
 */
public class TrafficProvider {
	public static List<TrafficInfo> getTraffics(Context context){
		 List<TrafficInfo> data=new ArrayList<TrafficInfo>();
		
		 PackageManager pm = context.getPackageManager();
		 List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
		 //遍历包
		 for(PackageInfo pInfo:installedPackages){
			 TrafficInfo info=new TrafficInfo();
			 info.name= pInfo.applicationInfo.loadLabel(pm).toString();
			 info.icon= pInfo.applicationInfo.loadIcon(pm);
			 info.packageName=pInfo.packageName;
			 info.uid=pInfo.applicationInfo.uid;//用户的id
			 //取该uid的接收的流量
			 info.rcv=getRcv(info.uid);
			 info.snd=getSnd(info.uid);
//			 TrafficStats.getTotalRxBytes();//系统提供的api
			 if(!(info.rcv==0&&info.snd==0)){
				 data.add(info);
			 }
		 }
		 return data;
		
	}

	private static long getRcv(int uid) {
		File file=new File("/proc/uid_stat/"+uid+"/tcp_rcv");
		BufferedReader reader=null;
		try {
			reader=new BufferedReader(new FileReader(file));
		    String readLine = reader.readLine();
		    if(!TextUtils.isEmpty(readLine)){
		    	return Long.valueOf(readLine);
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(reader!=null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				reader=null;
			}
		}
		
		return 0;
	}
	private static long getSnd(int uid) {
		File file=new File("/proc/uid_stat/"+uid+"/tcp_snd");
		BufferedReader reader=null;
		try {
			reader=new BufferedReader(new FileReader(file));
			String readLine = reader.readLine();
			if(!TextUtils.isEmpty(readLine)){
				return Long.valueOf(readLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(reader!=null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				reader=null;
			}
		}
		
		return 0;
	}

}
