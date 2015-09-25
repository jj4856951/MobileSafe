package com.zjw.mobilesafe.service;

import com.zjw.mobilesafe.R;
import com.zjw.mobilesafe.utils.QueryPhoneNumAddress;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

public class AddressShowService extends Service {
	private TelephonyManager tm;
	private MyPhoneStateListener listener;
	private OutgoingCallReceiver receiver;
	private WindowManager wm;
	private View view;
	private SharedPreferences sp;
	private WindowManager.LayoutParams params;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public class OutgoingCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String outgoing = getResultData();
			String address = QueryPhoneNumAddress.getPhoneNumAddress(outgoing);
//			Toast.makeText(context, address, 1).show();
			mToast(address);
		}
	}

	@Override
	public void onCreate() {
		sp = getSharedPreferences("config", MODE_PRIVATE);
		receiver = new OutgoingCallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(receiver, filter );
		
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		
		listener = new MyPhoneStateListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
	}
	
	public void mToast(String address) {
//		view = new TextView(getApplicationContext());
//		view.setTextSize(22);
//		view.setTextColor(Color.YELLOW);
//		view.setText(address);
		
		view = View.inflate(this, R.layout.show_address, null);
		TextView tv = (TextView) view.findViewById(R.id.tv_address);
		tv.setBackgroundResource(R.drawable.call_locate_blue);
		int[] items = {R.drawable.call_locate_white,R.drawable.call_locate_orange,R.drawable.call_locate_blue
			    ,R.drawable.call_locate_gray,R.drawable.call_locate_green};
		tv.setBackgroundResource(items[sp.getInt("which", 0)]);
		tv.setText(address);
		
		//谷歌官方多击事件代码
		view.setOnClickListener(new OnClickListener() {
			long[] mHits = new long[2];
			
			@Override
			public void onClick(View v) {
	            System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
	            mHits[mHits.length-1] = SystemClock.uptimeMillis();
	            if (mHits[0] >= (SystemClock.uptimeMillis()-500)) {
	            	//窗体水平居中代码
	            	params.x = wm.getDefaultDisplay().getWidth()/2 - view.getWidth()/2;
	            	wm.updateViewLayout(view, params);
	            }
				
			}
		});
		
		//实现自定义土司的自由拖动
		view.setOnTouchListener(new OnTouchListener() {
			int startx;
			int starty;
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startx = (int) event.getRawX();
					starty = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					int newx = (int) event.getRawX();
					int newy = (int) event.getRawY();
					int dx = newx - startx;
					int dy = newy - starty;
					
					params.x += dx;
					params.y += dy;
					//检测
					if (params.x < 0) {
						params.x = 0;
					}
					if (params.x > (wm.getDefaultDisplay().getWidth()-view.getWidth())) {
						params.x = (wm.getDefaultDisplay().getWidth()-view.getWidth());
					}
					if (params.y < 0) {
						params.y = 0;
					}
					if (params.y > (wm.getDefaultDisplay().getHeight() - view.getHeight())) {
						params.y = (wm.getDefaultDisplay().getHeight() - view.getHeight());
					}
					
					startx = (int) event.getRawX();
					starty = (int) event.getRawY();
					wm.updateViewLayout(view, params);
					break;
				case MotionEvent.ACTION_UP:
					Editor editor = sp.edit();
					editor.putInt("addressToastPointx", params.x);
					editor.putInt("addressToastPointy", params.y);
					editor.commit();
					break;

				default:
					break;
				}
				return false;//注意此时要返回false，不然遮住挂机键就按不了
			}
		});
		
		
		params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        
        params.gravity = Gravity.TOP+Gravity.LEFT;//作用之一：限定坐标原点为左上。下面的坐标代码才起作用
        int pointx = sp.getInt("addressToastPointx", 0);
        int pointy = sp.getInt("addressToastPointy", 0);
        
        params.x = pointx;
        params.y = pointy;
        params.format = PixelFormat.TRANSLUCENT;
      //土司类型的窗体默认是不能响应点击事件的，TYPE_TOAST要更改为
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
//        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                //土司类型的窗体默认是不能响应点击事件的,如下代码要去掉
//                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
		
		wm.addView(view, params);
	}

	@Override
	public void onDestroy() {
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
		unregisterReceiver(receiver);
		receiver = null;
		super.onDestroy();
	}
	
	class MyPhoneStateListener extends PhoneStateListener{

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			//响铃时监听
			case TelephonyManager.CALL_STATE_RINGING:
				String numAddress = QueryPhoneNumAddress.getPhoneNumAddress(incomingNumber);
//				Toast.makeText(AddressShowService.this, numAddress, 0).show();
				mToast(numAddress);
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				if (view != null) {
					wm.removeView(view);					
				}
				break;

			default:
				break;
			}
		}
	}
}
