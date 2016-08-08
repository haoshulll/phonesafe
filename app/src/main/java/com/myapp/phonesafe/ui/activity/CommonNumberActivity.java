package com.myapp.phonesafe.ui.activity;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;

import com.myapp.phonesafe.R;
import com.myapp.phonesafe.adapter.CommonNumAdapter;

public class CommonNumberActivity extends Activity {
	private ExpandableListView mEListView;//常用号码拓展列表
	private int mCurrentOpenPosition=-1;//保存当前展开的组的索引号，位置，当为-1 表示 没有任何组展开
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_number);
		mEListView=(ExpandableListView) findViewById(R.id.tools_common_number_elstview);
		//1.展示数据、监听列表项  ,设置适配器 ，自定义适配器 ，继承 BaseExpandableListAdapter
		mEListView.setAdapter(new CommonNumAdapter(this));
		//2. 对可拓展的ListView设置监听
		// 2. 对组的监听
		mEListView.setOnGroupClickListener(new OnGroupClickListener() {
			
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				if(mCurrentOpenPosition==-1){
					//展开当前点击的组
					mEListView.expandGroup(groupPosition);//展开组
					mCurrentOpenPosition=groupPosition;
					mEListView.setSelectedGroup(groupPosition);//选择当前的组，把当前的组置为顶部
				}else{
					if(mCurrentOpenPosition==groupPosition){//表示当前点击的组已经展开，则应该折叠
						mEListView.collapseGroup(groupPosition);
						mCurrentOpenPosition=-1;//没有任何一个组展开
					}else{
						//先折叠展开的组，再展开 当前点击的组
						mEListView.collapseGroup(mCurrentOpenPosition);
						mEListView.expandGroup(groupPosition);
						mCurrentOpenPosition=groupPosition;
						mEListView.setSelectedGroup(groupPosition);//选择当前的组，把当前的组置为顶部
					}
				}
				return true;//返回true，表示当前的请求已经消费完毕，已经处理完毕，告诉android框架不再继续回调后续的方法
//				return false;  继续回调后续的方法，展示默认的效果
			}
		});
		
		//2.2 设置组的子列表项的点击监听
		mEListView.setOnChildClickListener(new OnChildClickListener() {
			/**当点击组的子列表项回调该方法
			 * parent：ExpandableListView
			 * v： view ：当前点击的子列表项的视图对象
			 * groupPosition： 组的索引号
			 * childPosition： 子列表项的索引号
			 * 
			 */
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// 取电话号码  方式1 ：查询数据库   2. 通过传递过来的v （子列表项的视图对象）来获取
//				String[] texts=CommonNumDao.getChildrenTexts(getApplicationContext(), groupPosition, childPosition);
				TextView tv=(TextView) v;
				String texts = tv.getText().toString().trim();
				String[] split = texts.split("\\n");
				System.out.println(split[0]+split[1]);
				String number=split[0];
				// 拨号  调用系统的拨号器 打电话
				Intent intent=new Intent();
				intent.setAction(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:"+number));
				startActivity(intent);
				
				return true;
//				return false;
			}
		});
	}


}
