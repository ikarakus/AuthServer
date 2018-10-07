package com.demo.rest.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class BalanceResponseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7953913714778220580L;

	private BigDecimal balance;
	private BigDecimal availableBalance;

	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	public BigDecimal getAvailableBalance() {
		return availableBalance;
	}
	public void setAvailableBalance(BigDecimal availableBalance) {
		this.availableBalance = availableBalance;
	}



}
