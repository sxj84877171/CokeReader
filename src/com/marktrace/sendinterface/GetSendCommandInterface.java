package com.marktrace.sendinterface;

/**
 * 处理发送的指令
 * 
 * @author Administrator
 * @hide
 */
public interface GetSendCommandInterface {
	/**
	 * 计算校验和
	 * 
	 * @param sendBuffer
	 *            要发送的指令(未计算校验和)
	 */
	public byte[] sendRcvByteNum(byte[] sendBuffer);

	/**
	 * 发送指令
	 * 
	 * @param command
	 *            发送的指令
	 */
	public void sendCommand(byte[] command);

	/**
	 * 获取同步时间的指令
	 * 
	 * @return 指令
	 */
	public byte[] getSyncTimeCmd();

	/**
	 * 获取修改设备名称的指令
	 * 
	 * @param name
	 *            新的名称
	 * @return 指令
	 */
	public byte[] getUpdateDeviceNameCmd(String name);

	/**
	 * 获取修改配对密码的指令
	 * 
	 * @param password
	 *            新的密码
	 * @return
	 */
	public byte[] getUpdateDevicePasswordCmd(String password);

	/**
	 * 将字符串转换成十六进制的字节数组
	 * 
	 * @param hexString
	 * @return
	 */
	public byte[] getByteArray(String hexString);

	/**
	 * BCD码
	 * 
	 * @param Dec
	 * @return
	 */
	public int DectoBCD(int Dec);

	/**
	 * 判断输入的数据是否合法
	 * 
	 * @param ver
	 *            输入的字符
	 * @param length
	 *            输入字符的长度
	 */
	public boolean isCheckStr(String str, int length);
}
