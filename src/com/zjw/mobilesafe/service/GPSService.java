package com.zjw.mobilesafe.service;

import java.io.IOException;
import java.io.InputStream;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class GPSService extends Service {
	private LocationManager lm;
	private Mylistener listener;
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		// List<String> allProviders = lm.getAllProviders();
		// for (String string : allProviders) {
		// System.out.println(string);
		// }
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String bestProvider = lm.getBestProvider(criteria, true);

		listener = new Mylistener();
		lm.requestLocationUpdates(bestProvider, 0, 0, listener);
		
	}
	
	public class Mylistener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			String latitude = "w:"+location.getLatitude()+"\n";
			String longitude = "j:"+location.getLongitude()+"\n";
			String accuracy = "a:"+location.getAccuracy();
//			Log.e("TAG", longitude+"_"+latitude+"_"+accuracy);
			
			//将标准坐标转换为实际坐标
			InputStream is;
			try {
				is = getAssets().open("axisoffset.dat");
				ModifyOffset offset = ModifyOffset.getInstance(is);
				PointDouble double1 = offset.s2c(new PointDouble(location.getLongitude(), location.getLatitude()));
				latitude = "w:"+double1.y+"\n";
				longitude = "j:"+double1.x+"\n";
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//发短信给安全手机了；但是不能直接发，会扣很多费，搞停机就不好了。
			//所以就用sp保存。
			SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
			Editor editor = sp.edit();
//			Log.e("TAG", longitude+"_"+latitude+"_"+accuracy);
			editor.putString("location", latitude+longitude+accuracy);
			editor.commit();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

	}

	@Override
	public void onDestroy() {
		lm.removeUpdates(listener);
		listener = null;
		super.onDestroy();
	}
	

}
