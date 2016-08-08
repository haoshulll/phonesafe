package com.myapp.phonesafe.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapp.phonesafe.R;

public class AddressStyleAdapter extends BaseAdapter {
	private String[] styleItems = { "半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿" };
	private int[] styleImageRes = { R.drawable.address_style_nomal,
			R.drawable.address_style_orange, R.drawable.address_style_blue,
			R.drawable.address_style_gray, R.drawable.address_style_green };
	private Context context;
	
	private int mSelectedPosition=0;//被选中的列表项的索引号

	public AddressStyleAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return styleItems.length;
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
		//装配数据
		View itemView = LayoutInflater.from(context).inflate(R.layout.item_address_style, parent, false);
		ImageView  iconIv=(ImageView) itemView.findViewById(R.id.style_icon_iv);
		ImageView  selectIv=(ImageView) itemView.findViewById(R.id.style_select_iv);
		TextView nameTv=(TextView)itemView.findViewById(R.id.style_name_tv);
		
		iconIv.setImageResource(styleImageRes[position]);
		nameTv.setText(styleItems[position]);
		if(mSelectedPosition==position){
			selectIv.setVisibility(View.VISIBLE);
		}
		return itemView;
	}
	//自定义的方法  用于设置选中的样式
	public void setSelected(int select){
		mSelectedPosition=select;
	}

}
