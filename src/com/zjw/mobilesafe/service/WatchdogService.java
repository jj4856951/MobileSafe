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
		protected_list = dao.findAll();//���list�ǻ��б仯�ģ������Ҫ��һ���㲥�����¸�list
		
		intent = new Intent(getApplicationContext(), InputPwdActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		//��ȡ���½��̣��˷������ǰ����½��̷��ڵ�һλ
		//������λ�ȡ�û���ǰ����ʹ�õ�activity
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(flag){
					List<RunningTaskInfo> runningTasks = am.getRunningTasks(1);//�Ż�1������ȡ����100��Ϊ1
					String packageName = runningTasks.get(0).topActivity.getPackageName();
					System.out.println("packagename:"+packageName);//�Ż�2��ע�͵����д���

//					if (dao.find(packageName)) {//�Ż�3����ѯ���ݿ�
					if(protected_list.contains(packageName)){
						if (packageName.equals(protected_packname)) {
							//��ʱ����Ҫ������ʲô��Ҳ����
						}else {
							//�Ż�4������������activity����ͼ���嵽����ȥ
							intent.putExtra("packname", packageName);
							startActivity(intent);
						}
					}
					try {
						//����һ��Ҫд�ģ���Ȼ̫��
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
	 * �ڲ��ࣺ�㲥�����ߣ�������ʱֹͣ���Ź����ָ�������Ĺ㲥
	 */
	private class TmpStopReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			protected_packname = intent.getStringExtra("packname");
		}
	}
	/**
	 * �㲥�����ߣ����������㲥
	 */
	private class ScreenOffReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("������");
			protected_packname = null;
		}
	}
	/**
	 * �㲥�����ߣ��������ݿⷢ���仯�Ĺ㲥
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
