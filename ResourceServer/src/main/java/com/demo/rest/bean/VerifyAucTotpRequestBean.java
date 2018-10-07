package com.demo.rest.bean;

import com.revoframework.domain.common.User;

public class VerifyAucTotpRequestBean extends SwitchRequestBean {

	private static final long serialVersionUID = -874530346110080600L;

	private String validationCode;
	private User user;
	
	public String getValidationCode() {
		return validationCode;
	}
	public void setValidationCode(String validationCode) {
		this.validationCode = validationCode;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
}
