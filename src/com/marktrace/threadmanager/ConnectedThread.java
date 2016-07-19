package com.marktrace.threadmanager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.bluetooth.BluetoothSocket;

import com.marktrace.bean.CardBean;
import com.marktrace.bean.DeviceInfo;
import com.marktrace.dealdatamanager.SendCommandManager;
import com.marktrace.sendinterface.AnalyseDataInterface;
import com.marktrace.sendinterface.DeviceInfoDataResult;
import com.marktrace.sendinterface.ReadTagDataResult;
import com.marktrace.sendinterface.SyncTime;
import com.marktrace.sendinterface.UpdatePassword;
import com.marktrace.sendinterface.UpdateState;
import com.marktrace.sendinterface.UpdateUserName;

/**
 * 连接之后控制取数据
 * 
 * @author Administrator
 * 
 */
public class ConnectedThread extends Thread implements AnalyseDataInterface {

	private ReadTagDataResult tagDataResult = null;
	private DeviceInfoDataResult deviceInfoResult = null;
	private SyncTime syncTime = null;
	private UpdatePassword updatePass = null;
	private UpdateUserName updateName = null;
	private UpdateState updateState = null;

	public ReadTagDataResult getTagDataResult() {
		return tagDataResult;
	}

	public void setTagDataResult(ReadTagDataResult tagDataResult) {
		this.tagDataResult = tagDataResult;
	}

	public DeviceInfoDataResult getDeviceInfoResult() {
		return deviceInfoResult;
	}

	public void setDeviceInfoResult(DeviceInfoDataResult deviceInfoResult) {
		this.deviceInfoResult = deviceInfoResult;
	}

	public SyncTime getSyncTime() {
		return syncTime;
	}

	public void setSyncTime(SyncTime syncTime) {
		this.syncTime = syncTime;
	}

	public UpdatePassword getUpdatePass() {
		return updatePass;
	}

	public void setUpdatePass(UpdatePassword updatePass) {
		this.updatePass = updatePass;
	}

	public UpdateUserName getUpdateName() {
		return updateName;
	}

	public void setUpdateName(UpdateUserName updateName) {
		this.updateName = updateName;
	}

	public UpdateState getUpdateState() {
		return updateState;
	}

	public void setUpdateState(UpdateState updateState) {
		this.updateState = updateState;
	}

	private final BluetoothSocket mmSocket;
	private final InputStream mmInStream;
	private final OutputStream mmOutStream;
	private SendCommandManager mSendCommandManager2 = null;

	private boolean isStop = true; // 线程是否停止
	private Thread myThread = null;
	private byte[] ReceiveData = new byte[256];// 设备返回的全部数据
	private byte ReceiveCount;// 设备返回的数据总字节数
	private byte ReceiveLen;
	private int RecordCount = 0;

	byte[] tagId = new byte[4];// 标签ID的字节数组
	byte[] byBcdTime = new byte[6];// 设置的时间的字节数组
	byte[] verInfo = new byte[2];// 得到设备的版本号的字节数组
	byte[] getDateTime = new byte[6];// 获取到的设备的时间的字节数组
	byte[] readSN = new byte[8];// 读取到的设备的S/N 序列号的字节数组
	byte RcvDeviceID[] = new byte[4];// 存储设备编码的字节数组
	List<CardBean> cardList = new ArrayList<CardBean>();// 存储标签的集合
	DeviceInfo deviceInfo = new DeviceInfo();

	public ConnectedThread(BluetoothSocket socket,
			SendCommandManager mSendCommandManager) {
		mmSocket = socket;
		this.mSendCommandManager2 = mSendCommandManager;
		setUpdateState(mSendCommandManager2.getUpdateState());
		setDeviceInfoResult(mSendCommandManager2.getDeviceInfoResult());
		setUpdateName(mSendCommandManager2.getUpdateName());
		setUpdatePass(mSendCommandManager2.getUpdatePass());
		setTagDataResult(mSendCommandManager2.getTagDataResult());
		setSyncTime(mSendCommandManager2.getSyncTime());
		InputStream tmpIn = null;
		OutputStream tmpOut = null;

		// Get the BluetoothSocket input and output streams
		try {
			tmpIn = socket.getInputStream();
			tmpOut = socket.getOutputStream();
		} catch (IOException e) {
			mSendCommandManager2.destroy();
		}

		mmInStream = tmpIn;
		mmOutStream = tmpOut;
	}

	public synchronized void start() {
		if (isStop) {
			isStop = false;
			myThread = new Thread(this);
			myThread.start();
			// TODO
		}
	}

	public void pause() {
		// TODO

	}

	public void run() {
		byte[] buffer = new byte[1024];
		int bytes;
		// 判断是否线程是否终止
		while (!isStop) {
			// 读取一次字节，分析
			try {
				// Read from the InputStream
				bytes = mmInStream.read(buffer);
				readStatus(buffer, bytes);
			} catch (IOException e) {
				isStop = true;
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 读写器返回的数据
	 * 
	 * @param buffer
	 *            每次从管道流中读取的字节数组
	 * @param bytes
	 *            每次从管道流中读取的字节数
	 */
	public void readStatus(byte[] buffer, int bytes) {
		cardList.clear();
		if (bytes >= 0) {
			for (int i = 0; i < bytes; i++) {
				if (buffer[i] == (byte) 0x0b) {
					ReceiveData[0] = 0x0b;
					ReceiveCount = 1;
					continue;
				}
				if (ReceiveCount == 1) {
					ReceiveData[1] = buffer[i];
					ReceiveCount++;
					continue;
				}
				if (ReceiveCount == 2) {
					ReceiveData[2] = buffer[i];
					ReceiveCount++;
					ReceiveLen = ReceiveData[2];
					continue;
				}
				if (ReceiveCount >= 2) {
					ReceiveData[ReceiveCount] = buffer[i];
					ReceiveCount++;
				}
				// 去处理命令应答相应的界面数据。
				if (ReceiveCount >= ReceiveLen + 3) {
					switch (mSendCommandManager2.getCommand()) {
					case (byte) 0x92:// 取数据
						receiveTagData();
						break;
					case (byte) 0x9a: // 读卡
						receiveReadCard();
						break;
					case (byte) 0x9b: // 提取记录
						receivePickupNote();
						break;
					case (byte) 0x2a:// 获取序列号
						receiveGetSN();
						break;
					case (byte) 0x22:
						receiveGetVerNum();
						break;
					case (byte) 0x28:
						receiveGetCoding();
						break;
					case (byte) 0x25:
						receiveSyncTime();
						break;
					case (byte) 0x26:
						receiveGetTime();
						break;
					case (byte) 0x29:// 设置序列号
						receiveSetVerNum();
						break;
					case (byte) 0x2b: // 设置蓝牙名称
						receiveUpdateBlueName();
						break;
					case (byte) 0x27:// 设置设备编码
						receiveSetCoding();
						break;
					case (byte) 0x2c:// 修改密码
						receiveUpdateBluePass();
						break;
					case (byte) 0x2d:
						receiveGetName();
						break;
					case (byte) 0x2e:
						receiveGetPassword();
						break;
					}

				}
			}
		}
		if (tagDataResult != null) {
			tagDataResult.visble(cardList);
		}
		if (deviceInfoResult != null) {
			deviceInfoResult.getDeviceInfo(deviceInfo);
		}

	}

	/**
	 * 取数据
	 */
	public void receiveTagData() {

		if (ReceiveData[3] == 0) {
			if (ReceiveData[4] > 0) {
				// int receiveCount = ReceiveData[4];
				for (int j = 0; j < ReceiveData[4]; j++) {
					tagId[0] = ReceiveData[j * 4 + 5];
					tagId[1] = ReceiveData[j * 4 + 6];
					tagId[2] = ReceiveData[j * 4 + 7];
					tagId[3] = ReceiveData[j * 4 + 8];
					String tagData = getHexString(tagId);
					Date currentTime = new Date();
					java.text.DateFormat df = new java.text.SimpleDateFormat(
							"yy-MM-dd hh:mm:ss");
					String readTime = df.format(currentTime);
					CardBean cardData = new CardBean();
					cardData.setCardNo(tagData);
					cardData.setTime(readTime);
					cardList.add(cardData);
				}
			}
		}
	}

	/**
	 * 读卡
	 */
	public void receiveReadCard() {
		if (RecordCount > 0) {
			if (ReceiveData[3] == 0) {
				int nGetCnt = ReceiveData[4];
				for (int kl = 0; kl < nGetCnt; kl++) {
					tagId[0] = ReceiveData[kl * 10 + 5];
					tagId[1] = ReceiveData[kl * 10 + 6];
					tagId[2] = ReceiveData[kl * 10 + 7];
					tagId[3] = ReceiveData[kl * 10 + 8];
					String tagData = getHexString(tagId);

					byBcdTime[0] = ReceiveData[kl * 10 + 9];
					byBcdTime[1] = ReceiveData[kl * 10 + 10];
					byBcdTime[2] = ReceiveData[kl * 10 + 11];
					byBcdTime[3] = ReceiveData[kl * 10 + 12];
					byBcdTime[4] = ReceiveData[kl * 10 + 13];
					byBcdTime[5] = ReceiveData[kl * 10 + 14];
					String readTime = getBcdString(byBcdTime);

					CardBean cardData = new CardBean();
					cardData.setCardNo(tagData);
					cardData.setTime(readTime);
					cardList.add(cardData);
				}
				RecordCount = RecordCount - nGetCnt;
				if (RecordCount > 0) {
					mSendCommandManager2.setCommand((byte) 0x9a);
					byte[] getDataCmd = { (byte) 0x0a, (byte) 0xff,
							(byte) 0x02, (byte) 0x9a, (byte) 0x5b };
					write(getDataCmd);
				}
			}
		}
	}

	/**
	 * 提取记录
	 */
	public void receivePickupNote() {
		if (ReceiveData[3] == 0) {
			RecordCount = ReceiveData[4] * 256 + ReceiveData[5];
			if (RecordCount > 0) {
				mSendCommandManager2.setCommand((byte) 0x9a);
				byte[] getDataCmd = { (byte) 0x0a, (byte) 0xff, (byte) 0x02,
						(byte) 0x9a, (byte) 0x5b };
				write(getDataCmd);
			}
		}
	}

	/**
	 * 获取序列号
	 */
	public void receiveGetSN() {
		if (ReceiveCount > 0) {
			if (ReceiveData[3] == 0) {
				for (int sni = 0; sni < readSN.length; sni++) {
					readSN[sni] = ReceiveData[4 + sni];
				}
				String sn = getHexString(readSN);
				deviceInfo.setDeviceSN(sn);

				mSendCommandManager2.setCommand((byte) 0x22);
				byte[] readVer = mSendCommandManager2.getPubReadVersionCmd();
				write(readVer);

			}
		}
	}

	/**
	 * 获取版本号
	 */
	public void receiveGetVerNum() {
		if (ReceiveCount > 0) {
			if (ReceiveData[3] == 0) {
				verInfo[0] = ReceiveData[4];
				verInfo[1] = ReceiveData[5];
				String strVerInfo = "MR" + getHexString(verInfo);
				deviceInfo.setVersionNumber(strVerInfo);
				mSendCommandManager2.setCommand((byte) 0x28);
				byte[] readCoding = mSendCommandManager2.getPubReadCodingCmd();
				write(readCoding);
			}
		}
	}

	/**
	 * 获取设备编码
	 */
	public void receiveGetCoding() {
		if (ReceiveCount > 0) {
			if (ReceiveData[3] == 0) {
				for (int id = 0; id < RcvDeviceID.length; id++) {
					RcvDeviceID[id] = ReceiveData[4 + id];
				}
				String devicecoding = getHexString(RcvDeviceID);
				deviceInfo.setDeviceCoding(devicecoding);
				mSendCommandManager2.setCommand((byte) 0x26);
				byte[] getTime = mSendCommandManager2.getPubSendGetTime();
				write(getTime);
			}
		}
	}

	/**
	 * 获取设备时间
	 */
	public void receiveGetTime() {
		if (ReceiveCount > 0) {
			if (ReceiveData[3] == 0) {
				for (int time = 0; time < getDateTime.length; time++) {
					getDateTime[time] = ReceiveData[4 + time];
				}
				String gettime = getBcdString(getDateTime);
				deviceInfo.setTime(gettime);
				mSendCommandManager2.setCommand((byte) 0x2d);
				byte[] getName = mSendCommandManager2.getPubGetName();
				write(getName);
			}
		}
	}

	/**
	 * 获取当前蓝牙设备的名称
	 */
	public void receiveGetName() {
		char[] cname = new char[ReceiveData[2] - 2];
		if (ReceiveCount > 0) {
			if (ReceiveData[3] == 0) {
				for (int i = 0; i < cname.length; i++) {
					cname[i] = (char) ReceiveData[4 + i];
				}
			}
			String name = String.valueOf(cname);
			deviceInfo.setBluetoothName(name);
			mSendCommandManager2.setCommand((byte) 0x2e);
			write(mSendCommandManager2.getPubGetPassword());
		}
	}

	/**
	 * 获取当前设备的配对密钥
	 */
	public void receiveGetPassword() {
		char[] cpass = new char[4];
		if (ReceiveCount > 0) {
			if (ReceiveData[3] == 0) {
				for (int i = 0; i < cpass.length; i++) {
					cpass[i] = (char) ReceiveData[4 + i];
				}
				String pass = String.valueOf(cpass);
				deviceInfo.setBluetoothPass(pass);
			}
		}

	}

	/**
	 * 同步时间
	 */
	public void receiveSyncTime() {
		boolean isSyncTimeOk = false;
		if (ReceiveCount > 0) {
			if (ReceiveData[3] == 0)
				isSyncTimeOk = true;
			else
				isSyncTimeOk = false;
		}
		if (syncTime != null) {
			syncTime.isSyncTime(isSyncTimeOk);
		}
	}

	/**
	 * 修改蓝牙名称
	 */
	public void receiveUpdateBlueName() {
		boolean updateNameFlag = false;
		if (ReceiveCount > 0) {
			if (ReceiveData[3] == 0)
				updateNameFlag = true;
			else
				updateNameFlag = false;
		}
		if (updateName != null)
			updateName.isUpdatName(updateNameFlag);
	}

	/**
	 * 修改蓝牙密码
	 */
	public void receiveUpdateBluePass() {
		boolean updatePassFlag = false;
		if (ReceiveCount > 0) {
			if (ReceiveData[3] == 0)
				updatePassFlag = true;
			else
				updatePassFlag = false;
		}
		if (updatePass != null)
			updatePass.isUpdatePassword(updatePassFlag);
	}

	/**
	 * 设置设备编码
	 */
	public void receiveSetCoding() {
		if (ReceiveCount > 0) {
			if (ReceiveData[3] == 0) {
			} else {
			}
		}
	}

	/**
	 * 设置设备版本号
	 */
	public void receiveSetVerNum() {
		if (ReceiveCount > 0) {
			if (ReceiveData[3] == 0) {
			} else {
			}
		}
	}

	/**
	 * 将16进制字节数组转换为字符串，用于显示
	 * 
	 * @param b
	 *            字节数组
	 * @return 字符串
	 */
	public String getHexString(byte[] b) {

		String result = "";
		try {
			for (int i = 0; i < b.length; i++) {
				result += Integer.toString((b[i] & 0xff) + 0x100, 16)
						.substring(1);
			}
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
		return result;
	}

	/**
	 * 将时间的16进制字节数组转成字符串
	 * 
	 * @param b
	 * @return
	 */
	public String getBcdString(byte[] b) {

		String result = "20";
		try {
			result += Integer.toString((b[0] & 0xff) + 0x100, 16).substring(1);
			result += "-";
			result += Integer.toString((b[1] & 0xff) + 0x100, 16).substring(1);
			result += "-";
			result += Integer.toString((b[2] & 0xff) + 0x100, 16).substring(1);
			result += " ";
			result += Integer.toString((b[3] & 0xff) + 0x100, 16).substring(1);
			result += ":";
			result += Integer.toString((b[4] & 0xff) + 0x100, 16).substring(1);
			result += ":";
			result += Integer.toString((b[5] & 0xff) + 0x100, 16).substring(1);
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
		return result;
	}

	public void interrupt() {
		isStop = true;

		// TODO :
	}

	/**
	 * 往数据流中写入数据(指令)
	 * 
	 * @param buffer
	 *            每次写的数据
	 */
	public void write(byte[] buffer) {
		try {
			mmOutStream.write(buffer);
			mmOutStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 连接结束 即关闭socket
	 */
	public void cancel() {
		try {
			mmSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
