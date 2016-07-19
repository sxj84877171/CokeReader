package com.marktrace.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.marktrace.R;
import com.marktrace.mode.AbstractCodeReader;
import com.marktrace.mode.DataReadActivity;
import com.marktrace.mode.DeviceInfoActivity;
import com.marktrace.mode.TagReadActivity;
import com.marktrace.sendinterface.SendCommandInterface;
import com.marktrace.sendinterface.UpdateState;
import com.marktrace.threadmanager.Constants;

public class CokeReaderActivity extends Activity {
	public static final int LAYOUT1 = 1;
	public static final int LAYOUT2 = 2;
	public static final int LAYOUT3 = 3;

	private View deviceManager = null;
	private View tagRead = null;
	private View dataSelect = null;

	private View page1 = null;
	private View page2 = null;
	private View page3 = null;
	private View tab = null;

	private AbstractCodeReader dataRead = null;
	private AbstractCodeReader deviceInfo = null;
	private AbstractCodeReader tagReader = null;

	private SendCommandInterface mDataSelectImple;

	private UpdateState mUpdateState = null;
	private Handler myHandler = null;

	public SendCommandInterface getmDataSelectImple() {
		return mDataSelectImple;
	}

	public void setmDataSelectImple(SendCommandInterface mDataSelectImple) {
		this.mDataSelectImple = mDataSelectImple;
		this.mDataSelectImple.setUpdateState(mUpdateState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		findViewById();
		bindView();

		dataRead = new DataReadActivity(this);
		deviceInfo = new DeviceInfoActivity(this);
		tagReader = new TagReadActivity(this);

		dataRead.onCreate();
		deviceInfo.onCreate();
		tagReader.onCreate();
		myHandler = new Handler();
		mUpdateState = new UpdateStateImpl();
		setTitle(R.string.device_connect_unknow);
		tagRead.setEnabled(false);
		((TextView)tagRead).setTextColor(0xffAAAAAA);
	}

	private void findViewById() {
		deviceManager = findViewById(R.id.device_manager);
		tagRead = findViewById(R.id.tag_read);
		dataSelect = findViewById(R.id.data_select);
		page1 = findViewById(R.id.layout_1);
		page2 = findViewById(R.id.layout_2);
		page3 = findViewById(R.id.layout_3);
		tab = findViewById(R.id.tab);
	}

	private void bindView() {
		deviceManager.setOnClickListener(mOnClickListener);
		tagRead.setOnClickListener(mOnClickListener);
		dataSelect.setOnClickListener(mOnClickListener);
	}

	private void setVisble(int id) {
		page1.setVisibility(View.GONE);
		page2.setVisibility(View.GONE);
		page3.setVisibility(View.GONE);
		switch (id) {
		case LAYOUT1:
			page1.setVisibility(View.VISIBLE);
			tab.setBackgroundResource(R.drawable.pa);
			break;
		case LAYOUT2:
			page2.setVisibility(View.VISIBLE);
			tab.setBackgroundResource(R.drawable.pb);
			
			break;
		case LAYOUT3:
			page3.setVisibility(View.VISIBLE);
			tab.setBackgroundResource(R.drawable.pc);
			break;
		default:
			page1.setVisibility(View.VISIBLE);
			tab.setBackgroundResource(R.drawable.pa);
			break;
		}
		deviceManager.findFocus();
	}

	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if (arg0.equals(deviceManager)) {
				setVisble(LAYOUT1);
			} else if (arg0.equals(tagRead)) {
				setVisble(LAYOUT2);
			} else {
				setVisble(LAYOUT3);
			}
		}
	};

	protected void onResume() {
		super.onResume();
		dataRead.onResume();
		deviceInfo.onResume();
		tagReader.onResume();
		deviceManager.findFocus();
		setVisble(LAYOUT1);

	};

	@Override
	protected void onStart() {
		super.onStart();
		dataRead.onStart();
		deviceInfo.onStart();
		tagReader.onStart();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		dataRead.onRestart();
		deviceInfo.onRestart();
		tagReader.onRestart();
	}

	@Override
	protected void onPause() {
		super.onPause();
		dataRead.onPause();
		deviceInfo.onPause();
		tagReader.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dataRead.onDestroy();
		deviceInfo.onDestroy();
		tagReader.onDestroy();
		if(mDataSelectImple != null){
			mDataSelectImple.destroy();
			mDataSelectImple = null ;
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AlertDialog.Builder builder = new Builder(this);
			builder.setTitle(R.string.tip);
			builder.setMessage(R.string.question_exit);
			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							finish();
						}
					});
			builder.setNegativeButton(R.string.cancel, null);
			builder.create().show();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	private class UpdateStateImpl implements UpdateState {

		@Override
		public void updateState(final int state) {
			myHandler.post(new Runnable() {

				@Override
				public void run() {
					switch (state) {
					case Constants.STATE_CONNECTED:
						showToast(R.string.device_connect_success);
						showToast(R.string.device_connected);
						setTitle(R.string.device_connected);
						dataRead.setConnect(true);
						deviceInfo.setConnect(true);
						tagReader.setConnect(true);
						tagRead.setEnabled(true);
						((TextView)tagRead).setTextColor(Color.GREEN);
						break;
					case Constants.STATE_CONNECT_FAIL:
						showToast(R.string.device_connect_fail);
						setTitle(R.string.device_connect_fail);
						dataRead.setConnect(false);
						deviceInfo.setConnect(false);
						tagReader.setConnect(false);
						tagRead.setEnabled(false);
						((TextView)tagRead).setTextColor(0xffAAAAAA);
						if(page2.getVisibility() == View.VISIBLE){
							setVisble(LAYOUT1);
						}
						myHandler.postDelayed(new Runnable() {
							
							@Override
							public void run() {
								setTitle(R.string.device_connect_unknow);
							}
						}, 500);
						break;
					case Constants.STATE_CONNECTING:
						showToast(R.string.device_connecting);
						setTitle(R.string.device_connecting);
						break;
					case Constants.STATE_LISTEN:
					case Constants.STATE_NONE:
						showToast(R.string.device_connect_unknow);
						setTitle(R.string.device_connect_unknow);
						break;

					default:
						break;
					}

				}
			});

		}

	}

	protected void showToast(int id) {
		Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
	}

	protected void showToast(String id) {
		Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
	}
	
	
	
	
}