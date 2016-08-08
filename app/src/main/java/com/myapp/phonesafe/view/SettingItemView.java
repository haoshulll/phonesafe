package com.myapp.phonesafe.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.myapp.phonesafe.R;

/**
 * 自定义组合控件
 * 
 * @author lijun
 * 
 */
public class SettingItemView extends RelativeLayout {
	private TextView titleTv;//标题控件
	private ImageView toggleIv;//状态图片控件
	private View rootView;
	private  boolean isCheck=false;//是否选择
	// 内部调用，或者显示调用，一般不用
	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	// 当xml控件实例化时回调
	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
		//取得自定义属性的值
		// 1. 通过命名空间和属性名来取得属性值   namespace:命名空间   name：属性名
		String title = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "itcast_title");

		// 2. 通过上下文的obtainStyledAttributes： 获取所有的样式属性
		// set: 属性的集合 AttributeSet
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SettingItemView);
//		String title1 = a.getString(R.styleable.SettingItemView_itcast_title);
//		System.out.println("从布局中获取的属性"+title+title1);
		//开关图片控件是否可见
		boolean toggleIvEnable = a.getBoolean(R.styleable.SettingItemView_itcast_toggle_ennable, true);

		//取枚举的自定义属性
		int bgvalue = a.getInt(R.styleable.SettingItemView_itcast_bg, 0);
		a.recycle();//回收
		switch (bgvalue) {
			case 0://first背景
				rootView.setBackgroundResource(R.drawable.setting_item_first_select);
				break;
			case 1://first背景
				rootView.setBackgroundResource(R.drawable.setting_item_middle_select);
				break;
			case 2://first背景
				rootView.setBackgroundResource(R.drawable.setting_item_last_select);
				break;

			default:
				rootView.setBackgroundResource(R.drawable.setting_item_first_select);
				break;
		}

		titleTv.setText(title);
		setChecked(isCheck);
		if(!toggleIvEnable){
			toggleIv.setVisibility(View.GONE);//不占空间  ，不显示
//		   toggleIv.setVisibility(View.INVISIBLE);//占空间，但是不显示
		}
	}

	// 用java代码构建对象时，回调
	public SettingItemView(Context context) {

		super(context);
		initView();
	}
	private void initView() {
		rootView = LayoutInflater.from(getContext()).inflate(R.layout.view_setting_item, this);

//		this.addView(rootView);
		titleTv=(TextView) rootView.findViewById(R.id.setting_item_title_tv);
		toggleIv=(ImageView) rootView.findViewById(R.id.setting_item_toggle_iv);

	}


	// 自定义控件的自定义方法

	//取反的方法
	public void toggle() {
		setChecked(!isCheck);

	}
	//取得当前的状态
	public boolean isChecked(){
		return isCheck;
	}
	//设置状态
	public void setChecked(boolean check){
		this.isCheck=check;
		//设置图片控件的背景
		if(isCheck){
			toggleIv.setBackgroundResource(R.drawable.on);
		}else{
			toggleIv.setBackgroundResource(R.drawable.off);
		}

	}

}
