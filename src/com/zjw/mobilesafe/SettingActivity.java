package com.zjw.mobilesafe;

import com.zjw.mobilesafe.service.AddressShowService;
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
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
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
		
		siv_incomming_address_show = (SettingItemView) findViewById(R.id.mm_siv_incomming_address_show);
		boolean serviceRunning = CheckIsServiceRunning.isRunning(this, "com.zjw.mobilesafe.service.AddressShowService");
		if (serviceRunning) {
			siv_incomming_address_show.setChecked(true);
		}else {
			siv_incomming_address_show.setChecked(false);
		}
		
		siv_incomming_address_show.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this, AddressShowService.class);
				if (siv_incomming_address_show.isChecked()) {
					siv_incomming_address_show.setChecked(false);
					stopService(intent);
				}else {
					siv_incomming_address_show.setChecked(true);					
					startService(intent);
				}
			}
		});
		
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
