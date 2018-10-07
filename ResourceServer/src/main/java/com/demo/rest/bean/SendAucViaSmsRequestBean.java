package com.demo.rest.bean;

import com.demo.common.domain.customer.ConsumerMapped;

public class SendAucViaSmsRequestBean extends SwitchRequestBean {

	private static final long serialVersionUID = -8818532489804384654L;

	private ConsumerMapped consumer; 
	private Integer smsType;
	
	public ConsumerMapped getConsumer() {
		return consumer;
	}
	public void setConsumer(ConsumerMapped consumer) {
		this.consumer = consumer;
	}
	public Integer getSmsType() {
		return smsType;
	}
	public void setSmsType(Integer smsType) {
		this.smsType = smsType;
	}
	
	

}
