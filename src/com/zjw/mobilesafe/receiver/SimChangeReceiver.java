package com.zjw.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class SimChangeReceiver extends BroadcastReceiver {
	private SharedPreferences sp;
	private TelephonyManager tm;

	@Override
	public void onReceive(Context context, Intent intent) {
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		boolean isProtecting = sp.getBoolean("isProtecting", false);
		if (isProtecting) {
			//手机处于保护中
			tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
			String curr_sim = tm.getSimSerialNumber();
			
			String saved_sim = sp.getString("sim_num", "");
			
			if (!curr_sim.equals(saved_sim)) {
//				Toast.makeText(context, "sim卡改变了", 1).show();
//				System.out.println("改变了");
				SmsManager manager = SmsManager.getDefault();
				manager.sendTextMessage(sp.getString("safe_num", ""), null, "您的手机sim卡已经改变，请注意。", null, null);
			}else {
//				Toast.makeText(context, "sim no change", 1).show();
//				System.out.println("没变");
			}			
		}
	}

}
