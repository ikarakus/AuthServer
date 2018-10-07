package com.demo.rest.bean;

public class ResultCodes {

	public static final int OK = 0;

	// 100 message errors
	public static final int MANDATORY_FIELD = 101;
	public static final int INVALID_VALUE_LENGHT = 102;
	public static final int BAD_SIGNATURE = 103;
	public static final int MANDATORY_CUSTOMER_NAME = 104;

	// 200 application errors
	public static final int CUSTOMER_NOT_FOUND = 201;
	public static final int CUSTOMER_BAD_CREDENTIALS = 202;
	public static final int MERCHANT_NOT_FOUND = 203;
	public static final int BLOCKED_CUSTOMER = 204;
	public static final int BLOCKED_CUSTOMER_ACCOUNT = 205;
	public static final int CUSTOMER_NOT_ABLE_TO_RECEIVE_OR_SEND_FUNDS = 206;
	public static final int TOKEN_NOT_FOUND = 207;
	public static final int EXPIRED_TOKEN = 208;
	public static final int WRONG_OTP = 209;
	public static final int MERCHANT_NOT_ENABLED = 210;
	public static final int MERCHANT_NOT_ABLE_TO_RECEIVE_OR_SEND_FUNDS = 211;
	public static final int CUSTOMER_INSUFFICIENT_FUNDS = 212;
	public static final int MERCHANT_INSUFFICIENT_FUNDS = 213;
	public static final int DUPLICATE_MERCHANT_TRANSACTION_ID = 214;
	public static final int WRONG_CUSTOMER_NAME = 215;
	public static final int PAYMENT_ALREADY_PROCESSED = 216;
	public static final int TRANSFER_NOT_FOUND = 217;
	public static final int PENDING_PAYMENT = 218;
	public static final int MERCHANT_NOT_CURRENCY = 219;
	public static final int CUSTOMER_NOT_CURRENCY = 220;
	
	// 300 rest errors
	public static final int DATABASE_EXCEPTION = 301;
	public static final int NOT_FOUND_EXCEPTION = 302;
	public static final int NUMBER_FORMAT_EXCEPTION = 303;
	public static final int INVALID_PARAMETER_EXCEPTION = 304;
	public static final int EWALLET_NOT_FOUND = 305;
	public static final int PASSWORD_STRENGTH_EXCEPTION = 306;
	
	public static final int UNEXPECTED_ERROR = 999;

}
