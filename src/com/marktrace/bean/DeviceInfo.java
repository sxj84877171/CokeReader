package com.marktrace.bean;

/**
 * 设备信息的实体对象
 * 
 * @author Administrator
 * 
 */
public class DeviceInfo {

	private String deviceSN = null;
	private String bluetoothName = null;
	private String bluetoothPass = null;
	private String versionNumber = null;
	private String deviceCoding = null;
	private String time;

	public String getDeviceCoding() {
		return deviceCoding;
	}

	public void setDeviceCoding(String deviceCoding) {
		this.deviceCoding = deviceCoding;
	}

	public String getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}

	public String getDeviceSN() {
		return deviceSN;
	}

	public void setDeviceSN(String deviceSN) {
		this.deviceSN = deviceSN;
	}

	public String getBluetoothName() {
		return bluetoothName;
	}

	public void setBluetoothName(String bluetoothName) {
		this.bluetoothName = bluetoothName;
	}

	public String getBluetoothPass() {
		return bluetoothPass;
	}

	public void setBluetoothPass(String bluetoothPass) {
		this.bluetoothPass = bluetoothPass;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
