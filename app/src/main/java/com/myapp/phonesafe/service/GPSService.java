package com.myapp.phonesafe.service;

import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class GPSService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate() {
		//Gps跟踪位置 
		//1. 获取本手机中所有的定位方式  ,取得定位服务
		LocationManager lm=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
		List<String> allProviders = lm.getAllProviders();
		for(String provider:allProviders){
			System.out.println("定位方式:"+provider);
		}
		
		/**
		 * 选择最佳的定位方式
		 * criteria: 条件设置
		 * enabledOnly:是否只有设置可用的情况下，才采用该方法
		 */
		Criteria criteria=new Criteria();
		criteria.setAltitudeRequired(true);//要求需要能获取海拔 ，一般就是gps定位
		String bestProvider = lm.getBestProvider(criteria, true);
		
		/**
		 * 使用一种定位方式,请求更新位置
		 * provider：定位方式
		 * minTime： 最小的时间更新位置 :单位是毫秒
		 * minDistance：最小的距离 :单位是米
		 * listener :位置监听
		 */
//		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, null);
		lm.requestLocationUpdates(bestProvider, 1000, 10, new LocationListener() {
			//定位方式的状态改变
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				
			}
			//定位方式能用
			@Override
			public void onProviderEnabled(String provider) {
				
			}
			//定位方式不能用
			@Override
			public void onProviderDisabled(String provider) {
				
			}
			//位置改变 ，则回调
			@Override
			public void onLocationChanged(Location location) {
				double longitude = location.getLongitude();//经度
				double latitude = location.getLatitude();//纬度
				location.getAccuracy();//精确度
				location.getSpeed();//速度
				location.getAltitude();//海拔
				System.out.println("经度："+longitude+" 纬度:"+latitude);
				
			}
		});
		
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
