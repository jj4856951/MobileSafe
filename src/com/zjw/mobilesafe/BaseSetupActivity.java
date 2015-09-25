package com.zjw.mobilesafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;

public abstract class BaseSetupActivity extends Activity {
	private GestureDetector detector;
	protected SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		detector = new GestureDetector(this, new SimpleOnGestureListener(){

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				float rawX1 = e1.getRawX();
				float rawX2 = e2.getRawX();
				if (rawX1 - rawX2 > 200) {
					//下一页，往右hua
					gotoNext();
					return true;
				}
				if (rawX2-rawX1>200) {
					//上一页，往左hua
					gotoPre();
					return true;
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});
	}
	
	abstract void gotoPre();

	abstract void gotoNext();
	
	public void click(View view) {
		gotoNext();
	}
	public void pre(View view) {
		gotoPre();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		detector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
	
}
