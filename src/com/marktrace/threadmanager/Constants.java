package com.marktrace.threadmanager;

public class Constants {

	public static final int STATE_NONE = 0; // we're doing nothing
	public static final int STATE_LISTEN = 1; // now listening for incoming
	// connections
	public static final int STATE_CONNECTING = 2; // now initiating an outgoing
													// connection
	public static final int STATE_CONNECTED = 3; // now connected to a remote
													// device

	public static final int STATE_CONNECT_FAIL = 4;

	// √¸¡Ó
	public static final String STARTCMD = "0aff0290";
	public static final String STOPCMD = "0aff0291";
	public static final String PICKDATACMD = "0aff029b";
	public static final String WRITECODINGCMD = "0aff0627";
	public static final String UPDATEPASSCMD = "0aff062c";
	public static final String WRITEEQUSNCMD = "0aff0a29";
	public static final byte[] READCODINGCMD = { (byte) 0x0a, (byte) 0xff,
			(byte) 0x02, (byte) 0x28 };
	public static final byte[] READVERSIONCMD = { (byte) 0x0a, (byte) 0xff,
			(byte) 0x02, (byte) 0x22 };
	public static final byte[] SENDGETTIMECMD = { (byte) 0x0a, (byte) 0xff,
			(byte) 0x02, (byte) 0x26 };
	public static final byte[] READEQUSNCMD = { (byte) 0x0a, (byte) 0xff,
			(byte) 0x02, (byte) 0x2a };
	public static final byte[] GETNAMECMD = { (byte) 0x0a, (byte) 0xff,
			(byte) 0x02, (byte) 0x2d };
	public static final byte[] GETPASSWORDCMD = { (byte) 0x0a, (byte) 0xff,
			(byte) 0x02, (byte) 0x2e };

}
