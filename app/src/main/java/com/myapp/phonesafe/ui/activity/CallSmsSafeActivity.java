package com.myapp.phonesafe.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.myapp.phonesafe.R;
import com.myapp.phonesafe.adapter.BlackAdapter;
import com.myapp.phonesafe.db.BlackDao;
import com.myapp.phonesafe.entity.BlackInfo;
import com.myapp.phonesafe.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;


public class CallSmsSafeActivity extends Activity {
	protected static final int REQUEST_ADD_CODE = 100;//请求添加黑名单的请求码
	protected static final int REQUEST_UPDATE_CODE = 200;//请求更新黑名单的请求码
	private static final int LIMIT=15;//每批加载的数据的最大值，限制
	private int startOffset;//偏移量，起始加载数据的偏移量

	private ListView mListView;
	private ImageView addImageView;
	private ImageView mEmptyImageView;
	private LinearLayout mLoadingLly;
	private Context context;
	private BlackDao mBlackDao;
	private List<BlackInfo> mData=new ArrayList<BlackInfo>();
	private BlackAdapter mAdapter;


	private int mPosition;//保存当前点击的列表项的位置

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_sms_safe);
		//1. 初始化标题

		initView();


		/**
		 * 2. 通过ListView展示数据的步骤：
		 *   - 设置列表项布局
		 *   - 初始化的数据List<BlcackInfo>
		 *   - 设置适配器-自定义适配器
		 *   - 展示列表项，监听列表项
		 */
		// 2.2 初始化的数据List<BlcackInfo>

		fillData();

		//3. 点击添加黑名
		addImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//todo	 进入到编辑黑名单的Activity
				Intent  intent=new Intent()	;
				intent.setClass(context, BlackEditActivity.class);
//				startActivity(intent);
				startActivityForResult(intent, REQUEST_ADD_CODE);
//				finish();
			}
		});

		// 4. 对列表项设置点击监听事件
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
//				if(mData.size()>0){//数组越界异常
				BlackInfo blackInfo=mData.get(position);
				//进入到编辑黑名单界面
				Intent intent=new Intent(context,BlackEditActivity.class);
				intent.setAction(BlackEditActivity.ACTON_UPDATE);//设置动作为更新
				intent.putExtra(BlackEditActivity.EXTRA_NUMBER, blackInfo.number);
				intent.putExtra(BlackEditActivity.EXTRA_TYPE, blackInfo.type);
				//附加位置  todo

				mPosition=position;//保存当前操作的列表项的位置
				startActivityForResult(intent, REQUEST_UPDATE_CODE);
//				}


			}
		});

		// 5. 设置滚屏监听
		mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
			/**
			 *  当滚屏状态发生改变时，回调
			 *  SCROLL_STATE_IDLE: 处于空闲，静止状态
			 *  SCROLL_STATE_TOUCH_SCROLL： 触摸滚动的状态， 速度偏慢，手指触摸屏幕
			 *  SCROLL_STATE_FLING ： 惯性滑动，速度偏快，手指离开屏幕
			 */
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(scrollState== AbsListView.OnScrollListener.SCROLL_STATE_IDLE){//处于停止状态
					// 并且 最后一个可见的列表项为当前数据列表的最后一项
					int lastVisiblePosition = mListView.getLastVisiblePosition();
					mListView.getFirstVisiblePosition();//取第一个可见的列表项的位置
					if(lastVisiblePosition==mData.size()-1){
						//分批加载数据
						startOffset+=LIMIT;
						fillData();
					}
				}

			}
			//当滚动的时候，回调
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
								 int visibleItemCount, int totalItemCount) {

			}
		});
	}

	//处理由 startActivityForResult打开的Activity关闭后返回的结果
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		System.out.println("onActivityResult");
		if(REQUEST_ADD_CODE==requestCode&&RESULT_OK==resultCode){//todo 添加了黑名单的请求码
			String number=data.getStringExtra(BlackEditActivity.EXTRA_NUMBER);
			int  type=data.getIntExtra(BlackEditActivity.EXTRA_TYPE,-1);
			BlackInfo blackInfo=new BlackInfo();
			blackInfo.number=number;
			blackInfo.type=type;
			//注意 mData对象的地址是没有改变的
//			if(mData==null){
//				mData=new ArrayList<BlackInfo>();
//			}
			mData.add(blackInfo);

			mAdapter.notifyDataSetChanged();//通知数据集发生改变
		}
		//请求更新的返回值
		if(REQUEST_UPDATE_CODE==requestCode&&RESULT_OK==resultCode){
			int  type=data.getIntExtra(BlackEditActivity.EXTRA_TYPE,-1);
			mData.get(mPosition).type=type;
			mAdapter.notifyDataSetChanged();//通知数据集发生改变
		}
	}
	//填充数据 ,访问数据库 ，耗时的操作，开辟子线程 ，AsyncTask
	private void fillData() {
		new AsyncTask<String, Integer, List<BlackInfo>>() {
			// 1. 访问子线程之前的初始化操作
			@Override
			protected void onPreExecute() {
				mLoadingLly.setVisibility(View.VISIBLE);
			}
			// 2. 在子线程处理耗时的操作
			@Override
			protected List<BlackInfo> doInBackground(String... params) {
				SystemClock.sleep(1000);//休眠1秒
				List<BlackInfo> data=mBlackDao.getPartBlackList(LIMIT,startOffset);
				return data;
			}
			// 3. 当doInBackground方法执行完毕之后，再回调该方法
			@Override
			protected void onPostExecute(List<BlackInfo> data) {
				if(data!=null&&data.size()!=0){
					mData.addAll(data);
				}else{
					ToastUtil.show(context, "没有数据");
				}

				System.out.println("onPostExecute");
				mLoadingLly.setVisibility(View.GONE);//消失加载进度条
				mListView.setEmptyView(mEmptyImageView);
				if(mAdapter==null){
					mAdapter=new BlackAdapter(context,mData);
					mListView.setAdapter(mAdapter);
				}else{
					//todo
					mAdapter.notifyDataSetChanged();//数据改变的通知
				}
			}
		}.execute();
	}
	//初始化视图
	private void initView() {
		context=this;
		mBlackDao=new BlackDao(context);
		TextView titleBarTv=(TextView) findViewById(R.id.title_bar);
		titleBarTv.setText("黑名单管理");
		addImageView=(ImageView) findViewById(R.id.black_menu_add_iv);
		mListView=(ListView) findViewById(R.id.black_lstView);
		mEmptyImageView=(ImageView)findViewById(R.id.black_empty_tv);
		mLoadingLly=(LinearLayout) findViewById(R.id.black_loading_lly);
		//当Adapter数据为空时，则显示设置空视图的界面
//		mListView.setEmptyView(mEmptyImageView);
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		System.out.println("start");
		super.onStart();
	}
	@Override
	protected void onResume() {
		System.out.println("onResume");
		super.onResume();
	}

}

