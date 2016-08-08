package com.myapp.phonesafe.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.myapp.phonesafe.db.CommonNumDao;

public class CommonNumAdapter extends BaseExpandableListAdapter {
	private Context context;
	public CommonNumAdapter(Context context){
		this.context=context;
	}
	// 取得组列表的数量
	@Override
	public int getGroupCount() {
		return CommonNumDao.getGroupCount(context);
	}
	// 取得当前组的列表项的数量     groupPosition： 组的位置，组的索引号
	@Override
	public int getChildrenCount(int groupPosition) {
		return CommonNumDao.getChildrenCount(context,groupPosition);
	}
	@Override
	public Object getGroup(int groupPosition) {
		return null;
	}
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}
	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}
	@Override
	public boolean hasStableIds() {
		return false;
	}
    /**取得当前要展示的组的视图
     * groupPosition:组的位置
     * isExpanded： 是否处于展开的状态
     * convertView： 可回收的View
     * parent： ExpandableListView
     */
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		TextView tv=null;
		if(convertView==null){
			tv=new TextView(context);
			tv.setBackgroundColor(Color.parseColor("#33000000"));
			tv.setTextColor(Color.BLACK);//设置文本的颜色
			tv.setTextSize(18);
			int padding=(int) getPx(10);
			tv.setPadding(padding, padding, padding, padding);
		}else{
			tv=(TextView) convertView;
		}
		
		//设置TextView的值
		//通过数据库查询组的名称
		String groupText=CommonNumDao.getGroupText(context,groupPosition);
		tv.setText(groupText);
		return tv;
	}
	/**取得当前组中要展示的子列表项的视图
	 * groupPosition：组的位置
	 * childPosition：子列表项的位置
	 * convertView： 可回收的View
	 * parent： 当前的组
	 */
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		TextView tv=null;
		if(convertView==null){
			tv=new TextView(context);
			tv.setTextColor(Color.BLACK);//设置文本的颜色
			tv.setTextSize(16);
			int padding=(int) getPx(8);
			tv.setPadding(padding, padding, padding, padding);
		}else{
			tv=(TextView) convertView;
		}
		
		//设置TextView的值
		//通过数据库查询组的名称
		String[] childrenTexts=CommonNumDao.getChildrenTexts(context,groupPosition,childPosition);
//		tv.setText(groupText);
		
		StringBuilder sb=new StringBuilder();
		//遍历数组
		for(int i=0;i<childrenTexts.length;i++){    //   text = number  /n  name 
			sb.append(childrenTexts[i]);
			if(i==0){
				sb.append("\n");//添加换行
			}
		}
		tv.setText(sb.toString());
		return tv;
	}
    //组的子列表项是否可以选择，点击
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
//		return false;
		return true;
	}
	
/*	
	// 自定义ListView 的 适配器 
	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return 0;
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
			return null;
		}
		
	}*/
	private float getPx(int dp){
		
		// 当前屏幕密度相对于基准密度的比例值  1  1.5   2  
		float density = context.getResources().getDisplayMetrics().density;
		return dp*density; 
	}

}
