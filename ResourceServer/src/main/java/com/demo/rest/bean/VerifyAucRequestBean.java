package com.demo.rest.bean;

import com.demo.common.domain.customer.ConsumerMapped;

public class VerifyAucRequestBean extends SwitchRequestBean {

	private static final long serialVersionUID = 4771257674304043724L;

	private String validationCode;
	private ConsumerMapped consumer;
	
	public String getValidationCode() {
		return validationCode;
	}
	public void setValidationCode(String validationCode) {
		this.validationCode = validationCode;
	}
	public ConsumerMapped getConsumer() {
		return consumer;
	}
	public void setConsumer(ConsumerMapped consumer) {
		this.consumer = consumer;
	}
	
	
	
}
