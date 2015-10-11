package com.zjw.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ScanVirusDBDao {

	private static String path = "/data/data/com.zjw.mobilesafe/files/antivirus.db";
	public static boolean isVirus(String md5) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select * from datable where md5=?", new String[]{md5});
		boolean res = false;
		if (cursor.moveToNext()) {
			res = true;
		}
		cursor.close();
		db.close();
		return res;
	}
}
