package com.myapp.phonesafe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapp.phonesafe.R;
import com.myapp.phonesafe.db.BlackDao;
import com.myapp.phonesafe.entity.BlackInfo;
import com.myapp.phonesafe.utils.ToastUtil;

import java.util.List;


public class BlackAdapter extends BaseAdapter {
	private Context context;
	private List<BlackInfo> mData;
	private BlackDao mBlackDao;

	public BlackAdapter(Context context, List<BlackInfo> mData) {
		this.context=context;
		this.mData=mData;
		mBlackDao=new BlackDao(context);
	}

	@Override
	public int getCount() {
		return mData==null?0:mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData==null?null:mData.get(position);
	}

	@Override
	public long getItemId(int position) {
//		return mData==null?0:position;
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 获取要展示的数据
		final BlackInfo blackInfo=mData.get(position);
		HolderView hodlerView=null;
		if(convertView==null){
			hodlerView=new HolderView();
			convertView=LayoutInflater.from(context).inflate(R.layout.item_black, parent, false);
			hodlerView.holderNumberTv=(TextView) convertView.findViewById(R.id.black_item_number_tv);
			hodlerView.holderTypeTv=(TextView) convertView.findViewById(R.id.black_item_type_tv);
			hodlerView.holderDelIv=(ImageView)convertView.findViewById(R.id.black_item_del_iv);
			convertView.setTag(hodlerView);
		}else{
			hodlerView=(HolderView) convertView.getTag();
		}
		
		//设置值
		hodlerView.holderNumberTv.setText(blackInfo.number);
		int type=blackInfo.type;
		switch (type) {
		case BlackInfo.TYPE_CALL:
			hodlerView.holderTypeTv.setText("电话拦截");
			break;
		case BlackInfo.TYPE_SMS:
			hodlerView.holderTypeTv.setText("短信拦截");
			break;
		case BlackInfo.TYPE_ALL:
			hodlerView.holderTypeTv.setText("电话拦截+短信拦截");
			break;
		default:
			break;
		}
		// 对删除按钮添加 点击监听事件
		hodlerView.holderDelIv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//删除操作：在数据库表删除黑名单
				boolean deleteFlag = mBlackDao.delete(blackInfo.number);
				ToastUtil.show(context,"删除的号码"+blackInfo.number);
				if(deleteFlag){
					// 在List集合中删除当前删除的黑名单对象
					mData.remove(blackInfo);
					notifyDataSetChanged();//通知数据集发生改变
					
				}else{
					ToastUtil.show(context, "删除失败");
				}
			}
		});
		return convertView;
	}
	private static class HolderView{
		TextView holderNumberTv;
		TextView holderTypeTv;
		ImageView holderDelIv;
	}

}
