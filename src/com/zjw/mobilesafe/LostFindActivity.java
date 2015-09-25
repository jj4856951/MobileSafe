package com.zjw.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class LostFindActivity extends Activity {
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean configed = sp.getBoolean("configed", false);
		if (configed) {
			//设置过了
			setContentView(R.layout.activity_lost_find);
			TextView mm_safe_num = (TextView) findViewById(R.id.mm_safe_num);
			ImageView mm_islock = (ImageView) findViewById(R.id.mm_islock);	
			boolean isProtecting = sp.getBoolean("isProtecting", false);
			if (isProtecting) {
				mm_safe_num.setText(sp.getString("safe_num", "无"));
				mm_islock.setImageResource(R.drawable.lock);
			}else {
				mm_safe_num.setText("无");
				mm_islock.setImageResource(R.drawable.unlock);				
			}
			
			
		}else {
			//没设置过，进入向导
			Intent intent = new Intent(LostFindActivity.this, Setup1Activity.class);
			startActivity(intent);
			finish();
		}
	}
	
	public void reEnterSetup(View view) {
		Intent intent = new Intent(LostFindActivity.this, Setup1Activity.class);
		startActivity(intent);
		finish();
	}
	
	
}
