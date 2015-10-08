package com.zjw.mobilesafe.receiver;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class KillAllProcessReceiver extends BroadcastReceiver {
	private ActivityManager am;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		for (RunningAppProcessInfo info : runningAppProcesses) {
			am.killBackgroundProcesses(info.processName);
		}
	}

}
