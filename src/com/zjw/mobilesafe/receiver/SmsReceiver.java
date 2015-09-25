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
					//����ֻ�λ�õ�ַ
					Log.e(TAG, "����ֻ�λ�õ�ַ");
					//������gps�ķ���Ȼ��ȡsp�е�location
					Intent i = new Intent(context, GPSService.class);
					context.startService(i);
					
					String location = sp.getString("location", "");
					if (TextUtils.isEmpty(location)) {
						//�ص�λ��Ϊ�գ�˵�����ڻ�ȡ
						SmsManager.getDefault().sendTextMessage(sender, null, "getting location...", null, null);
					}else {
						//��ȡ���ص���Ϣ�ˣ��ͷ��͵ص�
						SmsManager.getDefault().sendTextMessage(sender, null, location, null, null);
					}
					
					abortBroadcast();
				}else if("#*alarm*#".equals(body)){
					//�ֻ�����
					Log.e(TAG, "�ֻ�����");
					MediaPlayer.create(context, R.raw.ylzs).start();
					abortBroadcast();
				}else if("#*lockscreen*#".equals(body)){
					//Զ������
					Log.e(TAG, "Զ������");
					mDPM.lockNow();
					abortBroadcast();
				}else if("#*wipedata*#".equals(body)){
					//����ֻ�����
					Log.e(TAG, "�������");
					mDPM.wipeData(0);
					abortBroadcast();
				}
			}
		}
	}
}
