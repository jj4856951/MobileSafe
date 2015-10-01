package com.zjw.mobilesafe.test;

import java.util.List;

import com.zjw.mobilesafe.db.BlackNumberDBOpenHelper;
import com.zjw.mobilesafe.db.dao.BlackNumberDao;
import com.zjw.mobilesafe.domain.BlackNumberInfo;

import android.test.AndroidTestCase;

public class BlackNumberDBTest extends AndroidTestCase {
	public void testDBCreate() {
		BlackNumberDBOpenHelper helper = new BlackNumberDBOpenHelper(getContext());
		helper.getWritableDatabase();
	}
	
	public void add() {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		for(int i = 0; i< 100; i++){
			dao.add("1880000000"+i, String.valueOf((int)(Math.random()*4)));
		}
	}
	
	public void findAll() {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		List<BlackNumberInfo> list = dao.findAll();
		for (BlackNumberInfo blackNumberInfo : list) {
			System.out.println(blackNumberInfo.toString());
		}
	}

}
