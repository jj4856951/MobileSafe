package com.zjw.mobilesafe;

import com.zjw.mobilesafe.utils.QueryPhoneNumAddress;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class QueryNumAddressActivity extends Activity {

	private EditText ed_phone;
	private TextView tv_res;
	private Vibrator vibrator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query_num_address);
		
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		ed_phone = (EditText) findViewById(R.id.ed_phone);
		tv_res = (TextView) findViewById(R.id.result);
		ed_phone.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s!= null&&s.length()>=3){
					//查询数据库，并且显示结果
					String address = QueryPhoneNumAddress.getPhoneNumAddress(s.toString());
					tv_res.setText(address);
				}
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	public void numberAddressQuery(View view) {
		String phoneNum = ed_phone.getText().toString().trim();
		if (TextUtils.isEmpty(phoneNum)) {
			Toast.makeText(this, "号码为空", 0).show();
	        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
//	        自定义动画
//	        shake.setInterpolator(new Interpolator() {
//				
//				@Override
//				public float getInterpolation(float input) {
//					// TODO Auto-generated method stub
//					return 0;
//				}
//			});
	        ed_phone.startAnimation(shake);
	        vibrator.vibrate(2000);//手机振动两秒
			return;
		}
		String numAddress = QueryPhoneNumAddress.getPhoneNumAddress(phoneNum);
		tv_res.setText(numAddress);
	}
	
	
}
