package com.zjw.mobilesafe;

import com.zjw.mobilesafe.service.AddressShowService;
import com.zjw.mobilesafe.service.CallSmsSafeService;
import com.zjw.mobilesafe.ui.SettingClickView;
import com.zjw.mobilesafe.ui.SettingItemView;
import com.zjw.mobilesafe.utils.CheckIsServiceRunning;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity {
	private SettingItemView siv;
	private SettingItemView siv_incomming_address_show;
	private SettingClickView scv_address_background;
	private SettingItemView siv_black_number;
	
	
	private SharedPreferences sp;
	private Intent addressIntent;
	private Intent callSmsSafeIntent;
	
	@Override
	protected void onResume() {
		super.onResume();
		addressIntent = new Intent(SettingActivity.this, AddressShowService.class);
		boolean address_show_serviceRunning = CheckIsServiceRunning.isRunning(this, "com.zjw.mobilesafe.service.AddressShowService");
		
		if(address_show_serviceRunning){
			//��������ķ����ǿ�����
			siv_incomming_address_show.setChecked(true);
		}else{
			siv_incomming_address_show.setChecked(false);
		}
		
		/**
		 * �жϺ����������Ƿ��ڿ���״̬
		 */
		boolean call_sms_safe_service_running = CheckIsServiceRunning.isRunning(this, "com.zjw.mobilesafe.service.CallSmsSafeService");
		siv_black_number.setChecked(call_sms_safe_service_running);
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		/**
		 * ���ú�������ط���
		 */
		siv_black_number = (SettingItemView) findViewById(R.id.mm_siv_black_number);
		callSmsSafeIntent = new Intent(SettingActivity.this, CallSmsSafeService.class);
		siv_black_number.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (siv_black_number.isChecked()) {
					siv_black_number.setChecked(false);
					stopService(callSmsSafeIntent);
				}else {
					siv_black_number.setChecked(true);
					startService(callSmsSafeIntent);
				}
			}
		});
		
		
		/**
		 * �������������˾�ı���
		 */
		scv_address_background = (SettingClickView) findViewById(R.id.mm_scv_address_background);
		final String [] items = {"��͸��","������","��ʿ��","������","ƻ����"};
		scv_address_background.changeDescripion(items[sp.getInt("which", 0)]);
		scv_address_background.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new Builder(SettingActivity.this);
				
				builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//����ѡ�����
						Editor editor = sp.edit();
						editor.putInt("which", which);
						editor.commit();
						
						scv_address_background.changeDescripion(items[which]);
						dialog.dismiss();
					}
					
				});
				builder.setNegativeButton("cancel", null);
				builder.show();
			}
		});
		
		/**
		 * ���������������Ƿ���
		 */
		siv_incomming_address_show = (SettingItemView) findViewById(R.id.mm_siv_incomming_address_show);
		boolean serviceRunning = CheckIsServiceRunning.isRunning(this, "com.zjw.mobilesafe.service.AddressShowService");
		if (serviceRunning) {
			siv_incomming_address_show.setChecked(true);
		}else {
			siv_incomming_address_show.setChecked(false);
		}
		
		/**
		 * �������ر������������
		 */
		siv_incomming_address_show.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addressIntent = new Intent(SettingActivity.this, AddressShowService.class);
				if (siv_incomming_address_show.isChecked()) {
					siv_incomming_address_show.setChecked(false);
					stopService(addressIntent);
				}else {
					siv_incomming_address_show.setChecked(true);					
					startService(addressIntent);
				}
			}
		});
		
		/**
		 * ����Զ������Ƿ���
		 */
		siv = (SettingItemView) findViewById(R.id.mm_siv_update);
		
		boolean update = sp.getBoolean("update", false);
		if (update) {
			//��ѡ�ŵ�
			siv.setChecked(true);
//			siv.changeDescripion("�Զ������ѿ���");
		}else {
			//δ��ѡ��
			siv.setChecked(false);
//			siv.changeDescripion("�Զ������ѹر�");
		}
		
		/**
		 * ������ر��Զ�����
		 */
		siv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				if (siv.isChecked()) {
					//�ѹ�ѡ
					siv.setChecked(false);
//					siv.changeDescripion("�Զ������ѹر�");
					editor.putBoolean("update", false);
				}else {
					//δ��ѡ
					siv.setChecked(true);
//					siv.changeDescripion("�Զ������ѿ���");
					editor.putBoolean("update", true);
				}
				editor.commit();
			}
		});
	}
}
