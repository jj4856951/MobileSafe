package com.zjw.mobilesafe;

import com.zjw.mobilesafe.utils.EncodeMd5;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {
	private SharedPreferences sp;

	private GridView gv_homelist;
	private MyAdapter adapter;

	private static String[] names = { "�ֻ�����", "ͨѶ��ʿ", "�������", "���̹���", "����ͳ��", "�ֻ�ɱ��", "��������", "�߼�����", "��������" };
	private static int[] names_pic = { R.drawable.safe, R.drawable.callmsgsafe, R.drawable.app, 
			R.drawable.taskmanager,	R.drawable.netmanager, R.drawable.trojan, 
			R.drawable.sysoptimize, R.drawable.atools, R.drawable.settings };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		sp = getSharedPreferences("config", MODE_PRIVATE);

		gv_homelist = (GridView) findViewById(R.id.mm_gv_homelist);
		adapter = new MyAdapter();
		gv_homelist.setAdapter(adapter);
		gv_homelist.setOnItemClickListener(new OnItemClickListener() {
			Intent intent;
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case 0:
					// �����Ի����������롣�ȼ����ľ����������
					showLostFindDialog();
					break;
				case 1:
					intent = new Intent(HomeActivity.this, CallSmsSafeActivity.class);
					startActivity(intent);
					break;
				case 2:
					intent = new Intent(HomeActivity.this, AppManagerActivity.class);
					startActivity(intent);
					break;
				case 3:
					intent = new Intent(HomeActivity.this, TaskManagerActivity.class);
					startActivity(intent);
					break;
				case 4:
					intent = new Intent(HomeActivity.this, TrafficActivity.class);
					startActivity(intent);
					break;
				case 5:
					intent = new Intent(HomeActivity.this, AntiVirusActivity.class);
					startActivity(intent);
					break;
				case 6:
					intent = new Intent(HomeActivity.this, ClearCacheActivity.class);
					startActivity(intent);
					break;
				case 7:
					intent = new Intent(HomeActivity.this, AdvanceToolActivity.class);
					startActivity(intent);
					break;
				case 8:
					intent = new Intent(HomeActivity.this, SettingActivity.class);
					startActivity(intent);
					break;
				}

			}
		});

	}

	protected void showLostFindDialog() {
		// ����Ƿ��Ѿ���������
		if (isPwdSetted()) {
			// ���������룬����������������Ի���
			showInputPwdDialog();
		} else {
			// δ�������룬����������������Ի���
			showSetPwdDialog();
		}
	}

	private TextView mm_pwd_set;
	private TextView mm_pwd_confirm;
	private Button mm_pwd_ok;
	private Button mm_pwd_cancel;
	private AlertDialog dialog;

	/**
	 * ��������Ի���
	 */

	private void showSetPwdDialog() {
		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		View view = View.inflate(HomeActivity.this, R.layout.setuppasswd_layout, null);

		mm_pwd_set = (TextView) view.findViewById(R.id.mm_pwd_set);
		mm_pwd_confirm = (TextView) view.findViewById(R.id.mm_pwd_confirm);
		mm_pwd_ok = (Button) view.findViewById(R.id.mm_pwd_ok);
		mm_pwd_cancel = (Button) view.findViewById(R.id.mm_pwd_cancel);

		mm_pwd_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		mm_pwd_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String pwd = mm_pwd_set.getText().toString().trim();
				String pwd_confirm = mm_pwd_confirm.getText().toString().trim();
				Editor editor = sp.edit();
				if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(pwd_confirm)) {
					Toast.makeText(HomeActivity.this, "���벻��Ϊ��", 0).show();
					return;
				}
				if (pwd.equals(pwd_confirm)) {
					//��¼���룬ȡ���������봰�ڣ�����������ҳ
					editor.putString("password", EncodeMd5.md5(pwd));
					editor.commit();
					dialog.dismiss();
//					Toast.makeText(HomeActivity.this, "GO GO", 0).show();
					enterLostFindPage();
				}else {
					Toast.makeText(HomeActivity.this, "���벻һ��", 0).show();
					return;
				}
			}
		});

		dialog = builder.create();
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
	}

	/**
	 * ��������Ի���
	 */
	private void showInputPwdDialog() {
		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		View view = View.inflate(HomeActivity.this, R.layout.inputpasswd_layout, null);

		mm_pwd_set = (TextView) view.findViewById(R.id.mm_pwd_set);
		mm_pwd_ok = (Button) view.findViewById(R.id.mm_pwd_ok);
		mm_pwd_cancel = (Button) view.findViewById(R.id.mm_pwd_cancel);

		mm_pwd_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		mm_pwd_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String pwd = mm_pwd_set.getText().toString().trim();
				String origin_pwd = sp.getString("password", "");
				if (TextUtils.isEmpty(pwd) || !EncodeMd5.md5(pwd).equals(origin_pwd)) {
					Toast.makeText(HomeActivity.this, "��������", 0).show();
					mm_pwd_set.setText("");
					return;
				}else {
					dialog.dismiss();
//					Toast.makeText(HomeActivity.this, "GOGO", 0).show();
					enterLostFindPage();
				}
			}
		});

//		builder.setView(view);
		// ����Ҫ�õĻ����ǵ�Ҫ����һ��dialog
		dialog = builder.create();
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();

	}

	protected void enterLostFindPage() {
		Intent intent = new Intent(HomeActivity.this, LostFindActivity.class);
		startActivity(intent);
	}

	public boolean isPwdSetted() {
		String password = sp.getString("password", null);
		return !TextUtils.isEmpty(password);
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return names.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(), R.layout.list_item, null);
			ImageView iv = (ImageView) view.findViewById(R.id.mm_iv_item);
			TextView tv = (TextView) view.findViewById(R.id.mm_tv_item);
			iv.setImageResource(names_pic[position]);
			tv.setText(names[position]);

			return view;
		}

	}

}
