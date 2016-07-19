package com.marktrace.mode;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.marktrace.R;
import com.marktrace.activity.CokeReaderActivity;
import com.marktrace.bean.DeviceInfo;
import com.marktrace.dealdatamanager.SendCommandManager;
import com.marktrace.sendinterface.DeviceInfoDataResult;
import com.marktrace.sendinterface.SyncTime;
import com.marktrace.sendinterface.UpdatePassword;
import com.marktrace.sendinterface.UpdateUserName;

public class DeviceInfoActivity extends AbstractCodeReader {
	private View deviceConnectView = null;
	private View deviceInfoView = null;
	private View syncTimeView = null;

	private TextView userNameView = null;
	private TextView passwordView = null;
	private View changeUserNameView = null;
	private View changePasswordView = null;

	private TableLayout infoView = null;
	private SendCommandManager sendCmdManager = null;
	private Handler myHandler = null;

	private String newUserName = null;
	private String newPassword = null;
	
	public SendCommandManager getSendCmdManager() {
		return sendCmdManager;
	}

	public void setSendCmdManager(SendCommandManager sendCmdManager) {
		this.sendCmdManager = sendCmdManager;
	}

	public DeviceInfoActivity(CokeReaderActivity mActivity) {
		this.mActivity = mActivity;
	}

	@Override
	public void bindView() {
		deviceConnectView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				connect();
			}
		});

		deviceInfoView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getDeviceInfo();
			}
		});

		changeUserNameView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				chanage(1);
			}
		});

		changePasswordView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				chanage(0);
			}
		});

		syncTimeView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				syncTime();
			}
		});
	}

	@Override
	public void findView() {
		deviceConnectView = getActivity().findViewById(R.id.device_connect);
		deviceInfoView = getActivity().findViewById(R.id.device_info);
		syncTimeView = getActivity().findViewById(R.id.device_time);

		userNameView = (TextView) getActivity().findViewById(R.id.username);
		passwordView = (TextView) getActivity().findViewById(R.id.password);

		changeUserNameView = getActivity().findViewById(R.id.chanageUsername);
		changePasswordView = getActivity().findViewById(R.id.chanagerPassword);

		infoView = (TableLayout) getActivity().findViewById(R.id.info1);
		changeUserNameView.setEnabled(false);
		changePasswordView.setEnabled(false);
	}

	public void init() {
		myHandler = new Handler(getActivity().getMainLooper());
	}

	private void connect() {

		SearchBluetooth sBluetooth = new SearchBluetooth(getActivity());
		sBluetooth.onResume();
		sBluetooth.setAcr(this);
		sBluetooth.show();
	}

	private void getDeviceInfo() {
		if (getmDataSelectImple() != null) {
			getActivity().getmDataSelectImple().setDeviceInfoResult(
					new DeviceInfoResultImpl());
		}
		getActivity().getmDataSelectImple().getDeviceInfo();
	}

	private void chanage(final int id) {
		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setTitle(R.string.update_info);
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		final View view = inflater.inflate(R.layout.update_info, null);
		builder.setView(view);
		final EditText updateUserNameEditText = ((EditText) view
				.findViewById(R.id.update_username));
		final EditText oldPasswordEditText = (EditText) view
				.findViewById(R.id.old_password);
		final EditText newPasswordEditText = ((EditText) view
				.findViewById(R.id.new_password));
		final EditText updatePasswordEditText = ((EditText) view
				.findViewById(R.id.update_password));
		if (id == 1) {
			updateUserNameEditText.setEnabled(true);
			newPasswordEditText.setEnabled(false);
			updatePasswordEditText.setEnabled(false);
		} else {
			updateUserNameEditText.setEnabled(false);
			newPasswordEditText.setEnabled(true);
			updatePasswordEditText.setEnabled(true);
		}
		updateUserNameEditText.setText(userNameView.getText());
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if (passwordView.getText().toString().equals(
								oldPasswordEditText.getText().toString())) {
							if (id == 1) {
								newUserName = updateUserNameEditText.getText()
										.toString();
								Log.i(TAG, "userNameaaaaaaaaaa" + newUserName);
								getActivity().getmDataSelectImple()
										.setUpdateName(new UpdateImpl());
								getActivity().getmDataSelectImple()
										.updateDeviceName(newUserName);
								deviceInfoView.setEnabled(false);
								getActivity().getmDataSelectImple().setDeviceInfoResult(null);
							} else {
								if (updatePasswordEditText.getText().toString()
										.equals(
												newPasswordEditText.getText()
														.toString())) {
									if (newPasswordEditText.getText()
											.toString().length() == 4) {
										newPassword = updatePasswordEditText
												.getText().toString();
										// passwordView.setText(tp);
										getActivity()
												.getmDataSelectImple()
												.setUpdatePass(new UpdateImpl());
										getActivity().getmDataSelectImple()
												.updateDevicePassword(
														newPassword);
										deviceInfoView.setEnabled(false);
										getActivity().getmDataSelectImple().setDeviceInfoResult(null);
									} else {
										showToast(R.string.empty_password);
									}
								} else {
									showToast(R.string.re_error);
								}
							}
						} else {
							showToast(R.string.old_error);
						}
					}
				});
		builder.setNegativeButton(R.string.cancel, null);
		builder.create().show();
	}

	private class DeviceInfoResultImpl implements DeviceInfoDataResult {

		@Override
		public void getDeviceInfo(final DeviceInfo info) {
			myHandler.post(new Runnable() {

				@Override
				public void run() {
					if (info != null) {
						Log.i(TAG, "device info" + info);
						infoView.removeAllViews();
						TableRow row = new TableRow(getActivity());
						TextView textView = new TextView(getActivity());
						textView.setWidth(140);
						textView.setText(R.string.device_s_n);
						textView.setGravity(Gravity.CENTER_HORIZONTAL);
						textView.setTextColor(Color.BLACK);
						row.addView(textView);
						textView = new TextView(getActivity());
						textView.setWidth(280);
						textView.setText(info.getDeviceSN());
						textView.setGravity(Gravity.LEFT);
						textView.setTextColor(Color.BLACK);
						row.addView(textView);
						infoView.addView(row);

						row = new TableRow(getActivity());
						textView = new TextView(getActivity());
						textView.setWidth(140);
						textView.setText(R.string.version_number);
						textView.setGravity(Gravity.CENTER_HORIZONTAL);
						textView.setTextColor(Color.BLACK);
						row.addView(textView);
						textView = new TextView(getActivity());
						textView.setWidth(280);
						textView.setText(info.getVersionNumber());
						textView.setGravity(Gravity.LEFT);
						textView.setTextColor(Color.BLACK);
						row.addView(textView);
						infoView.addView(row);

						row = new TableRow(getActivity());
						textView = new TextView(getActivity());
						textView.setWidth(140);
						textView.setText(R.string.device_coding);
						textView.setGravity(Gravity.CENTER_HORIZONTAL);
						textView.setTextColor(Color.BLACK);
						row.addView(textView);
						textView = new TextView(getActivity());
						textView.setWidth(280);
						textView.setText(info.getDeviceCoding());
						textView.setGravity(Gravity.LEFT);
						textView.setTextColor(Color.BLACK);
						row.addView(textView);
						infoView.addView(row);

						row = new TableRow(getActivity());
						textView = new TextView(getActivity());
						textView.setWidth(140);
						textView.setText(R.string.device_time);
						textView.setGravity(Gravity.CENTER_HORIZONTAL);
						textView.setTextColor(Color.BLACK);
						row.addView(textView);
						textView = new TextView(getActivity());
						textView.setWidth(280);
						textView.setText(info.getTime());
						textView.setGravity(Gravity.LEFT);
						textView.setTextColor(Color.BLACK);
						row.addView(textView);
						infoView.addView(row);

							Log.i(TAG, "username:" + info.getBluetoothName());
							userNameView.setText(info.getBluetoothName());
							passwordView.setText(info.getBluetoothPass());
					}
				}
			});

		}

	}

	private void syncTime() {
		if (getActivity().getmDataSelectImple() != null) {
			getActivity().getmDataSelectImple().setSyncTime(new SyncTimeImpl());
		}
		getActivity().getmDataSelectImple().syncTime();
	}

	@Override
	public void setConnect(boolean isConnect) {
		super.setConnect(isConnect);
		deviceInfoView.setEnabled(isConnect);
		syncTimeView.setEnabled(isConnect);
		changeUserNameView.setEnabled(isConnect);
		changePasswordView.setEnabled(isConnect);
//		if(isConnect){
//			deviceConnectView.setEnabled(false);
//		}else {
//			deviceConnectView.setEnabled(true);
//		}
		
	}

	private class SyncTimeImpl implements SyncTime {

		@Override
		public void isSyncTime(final boolean isOk) {
			Log.i(TAG, "isok" + isOk);
			myHandler.post(new Runnable() {

				@Override
				public void run() {
					if (isOk) {
						showToast(R.string.sync_time_sucess);
					} else {
						showToast(R.string.sync_time_fail);
					}
				}
			});
		}

	}

	private class UpdateImpl implements UpdateUserName, UpdatePassword {

		@Override
		public void isUpdatePassword(final boolean isOk) {
			myHandler.post(new Runnable() {

				@Override
				public void run() {
					if (isOk) {
						passwordView.setText(newPassword);
						showToast(R.string.update_password_success);
					} else {
						showToast(R.string.update_password_fail);
					}
					deviceInfoView.setEnabled(true);
				}

			});
		}

		@Override
		public void isUpdatName(final boolean isOk) {
			myHandler.post(new Runnable() {

				@Override
				public void run() {
					if (isOk) {
						Log.i(TAG, "newUserName:" + newUserName);
						userNameView.setText(newUserName);
						showToast(R.string.update_username_success);
					} else {
						showToast(R.string.update_username_fail);
					}
					deviceInfoView.setEnabled(true);
				}
			});

		}
	}
}
