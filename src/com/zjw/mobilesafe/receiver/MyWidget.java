package com.zjw.mobilesafe.receiver;

import com.zjw.mobilesafe.service.UpdateWidgetService;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class MyWidget extends AppWidgetProvider {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context, UpdateWidgetService.class);
		context.startService(i);
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		//onUpdate是框架更新，最低要半小时才能更新一下，
		//如果要很快就获得反应，应该自定义一个服务，长期驻守内存，用来更新桌面小控件
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onEnabled(Context context) {
		//此方法在控件被拖到桌面上（即第一次被创建时）执行，之后无论在创建多少小控件，都不再执行。
		//适合开启一个服务去更新控件界面
		Intent intent = new Intent(context, UpdateWidgetService.class);
		context.startService(intent);
		super.onEnabled(context);
	}

	@Override
	public void onDisabled(Context context) {
		//此方法在所有相同控件中的最后一个被移走时执行
		//适合关闭一个服务
		Intent intent = new Intent(context, UpdateWidgetService.class);
		context.stopService(intent);
		super.onDisabled(context);
	}
}
