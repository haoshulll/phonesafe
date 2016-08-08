package com.myapp.phonesafe.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapp.phonesafe.R;
import com.myapp.phonesafe.utils.ContactsUtil;


public class ContactCursorAdapter extends CursorAdapter {
	/**
	 * 构造方法 context：上下文
	 * 
	 * @param context
	 * @param c
	 *            ：Cursor：游标
	 * @param flags
	 *            ：是否注册内容观察者模式
	 */
	public ContactCursorAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
	}

	protected static final String TAG = "ContactAdapter";

	/**
	 * 绑定列表项视图 ，绑定数据给 列表项视图的子控件 view ：当前列表项视图对象
	 */
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView nameTv = (TextView) view.findViewById(R.id.contact_name_tv);
		TextView numberTv = (TextView) view
				.findViewById(R.id.contact_number_tv);
		ImageView iconIv = (ImageView) view.findViewById(R.id.contact_icon_iv);

		// 对子控件设置值
		String name = cursor
				.getString(cursor
						.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
		String number = cursor.getString(cursor
				.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		int contactId = cursor
				.getInt(cursor
						.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));

		nameTv.setText(name);
		numberTv.setText(number);

		Bitmap bitmap = ContactsUtil.getContactPhoto(context, contactId);
		if (bitmap != null) {
			iconIv.setImageBitmap(bitmap);
		} else {
			iconIv.setImageResource(R.drawable.ic_contact);
		}

	}

	// 返回 列表项视图对象
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return LayoutInflater.from(context).inflate(R.layout.item_contact,
				parent, false);

	}

}
