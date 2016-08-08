package com.myapp.phonesafe.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.myapp.phonesafe.R;
import com.myapp.phonesafe.adapter.ProcessManagerAdapter;
import com.myapp.phonesafe.business.ProcessInfoProvider;
import com.myapp.phonesafe.entity.ProcessInfo;
import com.myapp.phonesafe.service.AutoClearProcessService;
import com.myapp.phonesafe.utils.CacheConfigUtils;
import com.myapp.phonesafe.utils.ServiceStateUtil;
import com.myapp.phonesafe.view.SettingItemView;

import java.util.ArrayList;
import java.util.List;


public class ProcessManagerActivity extends Activity {
	@ViewInject(R.id.loading_lly)
	private LinearLayout mLoadingLly;//进度条
	@ViewInject(android.R.id.list)
	private ListView mListView;
	@ViewInject(R.id.pm_type_title_tv)
	private TextView mTypeTitleTv;
	
	@ViewInject(R.id.pm_drawer)
	private SlidingDrawer mSlidingDrawer;
	@ViewInject(R.id.pm_drawer_arrow1)
	private ImageView mArrow1Iv;//箭头1
	@ViewInject(R.id.pm_drawer_arrow2)
	private ImageView mArrow2Iv;//箭头2
	
	@ViewInject(R.id.pm_showSystem_sv)
	private SettingItemView mShowSystemSv;
	@ViewInject(R.id.pm_lockScreenClean_sv)
	private SettingItemView mAutoCleanSv;
	
	
	private Context context;
	private List<ProcessInfo> mData;
	private List<ProcessInfo> mUserInfoDatas;
	private List<ProcessInfo> mSystemInfoDatas;
	private ProcessManagerAdapter mAdapter;
	private boolean showSystem=true;//是否显示系统进程的状态

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_manager);
		context=this;
		ViewUtils.inject(this);
		mData=new ArrayList<ProcessInfo>();
		mUserInfoDatas=new ArrayList<ProcessInfo>();
		mSystemInfoDatas=new ArrayList<ProcessInfo>();
		
		//对设置是否显示系统进行初始化
		showSystem = CacheConfigUtils.getBoolean(context, CacheConfigUtils.SHOW_SYSTEM_PROCESS,true);
		if(showSystem){
			mShowSystemSv.setChecked(true);
		}
		
		//1. 装配数据，及展示数据
		fillData();
		
		//2. 对ListView设置滚动监听
		mListView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if(firstVisibleItem<=mUserInfoDatas.size()){
					mTypeTitleTv.setText("用户进程("+mUserInfoDatas.size()+")个");
				}else{
					mTypeTitleTv.setText("系统进程("+mSystemInfoDatas.size()+")个");
				}
				
			}
		});
		
		upDrawerAnimation();//默认是执行动画，默认也是close状态
		//3. 监听抽屉的状态 ： 开和关的状态  
		//监听打开抽屉状态
		mSlidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			//抽屉开启，则回调
			@Override
			public void onDrawerOpened() {
				downDrawerAnimation();
				
			}
		});
		
		//抽屉关闭
		mSlidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
			
			@Override
			public void onDrawerClosed() {
				//关闭状态的箭头的动画
				upDrawerAnimation();
			}
		});
		
		
		// 4. 列表项点击监听
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position==0||position==mUserInfoDatas.size()+1){
					return ;
				}
				
				//
				ProcessInfo processInfo=null;
				
				if(position<=mUserInfoDatas.size()){
					processInfo=mUserInfoDatas.get(position-1);
				}else{
					processInfo=mSystemInfoDatas.get(position-mUserInfoDatas.size()-1-1);
				}
				
				// 每次点击切换复选框的状态   保存复选框的状态给 ProcessInfo.isChecked
				
				processInfo.isChecked=!processInfo.isChecked;
				//通过父控件找到子控件，改变相应控件的状态
				CheckBox chx=(CheckBox) view.findViewById(R.id.pm_chb);
				chx.setChecked(processInfo.isChecked);
//				mAdapter.notifyDataSetChanged();//发送数据改变的通知
				
			}
		});
		
		//5 .设置是否显示系统进程
		mShowSystemSv.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mShowSystemSv.toggle();
				showSystem=mShowSystemSv.isChecked();
				 CacheConfigUtils.putBoolean(context, CacheConfigUtils.SHOW_SYSTEM_PROCESS, showSystem);
				 mAdapter.setShowSystem(showSystem);
				 mAdapter.notifyDataSetChanged();
			}
		});
		
		//6. 锁频自动清除进程
		mAutoCleanSv.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//切换状态
				boolean isRunning = ServiceStateUtil.isRunning(context, AutoClearProcessService.class);
				if(isRunning){
					Intent intent=new Intent();
					intent.setClass(context,AutoClearProcessService.class);
					stopService(intent);
					CacheConfigUtils.putBoolean(context, CacheConfigUtils.IS_AUTO_CLEAR_PROCESS, false);
					mAutoCleanSv.setChecked(false);
				}else{
					Intent intent=new Intent();
					intent.setClass(context,AutoClearProcessService.class);
					startService(intent);
					CacheConfigUtils.putBoolean(context, CacheConfigUtils.IS_AUTO_CLEAR_PROCESS, true);
					mAutoCleanSv.setChecked(true);
					
				}
			}
		});
		
		
	}
	//抽屉向下的箭头的设置
	 protected void downDrawerAnimation() {
		 //清除动画
		 mArrow1Iv.clearAnimation();
		 mArrow2Iv.clearAnimation();
		 mArrow1Iv.setImageResource(R.drawable.drawer_arrow_down);
		 mArrow2Iv.setImageResource(R.drawable.drawer_arrow_down);
		
	}
	//抽屉向上的动画 
	 protected void upDrawerAnimation() {
		 mArrow1Iv.setImageResource(R.drawable.drawer_arrow_up);
		 mArrow2Iv.setImageResource(R.drawable.drawer_arrow_up);
		 //alpha动画
		 AlphaAnimation animation1=new AlphaAnimation(0.2f, 1f);
		 animation1.setDuration(600);
		 animation1.setRepeatCount(Animation.INFINITE);//无限循环
		 animation1.setRepeatMode(Animation.REVERSE);//反转模式
		 //执行动画
		 mArrow1Iv.startAnimation(animation1);
		 //alpha动画
		 AlphaAnimation animation2=new AlphaAnimation(1f, 0.2f);
		 animation2.setDuration(600);
		 animation2.setRepeatCount(Animation.INFINITE);//无限循环
		 animation2.setRepeatMode(Animation.REVERSE);//反转模式
		 //执行动画
		 mArrow2Iv.startAnimation(animation2);
		
	}

	// 装配数据，及展示数据
	private void fillData() {
		new AsyncTask<String, Integer, String>() {
			protected void onPreExecute() {
				mLoadingLly.setVisibility(View.VISIBLE);
			};

			@Override
			protected String doInBackground(String... params) {
//				System.out.println("execute传递过来的参数"+params[0]);
				mData.clear();
				mUserInfoDatas.clear();
				mSystemInfoDatas.clear();
				mData= ProcessInfoProvider.getAppProcesses(context);
//				System.out.println(mData);
				// 遍历集合数据
				for (ProcessInfo info : mData) {

					if (info.isSystem) {
						mSystemInfoDatas.add(info);
					} else {
						mUserInfoDatas.add(info);
					}
				}
				return null;
			}
			protected void onPostExecute(String result) {
				mLoadingLly.setVisibility(View.GONE);
				// 已经取得数据,展示数据
				if (mAdapter == null) {
					mAdapter = new ProcessManagerAdapter(context, mUserInfoDatas,
							mSystemInfoDatas);
					mAdapter.setShowSystem(showSystem);
					mListView.setAdapter(mAdapter);
				} else {
					mAdapter.notifyDataSetChanged();// 数据集发生改变的通知
				}
				
				
			}
		}.execute();
		
	}


	// 清除所选的进行
	public void clearProcess(View v){
		// 遍历用户集合
		for(ProcessInfo temp:mUserInfoDatas){
			if(temp.isChecked){
				//依据包杀死该进程
				ProcessInfoProvider.killProcess(context,temp.packageName);
			}
		}
		for(ProcessInfo temp:mSystemInfoDatas){
			if(temp.isChecked){
				//依据包杀死该进程
				ProcessInfoProvider.killProcess(context,temp.packageName);
			}
		}
//		mAdapter.notifyDataSetChanged();
		fillData();
		
		
	}
	// 清除所选的进行
	public void selectAll(View v){
		//遍历用户进程集合
		for(ProcessInfo temp:mUserInfoDatas){
			temp.isChecked=true;
		}
		//遍历系统进程集合
		for(ProcessInfo temp:mSystemInfoDatas){
			temp.isChecked=true;
		}
		
		// 发送数据改变的通知
		mAdapter.notifyDataSetChanged();
		
	}
	// 取消选择
	public void selectClear(View v){
		//遍历用户进程集合
		for(ProcessInfo temp:mUserInfoDatas){
			temp.isChecked=false;
		}
		//遍历系统进程集合
		for(ProcessInfo temp:mSystemInfoDatas){
			temp.isChecked=false;
		}
		
		// 发送数据改变的通知
		mAdapter.notifyDataSetChanged();
		
		
	}
	
	@Override
	protected void onStart() {
		
		//判断自动清理进程 服务是否开启
		boolean isRunning = ServiceStateUtil.isRunning(context, AutoClearProcessService.class);
		if(isRunning){
			mAutoCleanSv.setChecked(true);
		}else{
			mAutoCleanSv.setChecked(false);
		}
		super.onStart();
	}
}
