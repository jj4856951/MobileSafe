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
				// ͨ���鿴Դ�룺���ǵ�֪packageManager����һ����ϵͳ���صķ�����ͨ���÷������Ի�û�����Ϣ
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

					// Ϊ�˻�ȡpackageManager��getPackageSizeInfo��һ���ط���
					// public void getPackageSizeInfo(String packageName,
					// IPackageStatsObserver observer)
					try {
						getPackageSizeInfoMethod.invoke(pm, info.packageName, new MyPackageStatsObserver());
						Thread.sleep(50);//��һ˯���߼�Ӧ������һ��������
					} catch (Exception e) {
						e.printStackTrace();
					}
					count++;
					pBar.setProgress(count);
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						tv_stats.setText("ɨ�����");
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
						tv_stats.setText("����ɨ�軺�桭��" + applicationInfo.loadLabel(pm));
						if (cacheSize > 0) {
//							TextView tv = new TextView(getApplicationContext());
//							tv.setText("���ƣ�" + applicationInfo.loadLabel(pm) + ";���棺"
//									+ Formatter.formatFileSize(getApplicationContext(), cacheSize));
//							tv.setTextColor(Color.BLACK);
							
							View view = View.inflate(getApplicationContext(), R.layout.item_cache, null);
							TextView tv_cache_name = (TextView) view.findViewById(R.id.tv_cache_name);
							TextView tv_cache_size = (TextView) view.findViewById(R.id.tv_cache_size);
							ImageView iv_delete = (ImageView) view.findViewById(R.id.iv_delede);
							tv_cache_name.setText("���ƣ�"+applicationInfo.loadLabel(pm));
							tv_cache_size.setText("���棺"+Formatter.formatFileSize(getApplicationContext(), cacheSize));
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
	 *�˷�����Ҫ��ϵͳӦ�õ�Ȩ�ޣ�����Լ�ʵʩ���ˣ��ᱨ��ֻ�н���ϵͳ��������á�
	 *10-10 20:08:29.580: current process has android.permission.DELETE_CACHE_FILES.
	 */
	private class MyPackageDataObserver extends IPackageDataObserver.Stub{
		@Override
		public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
			System.out.println(packageName + succeeded);
		}
	}
	
	public void clearAll(View view) {
		//deleteApplicationCacheFiles������淽�������ã�ֻ����ϵͳbug��������ڴ棬����ϵͳ���ջ���ռ�
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
