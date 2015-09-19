package com.zjw.mobilesafe;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.zjw.mobilesafe.R.id;
import com.zjw.mobilesafe.utils.StreamTools;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

public class SplashActivity extends Activity {
	private TextView tv_progress;
	private String description;
	private String apkurl;
	private SharedPreferences sp;

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
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		TextView tv_version = (TextView) findViewById(R.id.mm_tv_version);
		tv_version.setText("版本号："+getVersion());
		
		tv_progress = (TextView) findViewById(R.id.mm_tv_progress);
		
		boolean update = sp.getBoolean("update", false);
		if (update) {
			checkUpdate();			
		}else {
			//无需联网更新，直接进入主页(需要延时两秒)
			//延时操作的模板代码
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					enterHome();
				}
			}, 2000);
		}
		
		AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
		aa.setDuration(800);
		findViewById(R.id.mm_rl_splash).startAnimation(aa);
	}
	
	private void checkUpdate() {
		//url:http://10.0.2.2/mobilesafe/update.html
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = Message.obtain();
				long start = System.currentTimeMillis();
				try {
					URL url = new URL(getString(R.string.serverurl));
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout(2000);
//					conn.setReadTimeout(4000);
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
				}finally {
					
					long end = System.currentTimeMillis();
					long dtime = end - start;
					if (dtime < 1500) {
						try {
							Thread.sleep(2000 - dtime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					handler.sendMessage(msg);
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
				Toast.makeText(getApplicationContext(), "JSON解析错误", 0).show();
				enterHome();
				break;
			case MM_NETWORK_ERROR:
				Toast.makeText(getApplicationContext(), "网络错误", 0).show();
				enterHome();
				break;
			case MM_UPDATE_VERSION:
				Log.i(TAG, "联网成功，显示升级对话框");

				showUpdateDialog();
				break;
			case MM_URL_ERROR:
				Toast.makeText(getApplicationContext(), "网络错误", 0).show();
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

	protected void showUpdateDialog() {
		AlertDialog.Builder builder = new Builder(this);//这里不能用getApplicationContext(),会报错不能添加windows
//		builder.setCancelable(false);//用户体验不是很好
		builder.setTitle("更新提示");
		builder.setMessage(description);
		
		builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				//用户点空白区域或者返回键时，其意图是进入不管升级而直接进入主界面
				dialog.dismiss();
				enterHome();
			}
		});
		builder.setPositiveButton("现在更新", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//下载apk，先判断sd卡是否存在
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					//sd卡存在
					FinalHttp http = new FinalHttp();
					String target = Environment.getExternalStorageDirectory().getAbsolutePath()+"/mobilesafe2.0.apk";
					http.download(apkurl, target, new AjaxCallBack<File>() {

						@Override
						public void onFailure(Throwable t, int errorNo, String strMsg) {
							t.printStackTrace();
							Toast.makeText(getApplicationContext(), "下载出错", 0).show();
							super.onFailure(t, errorNo, strMsg);
						}

						@Override
						public void onLoading(long count, long current) {
							tv_progress.setText("当前下载进度："+(int)(current*100/count)+"%");
							super.onLoading(count, current);
						}

						@Override
						public void onSuccess(File t) {
							//在完成开始安装
							Intent intent = new Intent();
							intent.setAction("android.intent.action.VIEW");
							intent.addCategory("android.intent.category.DEFAULT");
							intent.setDataAndType(Uri.fromFile(t), "application/vnd.android.package-archive");
							startActivity(intent);
							
							super.onSuccess(t);
						}
						
					});
				}else {
					//sd卡不存在
					Toast.makeText(getApplicationContext(), "请检查sd卡是否存在", 0).show();
				}
			}
		});
		builder.setNegativeButton("下次再说", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				enterHome();
			}
		});
		builder.show();
		
	}

	protected void enterHome() {
		Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
		startActivity(intent);
		finish();//开始主页面之后，不能回退到起始页面。
	}
	

}
