package com.demo.rest.bean;

import com.demo.common.domain.customer.ConsumerMapped;

public class PostRegisterNewLoadCardRequestBean extends SwitchRequestBean {

	private static final long serialVersionUID = -8958431181957354657L;

	private ConsumerMapped consumerMapped;
	private String clientIp;
	private String cid; 
	
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

}
