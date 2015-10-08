package com.zjw.mobilesafe.service;

import java.util.Timer;
import java.util.TimerTask;

import com.zjw.mobilesafe.R;
import com.zjw.mobilesafe.receiver.MyWidget;
import com.zjw.mobilesafe.utils.SystemInfoUtil;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

public class UpdateWidgetService extends Service {
	private LockScreenBroadcastReceiver lockScreen_receiver;
	private OpenScreenBroadcastReceiver openScreen_receiver;
	// 实现定时任务，定时更新桌面小控件
	private Timer timer;
	private TimerTask task;
	private AppWidgetManager awm;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private class LockScreenBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("锁屏了");
			stopTask();
		}
	}

	private class OpenScreenBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("开启屏幕了");
			startTask();
		}
	}

	@Override
	public void onCreate() {
		lockScreen_receiver = new LockScreenBroadcastReceiver();
		openScreen_receiver = new OpenScreenBroadcastReceiver();
		registerReceiver(lockScreen_receiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		registerReceiver(openScreen_receiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
		
		awm = AppWidgetManager.getInstance(this);
		startTask();
		super.onCreate();
	}

	private void startTask() {
		if (timer == null && task == null) {
			timer = new Timer();
			task = new TimerTask() {

				@Override
				public void run() {
					System.out.println("小控件更新了");
					// 远程更新桌面小控件界面的方法
					ComponentName provider = new ComponentName(UpdateWidgetService.this, MyWidget.class);
					RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_layout);
					views.setTextViewText(R.id.tv_running_process_num,
							"当前进程数：" + SystemInfoUtil.getRunningProcessCount(getApplicationContext()));
					long availMem = SystemInfoUtil.getAvailMem(getApplicationContext());
					views.setTextViewText(R.id.tv_avail_rom,
							"可用内存：" + Formatter.formatFileSize(getApplicationContext(), availMem));
					Intent intent = new Intent();
					intent.setAction("com.zjw.mobile.killall");
					PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent,
							PendingIntent.FLAG_UPDATE_CURRENT);
					views.setOnClickPendingIntent(R.id.btn_kill, pendingIntent);

					awm.updateAppWidget(provider, views);
				}
			};
			timer.schedule(task, 0, 3000);
		}
	}

	@Override
	public void onDestroy() {
		stopTask();
		unregisterReceiver(lockScreen_receiver);
		unregisterReceiver(openScreen_receiver);
		lockScreen_receiver = null;
		openScreen_receiver = null;
		super.onDestroy();
	}

	private void stopTask() {
		if (timer != null && task != null) {
			timer.cancel();
			task.cancel();
			timer = null;
			task = null;
		}
	}
}
