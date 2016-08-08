package com.myapp.phonesafe.view;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;

public class FocusTextView extends TextView {

	private static final String TAG = "FocusTextView";
	//一般内部调用，或者开发者，自己手动调用，一般不使用
	public FocusTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Log.d(TAG, "defStyle");
		
	}
    // 当xml布局的控件要实例化为对象时，则调用该构造方法
	public FocusTextView(Context context, AttributeSet attrs) {
		
		super(context, attrs);
		Log.d(TAG, "attrs");
		initAttrs();
	}
	//初始化默认属性
    private void initAttrs() {
		this.setSingleLine();//设置单行
		setEllipsize(TruncateAt.MARQUEE);//实现滚动字幕
		setMarqueeRepeatLimit(-1);//无限循环
		setFocusable(true);
		setFocusableInTouchMode(true);
	}
	// 该构造方法 ，主要用于java代码构建对象
	public FocusTextView(Context context) {
		super(context);
		Log.d(TAG, "context");
		initAttrs();
//		ImageView iv=new ImageView(context)
	}
	
	//默认返回获取焦点为true,制造假象
	@Override
	@ExportedProperty(category = "focus")
	public boolean isFocused() {
//		return super.isFocused();
		return true;
	}
	

}
