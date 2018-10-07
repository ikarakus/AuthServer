package com.demo.rest.bean;

import com.demo.common.bean.payon.PayonTransactionBean;
import com.demo.common.domain.customer.ConsumerMapped;

public class PostAmountLoadFundsByCardRequestBean extends SwitchRequestBean {
	private static final long serialVersionUID = -3207593855284030624L;
	
	private String amount;
	private ConsumerMapped consumerMapped;
	private String clientIp;
	private String cid; 
	private String language;
	private PayonTransactionBean payonBean;

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public ConsumerMapped getConsumerMapped() {
		return consumerMapped;
	}

	public void setConsumerMapped(ConsumerMapped consumerMapped) {
		this.consumerMapped = consumerMapped;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public PayonTransactionBean getPayonBean() {
		return payonBean;
	}

	public void setPayonBean(PayonTransactionBean payonBean) {
		this.payonBean = payonBean;
	}
	
}
