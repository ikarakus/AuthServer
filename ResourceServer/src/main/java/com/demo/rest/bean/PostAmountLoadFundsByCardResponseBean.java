package com.demo.rest.bean;

import com.demo.common.bean.payon.PayonTransactionBean;

public class PostAmountLoadFundsByCardResponseBean extends SwitchResponseBean {
	
	private static final long serialVersionUID = 1017339529132704658L;

	private String authenticationEntityId;
	private String authenticationUserId;
	private String authenticationPassword;
	private String token;
	private PayonTransactionBean payonBean;
	
	public String getAuthenticationEntityId() {
		return authenticationEntityId;
	}
	public void setAuthenticationEntityId(String authenticationEntityId) {
		this.authenticationEntityId = authenticationEntityId;
	}
	public String getAuthenticationUserId() {
		return authenticationUserId;
	}
	public void setAuthenticationUserId(String authenticationUserId) {
		this.authenticationUserId = authenticationUserId;
	}
	public String getAuthenticationPassword() {
		return authenticationPassword;
	}
	public void setAuthenticationPassword(String authenticationPassword) {
		this.authenticationPassword = authenticationPassword;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public PayonTransactionBean getPayonBean() {
		return payonBean;
	}
	public void setPayonBean(PayonTransactionBean payonBean) {
		this.payonBean = payonBean;
	}
	
}
