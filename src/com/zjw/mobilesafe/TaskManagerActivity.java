package com.zjw.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import com.zjw.mobilesafe.domain.ProcessInfo;
import com.zjw.mobilesafe.engine.TaskInfoProvider;
import com.zjw.mobilesafe.utils.SystemInfoUtil;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TaskManagerActivity extends Activity {

	private TextView tv_running_process_count;
	private TextView tv_rom;
	private TextView tv_status;
	private LinearLayout ll_loading;
	private ListView lv_task_manager;
	/**
	 * 全部当前进程
	 */
	private List<ProcessInfo> allProcesses;
	private List<ProcessInfo> userList;
	private List<ProcessInfo> systemList;

	private MyAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_manager);

		tv_running_process_count = (TextView) findViewById(R.id.tv_running_process_count);
		tv_rom = (TextView) findViewById(R.id.tv_rom);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		lv_task_manager = (ListView) findViewById(R.id.lv_task_manager);
		tv_status = (TextView) findViewById(R.id.tv_status);

		fillData();

		lv_task_manager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ProcessInfo info;
				if (position == 0) {
					return;
				} else if (position == (userList.size() + 1)) {
					return;
				} else if (position <= userList.size()) {
					info = userList.get(position - 1);
				} else {
					info = systemList.get(position - userList.size() - 2);
				}
				// 不让用户选中自己（杀掉自身进程）
				if (getPackageName().equals(info.getPackName())) {
					return;
				}

				ViewHolder holder = (ViewHolder) view.getTag();
				if (info.isChecked()) {
					info.setChecked(false);
					holder.cb_check.setChecked(false);
				} else {
					info.setChecked(true);
					holder.cb_check.setChecked(true);
				}

			}
		});

		lv_task_manager.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (userList != null && systemList != null) {

					if (firstVisibleItem > userList.size()) {
						tv_status.setText("系统进程：" + systemList.size() + "个");
					} else {
						tv_status.setText("用户进程：" + userList.size() + "个");
					}
				}
			}
		});
	}

	private void fillTitle() {
		long availMem = SystemInfoUtil.getAvailMem(this);
		long totalMem = SystemInfoUtil.getTotalMem(this);
		int runningProcessCount = SystemInfoUtil.getRunningProcessCount(this);
		tv_rom.setText("可用内存/总内存：" + Formatter.formatFileSize(this, availMem) + "/"
				+ Formatter.formatFileSize(this, totalMem));
		tv_running_process_count.setText("当前运行进程数：" + runningProcessCount);
	}

	private void fillData() {
		ll_loading.setVisibility(View.VISIBLE);
		// 耗时
		new Thread(new Runnable() {

			@Override
			public void run() {
				allProcesses = TaskInfoProvider.getProcessList(getApplicationContext());
				userList = new ArrayList<>();
				systemList = new ArrayList<>();

				for (ProcessInfo info : allProcesses) {
					if (info.isUserProcress()) {
						userList.add(info);
					} else {
						systemList.add(info);
					}
				}

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						ll_loading.setVisibility(View.INVISIBLE);
						if (adapter == null) {
							adapter = new MyAdapter();
							lv_task_manager.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}
						fillTitle();
					}
				});
			}
		}).start();
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
			if (sp.getBoolean("systemProcess", false)) {
				return userList.size() + 1 + systemList.size() + 1;				
			}else {
				return userList.size() + 1;
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ProcessInfo info;
			if (position == 0) {
				TextView tv = new TextView(getApplicationContext());
				tv.setText("用户进程：" + userList.size() + "个");
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				return tv;
			} else if (position == (userList.size() + 1)) {
				TextView tv = new TextView(getApplicationContext());
				tv.setText("系统进程：" + systemList.size() + "个");
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				return tv;
			} else if (position <= userList.size()) {
				info = userList.get(position - 1);
			} else {
				info = systemList.get(position - userList.size() - 2);
			}

			View view = null;
			ViewHolder holder = null;
			if (convertView != null && convertView instanceof RelativeLayout) {
				// 满足以上两个条件方可复用
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				holder = new ViewHolder();
				view = View.inflate(getApplicationContext(), R.layout.list_item_task_info, null);
				holder.iv_task_icon = (ImageView) view.findViewById(R.id.iv_task_icon);
				holder.tv_task_name = (TextView) view.findViewById(R.id.tv_task_name);
				holder.tv_rom_ocupy = (TextView) view.findViewById(R.id.tv_rom_ocupy);
				holder.cb_check = (CheckBox) view.findViewById(R.id.cb_check);
				view.setTag(holder);
			}

			holder.iv_task_icon.setImageDrawable(info.getIcon());
			holder.tv_task_name.setText(info.getName());
			holder.tv_rom_ocupy.setText("占用内存：" + Formatter.formatFileSize(getApplicationContext(), info.getMemSize()));
			holder.cb_check.setChecked(info.isChecked());
			// 不让用户选中自己（杀掉自身进程）
			if (getPackageName().equals(info.getPackName())) {
				holder.cb_check.setVisibility(View.INVISIBLE);
			} else {
				holder.cb_check.setVisibility(View.VISIBLE);
			}

			return view;
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
	}

	static class ViewHolder {
		TextView tv_task_name;
		TextView tv_rom_ocupy;
		ImageView iv_task_icon;
		CheckBox cb_check;
	}

	public void selestAll(View view) {
		for (ProcessInfo info : allProcesses) {
			// 不让用户选中自己（杀掉自身进程）
			if (getPackageName().equals(info.getPackName())) {
				continue;
			}
			info.setChecked(true);
		}
		adapter.notifyDataSetChanged();
	}

	public void unSelect(View view) {
		for (ProcessInfo info : allProcesses) {
			// 不让用户选中自己（杀掉自身进程）
			if (getPackageName().equals(info.getPackName())) {
				continue;
			}
			info.setChecked(!info.isChecked());
		}
		adapter.notifyDataSetChanged();
	}

	public void clear(View view) {
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (ProcessInfo info : allProcesses) {
			if (info.isChecked()) {
				am.killBackgroundProcesses(info.getPackName());
			}
		}
		fillData();// 重新刷新界面
	}

	public void config(View view) {
		Intent intent = new Intent(this, TaskSettingActivity.class);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		adapter.notifyDataSetChanged();
	}
}
