package com.myapp.phonesafe.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;

import com.myapp.phonesafe.entity.ContactInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * 联系人的工具类
 * 
 * 
 */
public class ContactsUtil {
	// 取得手机中所有的联系人信息，同Contentprovider 来获取
	public static List<ContactInfo> getAllContacts(Context context) {
		List<ContactInfo> data = null;

		// sqlite 默认的数据类型：
		// integer，real（实数），text，null，blob（二进制的大数据），因为sqlite的数据格式比较松散，不严谨
		// ，也支持varchar等
		// 取得内容访问者对象
		ContentResolver cr = context.getContentResolver();

		Uri contactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;// 取得访问电话数据的uri
		String[] projection = {
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.NUMBER,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID };
		/**
		 * 通过内容访问者来访问内容提供者 uri： uri
		 */
		Cursor cursor = cr.query(contactUri, projection, null, null, null);

		// 假如有记录，则构建List对象
		if (cursor.getCount() > 0) {
			data = new ArrayList<ContactInfo>();

		}

		// 遍历游标
		while (cursor.moveToNext()) {
			ContactInfo contactInfo = new ContactInfo();
			String name = cursor
					.getString(cursor
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			String number = cursor
					.getString(cursor
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			int contactId = cursor
					.getInt(cursor
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
			contactInfo.name = name;
			contactInfo.number = number;
			contactInfo.contactId = contactId;
			// System.out.println(name+number);

			data.add(contactInfo);
		}

		// 关闭游标
		if (cursor != null) {
			cursor.close();
		}
		return data;
	}

	// 获取联系人的头像图片
	public static Bitmap getContactPhoto(Context context, int contactId) {
		Bitmap bitmap = null;
		// 取得内容访问者
		ContentResolver cr = context.getContentResolver();
		// 访问联系人图片的uri content://contacts/id
		// 在uri的尾部添加id ，返回的是新的uri

		Uri contactPhotoUri = ContentUris.withAppendedId(
				ContactsContract.Contacts.CONTENT_URI, contactId);
		InputStream is = ContactsContract.Contacts.openContactPhotoInputStream(
				cr, contactPhotoUri);
		if (is != null) {
			bitmap = BitmapFactory.decodeStream(is);
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return bitmap;
	}

	// 获取所有的联系人信息，返回的是游标
	public static Cursor getContactsByCursor(Context baseContext) {
		// TODO Auto-generated method stub
		ContentResolver cr = baseContext.getContentResolver();

		Uri contactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;// 取得访问电话数据的uri
		String[] projection = {
				ContactsContract.CommonDataKinds.Phone._ID,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.NUMBER,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID };
		/**
		 * 通过内容访问者来访问内容提供者 uri： uri
		 */
		return cr.query(contactUri, projection, null, null, null);
		
	}

}
