package com.demo.rest.util;

import org.apache.log4j.Logger;

import com.demo.common.domain.customer.SecurityAnswer;
import com.demo.common.domain.customer.SecurityQuestion;
import com.demo.common.exception.EwalletNotFoundException;
import com.demo.common.exception.InvalidParameterException;
import com.demo.rest.bean.ResultCodes;
import com.demo.rest.bean.SwitchResponseBean;
import com.revoframework.exception.DatabaseException;
import com.revoframework.exception.NotFoundException;
import com.revoframework.exception.PasswordStrengthException;

public class SwitchHelper {

	private static Logger logger = Logger.getLogger(SwitchHelper.class);
	
	public static SecurityAnswer createSecurityAnswer(Integer questionId, String answer) {
		SecurityAnswer securityAnswer = new SecurityAnswer();
		securityAnswer.setAnswer(answer);
		securityAnswer.setSecurityQuestion(new SecurityQuestion());
		securityAnswer.getSecurityQuestion().setId(questionId);
		return securityAnswer;
	}
	
	public static void fillResponse(SwitchResponseBean<?> response,int resultCode, String errorMessage) {

		response.setResultCode(resultCode);
		response.setErrorMessage(errorMessage);
	}
	
	public static void handleException(SwitchResponseBean<?> response,Exception e) {
		
		try {
			
			if (e instanceof EwalletNotFoundException) {
				EwalletNotFoundException ex = (EwalletNotFoundException) e;		
				fillResponse(response, ResultCodes.EWALLET_NOT_FOUND, ex.getMessage());
			} else if (e instanceof NumberFormatException) {
				NumberFormatException ex = (NumberFormatException) e;		
				fillResponse(response, ResultCodes.NUMBER_FORMAT_EXCEPTION, ex.getMessage());
			} else if (e instanceof DatabaseException) {
				DatabaseException ex = (DatabaseException) e;		
				fillResponse(response, ResultCodes.DATABASE_EXCEPTION, ex.getMessage());
			} else if (e instanceof NotFoundException) {
				NotFoundException ex = (NotFoundException) e;		
				fillResponse(response, ResultCodes.NOT_FOUND_EXCEPTION, ex.getMessage());
			} else if (e instanceof InvalidParameterException) {
				InvalidParameterException ex = (InvalidParameterException) e;		
				fillResponse(response, ResultCodes.INVALID_PARAMETER_EXCEPTION, ex.getMessage());
			} else if (e instanceof PasswordStrengthException) {
				PasswordStrengthException ex = (PasswordStrengthException) e;		
				fillResponse(response, ResultCodes.PASSWORD_STRENGTH_EXCEPTION, ex.getMessage());
			} 
			
			else {
				logger.error(e, e);
			}
			
		} catch (Exception e1) {
			logger.error(e, e);
		}
	}

}
