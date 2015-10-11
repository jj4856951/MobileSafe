package com.zjw.mobilesafe.service;

import java.util.List;

import com.zjw.mobilesafe.InputPwdActivity;
import com.zjw.mobilesafe.db.dao.AppLockDao;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class WatchdogService extends Service {
	private ActivityManager am;
	private boolean flag;
	private AppLockDao dao;
	private TmpStopReceiver tmpStopReceiver;
	private ScreenOffReceiver screenOffReceiver;
	private AppLockListChangedReceiver appLockListChangedReceiver;
	private String protected_packname;
	private List<String> protected_list;
	private Intent intent;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		tmpStopReceiver = new TmpStopReceiver();
		registerReceiver(tmpStopReceiver, new IntentFilter("com.zjw.mobilesafe.tmpstop"));
		screenOffReceiver = new ScreenOffReceiver();
		registerReceiver(screenOffReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		appLockListChangedReceiver = new AppLockListChangedReceiver();
		registerReceiver(appLockListChangedReceiver, new IntentFilter("com.zjw.mobilesafe.appLockListChanged"));
		
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		dao = new AppLockDao(this);
		flag = true;
		protected_list = dao.findAll();//这个list是会有变化的，因此需要有一个广播来更新该list
		
		intent = new Intent(getApplicationContext(), InputPwdActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		//获取最新进程，此方法总是把最新进程放在第一位
		//即：如何获取用户当前正在使用的activity
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(flag){
					List<RunningTaskInfo> runningTasks = am.getRunningTasks(1);//优化1，将获取数从100改为1
					String packageName = runningTasks.get(0).topActivity.getPackageName();
					System.out.println("packagename:"+packageName);//优化2，注释掉此行代码

//					if (dao.find(packageName)) {//优化3：查询数据库
					if(protected_list.contains(packageName)){
						if (packageName.equals(protected_packname)) {
							//此时不需要保护，什么事也不做
						}else {
							//优化4：将开启密码activity的意图定义到外面去
							intent.putExtra("packname", packageName);
							startActivity(intent);
						}
					}
					try {
						//这是一定要写的，不然太快
						Thread.sleep(20);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		super.onCreate();
	}
	
	/**
	 * 内部类：广播接受者，接收暂时停止看门狗监控指定包名的广播
	 */
	private class TmpStopReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			protected_packname = intent.getStringExtra("packname");
		}
	}
	/**
	 * 广播接收者，接受锁屏广播
	 */
	private class ScreenOffReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("锁屏了");
			protected_packname = null;
		}
	}
	/**
	 * 广播接收者，接收数据库发生变化的广播
	 */
	private class AppLockListChangedReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			protected_list = dao.findAll();
		}
	}
	
	@Override
	public void onDestroy() {
		flag = false;
		unregisterReceiver(tmpStopReceiver);
		tmpStopReceiver = null;
		unregisterReceiver(screenOffReceiver);
		screenOffReceiver = null;
		unregisterReceiver(appLockListChangedReceiver);
		appLockListChangedReceiver = null;
		super.onDestroy();
	}
	
}
