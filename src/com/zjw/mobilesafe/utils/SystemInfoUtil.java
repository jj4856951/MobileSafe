package com.zjw.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

public class SystemInfoUtil {
	/**
	 * 获取当前运行的进程的个数
	 * 
	 * @param context
	 * @return int
	 */
	public static int getRunningProcessCount(Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcesses = manager.getRunningAppProcesses();
		return runningAppProcesses.size();
	}

	public static long getAvailMem(Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		manager.getMemoryInfo(outInfo);
		long availMem = outInfo.availMem;
		return availMem;
	}

	/**
	 * 获取全部内存（此方法适用于4.0以上）
	 * 
	 * @param context
	 * @return
	 */
	// public static long getTotalMem(Context context) {
	// ActivityManager manager = (ActivityManager)
	// context.getSystemService(Context.ACTIVITY_SERVICE);
	// MemoryInfo outInfo = new MemoryInfo();
	// manager.getMemoryInfo(outInfo);
	// long totalMem = outInfo.totalMem;
	// return totalMem;
	// }
	/**
	 * 获取全部内存（此方法适用于低版本）
	 * @throws IOException 
	 */
	public static long getTotalMem(Context context){
		File file = new File("/proc/meminfo");
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			// root@generic_x86:/proc # cat meminfo
			// cat meminfo
			// MemTotal: 511056 kB
			String line = br.readLine();
			StringBuilder sb = new StringBuilder();
			for (char c : line.toCharArray()) {
				if (c >= '0' && c <= '9') {
					sb.append(c);
				}
			}
			fis.close();
			return Long.parseLong(sb.toString())*1024;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

}
