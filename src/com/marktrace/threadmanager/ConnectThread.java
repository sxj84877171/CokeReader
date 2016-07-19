package com.marktrace.threadmanager;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.marktrace.dealdatamanager.SendCommandManager;

/**
 * 控制连接设备
 * 
 * @author Administrator
 * 
 */
public class ConnectThread extends Thread {
	private final BluetoothSocket mmSocket;
	private final BluetoothDevice mmDevice;
	private SendCommandManager mSendCommandManager2 = null;
	private ConnectedThread connectedThread = null ;
	private Context mContext ;

	public ConnectThread(BluetoothDevice device,Context mContext) {
		mmDevice = device;
		this.mContext = mContext ;
		BluetoothSocket tmp = null;

		// Get a BluetoothSocket for a connection with the
		// given BluetoothDevice
		try {
			tmp = device
					.createRfcommSocketToServiceRecord(UUID
							.fromString("00001101-0000-1000-8000-00805F9B34FB"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		mmSocket = tmp;
	}

	public void run() {
		
		// Always cancel discovery because it will slow down a connection
		BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
		if(mmSocket == null){
			if(mSendCommandManager2 != null){
				mSendCommandManager2.setState(Constants.STATE_CONNECT_FAIL);
			}
		}
		// Make a connection to the BluetoothSocket
		try {
			// This is a blocking call and will only return on a
			// successful connection or an exception
			mmSocket.connect();
		} catch (IOException e) {
			// Close the socket
			try {
				mmSocket.close();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			if(mSendCommandManager2 != null){
				mSendCommandManager2.setState(Constants.STATE_CONNECT_FAIL);
			}
			return;
		}

		connected(mmSocket, mmDevice);
		if(mSendCommandManager2 != null){
			mSendCommandManager2.setState(Constants.STATE_CONNECTED);
		}

	}

	public Context getmContext() {
		return mContext;
	}

	public void setmContext(Context mContext) {
		this.mContext = mContext;
	}

	/**
	 * 连接设备
	 * 
	 * @param socket
	 * @param device
	 */
	public synchronized void connected(BluetoothSocket socket,
			BluetoothDevice device) {
		connectedThread = new ConnectedThread(socket,mSendCommandManager2);
		if(mSendCommandManager2 != null){
			mSendCommandManager2.setmConnectedThread(connectedThread);
		}
		connectedThread.start();
	}


	public void cancel() {
		try {
			mmSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public BluetoothSocket getSocket() {
		return mmSocket;
	}

	public BluetoothDevice getDevice() {
		return mmDevice;
	}
	
	public ConnectedThread getConnectedThread(){
		return this.connectedThread ;
	}

	public SendCommandManager getmSendCommandManager2() {
		return mSendCommandManager2;
	}

	public void setmSendCommandManager2(SendCommandManager mSendCommandManager2) {
		this.mSendCommandManager2 = mSendCommandManager2;
	}
	
	
}
