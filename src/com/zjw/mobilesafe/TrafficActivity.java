package com.zjw.mobilesafe;

import java.util.List;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Bundle;

public class TrafficActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_traffic);
		
		PackageManager packageManager = getPackageManager();
		List<ApplicationInfo> applications = packageManager.getInstalledApplications(0);
		for (ApplicationInfo info : applications) {
			int uid = info.uid;
			long uidRxBytes = TrafficStats.getUidRxBytes(uid);
			long uidTxBytes = TrafficStats.getUidTxBytes(uid);
		}
	}
}
