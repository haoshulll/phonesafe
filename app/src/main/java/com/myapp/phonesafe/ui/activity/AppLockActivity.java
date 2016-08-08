package com.myapp.phonesafe.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.myapp.phonesafe.R;
import com.myapp.phonesafe.adapter.AppLockAdapter;
import com.myapp.phonesafe.business.AppInfoProvider;
import com.myapp.phonesafe.db.AppLockDao;
import com.myapp.phonesafe.entity.AppInfo;

import java.util.ArrayList;
import java.util.List;


public class AppLockActivity extends Activity  {
	@ViewInject(android.R.id.list)
	private ListView mListView;
	@ViewInject(R.id.app_loading_lly)
	private LinearLayout mLoadLly;// 进度显示的布局控件
	@ViewInject(R.id.content_fl)
	private FrameLayout mContnetFLayout;

	@ViewInject(R.id.app_type_title_tv)
	private TextView mTyepTitleTv;

	private Context context;
	private List<AppInfo> mData;
	private List<AppInfo> mUserInfodatas;// 用户程序的集合
	private List<AppInfo> mSystemInfoDatas;// 系统程序的集合
	private AppLockAdapter mAdapter;// 适配器
	private AppLockDao mAppLockDao;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_lock);
		ViewUtils.inject(this);
		context = this;
		mData = new ArrayList<AppInfo>();
		mUserInfodatas = new ArrayList<AppInfo>();
		mSystemInfoDatas = new ArrayList<AppInfo>();
		mAppLockDao=new AppLockDao(context);
		// 1. 获取数据，展示应用列表界面
		fillData();

		// 2. 对listView设置滚动监听
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			/**
			 * 当滚动时回调该方法 view： ListView firstVisibleItem：第一个可见的列表项的position，索引号
			 * visibleItemCount：可见的列表项的数量 totalItemCount：总共的列表项的数量
			 * 
			 * 
			 */
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem <= mUserInfodatas.size()) {
					mTyepTitleTv.setText("用户程序(" + mUserInfodatas.size() + "个)");
				} else {
					mTyepTitleTv.setText("系统程序(" + mSystemInfoDatas.size()
							+ "个)");
				}

			}
		});
		
		//对列表项设置长按监听
		mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position==0||position==mUserInfodatas.size()+1){
					return true;
				}
				
				//取数据
				AppInfo appInfo=null;
				
				if(position<=mUserInfodatas.size()){
					appInfo=mUserInfodatas.get(position-1);
				}else{
					appInfo=mSystemInfoDatas.get(position-mUserInfodatas.size()-1-1);
				}
				// 1.改控件图片： 加锁或者解锁图片  2. 把加锁的应用程序包写入数据库中
				ImageView lockIv=(ImageView) view.findViewById(R.id.applock_item_lock_iv);
				// 判断是否为加锁还是解锁
				//查询数据库
				boolean isLock = mAppLockDao.isLock(appInfo.pacakgeName);
				if(isLock){ //假如当前是加锁，则应该解锁
					lockIv.setImageResource(R.drawable.ic_unlock);
					mAppLockDao.removeApp(appInfo.pacakgeName);
					
				}else{
					lockIv.setImageResource(R.drawable.ic_lock);
					mAppLockDao.addLockedApp(appInfo.pacakgeName);
				}
				return true;
			}
		});
	}

	

	// 填充及展示数据
	private void fillData() {
		new AsyncTask<String, Integer, String>() {
			@Override
			protected void onPreExecute() {
				mLoadLly.setVisibility(View.VISIBLE);
			}

			@Override
			protected String doInBackground(String... params) {
				// 清除集合数据
				mData.clear();
				mUserInfodatas.clear();
				mSystemInfoDatas.clear();
				mData = AppInfoProvider.getAppInfos(context);
				// 遍历集合数据
				for (AppInfo appInfo : mData) {
					if (appInfo.isSystem) {
						mSystemInfoDatas.add(appInfo);
					} else {
						mUserInfodatas.add(appInfo);
					}
				}
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				// 通过父控件来移除子控件
				mContnetFLayout.removeView(mLoadLly);
				// mLoadLly.setVisibility(View.GONE);

				if (mAdapter == null) {
					mAdapter = new AppLockAdapter(context, mUserInfodatas,
							mSystemInfoDatas);
					mListView.setAdapter(mAdapter);
				} else {
					mAdapter.notifyDataSetChanged();// 数据集发生改变的通知
				}

			}
		}.execute();

	}

}
