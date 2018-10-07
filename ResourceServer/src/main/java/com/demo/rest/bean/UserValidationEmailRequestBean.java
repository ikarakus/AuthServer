package com.demo.rest.bean;

import com.revoframework.domain.common.MailBox;
import com.revoframework.domain.common.User;

public class UserValidationEmailRequestBean extends SwitchRequestBean {

	private static final long serialVersionUID = 7248277020360728856L;

	private User user; 
	private MailBox mailBox;
	private String validationTokenId;
	private String name;
	private String token;
	private String baseUrl;
	
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public MailBox getMailBox() {
		return mailBox;
	}
	public void setMailBox(MailBox mailBox) {
		this.mailBox = mailBox;
	}
	public String getValidationTokenId() {
		return validationTokenId;
	}
	public void setValidationTokenId(String validationTokenId) {
		this.validationTokenId = validationTokenId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getBaseUrl() {
		return baseUrl;
	}
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	

}
