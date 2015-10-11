package com.zjw.mobilesafe;

import java.lang.reflect.Method;
import java.util.List;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ClearCacheActivity extends Activity {
	private ProgressBar pBar;
	private TextView tv_stats;
	private PackageManager pm;
	private LinearLayout ll_content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clearcache);

		pBar = (ProgressBar) findViewById(R.id.pb1);
		tv_stats = (TextView) findViewById(R.id.tv_stats);
		ll_content = (LinearLayout) findViewById(R.id.ll_content);

		scanCache();
	}

	private void scanCache() {

		pm = getPackageManager();
		new Thread(new Runnable() {

			@Override
			public void run() {
				Method getPackageSizeInfoMethod = null;
				// 通过查看源码：我们得知packageManager下有一个被系统隐藏的方法，通过该方法可以获得缓存信息
				Method[] methods = PackageManager.class.getMethods();
				for (Method method : methods) {
					if ("getPackageSizeInfo".equals(method.getName())) {
						getPackageSizeInfoMethod = method;
						break;
					}
					// System.out.println(method.getName());
				}

				List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
				pBar.setMax(installedPackages.size());
				int count = 0;
				for (PackageInfo info : installedPackages) {

					// 为了获取packageManager下getPackageSizeInfo这一隐藏方法
					// public void getPackageSizeInfo(String packageName,
					// IPackageStatsObserver observer)
					try {
						getPackageSizeInfoMethod.invoke(pm, info.packageName, new MyPackageStatsObserver());
						Thread.sleep(50);//这一睡觉逻辑应放在上一代码下面
					} catch (Exception e) {
						e.printStackTrace();
					}
					count++;
					pBar.setProgress(count);
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						tv_stats.setText("扫描完成");
					}
				});
			}
		}).start();
	}

	private class MyPackageStatsObserver extends IPackageStatsObserver.Stub {

		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
			final long cacheSize = pStats.cacheSize;
			// long codeSize = pStats.codeSize;
			// long dataSize = pStats.dataSize;
			final String packageName = pStats.packageName;
			final ApplicationInfo applicationInfo;
			try {
				applicationInfo = pm.getApplicationInfo(packageName, 0);

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						tv_stats.setText("正在扫描缓存……" + applicationInfo.loadLabel(pm));
						if (cacheSize > 0) {
//							TextView tv = new TextView(getApplicationContext());
//							tv.setText("名称：" + applicationInfo.loadLabel(pm) + ";缓存："
//									+ Formatter.formatFileSize(getApplicationContext(), cacheSize));
//							tv.setTextColor(Color.BLACK);
							
							View view = View.inflate(getApplicationContext(), R.layout.item_cache, null);
							TextView tv_cache_name = (TextView) view.findViewById(R.id.tv_cache_name);
							TextView tv_cache_size = (TextView) view.findViewById(R.id.tv_cache_size);
							ImageView iv_delete = (ImageView) view.findViewById(R.id.iv_delede);
							tv_cache_name.setText("名称："+applicationInfo.loadLabel(pm));
							tv_cache_size.setText("缓存："+Formatter.formatFileSize(getApplicationContext(), cacheSize));
							iv_delete.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									//    public abstract void deleteApplicationCacheFiles(String packageName,IPackageDataObserver observer);
									
									try {
										Method method = PackageManager.class.getMethod("deleteApplicationCacheFiles", String.class, IPackageDataObserver.class);
										method.invoke(pm, packageName, new MyPackageDataObserver());
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
								}
							});
							ll_content.addView(view, 0);
						}
					}
				});
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 *此方法需要有系统应用的权限，因此自己实施不了；会报错。只有借用系统程序才能用。
	 *10-10 20:08:29.580: current process has android.permission.DELETE_CACHE_FILES.
	 */
	private class MyPackageDataObserver extends IPackageDataObserver.Stub{
		@Override
		public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
			System.out.println(packageName + succeeded);
		}
	}
	
	public void clearAll(View view) {
		//deleteApplicationCacheFiles清除缓存方法不能用，只能用系统bug，申请大内存，逼迫系统回收缓存空间
//		public abstract void freeStorageAndNotify(long freeStorageSize, IPackageDataObserver observer);
		Method[] methods = PackageManager.class.getMethods();
		for (Method method : methods) {
			if(method.getName().equals("freeStorageAndNotify")){
				try {
					method.invoke(pm, Integer.MAX_VALUE, new MyPackageDataObserver());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			}
		}
	}

}
