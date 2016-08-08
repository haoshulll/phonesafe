package com.myapp.phonesafe.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.myapp.phonesafe.R;
import com.myapp.phonesafe.db.BlackDao;
import com.myapp.phonesafe.entity.BlackInfo;
import com.myapp.phonesafe.utils.ToastUtil;


public class BlackEditActivity extends Activity implements OnClickListener,OnCheckedChangeListener{
	public  static final String EXTRA_NUMBER = "number";//电话号码的标识
	public  static final String EXTRA_TYPE = "type";//拦截类型的标识
	public static final String ACTON_UPDATE = "update";//更新的动作
	private Context context;
	private BlackDao mBlackDao;

	private TextView mTitleBarTv;// 标题栏
	private EditText mNumberEt;
	private RadioGroup mTypeRg;// 拦截的类型的单选组
	private Button mOkBtn;
	private Button mCancelBtn;
	
	private int mCheckedId=-1;// 被选择的单选按钮的资源id
	private boolean mIsUpdate;//是添加还是更新模式

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_black_edit);
		// 1. 初始化界面
		initView();
		// 取得调用者传递过来的意图
		Intent intent = getIntent();
		//获取调用者传递过来的动作
		String update = intent.getAction();
		if(update!=null&&update.equals(ACTON_UPDATE)){//更新
			mIsUpdate=true;//设置 是否更新的状态
			mTitleBarTv.setText("更新黑名单");
			//设置电话号码
			String number=intent.getStringExtra(EXTRA_NUMBER);
			mNumberEt.setText(number);
			mNumberEt.setEnabled(false);//不能操作，只能更新拦截方式
			
			int type=intent.getIntExtra(EXTRA_TYPE, -1);
			switch (type) {
			case BlackInfo.TYPE_CALL:
				mCheckedId=R.id.black_type_call_rb;
				break;
			case BlackInfo.TYPE_SMS:
				mCheckedId=R.id.black_type_sms_rb;
				break;
			case BlackInfo.TYPE_ALL:
				mCheckedId=R.id.black_type_all_rb;
				break;
			default:
				break;
			}
			
			mTypeRg.check(mCheckedId);
			mOkBtn.setText("更新");
			
		}else{
			//添加黑名单，默认的操作
		}

	}

	// 初始化界面
	private void initView() {
		context=this;
		
		mTitleBarTv = (TextView) findViewById(R.id.title_bar);
		mNumberEt = (EditText) findViewById(R.id.black_number_et);
		mTypeRg = (RadioGroup) findViewById(R.id.black_type_rg);
		mOkBtn = (Button) findViewById(R.id.black_ok_btn);
		mCancelBtn = (Button) findViewById(R.id.black_cancel_btn);
		// 初始化标题
		mTitleBarTv.setText("添加黑名单");
		//绑定Button按钮 点击监听事件
		mOkBtn.setOnClickListener(this);
		mCancelBtn.setOnClickListener(this);
		mTypeRg.setOnCheckedChangeListener(this);
		
		mBlackDao=new BlackDao(context);

	}
    //3. 点击监听事件:保存、取消
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.black_ok_btn://点击了保存或者更新按钮
			addOrUpdateBlack();
			break;
		case R.id.black_cancel_btn://点击了取消按钮
			finish();
			break;

		default:
			break;
		}

	}
    // 3.1 添加或者更新黑名单
	private void addOrUpdateBlack() {
		/**添加或者更新黑名单 实现 步骤
		 * 1. 电话号码不能为空
		 * 2. 必须选择拦截方式
		 * 3. 判断是更新还是添加
		 */
		// 3.1.1 :判断电话号码是否为空
		String number=mNumberEt.getText().toString().trim();
		if(TextUtils.isEmpty(number)){
			ToastUtil.show(context, "号码不能为空");
			return;
		}
		// 3.1.2 :必须选择拦截方式
//		int checkedId=mTypeRg.getCheckedRadioButtonId();
		
		if(mCheckedId==-1){
			ToastUtil.show(context, "必须选择拦截方式");
			return;
		}
		
		int type=-1; //拦截类型
		switch (mCheckedId) {
		case R.id.black_type_call_rb:
			type=BlackInfo.TYPE_CALL;
			break;
		case R.id.black_type_sms_rb:
			type=BlackInfo.TYPE_SMS;
			break;
		case R.id.black_type_all_rb:
			type=BlackInfo.TYPE_ALL;
			break;
		default:
			break;
		}
		
		//3.1.3 判断是更新还是添加
		if(mIsUpdate){//更新黑名单
			//更新数据库
			boolean updateFlag = mBlackDao.update(number, type);
			if(updateFlag){
				// 更新成功，返还数据给调用者
				Intent data=new Intent();
				data.putExtra(EXTRA_NUMBER, number);
				data.putExtra(EXTRA_TYPE, type);
				setResult(RESULT_OK, data);
				
			}else{
				ToastUtil.show(context, "更新不成功");
			}
			
		}else{//添加黑名单
			boolean addFlag = mBlackDao.add(number, type);
			if(addFlag){
				ToastUtil.show(context, "添加成功");
				/**
				 *  把新添加的数据返回数据给调用者
				 *  1. setResult
				 *  2. finish
				 */
				Intent data=new Intent();
				data.putExtra(EXTRA_TYPE, type);
				data.putExtra(EXTRA_NUMBER, number);
				setResult(RESULT_OK, data);
				
			}else{
				ToastUtil.show(context, "添加失败");
			}
		}
		finish();//销毁当前的Activity
		
	}
    /*
     * **2. 监听单选组状态改变
     * - group :单选组
     * - checkedId： 被选择的单选按钮的id的资源id
     */
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		mCheckedId=checkedId;
	}

}
