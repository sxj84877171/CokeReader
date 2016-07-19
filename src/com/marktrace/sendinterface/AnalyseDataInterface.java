package com.marktrace.sendinterface;

/**
 * 解析蓝牙读卡设备返回的数据
 * 
 * @author Administrator
 * @hide
 */
public interface AnalyseDataInterface {
	/**
	 * 读写器返回的数据
	 * 
	 * @param buffer
	 *            每次从管道流中读取的字节数组
	 * @param bytes
	 *            每次从管道流中读取的字节数
	 */
	public void readStatus(byte[] buffer, int bytes);
	/**
	 * 取数据
	 */
	public void receiveTagData();
	/**
	 * 读卡
	 */
	public void receiveReadCard();

	/**
	 * 提取记录
	 */
	public void receivePickupNote();

	/**
	 * 获取序列号
	 */
	public void receiveGetSN();
	/**
	 * 获取版本号
	 */
	public void receiveGetVerNum();

	/**
	 * 获取设备编码
	 */
	public void receiveGetCoding();
	/**
	 * 获取设备时间
	 */
	public void receiveGetTime();

	/**
	 * 获取当前蓝牙设备的名称
	 */
	public void receiveGetName();
	/**
	 * 获取当前设备的配对密钥
	 */
	public void receiveGetPassword();

	/**
	 * 同步时间
	 */
	public void receiveSyncTime();

	/**
	 * 修改蓝牙名称
	 */
	public void receiveUpdateBlueName();
	/**
	 * 修改蓝牙密码
	 */
	public void receiveUpdateBluePass();
	/**
	 * 设置设备编码
	 */
	public void receiveSetCoding();
	/**
	 * 设置设备版本号
	 */
	public void receiveSetVerNum();

	/**
	 * 将16进制字节数组转换为字符串，用于显示
	 * 
	 * @param b
	 *            字节数组
	 * @return 字符串
	 */
	public String getHexString(byte[] b);

	/**
	 * 将时间的16进制字节数组转成字符串
	 * 
	 * @param b
	 * @return
	 */
	public String getBcdString(byte[] b);

}
