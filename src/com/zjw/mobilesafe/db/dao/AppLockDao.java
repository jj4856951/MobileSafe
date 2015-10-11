package com.zjw.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.zjw.mobilesafe.db.AppLockDBOpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AppLockDao {
	private AppLockDBOpenHelper helper;
	private Context context;

	public AppLockDao(Context context) {
		helper = new AppLockDBOpenHelper(context);
		this.context = context;
	}
	
	public void add(String packname) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("packname", packname);
		db.insert("applock", null, values);
		db.close();
		Intent intent = new Intent();
		intent.setAction("com.zjw.mobilesafe.appLockListChanged");
		context.sendBroadcast(intent);
	}
	
	public void delete(String packname) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("applock", "packname=?", new String[]{packname});
		db.close();
		Intent intent = new Intent();
		intent.setAction("com.zjw.mobilesafe.appLockListChanged");
		context.sendBroadcast(intent);
	}
	
	public boolean find(String packname) {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("applock", null, "packname=?", new String[]{packname}, null, null, null);
		if (cursor.moveToNext()) {
			cursor.close();
			return true;
		}
		return false;
	}
	//从内存中查询，比直接从数据库查询效率高
	public List<String> findAll() {
		List<String> list = new ArrayList<String>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("applock", new String[]{"packname"}, null, null, null, null, null);
		while(cursor.moveToNext()){
			list.add(cursor.getString(0));
		}
		return list;
	}
}
