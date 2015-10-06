package com.zjw.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import com.zjw.mobilesafe.engine.AppInfo;
import com.zjw.mobilesafe.engine.AppInfoProvider;
import com.zjw.mobilesafe.utils.DensityUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AppManagerActivity extends Activity implements OnClickListener {

	private static final String TAG = "AppManagerActivity";
	private TextView tv_avail_rom;
	private TextView tv_avail_sd;
	private ListView lv_app_manager;
	private LinearLayout ll_loading;
	private List<AppInfo> appInfos;
	private LinearLayout ll_uninstall;
	private LinearLayout ll_share;
	private LinearLayout ll_start;
	private AppInfoAdapter myAdapter;
	/**
	 * 点击listview时所获取的item项对应的appinfo
	 */
	private AppInfo info;

	/**
	 * 用户程序
	 */
	private List<AppInfo> userAppLists;
	/**
	 * 系统程序
	 */
	private List<AppInfo> systemAppLists;

	/**
	 * 固定在上方的程序个数提示状态
	 */
	private TextView tv_status;

	/**
	 * 弹出的小窗体
	 */
	private PopupWindow pw;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		tv_avail_rom = (TextView) findViewById(R.id.tv_avail_rom);
		tv_avail_sd = (TextView) findViewById(R.id.tv_avaul_sd);
		lv_app_manager = (ListView) findViewById(R.id.lv_app_manager);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		tv_status = (TextView) findViewById(R.id.tv_status);

		long romSize = getAvailSpace(Environment.getExternalStorageDirectory().getAbsolutePath());
		long sdSize = getAvailSpace(Environment.getDataDirectory().getAbsolutePath());
		tv_avail_rom.setText("手机可用空间：" + Formatter.formatFileSize(this, romSize));
		tv_avail_sd.setText("SD卡可用空间：" + Formatter.formatFileSize(this, sdSize));

		ll_loading.setVisibility(View.VISIBLE);
		fillData();

		lv_app_manager.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				dismissPopupWindow();
				if (userAppLists != null && systemAppLists != null) {

					if (firstVisibleItem > userAppLists.size()) {
						tv_status.setText("系统程序：" + systemAppLists.size() + "个");
					} else {
						tv_status.setText("用户程序：" + userAppLists.size() + "个");
					}
				}
			}
		});

		lv_app_manager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				dismissPopupWindow();

				if (position == 0 || position == userAppLists.size() + 1) {
					return;
				} else if (position <= userAppLists.size()) {
					info = userAppLists.get(position - 1);
				} else {
					info = systemAppLists.get(position - userAppLists.size() - 2);
				}
				// System.out.println(info.getPackname());
				// TextView contentView = new TextView(getApplicationContext());
				// contentView.setText(info.getPackname());
				// contentView.setBackgroundColor(Color.RED);
				View contentView = View.inflate(getApplicationContext(), R.layout.pop_app_item, null);
				ll_share = (LinearLayout) contentView.findViewById(R.id.ll_share);
				ll_start = (LinearLayout) contentView.findViewById(R.id.ll_start);
				ll_uninstall = (LinearLayout) contentView.findViewById(R.id.ll_uninstall);

				ll_share.setOnClickListener(AppManagerActivity.this);
				ll_start.setOnClickListener(AppManagerActivity.this);
				ll_uninstall.setOnClickListener(AppManagerActivity.this);

				pw = new PopupWindow(contentView, -2, -2);
				// 注意动画效果要想生效，必须要求popupwindow窗体有背景颜色
				pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));// 这一句有时候要有时候不要。
				int[] location = new int[2];
				view.getLocationInWindow(location);
				// 解决屏幕适配的问题，自定义一个工具类，进行dp和px的转换
				int dip = 60;
				int px = DensityUtil.dip2px(getApplicationContext(), dip);
				pw.showAtLocation(parent, Gravity.LEFT | Gravity.TOP, px, location[1]);

				// 给弹出气泡窗体设置动画
				ScaleAnimation sa = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f, Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0.5f);
				sa.setDuration(300);
				AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
				aa.setDuration(300);
				AnimationSet set = new AnimationSet(false);// false代表各自播放各自独立的动画
				set.addAnimation(sa);
				set.addAnimation(aa);
				contentView.startAnimation(set);
			}
		});
	}

	private void fillData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				appInfos = AppInfoProvider.getAppInfos(getApplicationContext());
				userAppLists = new ArrayList<>();
				systemAppLists = new ArrayList<>();

				for (AppInfo info : appInfos) {
					if (info.isUerApp()) {
						userAppLists.add(info);
					} else {
						systemAppLists.add(info);
					}
				}

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (myAdapter == null) {
							myAdapter = new AppInfoAdapter();
							lv_app_manager.setAdapter(myAdapter);
						}else {
							//刷新数据
							myAdapter.notifyDataSetChanged();
						}
						ll_loading.setVisibility(View.INVISIBLE);
					}
				});

			}
		}).start();
	}

	private void dismissPopupWindow() {
		if (pw != null && pw.isShowing()) {
			// 小窗体正好不为空且正在显示，那就关掉
			pw.dismiss();
			pw = null;
		}
	}

	private class AppInfoAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// return appInfos.size();
			return userAppLists.size() + systemAppLists.size() + 2;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AppInfo info;
			if (position == 0) {
				TextView tv = new TextView(getApplicationContext());
				tv.setText("用户程序：" + userAppLists.size() + "个");
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				return tv;
			} else if (position == (userAppLists.size() + 1)) {
				TextView tv = new TextView(getApplicationContext());
				tv.setText("系统程序：" + systemAppLists.size() + "个");
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				return tv;
			} else if (position <= userAppLists.size()) {
				info = userAppLists.get(position - 1);
			} else {
				info = systemAppLists.get(position - userAppLists.size() - 2);
			}

			View view = null;
			ViewHolder holder;

			// if (position < userAppLists.size()) {
			// info = userAppLists.get(position);
			// }else{
			// int newPosition = position - userAppLists.size();
			// info = systemAppLists.get(newPosition);
			// }
			//
			// info = appInfos.get(position);

			if (convertView != null && convertView instanceof RelativeLayout) {
				// 不加上面的类型判断的话，tv也被复用了，不适合下面的类型，就会报错（空指针）。所以要加类型检验
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				holder = new ViewHolder();
				view = View.inflate(getApplicationContext(), R.layout.list_item_app_info, null);
				holder.tv_app_name = (TextView) view.findViewById(R.id.tv_app_name);
				holder.tv_app_location = (TextView) view.findViewById(R.id.tv_app_location);
				holder.iv_app_icon = (ImageView) view.findViewById(R.id.iv_app_icon);
				view.setTag(holder);
			}

			holder.iv_app_icon.setImageDrawable(info.getIcon());
			holder.tv_app_name.setText(info.getName());
			// holder.tv_app_location.setText(info.getName());
			if (info.isInRom()) {
				holder.tv_app_location.setText("手机内存");
			} else {
				holder.tv_app_location.setText("外部存储");
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

	private class ViewHolder {
		TextView tv_app_name;
		TextView tv_app_location;
		ImageView iv_app_icon;
	}

	public long getAvailSpace(String path) {
		StatFs statFs = new StatFs(path);
		statFs.getBlockCount();// 获取分区的个数
		long size = statFs.getBlockSize();// 获取分区的大小
		long counts = statFs.getAvailableBlocks();// 获取可用空间个数
		return size * counts;
	}

	@Override
	protected void onDestroy() {
		dismissPopupWindow();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_uninstall:
			// Log.e(TAG, info.getPackname()+"卸载");
			if (info.isUerApp()) {
				uninstallApp();				
			}else {
				Toast.makeText(getApplicationContext(), "系统应用需root后才能卸载", 0).show();
//				Runtime.getRuntime().exec("...");//执行linux代码
			}

			break;
		case R.id.ll_start:
			// Log.e(TAG, info.getPackname()+"启动");
			// 获取全部具有所有启动能力的activity
			PackageManager pm = getPackageManager();
			// Intent intent = new Intent();
			// intent.setAction("android.intent.action.MAIN");
			// intent.addCategory("android.intent.category.LAUNCHER");
			// List<ResolveInfo> infos = pm.queryIntentActivities(intent ,
			// PackageManager.GET_INTENT_FILTERS);
			Intent intent = pm.getLaunchIntentForPackage(info.getPackname());
			if (intent != null) {
				startActivity(intent);
			} else {
				Toast.makeText(getApplicationContext(), "不能启动当前应用", 0).show();
			}

			break;
		case R.id.ll_share:
			// Log.e(TAG, info.getPackname()+"分享");
			shareApp();
			break;
		}

	}

	private void shareApp() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("text/plain");
		//要传递一串字符串的固定写法是：Intent.EXTRA_TEXT
		intent.putExtra(Intent.EXTRA_TEXT, "推荐一款不错的app："+info.getName());
		startActivity(intent);
	}

	private void uninstallApp() {
		// <action android:name="android.intent.action.VIEW" />
		// <action android:name="android.intent.action.DELETE" />
		// <category android:name="android.intent.category.DEFAULT" />
		// <data android:scheme="package" />
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.setAction("android.intent.action.DELETE");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("package:" + info.getPackname()));
		// startActivity(intent);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		fillData();
		super.onActivityResult(requestCode, resultCode, data);
	}
}
