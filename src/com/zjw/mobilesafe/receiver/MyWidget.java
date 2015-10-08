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
		//onUpdate�ǿ�ܸ��£����Ҫ��Сʱ���ܸ���һ�£�
		//���Ҫ�ܿ�ͻ�÷�Ӧ��Ӧ���Զ���һ�����񣬳���פ���ڴ棬������������С�ؼ�
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onEnabled(Context context) {
		//�˷����ڿؼ����ϵ������ϣ�����һ�α�����ʱ��ִ�У�֮�������ڴ�������С�ؼ���������ִ�С�
		//�ʺϿ���һ������ȥ���¿ؼ�����
		Intent intent = new Intent(context, UpdateWidgetService.class);
		context.startService(intent);
		super.onEnabled(context);
	}

	@Override
	public void onDisabled(Context context) {
		//�˷�����������ͬ�ؼ��е����һ��������ʱִ��
		//�ʺϹر�һ������
		Intent intent = new Intent(context, UpdateWidgetService.class);
		context.stopService(intent);
		super.onDisabled(context);
	}
}
