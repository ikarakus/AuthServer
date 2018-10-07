/**
 * @author Ern
 */
package com.demo.rest.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Locale;

import com.revoframework.module.cms.ApplicationContext;
import com.revoframework.util.NumberUtil;

/**
 * @author Ern
 *
 */
public class TransactionBean implements Serializable {

	private static final long serialVersionUID = 2241207749275071614L;

	private String currency;
	private BigDecimal amount;
	private String amountLocale;
	private String description;
	private String accountNumber;
	private Locale locale;
	private BigDecimal currentBalance;
	private String currentBalanceLocale;
	private BigDecimal currentAvailableBalance;
	private String currentAvailableBalanceLocale;
	private String mensilyLoadTransaction;
	private String mensilyUnLoadTransaction;

	public TransactionBean() {
		this.locale = ApplicationContext.getApplication().getUserLocale();
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public BigDecimal getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(BigDecimal currentBalance) {
		setCurrentBalanceLocale(NumberUtil.formatCurrencyNoEuro(currentBalance.doubleValue(), getLocale()));
		this.currentBalance = currentBalance;
	}

	public String getCurrentBalanceLocale() {
		return currentBalanceLocale;
	}

	public void setCurrentBalanceLocale(String currentBalanceLocale) {
		this.currentBalanceLocale = currentBalanceLocale;
	}

	public BigDecimal getCurrentAvailableBalance() {
		return currentAvailableBalance;
	}

	public void setCurrentAvailableBalance(BigDecimal currentAvailableBalance) {
		setCurrentAvailableBalanceLocale(NumberUtil.formatCurrencyNoEuro(currentAvailableBalance.doubleValue(), getLocale()));
		this.currentAvailableBalance = currentAvailableBalance;
	}

	public String getCurrentAvailableBalanceLocale() {
		return currentAvailableBalanceLocale;
	}

	public void setCurrentAvailableBalanceLocale(String currentAvailableBalanceLocale) {
		this.currentAvailableBalanceLocale = currentAvailableBalanceLocale;
	}

	public String getMensilyLoadTransaction() {
		return mensilyLoadTransaction;
	}

	public void setMensilyLoadTransaction(String mensilyLoadTransaction) {
		this.mensilyLoadTransaction = mensilyLoadTransaction;
	}

	public String getMensilyUnLoadTransaction() {
		return mensilyUnLoadTransaction;
	}

	public void setMensilyUnLoadTransaction(String mensilyUnLoadTransaction) {
		this.mensilyUnLoadTransaction = mensilyUnLoadTransaction;
	}

	public String getAmountLocale() {
		return NumberUtil.formatCurrencyNoEuro(amount.doubleValue());
	}

	public void setAmountLocale(String amountLocale) {
		this.amountLocale = amountLocale;
	}

	 
}
