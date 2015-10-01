package com.zjw.mobilesafe.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.ProgressBar;

/**
 * 短信工具类
 * 
 * @author Lincoln
 *
 */
public class SmsUtils {
	public interface SmsCallback {
		public void beforeBackup(int max);

		public void onBackup(int currentProgress);
	}

	private static final String TAG = "SmsUtils";

	public static void backSMS(Context context, SmsCallback callback) throws Exception {
		File file = new File(Environment.getExternalStorageDirectory(), "backup.xml");
		FileOutputStream fos = new FileOutputStream(file);
		// 序列化xml文件——序列化即：将内存中的文件写到文件
		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(fos, "utf-8");
		serializer.startDocument("utf-8", true);
		serializer.startTag(null, "smss");
		Uri uri = Uri.parse("content://sms/");
		Cursor cursor = context.getContentResolver().query(uri, new String[] { "date", "body", "type", "address" },
				null, null, null);
		int count = cursor.getCount();
		serializer.attribute(null, "length", count + "");
		// pb.setMax(count);
		callback.beforeBackup(count);
		int tmp = 0;
		if (cursor != null && count > 0) {
			while (cursor.moveToNext()) {
				Thread.sleep(100);
				String date = cursor.getString(0);
				String body = cursor.getString(1);
				String type = cursor.getString(2);
				String address = cursor.getString(3);

				serializer.startTag(null, "sms");
				serializer.startTag(null, "date");
				serializer.text(date);
				serializer.endTag(null, "date");

				serializer.startTag(null, "address");
				serializer.text(address);
				serializer.endTag(null, "address");

				serializer.startTag(null, "body");
				serializer.text(body);
				serializer.endTag(null, "body");

				serializer.startTag(null, "type");
				serializer.text(type);
				serializer.endTag(null, "type");

				serializer.endTag(null, "sms");
				tmp++;
				// pb.setProgress(tmp);
				callback.onBackup(tmp);
			}
			cursor.close();
		}

		serializer.endTag(null, "smss");
		serializer.endDocument();
		fos.close();
	}

	public static void recoverSMS(Context context, boolean flag, SmsCallback callback) throws Exception {

		String date = null;
		String body = null;
		String type = null;
		String address = null;
		int tmp = 0;
		Uri uri = Uri.parse("content://sms/");
		if (flag) {
			//恢复前先删除旧的短信
			context.getContentResolver().delete(uri, null, null);
		}

		File file = new File(Environment.getExternalStorageDirectory(), "backup.xml");
		XmlPullParser parser = Xml.newPullParser();
		FileReader reader = null;

		reader = new FileReader(file);
		parser.setInput(reader);
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			String name = parser.getName();

			switch (eventType) {
			case XmlPullParser.START_TAG:
				if (name.equals("smss")) {
					String length = parser.getAttributeValue(0);
					// Log.e(TAG, length);
					callback.beforeBackup(Integer.parseInt(length));
				} else if (name.equals("date")) {// "date", "body", "type",
													// "address"
					date = parser.nextText();
				} else if (name.equals("body")) {
					body = parser.nextText();
				} else if (name.equals("type")) {
					type = parser.nextText();
				} else if (name.equals("address")) {
					address = parser.nextText();
				}
				break;

			case XmlPullParser.END_TAG:
				if (name.equals("sms")) {
					ContentValues values = new ContentValues();
					values.put("date", date);
					values.put("body", body);
					values.put("type", type);
					values.put("address", address);
					context.getContentResolver().insert(uri, values);
					tmp++;
					callback.onBackup(tmp);
				}
				break;
			}
			eventType = parser.next();
			Thread.sleep(100);
		}

		if (reader != null) {
			reader.close();
		}

	}

}
