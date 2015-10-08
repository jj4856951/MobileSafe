package com.zjw.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import com.zjw.mobilesafe.R;
import com.zjw.mobilesafe.domain.ProcessInfo;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;

public class TaskInfoProvider {
	public static List<ProcessInfo> getProcessList(Context context) {
		PackageManager pm = context.getPackageManager();
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		List<ProcessInfo> list = new ArrayList<ProcessInfo>();
		for (RunningAppProcessInfo info : runningAppProcesses) {
			ProcessInfo processInfo = new ProcessInfo();
			//获取到包名
			String packname = info.processName;
			processInfo.setPackName(packname);
			//获取到进程所占用内存信息
			MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{info.pid});
			MemoryInfo memoryInfo = processMemoryInfo[0];
			int totalPrivateDirty = memoryInfo.getTotalPrivateDirty();
			processInfo.setMemSize(totalPrivateDirty*1024);
			
			try {
				//获取到程序的清单文件，从中取出程序名
				ApplicationInfo applicationInfo = pm.getApplicationInfo(packname, 0);
				String name = applicationInfo.loadLabel(pm).toString();
				Drawable icon = applicationInfo.loadIcon(pm);
				
				processInfo.setName(name);
				processInfo.setIcon(icon);
				
				if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
					//是用户程序
					processInfo.setUserProcress(true);
				}else {
					processInfo.setUserProcress(false);
				}
			} catch (NameNotFoundException e) {
				//有些进程是c语言写的，不发获取名称和图标，因此就默认用小机器人图标
				processInfo.setName(packname);
				processInfo.setIcon(context.getResources().getDrawable(R.drawable.robot));
				e.printStackTrace();
			}
			list.add(processInfo);
		}
		return list;
	}

}
