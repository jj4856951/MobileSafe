package com.zjw.mobilesafe.ui;

import com.zjw.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingClickView extends RelativeLayout {
	private TextView tv_desc;
	private TextView tv_title;
	private String desc_on;
	private String desc_off;

	private void initView(Context context) {
		View.inflate(context, R.layout.setting_click_layout, SettingClickView.this);
		tv_desc = (TextView) findViewById(R.id.mm_tv_description);
		tv_title = (TextView) findViewById(R.id.mm_tv_title);
	}
	
	public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}


	public SettingClickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		
		String mtitle = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.zjw.mobilesafe", "mtitle");
		desc_on = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.zjw.mobilesafe", "desc_on");
		desc_off = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.zjw.mobilesafe", "desc_off");
		tv_title.setText(mtitle);
		tv_desc.setText(desc_off);
	}

	public SettingClickView(Context context) {
		super(context);
		initView(context);
	}

	public void changeDescripion(String str) {
		tv_desc.setText(str);
	}

}
