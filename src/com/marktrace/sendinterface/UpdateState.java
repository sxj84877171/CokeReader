package com.marktrace.sendinterface;

/**
 * 修改当前的连接状态
 * 
 * @author Administrator
 * 
 */
public interface UpdateState {
	/**
	 * 内部连接状态发生变化
	 * 
	 * @param state
	 */
	public void updateState(int state);
}
