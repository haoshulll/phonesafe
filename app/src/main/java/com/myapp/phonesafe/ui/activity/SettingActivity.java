package com.myapp.phonesafe.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.myapp.phonesafe.R;
import com.myapp.phonesafe.adapter.AddressStyleAdapter;
import com.myapp.phonesafe.service.AddressShowService;
import com.myapp.phonesafe.service.BlackIntercepterService;
import com.myapp.phonesafe.utils.CacheConfigUtils;
import com.myapp.phonesafe.utils.ServiceStateUtil;
import com.myapp.phonesafe.view.SettingItemView;


public class SettingActivity extends Activity {
	private SettingItemView mUpdateSv;//更新的自定义选项
	private SettingItemView mBlackInterceptSv;//拦截黑名单设置控件
	private SettingItemView mAddressShowSv;//归属地显示设置
	private SettingItemView mAddressStyleSv;//归属地风格设置

	private AddressStyleAdapter mAdapter;



	private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_settings);
		context=this;
		mAdapter=new AddressStyleAdapter(context);
		// 1. 更新apk 设置
		initUpdateVersionSet();
		// 2. 拦截黑名单设置
		initInterceptBlackSet();

		// 3. 初始化归属地显示设置
		initAddressShowSet();

		// 4. 初始化归属地风格设置
		initAddressStyleSet();

	}
	// 4. 初始化归属地风格设置
	private void initAddressStyleSet() {
		mAddressStyleSv=(SettingItemView)findViewById(R.id.setting_address_style_sv);

		// 初始化状态  取得卫士蓝
		mAddressStyleSv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 取得保存的选中的背景样式值
				int style = CacheConfigUtils.getInt(context, CacheConfigUtils.ADDRESS_STYLE);
				//设置 选择的列表项
				mAdapter.setSelected(style);

				// 显示对话框  注意： 对话框的上下文必须是 Activity ，对话框的显示要依赖Window，依赖Activity来显示
				AlertDialog.Builder builder=new AlertDialog.Builder(context);
				View dialogView = View.inflate(context, R.layout.dialog_address_style, null);

				builder.setView(dialogView);
				final AlertDialog dialog = builder.create();
				// 获取ListView控件对象
				ListView listView=(ListView) dialogView.findViewById(R.id.address_style_lstview);

				listView.setAdapter(mAdapter);

				// 对ListView设置列表项的点击监听
				listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
											int position, long id) {
						//持久化选择的样式
						CacheConfigUtils.putInt(context, CacheConfigUtils.ADDRESS_STYLE, position);
						//对话框消失
						dialog.dismiss();
					}
				});
				dialog.getWindow().setGravity(Gravity.BOTTOM);//对话框在底部显示
				dialog.show();
			}
		});




	}
	// 3. 初始化归属地显示设置
	private void initAddressShowSet() {
		//通过服务来实现电话号码归属地显示
		mAddressShowSv=(SettingItemView) findViewById(R.id.setting_address_show_sv);
		// 从配置文件中 取is_black_intercept 值，确定版本更新设置的状态，默认是 true 状态（更新的状态）
		//设置点击监听事件
		mAddressShowSv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//检测号码归属地显示服务的状态，假如运行，则停止，假如停止，则运行
				boolean isRunning=ServiceStateUtil.isRunning(context,AddressShowService.class);
				if(isRunning){
					//停止服务
					Intent intent=new Intent(context,AddressShowService.class);
					stopService(intent);
					mAddressShowSv.setChecked(false);
					CacheConfigUtils.putBoolean(context, CacheConfigUtils.SHOW_ADDRESS, false);
				}else{
					//停止服务
					Intent intent=new Intent(context,AddressShowService.class);
					startService(intent);
					mAddressShowSv.setChecked(true);
					CacheConfigUtils.putBoolean(context, CacheConfigUtils.SHOW_ADDRESS, true);
				}
			}
		});




	}

	private void initInterceptBlackSet() {
		mBlackInterceptSv=(SettingItemView) findViewById(R.id.setting_black_intercepter_sv);
		// 从配置文件中 取is_black_intercept 值，确定版本更新设置的状态，默认是 true 状态（更新的状态）
		//设置点击监听事件
		mBlackInterceptSv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//检测拦截黑名单服务的状态，假如运行，则停止，假如停止，则运行
				boolean isRunning=ServiceStateUtil.isRunning(context,BlackIntercepterService.class);
				if(isRunning){
					//停止服务
					Intent intent=new Intent(context,BlackIntercepterService.class);
					stopService(intent);
					mBlackInterceptSv.setChecked(false);
					CacheConfigUtils.putBoolean(context, CacheConfigUtils.IS_BLACK_INTERCEPT, false);
				}else{
					//停止服务
					Intent intent=new Intent(context,BlackIntercepterService.class);
					startService(intent);
					mBlackInterceptSv.setChecked(true);
					CacheConfigUtils.putBoolean(context, CacheConfigUtils.IS_BLACK_INTERCEPT, true);
				}
			}
		});
	}
	//初始化更新选项
	private void initUpdateVersionSet() {
		mUpdateSv=(SettingItemView) findViewById(R.id.update_sv);
		// 从配置文件中 取is_update_version 值，确定版本更新设置的状态，默认是 true 状态（更新的状态）
		if(CacheConfigUtils.getBoolean(context, CacheConfigUtils.IS_UPDATE_VERSION, true)){
			mUpdateSv.setChecked(true);
		}
		//设置点击监听事件
		mUpdateSv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO 每次点击一次，状态切换
				//调用自定义控件的自定义方法
				mUpdateSv.toggle();//取反
				CacheConfigUtils.putBoolean(context, CacheConfigUtils.IS_UPDATE_VERSION, mUpdateSv.isChecked());
			}
		});
	}

	@Override
	protected void onStart() {

		//检测黑名单拦截服务是否开启 ,修改显示状态
		boolean isRunning=ServiceStateUtil.isRunning(context,BlackIntercepterService.class);
		if(isRunning){
			mBlackInterceptSv.setChecked(true);
		}else{
			mBlackInterceptSv.setChecked(false);
		}

		// 检测号码归属地服务是否开启
		boolean addressFlag=ServiceStateUtil.isRunning(context,AddressShowService.class);
		if(addressFlag){
			mAddressShowSv.setChecked(true);
		}else{
			mAddressShowSv.setChecked(false);
		}
		super.onStart();
	}
}
