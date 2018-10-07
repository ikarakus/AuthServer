package com.demo.rest.bean;

public class VerifyRegisteredCardRequestBean extends SwitchRequestBean {

	private static final long serialVersionUID = 8014410043152224714L;
	
	private Long registeredCardId;
	private String verificationNumber;
	
	public Long getRegisteredCardId() {
		return registeredCardId;
	}
	public void setRegisteredCardId(Long registeredCardId) {
		this.registeredCardId = registeredCardId;
	}
	public String getVerificationNumber() {
		return verificationNumber;
	}
	public void setVerificationNumber(String verificationNumber) {
		this.verificationNumber = verificationNumber;
	}


}
