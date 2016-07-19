package com.marktrace.mode;

import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.marktrace.R;
import com.marktrace.dealdatamanager.SendCommandManager;

public class SearchBluetooth {

	protected Activity mActivity = null;
	private ArrayAdapter<String> mPairedDevicesArrayAdapter;
	private ArrayAdapter<String> mNewDevicesArrayAdapter;
	private AbstractCodeReader acr = null;
	private BluetoothAdapter mAdapter;
	private AlertDialog deviceDialog = null;

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					mNewDevicesArrayAdapter.add(device.getName() + "\n"
							+ device.getAddress());
				}
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				// onDestroy();
				if (mNewDevicesArrayAdapter.getCount() == 0) {
					mNewDevicesArrayAdapter.add("no devices");
				}
			}
		}
	};

	public void setAcr(AbstractCodeReader acr) {
		this.acr = acr;
	}

	public SearchBluetooth(Activity mActivity) {
		this.mActivity = mActivity;
	}

	public void setActivity(Activity mActivity) {
		this.mActivity = mActivity;
	}

	public Activity getActivity() {
		return this.mActivity;
	}

	public void onResume() {
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		mActivity.registerReceiver(mReceiver, filter);
	};

	public void onDestroy() {
		mActivity.unregisterReceiver(mReceiver);
	}

	public void startBluetooth() {

		mAdapter.startDiscovery();
	}

	public void show() {

		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setTitle(R.string.search_bluetooth);
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		final View bluetoothView = inflater.inflate(R.layout.device, null);
		ListView pairedListView = (ListView) bluetoothView
				.findViewById(R.id.paired_devices);
		ListView newDevicesListView = (ListView) bluetoothView
				.findViewById(R.id.new_devices);
		mPairedDevicesArrayAdapter = new ArrayAdapter<String>(getActivity(),
				R.layout.device_name);
		mNewDevicesArrayAdapter = new ArrayAdapter<String>(getActivity(),
				R.layout.device_name);
		newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
		newDevicesListView.setOnItemClickListener(mDeviceClickListener);
		pairedListView.setAdapter(mPairedDevicesArrayAdapter);
		pairedListView.setOnItemClickListener(mDeviceClickListener);
		View scanView = bluetoothView.findViewById(R.id.button_scan);
		scanView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				bluetoothView.findViewById(R.id.title_new_devices)
						.setVisibility(View.VISIBLE);

				startBluetooth();
			}
		});

		builder.setView(bluetoothView);
		deviceDialog = builder.create();
		// 蓝牙 是否已经打开，没打开，发送请求打开蓝牙
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mAdapter == null) {
			acr.showToast(R.string.device_bluetooth);
			return;
		}
		if (!mAdapter.isEnabled()) {
			mAdapter.enable();
		} else {
			if (mAdapter.isDiscovering()) {
				mAdapter.cancelDiscovery();
			}
		}

		deviceDialog.show();
		// Get a set of currently paired devices
		Set<BluetoothDevice> pairedDevices = mAdapter.getBondedDevices();

		// If there are paired devices, add each one to the ArrayAdapter
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				mPairedDevicesArrayAdapter.add(device.getName() + "\n"
						+ device.getAddress());
			}
		} else {
			String noDevices = "no devices";
			mPairedDevicesArrayAdapter.add(noDevices);
		}

	}

	private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
			// Cancel discovery because it's costly and we're about to connect
			mAdapter.cancelDiscovery();

			// Get the device MAC address, which is the last 17 chars in the
			// View
			String info = ((TextView) v).getText().toString();
			String address = info.substring(info.length() - 17);
			BluetoothDevice device = mAdapter.getRemoteDevice(address);
			acr.setDevice(device);
			acr.setmDataSelectImple(new SendCommandManager(getActivity(), device));
			deviceDialog.dismiss();
			onDestroy();
		}
	};

}
