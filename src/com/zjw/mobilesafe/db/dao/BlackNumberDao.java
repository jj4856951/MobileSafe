package com.zjw.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.zjw.mobilesafe.db.BlackNumberDBOpenHelper;
import com.zjw.mobilesafe.domain.BlackNumberInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BlackNumberDao {
	private BlackNumberDBOpenHelper helper;

	public BlackNumberDao(Context context) {
		helper = new BlackNumberDBOpenHelper(context);
	}
	
	/**
	 * 查询该号码是否为黑名单上的
	 * @param number
	 * @return
	 */
	public boolean find(String number) {
		SQLiteDatabase database = helper.getReadableDatabase();
		Cursor cursor = database.rawQuery("select * from blacknumber where number = ?", new String[]{number});
		if (cursor.moveToNext()) {
			return true;
		}
		cursor.close();
		database.close();
		return false;
	}
	
	/**
	 * 查询某黑号码的模式
	 * @param number
	 * @return
	 */
	public String findMode(String number) {
		String mode = null;
		SQLiteDatabase database = helper.getReadableDatabase();
		Cursor cursor = database.rawQuery("select mode from blacknumber where number = ?", new String[]{number});
		if (cursor.moveToNext()) {
			mode = cursor.getString(0);
		}
		cursor.close();
		database.close();
		return mode;
	}
	/**
	 * 查询全部黑名单号码
	 * @return
	 */
	public List<BlackNumberInfo> findAll() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select number,mode from blacknumber order by _id desc", null);
		while(cursor.moveToNext()){
			String number = cursor.getString(0);
			String mode = cursor.getString(1);
			BlackNumberInfo info = new BlackNumberInfo(number, mode);
			list.add(info);
		}
		cursor.close();
		db.close();
		return list;
	}

	/**
	 * 分批获取数据
	 * @param offset 从哪个位置开始获取数据
	 * @param max 一次获取多少条数据
	 * @return
	 */
	public List<BlackNumberInfo> findPart(int offset, int max) {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select number,mode from blacknumber order by _id desc limit ? offset ?", new String[]{String.valueOf(max), String.valueOf(offset)});
		while(cursor.moveToNext()){
			String number = cursor.getString(0);
			String mode = cursor.getString(1);
			BlackNumberInfo info = new BlackNumberInfo(number, mode);
			list.add(info);
		}
		cursor.close();
		db.close();
		return list;
	}

	/**
	 * 添加黑名单号码
	 * @param number 黑名单号码
	 * @param mode 拦截模式 1.电话拦截 2.短信拦截 3.全部拦截
	 */
	public void add(String number,String mode){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("number", number);
		values.put("mode", mode);
		db.insert("blacknumber", null, values);
		db.close();
	}
	/**
	 * 修改黑名单号码的拦截模式
	 * @param number 要修改的黑名单号码
	 * @param newmode 新的拦截模式
	 */
	public void update(String number,String newmode){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", newmode);
		db.update("blacknumber", values, "number=?", new String[]{number});
		db.close();
	}
	/**
	 * 删除黑名单号码
	 * @param number 要删除的黑名单号码
	 */
	public void delete(String number){
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("blacknumber",  "number=?", new String[]{number});
		db.close();
	}
}

