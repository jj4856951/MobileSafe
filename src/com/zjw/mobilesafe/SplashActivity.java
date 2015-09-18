package com.zjw.mobilesafe;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.zjw.mobilesafe.utils.StreamTools;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

public class SplashActivity extends Activity {

	private static final String TAG = "SplashActivity";
	protected static final int MM_ENTER_HOME = 0;
	protected static final int MM_UPDATE_VERSION = 1;
	protected static final int MM_URL_ERROR = 2;
	protected static final int MM_NETWORK_ERROR = 3;
	protected static final int MM_JSON_ERROR = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		TextView tv_version = (TextView) findViewById(R.id.mm_tv_version);
		tv_version.setText("版本号："+getVersion());
		
		checkUpdate();
		
		
	}
	
	private void checkUpdate() {
		//url:http://10.0.2.2/mobilesafe/update.html
		new Thread(new Runnable() {
			
			private String description;
			private String apkurl;

			@Override
			public void run() {
				Message msg = Message.obtain();
				try {
					URL url = new URL(getString(R.string.serverurl));
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout(4000);
					conn.setReadTimeout(4000);
					int code = conn.getResponseCode();
					if (code == 200) {
						InputStream is = conn.getInputStream();
						String res = StreamTools.readFromStream(is);
//						Log.i(TAG, "结果："+res);
						JSONObject jsonObject = new JSONObject(res);
						String version = (String) jsonObject.get("version");
						description = (String) jsonObject.get("description");
						apkurl = (String) jsonObject.get("apkurl");
						
						if (getVersion().equals(version)) {
							//版本没变，进入主页
							msg.what = MM_ENTER_HOME;
						}else {
							//版本变了，要更新版本
							msg.what = MM_UPDATE_VERSION;
						}
					}
					
				} catch (MalformedURLException e) {
					e.printStackTrace();
					//网络url异常
					msg.what = MM_URL_ERROR;
				} catch (IOException e) {
					e.printStackTrace();
					//网络异常
					msg.what = MM_NETWORK_ERROR;
				} catch (JSONException e) {
					e.printStackTrace();
					//json解析异常
					msg.what = MM_JSON_ERROR;
				}
				
			}
		}).start();

		
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MM_ENTER_HOME:
				enterHome();
				break;
			case MM_JSON_ERROR:
				enterHome();
				break;
			case MM_NETWORK_ERROR:
				enterHome();
				break;
			case MM_UPDATE_VERSION:
				Log.i(TAG, "联网成功，显示升级对话框");
				break;
			case MM_URL_ERROR:
				enterHome();
				break;
			}
		};
	};

	public String getVersion() {
		PackageManager manager = getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo("com.zjw.mobilesafe", 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";			
		}
	}

	protected void enterHome() {
		// TODO Auto-generated method stub
		
	}
	

}
