package com.demo.rest.bean;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.google.gson.annotations.Expose;

/**
 * @author LinesCode
 *
 */
public class ConfirmPaymentRequestBean extends MerchantRequestBean {

	@Expose
	@NotNull
	@Size(min = 32, max = 64)
	private String token;

	@Expose
	@NotNull
	@Size(min = 6, max = 6)
	private String otp;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

}
