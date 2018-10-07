package com.demo.rest.util;

import com.demo.common.exception.InvalidParameterException;
import com.revoframework.util.StringUtil;

public final class ServiceUtil {
	
	public static String convertStringToJsonString(String label, String value) {
		return "{\"" + label  + "\":\"" + value.replace("\"", "'") + "\"}";
	}	
	
	public static void checkStringParameter(String name, String value) throws InvalidParameterException {
		if (StringUtil.isEmpty(value))
			throw new InvalidParameterException(name + " can not be empty");
	}
	
}
