package com.myapp.phonesafe.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapp.phonesafe.R;
import com.myapp.phonesafe.entity.ContactInfo;
import com.myapp.phonesafe.utils.ContactsUtil;
import com.myapp.phonesafe.utils.LogUtil;

import java.util.List;


public class ContactAdapter extends BaseAdapter {
	protected static final String TAG = "ContactAdapter";
	private Context context;
	private List<ContactInfo> mData;

	public ContactAdapter(Context baseContext, List<ContactInfo> mData) {
		this.context=baseContext;
		this.mData=mData;
	}

	@Override
	public int getCount() {
		return mData==null?null:mData.size();
	}
    //返回当前要展示的列表项的数据
	@Override
	public Object getItem(int position) {
		return mData==null?null:mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
    /**返回当前要展示的列表项的视图
     * position: 位置： 在适配器中的位置或者索引号 
     * convertView： 可回收的View :通过可回收的View来优化ListView
     * parent：ListView
     */
/*	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ContactInfo contactInfo = mData.get(position);
		
		System.out.println("getView" +position+"位置"+position+" 联系人的id"+contactInfo.contactId);
		if(convertView==null){
			System.out.println("convertView 为空的情况");
			//好比new View
			convertView=LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
		}
		 //引用列表项的控件视图
		TextView nameTv=(TextView) convertView.findViewById(R.id.contact_name_tv);
		TextView numberTv=(TextView)convertView.findViewById(R.id.contact_number_tv);
		final ImageView iconIv=(ImageView)convertView.findViewById(R.id.contact_icon_iv);
		
		
		//设置当前列表项要展示的数据
		
		nameTv.setText(contactInfo.name);
		numberTv.setText(contactInfo.number);
		*//**异步任务类
		 * 1. 三个泛型参数 
		 *   - Params:输入参数 :AsyncTask.execute(params)-->doInBackground(String... params
		 *   - Progress：进度参数  : publishProgress(values): 在子线程发进度----》 onProgressUpdate(Integer... values) :在主线程更新进度
		 *   - Result：结果参数: Bitmap doInBackground()的返回值----》onPostExecute(Bitmap result)
		 * 2. 四个回调的封装方法：
		 *   注意： 除了doInBackground在子线程运行，其他三个方法onPreExecute、onPostExecute、onProgressUpdate在主线程运行
		 *   执行顺序1：   onPreExecute--》doInBackground--》onPostExecute
		 *   执行顺序2：publishProgress(values)--》onProgressUpdate
		 * 3. 线程规则：
		 *   - AsyncTask 的class字节码必须在UI线程加载
		 *   - AsyncTask实例必须在UI线程实例化
		 *   - AsyncTask.execute()方法必须在UI线程执行
		 *   - 不要手动调用四个封装方法
		 *   - AsyncTask.execute()只能执行一次，假如要多次执行，则在new AsyncTask
		 *   
		 *//*
        new AsyncTask<String, Integer, Bitmap>() {
        	  
        	  //1.  在执行子线程之前初始化操作 ，在UI线程运行
        	 protected void onPreExecute() {
        		 LogUtil.d(TAG, "onPreExecute");
        	 };
            //  2. 在后台执行耗时的操作，即在子线程处理耗时的操作，比如访问网络，访问数据库
			@Override
			protected Bitmap doInBackground(String... params) {
//				HttpURLConnection conn;
//				conn.getContentLength();//取得内容的长度，大小 ，一个要下载的文件的大小
				//在哪里发进度 ，在子线程中发进度，
//				publishProgress(values);
				LogUtil.d(TAG, "doInBackground");
				//访问数据库，获取图片
				Bitmap bitmap=ContactsUtil.getContactPhoto(context, contactInfo.contactId);
				return bitmap;
			}
			// 3. 在子线程处理耗时的操作完毕后，再执行该方法，在UI线程运行
			protected void onPostExecute(Bitmap result) {
				LogUtil.d(TAG, "onPostExecute");
				if(result!=null){
					iconIv.setImageBitmap(result);
				}else{
					iconIv.setImageResource(R.drawable.ic_contact);
				}
			}
			// 4. 更新进度的方法，UI线程处理
			@Override
			protected void onProgressUpdate(Integer... values) {
				//在主线程显示进度
				super.onProgressUpdate(values);
			}
			
		}.execute();
		return convertView;
	}*/
	
	// 通过HolderView来优化自定义适配器
		@Override
	public View getView(int position, View convertView, ViewGroup parent) {
			System.out.println("holderView");
		final ContactInfo contactInfo = mData.get(position);
		
		System.out.println("getView" +position+"位置"+position+" 联系人的id"+contactInfo.contactId);
		
		 HolderView holderView=null;
		if(convertView==null){
			holderView=new HolderView();
			System.out.println("convertView 为空的情况");
			//好比new View
			convertView=LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
			
			holderView.holderNameTv=(TextView) convertView.findViewById(R.id.contact_name_tv);
			holderView.holderNumberTv=(TextView)convertView.findViewById(R.id.contact_number_tv);
			holderView.holderIconIv=(ImageView)convertView.findViewById(R.id.contact_icon_iv);
			
			//设置标记，用来存放引用的控件的地址，即可以存放HolderView
			convertView.setTag(holderView);
		}else{
			holderView=(HolderView) convertView.getTag();
			
		}
		 //引用列表项的控件视图
//		TextView nameTv=(TextView) convertView.findViewById(R.id.contact_name_tv);
//		TextView numberTv=(TextView)convertView.findViewById(R.id.contact_number_tv);
//		final ImageView iconIv=(ImageView)convertView.findViewById(R.id.contact_icon_iv);
		holderView.holderNameTv.setText(contactInfo.name);
		holderView.holderNumberTv.setText(contactInfo.number);
		final ImageView iconIv=holderView.holderIconIv;
		
		
		//设置当前列表项要展示的数据
		
//		nameTv.setText(contactInfo.name);
//		numberTv.setText(contactInfo.number);
	 /**异步任务类
	 * 1. 三个泛型参数 
	 *   - Params:输入参数 :AsyncTask.execute(params)-->doInBackground(String... params
	 *   - Progress：进度参数  : publishProgress(values): 在子线程发进度----》 onProgressUpdate(Integer... values) :在主线程更新进度
	 *   - Result：结果参数: Bitmap doInBackground()的返回值----》onPostExecute(Bitmap result)
	 * 2. 四个回调的封装方法：
	 *   注意： 除了doInBackground在子线程运行，其他三个方法onPreExecute、onPostExecute、onProgressUpdate在主线程运行
	 *   执行顺序1：   onPreExecute--》doInBackground--》onPostExecute
	 *   执行顺序2：publishProgress(values)--》onProgressUpdate
	 * 3. 线程规则：
	 *   - AsyncTask 的class字节码必须在UI线程加载
	 *   - AsyncTask实例必须在UI线程实例化
	 *   - AsyncTask.execute()方法必须在UI线程执行
	 *   - 不要手动调用四个封装方法
	 *   - AsyncTask.execute()只能执行一次，假如要多次执行，则在new AsyncTask
	 *   
	 */
        new AsyncTask<String, Integer, Bitmap>() {
        	  
        	  //1.  在执行子线程之前初始化操作 ，在UI线程运行
        	 protected void onPreExecute() {
        		 LogUtil.d(TAG, "onPreExecute");
        	 };
            //  2. 在后台执行耗时的操作，即在子线程处理耗时的操作，比如访问网络，访问数据库
			@Override
			protected Bitmap doInBackground(String... params) {
//				HttpURLConnection conn;
//				conn.getContentLength();//取得内容的长度，大小 ，一个要下载的文件的大小
				//在哪里发进度 ，在子线程中发进度，
//				publishProgress(values);
				LogUtil.d(TAG, "doInBackground");
				//访问数据库，获取图片
				Bitmap bitmap= ContactsUtil.getContactPhoto(context, contactInfo.contactId);
				return bitmap;
			}
			// 3. 在子线程处理耗时的操作完毕后，再执行该方法，在UI线程运行
			protected void onPostExecute(Bitmap result) {
				LogUtil.d(TAG, "onPostExecute");
				if(result!=null){
					iconIv.setImageBitmap(result);
				}else{
					iconIv.setImageResource(R.drawable.ic_contact);
				}
			}
			// 4. 更新进度的方法，UI线程处理
			@Override
			protected void onProgressUpdate(Integer... values) {
				//在主线程显示进度
				super.onProgressUpdate(values);
			}
			
		}.execute();
		return convertView;
	}
		
   private static  class   HolderView{
	   ImageView holderIconIv;
	   TextView holderNameTv;
	   TextView holderNumberTv;
   }
	
	
	

}
