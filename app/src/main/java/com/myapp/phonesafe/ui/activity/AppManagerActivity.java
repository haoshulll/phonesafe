package com.myapp.phonesafe.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.myapp.phonesafe.R;
import com.myapp.phonesafe.adapter.AppManagerAdapter;
import com.myapp.phonesafe.business.AppInfoProvider;
import com.myapp.phonesafe.entity.AppInfo;
import com.myapp.phonesafe.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class AppManagerActivity extends Activity implements OnClickListener {
	private static final int REQUEST_UNINSTALL_CODE = 110;// 卸载应用的请求码
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
	private AppManagerAdapter mAdapter;// 适配器

	private PopupWindow mPopupWindow;// 弹出窗口：弹窗
	private LinearLayout mPwLayout;// 弹窗的界面布局对象
	private AppInfo appInfo = null;// 存放当前点击的应用信息

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		ViewUtils.inject(this);
		context = this;
		mData = new ArrayList<AppInfo>();
		mUserInfodatas = new ArrayList<AppInfo>();
		mSystemInfoDatas = new ArrayList<AppInfo>();
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

		// 3. 对ListView设置列表项点击监听
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 分类的标题点击不显示弹窗
				if (position == 00 || position == mUserInfodatas.size() + 1) {
					return;
				}
				//
				// 取数据

				if (position <= mUserInfodatas.size()) {
					appInfo = mUserInfodatas.get(position - 1);
				} else {
					appInfo = mSystemInfoDatas.get(position
							- mUserInfodatas.size() - 1 - 1);
				}

				if (mPopupWindow != null) {
					mPopupWindow.dismiss();// 消失
					mPopupWindow = null;
				}

				// 点击显示弹窗

				/**
				 * 构造弹窗 contentView：内容视图，弹窗的界面控件对象 width：弹窗宽度 : 100 ,
				 * wrap_content 0 height：弹窗的高度
				 */
				mPopupWindow = new PopupWindow(mPwLayout,
						ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);

				// 弹窗设置的模板
				mPopupWindow.setFocusable(true);// 设置获取焦点，假如失去焦点，则弹窗自动的消失
				mPopupWindow.setOutsideTouchable(true);// 在弹窗框以为也可以进行触摸操作
				// Color.TRANSPARENT:透明色
				mPopupWindow.setBackgroundDrawable(new ColorDrawable(
						Color.TRANSPARENT));// 弹窗默认是没有背景，假如执行View的动画，则要依赖背景

				/**
				 * 显示弹窗： anchor： 以哪个控件为参照位置，进行显示 ，在该控件的左下角显示弹窗 xoff：x方向的偏移量
				 * yOff：y方向的偏移量
				 */
				mPopupWindow.showAsDropDown(view, 30, -view.getHeight());
				// mPwLayout.startAnimation(animation);//开启动画 。渐变动画 ，缩放动画
				// scaleAnimation
			}
		});

		// 4. 初始化弹窗的视图
		initPwView();

	}

	// 初始化弹窗的视图
	private void initPwView() {
		mPwLayout = (LinearLayout) View.inflate(context,
				R.layout.layout_popupwindow, null);
		int childCount = mPwLayout.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View childView = mPwLayout.getChildAt(i);
			childView.setOnClickListener(this);
			childView.setTag(i);
		}

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
					mAdapter = new AppManagerAdapter(context, mUserInfodatas,
							mSystemInfoDatas);
					mListView.setAdapter(mAdapter);
				} else {
					mAdapter.notifyDataSetChanged();// 数据集发生改变的通知
				}

			}
		}.execute();

	}

	@Override
	public void onClick(View v) {
		int tag = (Integer) v.getTag();
		switch (tag) {
		case 0:// 详细
			appDetail();
			break;
		case 1:// 安装
			runApp();
			break;
		case 2:// 分享
			shareInfo();// 一键分享 jar包
			break;
		case 3:// 卸载
			unInstallApp();
			break;

		default:
			break;
		}

	}

	// 分享
	private void shareInfo() {
		// 分享文本信息
		/*
		 * Intent intent=new Intent(); intent.setAction(Intent.ACTION_SEND);
		 * intent.setType("text/plain"); intent.putExtra(Intent.EXTRA_TEXT,
		 * "开心分享信息"); startActivity(intent);
		 */

		// 分享网站
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("http://www.baidu.com"));
//		intent.addCategory(Intent.CATEGORY_BROWSABLE);
		startActivity(intent);
	}

	// 查看详细
	private void appDetail() {
		Intent intent = new Intent();
		intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
		intent.setData(Uri.parse("package:" + appInfo.pacakgeName));
		startActivity(intent);

	}

	// 运行app
	private void runApp() {
		// 取得某个包的运行的意图 ，即相关的Activity的意图为Main动作 launcher类别
		Intent launchIntentForPackage = getPackageManager()
				.getLaunchIntentForPackage(appInfo.pacakgeName);
		if (launchIntentForPackage == null) {
			ToastUtil.show(context, "系统服务，无权启动");
		} else {
			startActivity(launchIntentForPackage);
		}
	}

	// 卸载应用
	private void unInstallApp() {

		// 调用系统的Activity应用实现卸载
		/*
		 * <intent-filter> <action android:name="android.intent.action.VIEW" />
		 * <action android:name="android.intent.action.DELETE" /> <category
		 * android:name="android.intent.category.DEFAULT" /> <data
		 * android:scheme="package" /> </intent-filter>
		 */

		if (getPackageName().equals(appInfo.pacakgeName)) {
			ToastUtil.show(context, "傻逼");
		} else if (appInfo.isSystem) {
			ToastUtil.show(context, "系统核心应用，无权删除");
		} else {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_DELETE);
			intent.setData(Uri.parse("package:" + appInfo.pacakgeName));
			startActivityForResult(intent, REQUEST_UNINSTALL_CODE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 卸载成功，结果码不是RESULT_OK
		/*
		 * if(requestCode==REQUEST_UNINSTALL_CODE&&resultCode==RESULT_OK){
		 * //刷新数据 }
		 */
		if (requestCode == REQUEST_UNINSTALL_CODE) {
			// 刷新数据
			fillData();
		}
	}

}
