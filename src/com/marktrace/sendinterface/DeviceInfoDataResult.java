package com.marktrace.sendinterface;

import com.marktrace.bean.DeviceInfo;

/**
 * 得到设备的信息数据 , 再由界面来实现数据显示
 * 
 * @author Administrator
 * 
 */
public interface DeviceInfoDataResult {
	/**
	 * 得到设备的信息
	 * 
	 * @param card
	 */
	public void getDeviceInfo(DeviceInfo info);
}
