package com.demo.rest.bean;

import com.demo.common.domain.customer.Consumer;

public class EwalletToEwalletTransferRequestBean extends SwitchRequestBean {

	private static final long serialVersionUID = -1645964670937203871L;

	private TransactionEwalletBean ewalletBean;
	private boolean sendToAccountNotExist; 
	private Consumer consumer;
	private String emailBenefeciary;
	
	public TransactionEwalletBean getEwalletBean() {
		return ewalletBean;
	}
	public void setEwalletBean(TransactionEwalletBean ewalletBean) {
		this.ewalletBean = ewalletBean;
	}
	public boolean isSendToAccountNotExist() {
		return sendToAccountNotExist;
	}
	public void setSendToAccountNotExist(boolean sendToAccountNotExist) {
		this.sendToAccountNotExist = sendToAccountNotExist;
	}
	public Consumer getConsumer() {
		return consumer;
	}
	public void setConsumer(Consumer consumer) {
		this.consumer = consumer;
	}
	public String getEmailBenefeciary() {
		return emailBenefeciary;
	}
	public void setEmailBenefeciary(String emailBenefeciary) {
		this.emailBenefeciary = emailBenefeciary;
	}
	
	

}
