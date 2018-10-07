package com.demo.rest.bean;

import com.demo.common.domain.account.RegisteredCard;

public class PostRegisterNewLoadCardResponseBean extends SwitchResponseBean {

	private static final long serialVersionUID = 6369308858598097719L;

	private String authenticationEntityId;
	private String authenticationUserId;
	private String authenticationPassword;
	private RegisteredCard registeredCard;

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
	public RegisteredCard getRegisteredCard() {
		return registeredCard;
	}
	public void setRegisteredCard(RegisteredCard registeredCard) {
		this.registeredCard = registeredCard;
	}

}
