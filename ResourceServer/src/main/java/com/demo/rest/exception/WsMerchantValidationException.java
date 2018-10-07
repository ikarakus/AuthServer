package com.demo.rest.exception;

public class WsMerchantValidationException extends Exception {

	private static final long serialVersionUID = 5846153997264410493L;

	private int resultCode;
	
	public WsMerchantValidationException() {		
	}

	public WsMerchantValidationException(String message) {
		super(message);
	}
	
	public WsMerchantValidationException(int resultCode) {	
		this.resultCode = resultCode;
	}
	
	public WsMerchantValidationException(int resultCode, String errorMessage) {
		super(errorMessage);
		this.resultCode = resultCode;
	}

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
}
