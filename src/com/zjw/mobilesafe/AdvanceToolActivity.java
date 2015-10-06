package com.zjw.mobilesafe;

import com.zjw.mobilesafe.utils.SmsUtils;
import com.zjw.mobilesafe.utils.SmsUtils.SmsCallback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class AdvanceToolActivity extends Activity {
	private ProgressDialog pd;
	private ProgressBar pb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_advance_tool);
	}

	public void numberQuery(View view) {
		Intent intent = new Intent(this, QueryNumAddressActivity.class);
		startActivity(intent);	
	}
	
	/**
	 * 短信的备份逻辑
	 * @param view
	 */
	public void smsBackup(View view) {
		pb = (ProgressBar) findViewById(R.id.progressBar1);
		pd = new ProgressDialog(this);
		pd.setTitle("正在备份...");
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.show();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					SmsUtils.backSMS(AdvanceToolActivity.this, new SmsCallback(){

						@Override
						public void beforeBackup(int max) {
							pb.setMax(max);
							pd.setMax(max);
						}

						@Override
						public void onBackup(int currentProgress) {
							pd.setProgress(currentProgress);
							pb.setProgress(currentProgress);
							
						}
						
					});
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(AdvanceToolActivity.this, "备份成功", 0).show();
							pd.dismiss();
						}
					});
				} catch (Exception e) {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(AdvanceToolActivity.this, "备份失败", 0).show();
							pd.dismiss();
						}
					});
					e.printStackTrace();
				}
				
			}
		}).start();
	}
	/**
	 * 短信的还原逻辑
	 * @param view
	 */
	public void smsRecover(View view) {
		pd = new ProgressDialog(this);
		pd.setTitle("正在备份...");
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.show();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					SmsUtils.recoverSMS(getApplicationContext(), true, new SmsCallback() {
						
						@Override
						public void onBackup(int currentProgress) {
							pd.setProgress(currentProgress);
							
						}
						
						@Override
						public void beforeBackup(int max) {
							pd.setMax(max);
							
						}
					});
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(getApplicationContext(), "恢复成功", 0).show();
							pd.dismiss();
						}
					});
				} catch (Exception e) {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(getApplicationContext(), "恢复失败", 0).show();
							pd.dismiss();
						}
					});
				}
				
			}
		}).start();
	}
}
