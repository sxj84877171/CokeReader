package com.marktrace.mode;

import android.bluetooth.BluetoothDevice;
import android.widget.Toast;

import com.marktrace.activity.CokeReaderActivity;
import com.marktrace.dealdatamanager.SendCommandManager;
import com.marktrace.sendinterface.SendCommandInterface;

/**
 * @author elvis
 * 
 */
public abstract class AbstractCodeReader {

	protected String TAG = AbstractCodeReader.class.getName();
	protected CokeReaderActivity mActivity = null;
	protected BluetoothDevice device ; 

	protected boolean isConnect;

	public SendCommandInterface getmDataSelectImple() {
		return mActivity.getmDataSelectImple();
	}

	public void setmDataSelectImple(SendCommandManager mDataSelectImple) {
		mActivity.setmDataSelectImple(mDataSelectImple);
	}

	public void setDevice(BluetoothDevice device){
		this.device = device ;
	}
	
	public BluetoothDevice getDevice(){
		return this.device ;
	}

	

	public CokeReaderActivity getmActivity() {
		return mActivity;
	}
	
	public CokeReaderActivity getActivity() {
		return mActivity;
	}
	

	public void setmActivity(CokeReaderActivity mActivity) {
		this.mActivity = mActivity;
	}

	/**
	 * bind the view
	 */
	public abstract void bindView();

	/**
	 * find view by view id .
	 */
	public abstract void findView();
	/**
	 * init data
	 */
	public abstract void init();

	public void onCreate() {
		findView();
		bindView();
		init();
	}

	public void onStart() {

	}

	public void onResume() {

	}

	public void onPause() {

	}

	public void onDestroy() {

	}

	public void onRestart() {

	}

	protected void showToast(int id) {
		Toast.makeText(getActivity(), id, Toast.LENGTH_SHORT).show();
	}
	
	protected void showToast(String id) {
		Toast.makeText(getActivity(), id, Toast.LENGTH_SHORT).show();
	}

	public boolean isConnect() {
		return isConnect;
	}

	public void setConnect(boolean isConnect) {
		this.isConnect = isConnect;
	}
}
