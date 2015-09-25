package com.zjw.mobilesafe;

import com.zjw.mobilesafe.ui.SettingItemView;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class Setup2Activity extends BaseSetupActivity {
	
	private TelephonyManager tm;
	private SettingItemView siv_sim;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		
		siv_sim = (SettingItemView) findViewById(R.id.mm_siv_setup2_sim);
		String sum_num = sp.getString("sim_num", null);
		if (TextUtils.isEmpty(sum_num)) {
			siv_sim.setChecked(false);
		}else {
			siv_sim.setChecked(true);
		}
		
		siv_sim.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				if (siv_sim.isChecked()) {
					siv_sim.setChecked(false);
					editor.putString("sim_num", null);
				}else {
					siv_sim.setChecked(true);
					editor.putString("sim_num", tm.getSimSerialNumber());
				}
				editor.commit();
			}
		});
	}

	@Override
	void gotoPre() {
		Intent intent = new Intent(this, Setup1Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.trans_pre_in, R.anim.trans_pre_out);
	}

	@Override
	void gotoNext() {
		String saved_sim = sp.getString("sim_num", null);
		if (TextUtils.isEmpty(saved_sim)) {
			Toast.makeText(this, "sim¿¨Î´°ó¶¨", 0).show();
			return;
		}
		Intent intent = new Intent(this, Setup3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
	}
}
