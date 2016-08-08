package com.myapp.phonesafe.business;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.myapp.phonesafe.entity.AppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**获取应用软件信息的业务类
 * 
 */
public class AppInfoProvider {
	public static List<AppInfo> getAppInfos(Context context){
		List<AppInfo> data=new ArrayList<AppInfo>();
		//取得包管理器
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
		//遍历安装到系统的包
		for(PackageInfo packageInfo:installedPackages){
			AppInfo appInfo=new AppInfo();
			appInfo.pacakgeName=packageInfo.packageName;//包名
//			packageInfo.versionName;//版本名称
//			packageInfo.versionCode;//版本号
			
			//取得应用节点的信息
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;
			appInfo.name=applicationInfo.loadLabel(pm).toString();//取得应用的名称
			appInfo.icon=applicationInfo.loadIcon(pm);//取得应用的图标
			
			//应用的apk的大小  ,apk的位置   ：/data/data/cn.itcast.phonesafe/cn.cn.itcast.phonesafe-1.apk
			String sourceDir = applicationInfo.sourceDir;
			appInfo.size=new File(sourceDir).length();
			//应用信息的状态值-标记
			int flags = applicationInfo.flags;
			if((flags&ApplicationInfo.FLAG_SYSTEM)==ApplicationInfo.FLAG_SYSTEM){
				//表示当前程序为系统程序
				appInfo.isSystem=true;
			}
			if((flags&ApplicationInfo.FLAG_EXTERNAL_STORAGE)==ApplicationInfo.FLAG_EXTERNAL_STORAGE){
				//表示当前程序为系统程序
				appInfo.isInstallSdcard=true;
			}
			data.add(appInfo);
			System.out.println(appInfo.toString());
		}
		return data;
	}

}
