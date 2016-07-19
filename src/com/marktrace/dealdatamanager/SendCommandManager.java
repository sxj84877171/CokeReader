package com.marktrace.dealdatamanager;

import java.math.BigInteger;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.marktrace.sendinterface.DeviceInfoDataResult;
import com.marktrace.sendinterface.GetSendCommandInterface;
import com.marktrace.sendinterface.ReadTagDataResult;
import com.marktrace.sendinterface.SendCommandInterface;
import com.marktrace.sendinterface.SyncTime;
import com.marktrace.sendinterface.UpdatePassword;
import com.marktrace.sendinterface.UpdateState;
import com.marktrace.sendinterface.UpdateUserName;
import com.marktrace.threadmanager.ConnectThread;
import com.marktrace.threadmanager.ConnectedThread;
import com.marktrace.threadmanager.Constants;

public class SendCommandManager implements GetSendCommandInterface,
		SendCommandInterface {

	private Context mContext;
	// private BluetoothDevice bluetoothDevice = null ;
	// private final BluetoothAdapter mAdapter;
	private int mState;
	private byte pubCommand;// 命令的一部分，用于区分命令
	Timer timer = new Timer();
	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;

	private byte[] pubReadVersionCmd;
	private byte[] pubReadCodingCmd;
	private byte[] pubSendGetTime;
	private byte[] pubGetPassword;
	private byte[] pubGetName;

	public byte[] getPubGetName() {
		return pubGetName;
	}

	public void setPubGetName(byte[] pubGetName) {
		this.pubGetName = pubGetName;
	}

	public byte[] getByteArray(String hexString) {
		return new BigInteger(hexString, 16).toByteArray();

	}

	public void sendCommand(byte[] command) {
		if (getState() == Constants.STATE_CONNECTED) {
			if (command.length > 0) {
				mConnectedThread.write(command);
			}
		}
	}

	public byte[] sendRcvByteNum(byte[] sendbyte) {
		byte[] sendBuf = new byte[sendbyte.length + 1];
		for (int i = 0; i < sendbyte.length; i++) {
			sendBuf[i] = sendbyte[i];
		}
		int sum = 0;
		for (int i = 0; i < sendBuf.length - 1; i++) {
			sum += sendBuf[i];
		}
		sendBuf[sendBuf.length - 1] = (byte) (0xff & (~sum + 1));
		return sendBuf;
	}

	public void start() {
		// mConversationArrayAdapter.clear(); // 清列表
		setCommand((byte) 0x90);
		// TextView view = mOutEditText;
		byte[] startbyte = getByteArray(Constants.STARTCMD);
		byte[] message = sendRcvByteNum(startbyte);
		sendCommand(message);
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				setCommand((byte) 0x92);
				byte[] getdatacmd = { (byte) 0x0a, (byte) 0xff, (byte) 0x02,
						(byte) 0x92, (byte) 0x63 };// 取数据指令的命令
				sendCommand(getdatacmd);
			}
		}, 1000, 1000);
	}

	public void stop() {
		timer.cancel();
		setCommand((byte) 0x91);
		byte[] stopbyte = getByteArray(Constants.STOPCMD);
		byte[] messagestop = sendRcvByteNum(stopbyte);
		sendCommand(messagestop);
	}

	public void pickRecord() {
		setCommand((byte) 0x9b);
		byte[] databyte = getByteArray(Constants.PICKDATACMD);
		byte[] messagedata = sendRcvByteNum(databyte);
		sendCommand(messagedata);
	}

	public void syncTime() {
		// TODO Auto-generated method stub
		setCommand((byte) 0x25);
		byte[] currentTime = getSyncTimeCmd();
		sendCommand(currentTime);
	}

	public void updateDeviceName(String name) {
		// TODO Auto-generated method stub
		setCommand((byte) 0x2b);
		byte[] bname = getUpdateDeviceNameCmd(name);
		sendCommand(bname);
	}

	public void updateDevicePassword(String password) {
		// TODO Auto-generated method stub
		setCommand((byte) 0x2c);
		byte[] bpassword = getUpdateDevicePasswordCmd(password);
		sendCommand(sendRcvByteNum(bpassword));
	}

	public void getDeviceInfo() {
		setCommand((byte) 0x2a);
		sendCommand(sendRcvByteNum(Constants.READEQUSNCMD));
		setPubReadVersionCmd(sendRcvByteNum(Constants.READVERSIONCMD));
		setPubReadCodingCmd(sendRcvByteNum(Constants.READCODINGCMD));
		setPubSendGetTime(sendRcvByteNum(Constants.SENDGETTIMECMD));
		setPubGetName(sendRcvByteNum(Constants.GETNAMECMD));
		setPubGetPassword(sendRcvByteNum(Constants.GETPASSWORDCMD));
	}
   
	public void getCurrentNameAndPassword() {
		setCommand((byte) 0x2d);
		sendCommand(sendRcvByteNum(Constants.GETNAMECMD));
		setPubGetPassword(sendRcvByteNum(Constants.GETPASSWORDCMD));
	}

	public byte[] getSyncTimeCmd() {
		byte[] bcd_time = new byte[11];
		bcd_time[0] = (byte) 0x0a;
		bcd_time[1] = (byte) 0xff;
		bcd_time[2] = (byte) 0x08;
		bcd_time[3] = (byte) 0x25;
		Date currentTime = new Date();
		java.text.DateFormat df = new java.text.SimpleDateFormat("yy-MM-dd");
		String s = df.format(currentTime);
		String date[] = s.split("-");
		bcd_time[4] = (byte) DectoBCD(currentTime.getYear() % 100);
		bcd_time[5] = Byte.parseByte(date[1]);
		bcd_time[6] = Byte.parseByte(date[2]);
		bcd_time[7] = (byte) DectoBCD(currentTime.getHours());
		bcd_time[8] = (byte) DectoBCD(currentTime.getMinutes());
		bcd_time[9] = (byte) DectoBCD(currentTime.getSeconds());
		int sum = 0;
		int size = bcd_time.length;
		for (int j = 0; j < size - 1; j++) {
			sum += bcd_time[j];
		}
		bcd_time[size - 1] = (byte) (0xFF & (~sum + 1));

		return bcd_time;
	}

	public int DectoBCD(int Dec) {
		int Bcd = 0;
		Bcd = ((Dec / 10) << 4) + ((Dec % 10) & 0x0F);
		return Bcd;
	}

	public boolean isCheckStr(String str, int length) {
		try {
			Integer.parseInt(str, length);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public byte[] getUpdateDeviceNameCmd(String name) {
		byte[] newname = name.getBytes();
		int length = newname.length;
		// 修改蓝牙指令的命令
		byte[] updatename = new byte[6 + length];
		updatename[0] = (byte) 0x0a;
		updatename[1] = (byte) 0xff;
		updatename[2] = (byte) (length + 2 + 1);// 为输入蓝牙名称+换行/回车符+后面两个字节
		updatename[3] = (byte) 0x2b;
		for (int j = 0; j < length; j++) {
			updatename[4 + j] = newname[j];
		}
		updatename[updatename.length - 2] = (byte) 0x0a;// 后面以换行或回车结束
		int sum = 0;
		for (int i = 0; i < updatename.length - 1; i++) {
			sum += updatename[i];
		}
		updatename[updatename.length - 1] = (byte) (0xFF & (~sum + 1));
		return updatename;
	}

	public byte[] getUpdateDevicePasswordCmd(String password) {
		byte[] updatep = getByteArray(Constants.UPDATEPASSCMD);
		byte[] updatepasscmd = getByteArray(password);
		byte[] passcmd = new byte[updatep.length + updatepasscmd.length];
		for (int i = 0; i < updatep.length; i++) {
			passcmd[i] = updatep[i];
		}
		for (int i = 0; i < updatepasscmd.length; i++) {
			passcmd[updatep.length + i] = updatepasscmd[i];
		}
		return passcmd;
	}

	public SendCommandManager(Context context, BluetoothDevice bluetoothDevice) {
		this.mContext = context;
		setState(Constants.STATE_NONE);
		connect(bluetoothDevice);

	}

	public ReadTagDataResult getTagDataResult() {
		return this.tagDataResult;
	}

	public void setTagDataResult(ReadTagDataResult tagDataResult) {
		this.tagDataResult = tagDataResult ;
		if(mConnectedThread != null){
			mConnectedThread.setTagDataResult(tagDataResult);
		}
	}

	public synchronized void connect(BluetoothDevice device) {
		// Start the thread to connect with the given device
		mConnectThread = new ConnectThread(device, mContext);
		mConnectThread.setmSendCommandManager2(this);
		mConnectThread.start();
		setState(Constants.STATE_CONNECTING);
	}

	// private AcceptThread mAcceptThread;
	public void setState(final int state) {
		this.mState = state;
		if (updateState != null) {
			updateState.updateState(state);
		}

	}

	public int getState() {
		return mState;
	}

	public ConnectedThread getmConnectedThread() {
		return mConnectedThread;
	}

	public void setmConnectedThread(ConnectedThread mConnectedThread) {
		this.mConnectedThread = mConnectedThread;
	}

	public byte[] getPubReadVersionCmd() {
		return pubReadVersionCmd;
	}

	public void setPubReadVersionCmd(byte[] pubReadVersionCmd) {
		this.pubReadVersionCmd = pubReadVersionCmd;
	}

	public byte[] getPubReadCodingCmd() {
		return pubReadCodingCmd;
	}

	public void setPubReadCodingCmd(byte[] pubReadCodingCmd) {
		this.pubReadCodingCmd = pubReadCodingCmd;
	}

	public byte[] getPubSendGetTime() {
		return pubSendGetTime;
	}

	public void setPubSendGetTime(byte[] pubSendGetTime) {
		this.pubSendGetTime = pubSendGetTime;
	}

	public byte[] getPubGetPassword() {
		return pubGetPassword;
	}

	public void setPubGetPassword(byte[] pubGetPassword) {
		this.pubGetPassword = pubGetPassword;
	}

	public byte getCommand() {
		return pubCommand;
	}

	public void setCommand(byte pubCommand) {
		this.pubCommand = pubCommand;
	}

	public DeviceInfoDataResult getDeviceInfoResult() {
		return deviceInfoResult;
	}

	public void setDeviceInfoResult(DeviceInfoDataResult deviceInfoResult) {
		this.deviceInfoResult = deviceInfoResult ;
		if(mConnectedThread != null){
			mConnectedThread.setDeviceInfoResult(deviceInfoResult);
		}
	}

	public SyncTime getSyncTime() {
		return syncTime;
	}
	
	public void setSyncTime(SyncTime syncTime) {
		this.syncTime = syncTime ;
		if(mConnectedThread != null){
			mConnectedThread.setSyncTime(syncTime);
		}
	}

	public UpdatePassword getUpdatePass() {
		return this.updatePass;
	}

	public void setUpdatePass(UpdatePassword updatePass) {
		this.updatePass = updatePass ;
		if(mConnectedThread != null){
			mConnectedThread.setUpdatePass(updatePass);
		}
	}

	public UpdateUserName getUpdateName() {
		return updateName;
	}

	public void setUpdateName(UpdateUserName updateName) {
		this.updateName = updateName;
		if(mConnectedThread != null){
			mConnectedThread.setUpdateName(updateName);
		}
	}

	public UpdateState getUpdateState() {
		return updateState;
	}

	public void setUpdateState(UpdateState updateState) {
		this.updateState = updateState ;
		if(mConnectedThread != null){
			mConnectedThread.setUpdatePass(updatePass);
		}
	}
	
	private ReadTagDataResult tagDataResult = null;
	private DeviceInfoDataResult deviceInfoResult = null;
	private SyncTime syncTime = null;
	private UpdatePassword updatePass = null;
	private UpdateUserName updateName = null;
	private UpdateState updateState = null;
	
	
	public void destroy(){
		
		setState(Constants.STATE_NONE);
	}

}
