package com.zjw.mobilesafe;

import java.io.File;
import java.io.FileOutputStream;
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
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
		tv_version.setText("�汾�ţ�"+getVersion());
		
		tv_progress = (TextView) findViewById(R.id.mm_tv_progress);
		
		/**
		 * ����������ͼ��
		 */
		createShortcut();
		
		/**
		 * ����ѯ�����ص����ݿ��Ƿ�Ҫ������ֻ�п������ļ��£����ܷ��ʡ�
		 */
		copyAddressDatabases();
		boolean update = sp.getBoolean("update", false);
		if (update) {
			checkUpdate();			
		}else {
			//�����������£�ֱ�ӽ�����ҳ(��Ҫ��ʱ����)
			//��ʱ������ģ�����
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
	
	private void createShortcut() {
		if (sp.getBoolean("shortcut", false)) {
			return;
		}
		Editor editor = sp.edit();
		
		//���͹㲥����ͼ�����һ������Ҫ��������ͼ���ˡ�
		Intent intent = new Intent();
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		//һ����ݷ�ʽ������3���֣�ͼ�꣬���ֺͿ�������ͼ
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "�ֻ���ȫ��ʿ");
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
		Intent shortcutIntent = new Intent();
		shortcutIntent.setAction("android.intent.action.MAIN");
		shortcutIntent.addCategory("android.intent.category.LAUNCHER");
		shortcutIntent.setClassName(getPackageName(), "com.zjw.mobilesafe.SplashActivity");
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent );
		//����
		sendBroadcast(intent);
		editor.putBoolean("shortcut", true);
		editor.commit();
	}

	/**
	 * ��Ҫ���ʵ����ݿ�copy������/files/�ļ���
	 * "data/data/<����>/files/address.db"
	 */
	private void copyAddressDatabases() {
		try {
			File file = new File(getFilesDir(), "address.db");//getFilesDir����ȡdata/data/<����>/Ŀ¼
			//���files���������ݿ⣬����Ҫ�ٿ�����
			if (file.exists() && file.length() > 0) {
				Log.e(TAG, "������������ݿ����追��");
			}else {
				InputStream is = getAssets().open("address.db");
				FileOutputStream fos = new FileOutputStream(file);
				byte[] bts = new byte[1024];
				int len = 0;
				while((len = is.read(bts)) != -1){
					fos.write(bts, 0, len);
				}
				is.close();
				fos.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
//						Log.i(TAG, "�����"+res);
						JSONObject jsonObject = new JSONObject(res);
						String version = (String) jsonObject.get("version");
						description = (String) jsonObject.get("description");
						apkurl = (String) jsonObject.get("apkurl");
						
						if (getVersion().equals(version)) {
							//�汾û�䣬������ҳ
							msg.what = MM_ENTER_HOME;
						}else {
							//�汾���ˣ�Ҫ���°汾
							msg.what = MM_UPDATE_VERSION;
						}
					}
					
				} catch (MalformedURLException e) {
					e.printStackTrace();
					//����url�쳣
					msg.what = MM_URL_ERROR;
				} catch (IOException e) {
					e.printStackTrace();
					//�����쳣
					msg.what = MM_NETWORK_ERROR;
				} catch (JSONException e) {
					e.printStackTrace();
					//json�����쳣
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
				Toast.makeText(getApplicationContext(), "JSON��������", 0).show();
				enterHome();
				break;
			case MM_NETWORK_ERROR:
				Toast.makeText(getApplicationContext(), "�������", 0).show();
				enterHome();
				break;
			case MM_UPDATE_VERSION:
				Log.i(TAG, "�����ɹ�����ʾ�����Ի���");

				showUpdateDialog();
				break;
			case MM_URL_ERROR:
				Toast.makeText(getApplicationContext(), "�������", 0).show();
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
		AlertDialog.Builder builder = new Builder(this);//���ﲻ����getApplicationContext(),�ᱨ�������windows
//		builder.setCancelable(false);//�û����鲻�Ǻܺ�
		builder.setTitle("������ʾ");
		builder.setMessage(description);
		
		builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				//�û���հ�������߷��ؼ�ʱ������ͼ�ǽ��벻��������ֱ�ӽ���������
				dialog.dismiss();
				enterHome();
			}
		});
		builder.setPositiveButton("���ڸ���", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//����apk�����ж�sd���Ƿ����
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					//sd������
					FinalHttp http = new FinalHttp();
					String target = Environment.getExternalStorageDirectory().getAbsolutePath()+"/mobilesafe2.0.apk";
					http.download(apkurl, target, new AjaxCallBack<File>() {

						@Override
						public void onFailure(Throwable t, int errorNo, String strMsg) {
							t.printStackTrace();
							Toast.makeText(getApplicationContext(), "���س���", 0).show();
							super.onFailure(t, errorNo, strMsg);
						}

						@Override
						public void onLoading(long count, long current) {
							tv_progress.setText("��ǰ���ؽ��ȣ�"+(int)(current*100/count)+"%");
							super.onLoading(count, current);
						}

						@Override
						public void onSuccess(File t) {
							//����ɿ�ʼ��װ
							Intent intent = new Intent();
							intent.setAction("android.intent.action.VIEW");
							intent.addCategory("android.intent.category.DEFAULT");
							intent.setDataAndType(Uri.fromFile(t), "application/vnd.android.package-archive");
							startActivity(intent);
							
							super.onSuccess(t);
						}
						
					});
				}else {
					//sd��������
					Toast.makeText(getApplicationContext(), "����sd���Ƿ����", 0).show();
				}
			}
		});
		builder.setNegativeButton("�´���˵", new OnClickListener() {
			
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
		finish();//��ʼ��ҳ��֮�󣬲��ܻ��˵���ʼҳ�档
	}
	

}
