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
	 * ���ŵı����߼�
	 * @param view
	 */
	public void smsBackup(View view) {
		pb = (ProgressBar) findViewById(R.id.progressBar1);
		pd = new ProgressDialog(this);
		pd.setTitle("���ڱ���...");
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
							Toast.makeText(AdvanceToolActivity.this, "���ݳɹ�", 0).show();
							pd.dismiss();
						}
					});
				} catch (Exception e) {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(AdvanceToolActivity.this, "����ʧ��", 0).show();
							pd.dismiss();
						}
					});
					e.printStackTrace();
				}
				
			}
		}).start();
	}
	/**
	 * ���ŵĻ�ԭ�߼�
	 * @param view
	 */
	public void smsRecover(View view) {
		pd = new ProgressDialog(this);
		pd.setTitle("���ڻָ�...");
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.show();
		//��ʱ
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					SmsUtils.recoverSMS(AdvanceToolActivity.this, true, new SmsCallback() {
						
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
							Toast.makeText(AdvanceToolActivity.this, "�ָ��ɹ�", 0).show();
							pd.dismiss();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(AdvanceToolActivity.this, "�ָ�ʧ��", 0).show();
							pd.dismiss();
						}
					});
				}
			}
		}).start();
	}
	
}
