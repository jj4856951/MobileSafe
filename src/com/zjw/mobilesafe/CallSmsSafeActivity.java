package com.zjw.mobilesafe;

import java.util.List;

import com.zjw.mobilesafe.db.dao.BlackNumberDao;
import com.zjw.mobilesafe.domain.BlackNumberInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CallSmsSafeActivity extends Activity {
	private ListView mm_balck_list;
	private BlackNumberInfo info;
	private List<BlackNumberInfo> list;
	private BlackNumberDao dao;
	private MyAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_sms_safe);
		mm_balck_list = (ListView) findViewById(R.id.mm_balck_list);
		dao = new BlackNumberDao(this);
		list = dao.findAll();
		adapter = new MyAdapter();
		mm_balck_list.setAdapter(adapter);
	}
	
	private EditText ed_blackNumber;
	private CheckBox cb_call;
	private CheckBox cb_sms;
	private Button bt_ok;
	private Button bt_cancel;
	public void addBlackNumber(View view) {
		AlertDialog.Builder builder = new Builder(this);
		final AlertDialog dialog = builder.create();
		View Cview = View.inflate(this, R.layout.alert_add_black_number, null);
		ed_blackNumber = (EditText) Cview.findViewById(R.id.mm_balck_number);
		cb_call = (CheckBox) Cview.findViewById(R.id.mm_black_mode_call);
		cb_sms = (CheckBox) Cview.findViewById(R.id.mm_black_mode_sms);
		bt_ok = (Button) Cview.findViewById(R.id.mm_ok);
		bt_cancel = (Button) Cview.findViewById(R.id.mm_cancel);
		dialog.setView(Cview, 0, 0, 0, 0);
		dialog.show();
		
		bt_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		bt_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String number = ed_blackNumber.getText().toString().trim();
				if (TextUtils.isEmpty(number)) {
					Toast.makeText(CallSmsSafeActivity.this, "号码为空", 0).show();;
					return;
				}
				if (dao.find(number)) {
					Toast.makeText(CallSmsSafeActivity.this, "该号码已添加，请检查", 0).show();
					return;
				}
				boolean call = cb_call.isChecked();
				boolean sms = cb_sms.isChecked();
				String mode = null;
				if (call && sms) {
					mode = "3";
				}else if (call) {
					mode = "1";
				}else if (sms) {
					mode = "2";
				}else {
					Toast.makeText(CallSmsSafeActivity.this, "请选择拦截模式", 0).show();
					return;
				}
				
				dao.add(number, mode);
				list.add(0, new BlackNumberInfo(number, mode));
				adapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		});

	}
	
	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				view = View.inflate(CallSmsSafeActivity.this, R.layout.list_item_callsms, null);				
				holder.tv_blacknumber = (TextView) view.findViewById(R.id.tv_black_number);
				holder.tv_blackmode = (TextView) view.findViewById(R.id.tv_block_mode);
				holder.iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
				view.setTag(holder);
			}else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			info = list.get(position);
			final String number = info.getNumber();
			String mode = info.getMode();
			
			holder.tv_blacknumber.setText(number);
			
			if (mode.equals("3") ) {
				holder.tv_blackmode.setText("全部拦截");
			}else if(mode.equals("1")){
				holder.tv_blackmode.setText("拦截电话");
			}else if (mode.equals("2")) {
				holder.tv_blackmode.setText("拦截短信");
			}
			holder.iv_delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new Builder(CallSmsSafeActivity.this);
					builder.setTitle("警告");
					builder.setMessage("确定删除吗？");
					builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dao.delete(number);
							list.remove(position);
							adapter.notifyDataSetChanged();
						}
					});
					builder.setNegativeButton("取消", null);
					builder.show();
				}
			});
			return view;
		}
		
	}
	class ViewHolder {
		TextView tv_blacknumber;
		TextView tv_blackmode;
		ImageView iv_delete;
	}
		
}
