package com.demo.rest.bean;

import java.io.Serializable;

public class EwalletResponseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7953913714778220580L;

	private String accountType;
	private String accNumber;
	private String currencyCode;
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public String getAccNumber() {
		return accNumber;
	}
	public void setAccNumber(String accNumber) {
		this.accNumber = accNumber;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}


}
