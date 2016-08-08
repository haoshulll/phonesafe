package com.myapp.phonesafe.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapp.phonesafe.R;
import com.myapp.phonesafe.entity.AppInfo;

import java.util.List;

public class AppManagerAdapter extends BaseAdapter {
	private Context context;
	private List<AppInfo> mUserInfodatas;
    private List<AppInfo> mSystemInfoDatas;

	public AppManagerAdapter(Context context, List<AppInfo> mUserInfodatas,
			List<AppInfo> mSystemInfoDatas) {
		this.context=context;
		this.mUserInfodatas=mUserInfodatas;
		this.mSystemInfoDatas=mSystemInfoDatas;
	}

	@Override
	public int getCount() {
		return mUserInfodatas.size()+1+mSystemInfoDatas.size()+1;
	}
	@Override
	public Object getItem(int position) {
		return null;
	}
	@Override
	public long getItemId(int position) {
		return 0;
	}
   //列表项视图
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//取数据 
		if(position==0||position==mUserInfodatas.size()+1){
			TextView tv=new TextView(context);
			tv.setPadding(4, 4, 4, 4);
			tv.setBackgroundColor(Color.parseColor("#d1d1d1"));
			tv.setTextSize(18);//缩放的字体大小
			tv.setTextColor(Color.BLACK);
			//设置文本
			if(position==0){
				tv.setText("用户程序("+mUserInfodatas.size()+"个)");
			}else{
				tv.setText("系统程序("+mSystemInfoDatas.size()+"个)");
			}
			return tv;
		}
		
		//取数据
		AppInfo appInfo=null;
		
		if(position<=mUserInfodatas.size()){
			appInfo=mUserInfodatas.get(position-1);
		}else{
			appInfo=mSystemInfoDatas.get(position-mUserInfodatas.size()-1-1);
		}
		
		//列表项布局
		if(convertView==null||convertView instanceof TextView){
			convertView=LayoutInflater.from(context).inflate(R.layout.item_app_manager, parent, false);
		}
		
		ImageView iconIv=(ImageView)convertView.findViewById(R.id.app_item_icon_iv);
		TextView nameTv=(TextView)convertView.findViewById(R.id.app_item_name_tv);
		TextView installTv=(TextView)convertView.findViewById(R.id.app_item_install_tv);
		TextView sizeTv=(TextView)convertView.findViewById(R.id.app_item_size_tv);
		
		// 设置数据
		iconIv.setImageDrawable(appInfo.icon);//设置图标
		nameTv.setText(appInfo.name);//设置名称
		installTv.setText(appInfo.isInstallSdcard?"SD卡":"手机内存");
		//记得引用包的位置  import android.text.format.Formatter;
		String formatFileSize = Formatter.formatFileSize(context, appInfo.size);//把字节数转换为kM或者MB
		sizeTv.setText(formatFileSize);
		
		return convertView;
	}

}
