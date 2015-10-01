package com.zjw.mobilesafe.service;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.zjw.mobilesafe.db.dao.BlackNumberDao;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

public class CallSmsSafeService extends Service {
	public static final String TAG = "CallSmsSafeService";
	private SmsSafeReceiver smsReceiver;
	private BlackNumberDao dao;
	private TelephonyManager tm;//��ȡ�绰����ʵ�������ڼ����Ƿ��е绰�������
	private MyListener listener;
	private MyContentObserve observer;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private class SmsSafeReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e(TAG, "�յ�һ������");
			Object[] objects = (Object[]) intent.getExtras().get("pdus");
			for (Object object : objects) {
				SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
				String sender = message.getOriginatingAddress();
				Log.e(TAG, sender);
				String mode = dao.findMode(sender);
				
//				Log.e(TAG, mode);//ע��˴�mode�п���Ϊ�գ��ᱨ��Ҫ��ǰ�ж�
				if (!TextUtils.isEmpty(mode)) {
					if (mode.equals("2") || mode.equals("3")) {
						// ����ֹ����һ��
						Log.e(TAG, "���ű���ֹ��" + sender);
						abortBroadcast();
					}
				}
			}
		}
	}
	
	@Override
	public void onCreate() {
		Log.e(TAG, "��ʼ�����������ˡ�");
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		dao = new BlackNumberDao(this);
		smsReceiver = new SmsSafeReceiver();
		IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		registerReceiver(smsReceiver, filter);
		super.onCreate();
	}
	
	private class MyListener extends PhoneStateListener{

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				String mode = dao.findMode(incomingNumber);
				if (!TextUtils.isEmpty(mode)) {
					if (mode.equals("1") || mode.equals("3")){
//						Log.e(TAG, "�ҵ绰");
						Uri uri = Uri.parse("content://call_log/calls");
//						Uri uri = CallLog.Calls.CONTENT_URI;
						observer = new MyContentObserve(incomingNumber, new Handler());
						getContentResolver().registerContentObserver(uri, true, observer);
						endCall();//������Ľ����ܵķ����������꣬���ܺ��м�¼��δ���ɣ�
						//��˲�����������ɾ��ͨ����¼�ķ���
					}						
				}
				break;

			default:
				break;
			}
		}
		
	}
	
	private class MyContentObserve extends ContentObserver{

		private String incomingNumber;
		
		//����������ʣ��������onchange��������д���˹��췽����ֲ���ɾ������ͨ����¼�ء�
		public MyContentObserve(String incomingNumber,Handler handler) {
			super(handler);
			this.incomingNumber = incomingNumber;
		}

		@Override
		public void onChange(boolean selfChange) {
			deleteCallLog(incomingNumber);
			getContentResolver().unregisterContentObserver(this);//ɾ����¼֮���û��Ҫ�ټ����۲��ˣ���˿���ֱ��ע���������Լ���
			super.onChange(selfChange);
		}
	}


	public void deleteCallLog(String incomingNumber) {
		Uri uri = CallLog.Calls.CONTENT_URI;
		getContentResolver().delete(uri, "number = ?", new String[]{incomingNumber});
	}

	public void endCall() {
		//ServiceManager�����ص��ˣ���ô���÷��䣬����������
		//IBinder b = ServiceManager.getService(TELEPHONY_SERVICE);
		try {
			Class clazz = CallSmsSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
			Method method = clazz.getDeclaredMethod("getService", String.class);
			//����Ҫ��ȡIbinder���󡣼�����ȡϵͳ�ײ�������IBinder����
			//�˴�ʮ����Ҫ������ĺܳ�ʱ�䣬���ҳ�bug��TELEPHONY_SERVICE���ܼ�����
			IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);//receiver�ǵ����ߣ����󣩣���ˣ���̬����û��receiver
			ITelephony.Stub.asInterface(iBinder).endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void onDestroy() {
		if (smsReceiver != null) {
			unregisterReceiver(smsReceiver);
			smsReceiver = null;			
		}
		if (listener != null) {
			tm.listen(listener, PhoneStateListener.LISTEN_NONE);
			listener = null;			
		}
		super.onDestroy();
	}

}
