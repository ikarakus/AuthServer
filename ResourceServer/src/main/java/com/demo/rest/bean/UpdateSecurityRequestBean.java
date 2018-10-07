package com.demo.rest.bean;

public class UpdateSecurityRequestBean extends SwitchRequestBean {

	private static final long serialVersionUID = -5015239801839505820L;
	
	private String customerId;
	private String newPassword;
	private Integer firstSecurityQuestionId;
	private String firstSecurityQuestionAnswer;
	private Integer secondSecurityQuestionId;
	private String secondSecurityQuestionAnswer;

	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public String getFirstSecurityQuestionAnswer() {
		return firstSecurityQuestionAnswer;
	}
	public void setFirstSecurityQuestionAnswer(String firstSecurityQuestionAnswer) {
		this.firstSecurityQuestionAnswer = firstSecurityQuestionAnswer;
	}
	public String getSecondSecurityQuestionAnswer() {
		return secondSecurityQuestionAnswer;
	}
	public void setSecondSecurityQuestionAnswer(String secondSecurityQuestionAnswer) {
		this.secondSecurityQuestionAnswer = secondSecurityQuestionAnswer;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public Integer getFirstSecurityQuestionId() {
		return firstSecurityQuestionId;
	}
	public void setFirstSecurityQuestionId(Integer firstSecurityQuestionId) {
		this.firstSecurityQuestionId = firstSecurityQuestionId;
	}
	public Integer getSecondSecurityQuestionId() {
		return secondSecurityQuestionId;
	}
	public void setSecondSecurityQuestionId(Integer secondSecurityQuestionId) {
		this.secondSecurityQuestionId = secondSecurityQuestionId;
	}


}
