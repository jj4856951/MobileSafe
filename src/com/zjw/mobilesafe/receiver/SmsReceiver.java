package com.zjw.mobilesafe.receiver;

import com.zjw.mobilesafe.R;
import com.zjw.mobilesafe.service.GPSService;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {
	private DevicePolicyManager mDPM;
	private SharedPreferences sp;
	private static final String TAG = "SmsReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		mDPM = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		Object[] objectS = (Object[]) intent.getExtras().get("pdus");
		for (Object object : objectS) {
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
			String sender = sms.getOriginatingAddress();
			String body = sms.getMessageBody();
			String safenumber = sp.getString("safe_num", "");
			if (sender.contains(safenumber)) {
				
				if("#*location*#".equals(body)){
					//获得手机位置地址
					Log.e(TAG, "获得手机位置地址");
					//先启动gps的服务，然后取sp中的location
					Intent i = new Intent(context, GPSService.class);
					context.startService(i);
					
					String location = sp.getString("location", "");
					if (TextUtils.isEmpty(location)) {
						//地点位置为空，说明正在获取
						SmsManager.getDefault().sendTextMessage(sender, null, "getting location...", null, null);
					}else {
						//获取到地点信息了，就发送地点
						SmsManager.getDefault().sendTextMessage(sender, null, location, null, null);
					}
					
					abortBroadcast();
				}else if("#*alarm*#".equals(body)){
					//手机报警
					Log.e(TAG, "手机报警");
					MediaPlayer.create(context, R.raw.ylzs).start();
					abortBroadcast();
				}else if("#*lockscreen*#".equals(body)){
					//远程锁屏
					Log.e(TAG, "远程锁屏");
					mDPM.lockNow();
					abortBroadcast();
				}else if("#*wipedata*#".equals(body)){
					//清除手机数据
					Log.e(TAG, "清除数据");
					mDPM.wipeData(0);
					abortBroadcast();
				}
			}
		}
	}
}
