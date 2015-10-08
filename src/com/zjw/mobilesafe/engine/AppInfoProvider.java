package com.zjw.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import com.zjw.mobilesafe.domain.AppInfo;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

/**
 * 提供手机内程序详细信息的业务类
 *
 */
public class AppInfoProvider {

	
	/**
	 * 获取所有安装的应用程序信息
	 * @return
	 */
	public static List<AppInfo> getAppInfos(Context context) {
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packagesinfo = pm.getInstalledPackages(0);
		List<AppInfo> list = new ArrayList<AppInfo>();
		
		for (PackageInfo packageInfo : packagesinfo) {
			AppInfo info = new AppInfo();
			//packageInfo相当于一个apk文件的清单文件
			String packageName = packageInfo.packageName;
			Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
			String name = packageInfo.applicationInfo.loadLabel(pm).toString();
			int flag = packageInfo.applicationInfo.flags;//获取应用程序标记
			if ((flag & ApplicationInfo.FLAG_SYSTEM) == 0) {
				//是用户的程序
				info.setUerApp(true);
			}else {
				//是系统的
				info.setUerApp(false);
			}
			if ((flag & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
				//是系统存储的
				info.setInRom(true);
			}else {
				//是SD卡的
				info.setInRom(false);
			}
			
			info.setIcon(icon);
			info.setName(name);
			info.setPackname(packageName);
			list.add(info);
		}
		return list;
	}
	
}
