package com.zjw.mobilesafe;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.List;

import com.zjw.mobilesafe.db.dao.ScanVirusDBDao;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AntiVirusActivity extends Activity {
	protected static final int SCANNING = 0;
	protected static final int SUCCESS = 1;
	private ImageView iv_raider;
	private ProgressBar pBar;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SCANNING:
				Virus virus = (Virus) msg.obj;
				tv_scanning.setText("正在扫描："+virus.name);					
				TextView tv = new TextView(getApplicationContext());
				if (virus.isVirus) {
					tv.setText("发现病毒："+virus.name);
					ll_status.addView(tv, 0);	
				}else {
					tv.setText("扫描安全："+virus.name);
					ll_status.addView(tv, 0);
				}
				
				break;
			case SUCCESS:
				tv_scanning.setText("扫描完成");
				iv_raider.clearAnimation();
				break;
			default:
				break;
			}
			
		};
	};
	private TextView tv_scanning;
	private LinearLayout ll_status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_antivirus);
		
		iv_raider = (ImageView) findViewById(R.id.iv_raider);
		tv_scanning = (TextView) findViewById(R.id.tv_scanning);
		ll_status = (LinearLayout) findViewById(R.id.ll_status);
		
		RotateAnimation ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		ra.setDuration(700);
		ra.setRepeatCount(RotateAnimation.INFINITE);//此处一开始写的是setRepeatMode，结果不转
		iv_raider.startAnimation(ra);
		
		pBar = (ProgressBar) findViewById(R.id.pb1);
		
		//开始扫描病毒了
		scanVirus();
	}

	private void scanVirus() {
		final PackageManager pm = getPackageManager();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				List<ApplicationInfo> installedApplications = pm.getInstalledApplications(0);
				pBar.setMax(installedApplications.size());
				int count = 0;
				for (ApplicationInfo info : installedApplications) {
					Virus virus = new Virus();
					String sourceDir = info.sourceDir;
					String md5 = getMD5(sourceDir);
//					System.out.println(info.packageName+":"+md5);
					boolean isvirus = ScanVirusDBDao.isVirus(md5);
					virus.name = info.loadLabel(pm).toString();
					virus.packname = info.packageName;
					virus.isVirus = isvirus;
					Message msg = Message.obtain();
					msg.what = SCANNING;
					msg.obj = virus;
					handler.sendMessage(msg);
					count++;
					pBar.setProgress(count);
					
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				Message msg = Message.obtain();
				msg.what = SUCCESS;
				handler.sendMessage(msg);
			}
		}).start();
		
	}
	
	private class Virus{
		 String name;
		 String packname;
		 boolean isVirus;
	}
	
	public String getMD5(String path) {
		try {
			MessageDigest digest = MessageDigest.getInstance("md5");
			FileInputStream fis = new FileInputStream(new File(path));
			int len = 0;
			byte[] bts = new byte[1024];
			while((len = fis.read(bts))!=-1){
				digest.update(bts, 0, len);
			}
			byte[] bs = digest.digest();
			StringBuilder sb = new StringBuilder();
			for (byte b : bs) {
				
				int i = b & 0xff;
				String tmp = Integer.toHexString(i);
				if (tmp.length() == 1) {
					sb.append("0");
				}
				sb.append(tmp);
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
