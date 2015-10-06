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
	 * ��ѯ�ú����Ƿ�Ϊ�������ϵ�
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
	 * ��ѯĳ�ں����ģʽ
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
	 * ��ѯȫ������������
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
	 * ������ȡ����
	 * @param offset ���ĸ�λ�ÿ�ʼ��ȡ����
	 * @param max һ�λ�ȡ����������
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
	 * ��Ӻ���������
	 * @param number ����������
	 * @param mode ����ģʽ 1.�绰���� 2.�������� 3.ȫ������
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
	 * �޸ĺ��������������ģʽ
	 * @param number Ҫ�޸ĵĺ���������
	 * @param newmode �µ�����ģʽ
	 */
	public void update(String number,String newmode){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", newmode);
		db.update("blacknumber", values, "number=?", new String[]{number});
		db.close();
	}
	/**
	 * ɾ������������
	 * @param number Ҫɾ���ĺ���������
	 */
	public void delete(String number){
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("blacknumber",  "number=?", new String[]{number});
		db.close();
	}
}

