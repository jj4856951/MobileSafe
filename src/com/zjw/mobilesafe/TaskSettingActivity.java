package com.zjw.mobilesafe;

import com.zjw.mobilesafe.service.AutoCleanService;
import com.zjw.mobilesafe.utils.CheckIsServiceRunning;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class TaskSettingActivity extends Activity {

	private CheckBox cb_show_system_process;
	private CheckBox cb_auto_clean;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_setting);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		cb_show_system_process = (CheckBox) findViewById(R.id.cb_show_system_process);
		cb_auto_clean = (CheckBox) findViewById(R.id.cb_auto_clean);
		
		cb_show_system_process.setChecked(sp.getBoolean("systemProcess", false));
		cb_show_system_process.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Editor editor = sp.edit();
				editor.putBoolean("systemProcess", isChecked);
				editor.commit();
			}
		});
		
		cb_auto_clean.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//实现锁屏清理进程
				//定义一个服务，在服务中定义一个广播接受者。注：锁屏广播特殊，不能定义在清单文件中
				Intent intent = new Intent(TaskSettingActivity.this, AutoCleanService.class);
				if (isChecked) {
					startService(intent);
				}else {
					stopService(intent);
				}
			}
		});
	}

	@Override
	protected void onResume() {
		boolean running = CheckIsServiceRunning.isRunning(this, "com.zjw.mobilesafe.service.AutoCleanService");
		cb_auto_clean.setChecked(running);
		super.onResume();
	}
}
