package com.marktrace.sendinterface;

public interface SendCommandInterface {

	/**
	 * 开始读取
	 */
	public void start();

	/**
	 * 停止读取
	 */
	public void stop();

	/**
	 * 提取记录
	 */
	public void pickRecord();

	/**
	 * 同步时间
	 */
	public void syncTime();

	/**
	 * 修改设备名称
	 * 
	 * @param name
	 *            新的设备名称
	 */
	public void updateDeviceName(String name);

	/**
	 * 修改配对密码
	 * 
	 * @param password
	 *            新的配对密码
	 */
	public void updateDevicePassword(String password);

	/**
	 * 获取设备信息
	 */
	public void getDeviceInfo();

	/**
	 * 获取当前的设备名称和配对密码
	 */
	public void getCurrentNameAndPassword();

	/**
	 * 获取当前的连接状态
	 * 
	 * @return 连接状态
	 */
	public int getState();

	public UpdateUserName getUpdateName();

	public void setUpdateState(UpdateState updateState);

	public void setDeviceInfoResult(DeviceInfoDataResult deviceInfoResult);

	public void destroy();

	public void setUpdateName(UpdateUserName updateName);

	public void setSyncTime(SyncTime syncTime);
	
	public void setUpdatePass(UpdatePassword updatePass);
	
	public void setTagDataResult(ReadTagDataResult tagDataResult);
	
	public void setState(final int state);

}
