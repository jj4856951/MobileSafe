package com.zjw.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

/**
 * �ṩ�ֻ��ڳ�����ϸ��Ϣ��ҵ����
 *
 */
public class AppInfoProvider {

	
	/**
	 * ��ȡ���а�װ��Ӧ�ó�����Ϣ
	 * @return
	 */
	public static List<AppInfo> getAppInfos(Context context) {
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packagesinfo = pm.getInstalledPackages(0);
		List<AppInfo> list = new ArrayList<AppInfo>();
		
		for (PackageInfo packageInfo : packagesinfo) {
			AppInfo info = new AppInfo();
			//packageInfo�൱��һ��apk�ļ����嵥�ļ�
			String packageName = packageInfo.packageName;
			Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
			String name = packageInfo.applicationInfo.loadLabel(pm).toString();
			int flag = packageInfo.applicationInfo.flags;//��ȡӦ�ó�����
			if ((flag & ApplicationInfo.FLAG_SYSTEM) == 0) {
				//���û��ĳ���
				info.setUerApp(true);
			}else {
				//��ϵͳ��
				info.setUerApp(false);
			}
			if ((flag & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
				//��ϵͳ�洢��
				info.setInRom(true);
			}else {
				//��SD����
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
