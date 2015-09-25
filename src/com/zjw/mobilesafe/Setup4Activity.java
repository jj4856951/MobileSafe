package com.zjw.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Setup4Activity extends BaseSetupActivity {
	private SharedPreferences sp;
	private CheckBox mm_isrunning_protect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		mm_isrunning_protect = (CheckBox) findViewById(R.id.mm_isrunning_protect);
		boolean isChecked = sp.getBoolean("isProtecting", false);
		mm_isrunning_protect.setChecked(isChecked);
		mm_isrunning_protect.setText(isChecked?"防盗保护已开启":"防盗保护未开启");
		mm_isrunning_protect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mm_isrunning_protect.setText(isChecked?"防盗保护已开启":"防盗保护未开启");
				Editor editor = sp.edit();
				editor.putBoolean("isProtecting", isChecked);
				editor.commit();
			}
		});
	}

	@Override
	void gotoPre() {
		Intent intent = new Intent(this, Setup3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.trans_pre_in, R.anim.trans_pre_out);
	}

	@Override
	void gotoNext() {
		Editor editor = sp.edit();
		editor.putBoolean("configed", true);
		editor.commit();
		Intent intent = new Intent(this, LostFindActivity.class);
		startActivity(intent);
		finish();
	}
	
}
