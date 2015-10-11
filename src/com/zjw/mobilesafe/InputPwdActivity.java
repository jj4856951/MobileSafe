package com.zjw.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class InputPwdActivity extends Activity {
	
	private EditText ed;
	private Button btn;
	private String packname;
	private TextView tv_packname;
	private PackageManager packageManager;
	private ImageView iv_icon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inputpwd_watchdog);
		
		ed = (EditText) findViewById(R.id.ed1);
		tv_packname = (TextView) findViewById(R.id.tv_packname);
		iv_icon = (ImageView) findViewById(R.id.iv_icoon);
		
		Intent intent = getIntent();
		packname = intent.getStringExtra("packname");
		tv_packname.setText(packname);
		packageManager = getPackageManager();
		try {
			ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packname, 0);
			iv_icon.setImageDrawable(applicationInfo.loadIcon(packageManager));
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void click(View view){
		String pwd = ed.getText().toString();
		if (!"123".equals(pwd)) {
			Toast.makeText(this, "密码有误", 0).show();
			return;
		}
		//此时要告诉看门狗，暂时停止对此程序的监管
		//可以用服务绑定的方法，但是太麻烦。可以用自定义广播
		Intent intent = new Intent();
		intent.setAction("com.zjw.mobilesafe.tmpstop");
		intent.putExtra("packname", packname);
		sendBroadcast(intent);
		finish();
	}
	
	@Override
	public void onBackPressed() {
//        <action android:name="android.intent.action.MAIN" />
//        <category android:name="android.intent.category.HOME" />
//        <category android:name="android.intent.category.DEFAULT" />
//        <category android:name="android.intent.category.MONKEY"/>
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addCategory("android.intent.category.MONKEY");
		startActivity(intent);
		super.onBackPressed();
	}
	
	@Override
	protected void onStop() {
		finish();
		super.onStop();
	}
	
}
