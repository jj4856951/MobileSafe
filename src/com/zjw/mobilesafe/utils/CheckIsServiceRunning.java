package com.zjw.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class CheckIsServiceRunning {

	public static boolean isRunning(Context context, String serviceName) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> list = am.getRunningServices(100);//��ȡ��ǰ���з���
		for (RunningServiceInfo runningServiceInfo : list) {
			String className = runningServiceInfo.service.getClassName();
			if (className.equals(serviceName)) {
				return true;
			}
		}
		return false;
	}
}
