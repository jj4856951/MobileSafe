package com.zjw.mobilesafe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ChooseContactsActivity extends Activity {
	private ListView mm_contacts_list;
	private List<Map<String, String>> list;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_contacts);
		
		mm_contacts_list = (ListView) findViewById(R.id.mm_contacts_list);
		mm_contacts_list.setAdapter(new SimpleAdapter(this, getContectsInfo(), R.layout.item_contacts,
				new String[] { "name", "phone" }, new int[] { R.id.mm_name, R.id.mm_phone }));
		mm_contacts_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String phone = list.get(position).get("phone");
				Intent intent = new Intent();
				intent.putExtra("phone", phone);
				setResult(0, intent);
				//�ص���ǰҳ��
				finish();
			}
		});

	}

	private List<Map<String, String>> getContectsInfo() {
		list = new ArrayList<>();

		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri dataUri = Uri.parse("content://com.android.contacts/data");

		ContentResolver resolver = getContentResolver();
		Cursor cursor = resolver.query(uri, new String[] { "contact_id" }, null, null, null);

		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				String id = cursor.getString(0);
				Cursor dataCursor = resolver.query(dataUri, new String[] { "data1", "mimetype" }, "contact_id=?",
						new String[] { id }, null);
				if (dataCursor != null && cursor.getCount() > 0) {
					Map<String, String> map = new HashMap<>();
					while (dataCursor.moveToNext()) {
						String data1 = dataCursor.getString(0);
						String mimetype = dataCursor.getString(1);
//						System.out.println(data1+"---"+mimetype);
						if (mimetype.equals("vnd.android.cursor.item/phone_v2")) {
							map.put("phone", data1);
						} else if (mimetype.equals("vnd.android.cursor.item/name")) {
							map.put("name", data1);
						}
						//�˴�һ��ʼ��list.add�ˣ��Ǵ�ġ���һ��ѭ������������
					}
					list.add(map);//���ϣ���Ӽ��ϣ�Ӧ���ڴ˴���������Ϊʲô��
					dataCursor.close();
				}
			}
			cursor.close();
		}
		return list;
	}
}
