package com.marktrace.sendinterface;

import java.util.List;

import com.marktrace.bean.CardBean;

/**
 * 得到设备读取的标签数据, 再由界面来实现数据显示
 * 
 * @author Administrator
 * 
 */
public interface ReadTagDataResult {
	/**
	 * 得到设备读取的标签数据
	 * 
	 * @param data
	 */
	public void visble(List<CardBean> data);
}
