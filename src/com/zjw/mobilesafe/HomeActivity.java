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

	private static String[] names = { "手机防盗", "通讯卫士", "软件管理", "进程管理", "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心" };
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
					// 弹出对话框，设置密码。先检查有木有设置密码
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
		// 检查是否已经设置密码
		if (isPwdSetted()) {
			// 设置了密码，弹出的是输入密码对话框
			showInputPwdDialog();
		} else {
			// 未设置密码，弹出的是设置密码对话框
			showSetPwdDialog();
		}
	}

	private TextView mm_pwd_set;
	private TextView mm_pwd_confirm;
	private Button mm_pwd_ok;
	private Button mm_pwd_cancel;
	private AlertDialog dialog;

	/**
	 * 设置密码对话框
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
					Toast.makeText(HomeActivity.this, "密码不能为空", 0).show();
					return;
				}
				if (pwd.equals(pwd_confirm)) {
					//记录密码，取消设置密码窗口，进入设置主页
					editor.putString("password", EncodeMd5.md5(pwd));
					editor.commit();
					dialog.dismiss();
//					Toast.makeText(HomeActivity.this, "GO GO", 0).show();
					enterLostFindPage();
				}else {
					Toast.makeText(HomeActivity.this, "密码不一致", 0).show();
					return;
				}
			}
		});

		dialog = builder.create();
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
	}

	/**
	 * 输入密码对话框
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
					Toast.makeText(HomeActivity.this, "密码有误", 0).show();
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
		// 上面要用的话，记得要返回一个dialog
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
