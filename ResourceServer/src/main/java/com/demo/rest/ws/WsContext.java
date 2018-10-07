package com.demo.rest.ws;

import javax.servlet.http.HttpServletRequest;

import com.demo.common.domain.customer.MerchantProgramme;

public class WsContext {

	private HttpServletRequest httpServletRequest;

	private String customerUserName;

	private MerchantProgramme merchantProgramme;

	public HttpServletRequest getHttpServletRequest() {
		return httpServletRequest;
	}

	public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
		this.httpServletRequest = httpServletRequest;
	}

	public String getCustomerUserName() {
		return customerUserName;
	}

	public void setCustomerUserName(String customerUserName) {
		this.customerUserName = customerUserName;
	}

	public MerchantProgramme getMerchantProgramme() {
		return merchantProgramme;
	}

	public void setMerchantProgramme(MerchantProgramme merchantProgramme) {
		this.merchantProgramme = merchantProgramme;
	}
}
