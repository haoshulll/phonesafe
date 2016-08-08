package com.myapp.phonesafe.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.myapp.phonesafe.R;
import com.myapp.phonesafe.adapter.ContactAdapter;
import com.myapp.phonesafe.entity.ContactInfo;
import com.myapp.phonesafe.utils.ContactsUtil;

import java.util.List;


public class ContactActivity extends Activity {
	private List<ContactInfo> mData;
	
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
		
		/**
		 *  对ListView加载数据，展示批量的结构相同的数据  ：采用MVC设计模式
		 *  实现步骤：
		 *   1. 设置列表项布局
		 *   2. Model： 封装要装配的数据 List<Map>   \List<Student>   ： 
		 *   3. Controller: 准备适配器：ArrayAdapter、SimpleAdapter、SimpeCursor
		 *       Adapter自定义适配器1（继承BaseAdapter） 、Adapter自定义适配器2（继承CursorAdapter）
		 *   4. View: ListView设置适配器，并且展示列表项、监听列表项
		 */
		//2 . 获取联系人数据。数据来自系统的数据库，通过ContentProvider来访问
		
		
		
		//开辟子线程访问耗时的操作，比如 访问数据库
		new Thread(){
			

			public void run() {
				 mData = ContactsUtil.getAllContacts(getBaseContext());
				 
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
						mContactLstView.setAdapter(new ContactAdapter(getBaseContext(),mData));
						System.out.println("mContactLstView设置适配器");

					}
				});
			};
		}.start();
		

		
		
		// 5. 监听列表项
		
		mContactLstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//取得当前选择的列表项的电话号码数据
				ContactInfo contactInfo = mData.get(position);
				String tel=contactInfo.number;
				Intent data=new Intent();
				data.putExtra("tel", tel);
				setResult(RESULT_OK, data);
				finish();
			}
		});
		System.out.println("mContactLstView设置监听");
		
		
//		SimpleCursorAdapter adapter=new SimpleCursorAdapter(context, layout, c, from, to)

	}

}
