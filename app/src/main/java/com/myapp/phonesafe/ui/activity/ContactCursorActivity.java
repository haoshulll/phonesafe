package com.myapp.phonesafe.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.myapp.phonesafe.R;
import com.myapp.phonesafe.adapter.ContactCursorAdapter;
import com.myapp.phonesafe.utils.ContactsUtil;
import com.myapp.phonesafe.utils.ToastUtil;


public class ContactCursorActivity extends Activity {
	private Cursor mCursor;
	
	@ViewInject(R.id.title_bar)
	private TextView mTilteBarTv;
	
	@ViewInject(R.id.contact_lstview)
	private ListView mContactLstView;//联系人的ListView
	
	@ViewInject(R.id.load_contact_pb)
	private ProgressBar mProgressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);
		ViewUtils.inject(this);
		//初始化标题
		mTilteBarTv.setText("选择联系人");
		
		//2 . 获取联系人数据。数据来自系统的数据库，通过ContentProvider来访问
		//开辟子线程访问耗时的操作，比如 访问数据库
		new Thread(){
			

			public void run() {
				mCursor = ContactsUtil.getContactsByCursor(getBaseContext());
				 
				 SystemClock.sleep(1000);//休眠1秒
				 
//				 Hander.sendMessage();
				 //当子线程处理完毕后，发消息给主线程，异步通信
				 //把下面的语句发送到UI线程的语句队列中，在UI线程中处理封装的语句
				 runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						
						//隐藏进度条
						mProgressBar.setVisibility(View.GONE);//消失
						// this: Activity  --getBaseContext:Context
						//4. ListView 设置适配器，并且展示数据
						mContactLstView
						.setAdapter(new ContactCursorAdapter(getBaseContext(),mCursor,CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER));
					}
				});
			};
		}.start();
		
		// 5. 监听列表项
		
		mContactLstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			  mCursor.moveToPosition(position);
			  
			  String tel = mCursor.getString(mCursor
						.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			  ToastUtil.show(getApplicationContext(), tel+"Cursor");
				Intent data=new Intent();
				data.putExtra("tel", tel);
				setResult(RESULT_OK, data);
				finish();
			}
		});
		System.out.println("mContactLstView设置监听");

	}

}
