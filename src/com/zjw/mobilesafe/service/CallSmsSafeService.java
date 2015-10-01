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
	private TelephonyManager tm;//获取电话管理实例，用于监听是否有电话打进来。
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
			Log.e(TAG, "收到一个短信");
			Object[] objects = (Object[]) intent.getExtras().get("pdus");
			for (Object object : objects) {
				SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
				String sender = message.getOriginatingAddress();
				Log.e(TAG, sender);
				String mode = dao.findMode(sender);
				
//				Log.e(TAG, mode);//注意此处mode有可能为空，会报错，要提前判断
				if (!TextUtils.isEmpty(mode)) {
					if (mode.equals("2") || mode.equals("3")) {
						// 在阻止短信一列
						Log.e(TAG, "短信被阻止：" + sender);
						abortBroadcast();
					}
				}
			}
		}
	}
	
	@Override
	public void onCreate() {
		Log.e(TAG, "开始黑名单监听了。");
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
//						Log.e(TAG, "挂电话");
						Uri uri = Uri.parse("content://call_log/calls");
//						Uri uri = CallLog.Calls.CONTENT_URI;
						observer = new MyContentObserve(incomingNumber, new Handler());
						getContentResolver().registerContentObserver(uri, true, observer);
						endCall();//在另外的进程总的方法，调用完，可能呼叫记录还未生成，
						//因此不能立即调用删除通话记录的方法
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
		
		//在这里出错率，把下面的onchange方法内容写在了构造方法里，怪不得删除不掉通话记录呢。
		public MyContentObserve(String incomingNumber,Handler handler) {
			super(handler);
			this.incomingNumber = incomingNumber;
		}

		@Override
		public void onChange(boolean selfChange) {
			deleteCallLog(incomingNumber);
			getContentResolver().unregisterContentObserver(this);//删除记录之后就没必要再继续观察了，因此可以直接注销监听【自己】
			super.onChange(selfChange);
		}
	}


	public void deleteCallLog(String incomingNumber) {
		Uri uri = CallLog.Calls.CONTENT_URI;
		getContentResolver().delete(uri, "number = ?", new String[]{incomingNumber});
	}

	public void endCall() {
		//ServiceManager被隐藏掉了，怎么利用反射，给调出来？
		//IBinder b = ServiceManager.getService(TELEPHONY_SERVICE);
		try {
			Class clazz = CallSmsSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
			Method method = clazz.getDeclaredMethod("getService", String.class);
			//下面要获取Ibinder对象。即：获取系统底层真正的IBinder对象。
			//此处十分重要，错误的很长时间，才找出bug。TELEPHONY_SERVICE不能加引号
			IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);//receiver是调用者（对象），因此，静态方法没有receiver
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
