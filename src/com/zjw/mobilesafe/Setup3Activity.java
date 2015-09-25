package com.zjw.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Setup3Activity extends BaseSetupActivity {
	private EditText mm_chosed_phone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		mm_chosed_phone = (EditText) findViewById(R.id.mm_chosed_phone);
		mm_chosed_phone.setText(sp.getString("safe_num", ""));
	}

	@Override
	void gotoPre() {
		Intent intent = new Intent(this, Setup2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.trans_pre_in, R.anim.trans_pre_out);
	}

	@Override
	void gotoNext() {
		String choosedNum = mm_chosed_phone.getText().toString().trim();
		if (TextUtils.isEmpty(choosedNum)) {
			Toast.makeText(this, "安全号码尚未设置", 0).show();
			return;
		}
		
		Intent intent = new Intent(this, Setup4Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
	}
	
	public void chooseContacts(View view) {
		Intent intent = new Intent(this, ChooseContactsActivity.class);
		startActivityForResult(intent, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {
			return;//不然按返回键会报错
		}
		String chosedContact = data.getStringExtra("phone").replace("-", "");
		mm_chosed_phone.setText(chosedContact);
		Editor edit = sp.edit();
		edit.putString("safe_num", chosedContact);
		edit.commit();
		super.onActivityResult(requestCode, resultCode, data);
	}
}
