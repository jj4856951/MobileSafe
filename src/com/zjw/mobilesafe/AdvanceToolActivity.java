package com.zjw.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AdvanceToolActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_advance_tool);
	}

	public void numberQuery(View view) {
		Intent intent = new Intent(this, QueryNumAddressActivity.class);
		startActivity(intent);
		
	}
	
}
