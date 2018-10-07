package com.demo.rest.bean;

public class SavePayonLoadRequestBean extends SwitchRequestBean {

	private static final long serialVersionUID = -6727235501218630817L;
	
	private String amount;
	private String token;
	private String loadBy;

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getLoadBy() {
		return loadBy;
	}

	public void setLoadBy(String loadBy) {
		this.loadBy = loadBy;
	}

}
