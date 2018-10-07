package com.demo.rest.bean;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.google.gson.annotations.Expose;
import com.demo.common.domain.transaction.request.merchant.TransferFundsToMerchantRequest;

/**
 * @author LinesCode
 *
 */
public class TransferFundsToMerchantAuthRequestBean extends MerchantRequestBean<TransferFundsToMerchantRequest> {

	@Expose
	@NotNull
	@Size(min = 1, max = 12)
	private String merchantCode;

	@Expose
	@NotNull
	@Size(min = 7, max = 255)
	private String customerUserName;

	@NotNull
	@Size(min = 8, max = 32)
	private String customerPassword;

	@Expose
	private Boolean checkCustomerName;

	@Expose
	@Size(min = 2, max = 70)
	private String customerName;

	@Expose
	@NotNull
	@Pattern(regexp = "^[0-9]{1,9}\\.[0-9]{2}$")
	private String amount;

	@Expose
	@NotNull
	@Size(min = 3, max = 3)
	private String currency;

	@Expose
	@NotNull
	@Size(min = 1, max = 255)
	private String description;

	@Expose
	@Size(min = 1, max = 255)
	private String merchantCustomerId;

	@Expose
	@NotNull
	@Size(min = 1, max = 255)
	private String merchantTransactionId;

	public String getCustomerPassword() {
		return customerPassword;
	}

	public void setCustomerPassword(String customerPassword) {
		this.customerPassword = customerPassword;
	}

	public Boolean getCheckCustomerName() {
		return checkCustomerName;
	}

	public void setCheckCustomerName(Boolean checkCustomerName) {
		this.checkCustomerName = checkCustomerName;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMerchantCustomerId() {
		return merchantCustomerId;
	}

	public void setMerchantCustomerId(String merchantCustomerId) {
		this.merchantCustomerId = merchantCustomerId;
	}

	public String getMerchantTransactionId() {
		return merchantTransactionId;
	}

	public void setMerchantTransactionId(String merchantTransactionId) {
		this.merchantTransactionId = merchantTransactionId;
	}

	public String getCustomerUserName() {
		return customerUserName;
	}

	public void setCustomerUserName(String customerUserName) {
		this.customerUserName = customerUserName;
	}

	public String getMerchantCode() {
		return merchantCode;
	}

	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}
}
