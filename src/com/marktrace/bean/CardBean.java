package com.marktrace.bean;

/**
 * 界面显示的标签实体对象
 * 
 * @author Administrator
 * 
 */
public class CardBean {
	/**
	 * 标签
	 */
	private String cardNo;
	/**
	 * 时间
	 */
	private String time;
	/**
	 * 次数
	 */
	private int cardCount = 1;

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getCardCount() {
		return cardCount;
	}

	public void setCardCount(int cardCount) {
		this.cardCount = cardCount;
	}

}
