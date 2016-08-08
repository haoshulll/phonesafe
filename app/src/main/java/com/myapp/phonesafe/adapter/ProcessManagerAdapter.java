package com.myapp.phonesafe.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapp.phonesafe.R;
import com.myapp.phonesafe.entity.ProcessInfo;

import java.util.List;

public class ProcessManagerAdapter extends BaseAdapter {
	private Context context;
	private List<ProcessInfo> mUserInfoData;
	private List<ProcessInfo> mSystemInfoDatas;
	private boolean showSystem=true;

	public ProcessManagerAdapter(Context context,
			List<ProcessInfo> mUserInfoDatas, List<ProcessInfo> mSystemInfoDatas) {
		this.context = context;
		this.mUserInfoData = mUserInfoDatas;
		this.mSystemInfoDatas = mSystemInfoDatas;
	}
    public void setShowSystem(boolean isShow){
    	showSystem=isShow;
    }
	@Override
	public int getCount() {
		if(showSystem){
			return mUserInfoData.size()+1+mSystemInfoDatas.size()+1;
		}else{
			return mUserInfoData.size()+1;
		}
		
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		System.out.println("getView");
		// 取数据
		if(position==0||position==mUserInfoData.size()+1){
			TextView tv=new TextView(context);
			tv.setPadding(4, 4, 4, 4);
			tv.setBackgroundColor(Color.parseColor("#d1d1d1"));
			tv.setTextSize(18);//缩放的字体大小
			tv.setTextColor(Color.BLACK);
			//设置文本
			if(position==0){
				tv.setText("用户程序("+mUserInfoData.size()+"个)");
			}else{
				tv.setText("系统程序("+mSystemInfoDatas.size()+"个)");
			}
			return tv;
		}
		
		ProcessInfo processInfo=null;
		if(showSystem){ //是否要显示系统进程
			if(position<=mUserInfoData.size()){
				processInfo=mUserInfoData.get(position-1);
			}else{
				processInfo=mSystemInfoDatas.get(position-mUserInfoData.size()-1-1);
			}
		}else{
			processInfo=mUserInfoData.get(position-1);
		}

		
		//取列表项的界面
		if(convertView==null|| convertView instanceof TextView){
			convertView=LayoutInflater.from(context).inflate(R.layout.item_process_manager, parent, false);
		}
		
		ImageView iconIv=(ImageView)convertView.findViewById(R.id.pm_item_icon_iv);
		TextView nameTv=(TextView)convertView.findViewById(R.id.pm_item_name_tv);
		TextView memoryTv=(TextView)convertView.findViewById(R.id.pm_memory_tv);
		CheckBox chx=(CheckBox)convertView.findViewById(R.id.pm_chb);
		
		
		// 设置数据
		if(processInfo.icon==null){ //假如图标为空
			iconIv.setImageResource(R.mipmap.ic_launcher);
		}else{
			iconIv.setImageDrawable(processInfo.icon);//设置图标
		}
		if(TextUtils.isEmpty(processInfo.name)){//假如名称为空
			nameTv.setText(processInfo.packageName);
		}else{
			nameTv.setText(processInfo.name);//设置名称
		}
		
		//记得引用包的位置  import android.text.format.Formatter;
		String formatFileSize = Formatter.formatFileSize(context, processInfo.memory);//把字节数转换为kB或者MB
		memoryTv.setText(formatFileSize);
		
		if(processInfo.packageName.equals(context.getPackageName())){
			chx.setVisibility(View.GONE);
		}else{
			chx.setVisibility(View.VISIBLE);
			chx.setChecked(processInfo.isChecked);
		}
		//设置复选框的状态
		
		return convertView;
	}

}
