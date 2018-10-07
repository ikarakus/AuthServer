package com.demo.rest.service;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedList;

import javax.validation.constraints.NotNull;

import org.apache.commons.codec.digest.DigestUtils;

import com.demo.rest.bean.MerchantRequestBean;
import com.demo.rest.bean.ResultCodes;
import com.demo.rest.bean.TransferFundsToCustomerRequestBean;
import com.demo.rest.bean.TransferFundsToMerchantAuthRequestBean;
import com.demo.rest.exception.WsMerchantValidationException;
import com.revoframework.exception.FormFieldValidationException;
import com.revoframework.util.DynamicFieldUtils;
import com.revoframework.util.StringUtil;
import com.revoframework.validator.EntityValidator;

public class MerchantTransactionValidator {

	public static void validate(MerchantRequestBean bean) throws WsMerchantValidationException {

		Field[] fields = DynamicFieldUtils.getClassFieldForBean(bean.getClass());

		validateNulls(fields, bean);

		for (Field field : fields) {

			try {

				Object value = DynamicFieldUtils.getValue(bean, field.getName());
				EntityValidator.validateField(field, value);

			} catch (FormFieldValidationException e) {
				throw new WsMerchantValidationException(ResultCodes.INVALID_VALUE_LENGHT, String.valueOf(e.getPropertyKey() + " " + e.getMessage().toLowerCase()));
			}
		}

		if (bean instanceof TransferFundsToCustomerRequestBean)
			validateTransferFundsToCustomerRequestBean((TransferFundsToCustomerRequestBean) bean);
		else if (bean instanceof TransferFundsToMerchantAuthRequestBean)
			validateTransferFundsToMerchantAuthRequestBean((TransferFundsToMerchantAuthRequestBean) bean);
	}

	private static void validateTransferFundsToCustomerRequestBean(TransferFundsToCustomerRequestBean bean) throws WsMerchantValidationException {

		if (bean.getCheckCustomerName() != null && bean.getCheckCustomerName()) {

			if (StringUtil.isEmpty(bean.getCustomerName()))
				throw new WsMerchantValidationException(ResultCodes.MANDATORY_CUSTOMER_NAME);

			try {
				EntityValidator.validateField("customerName", bean.getCustomerName(), TransferFundsToCustomerRequestBean.class);
			} catch (FormFieldValidationException e) {
				throw new WsMerchantValidationException(ResultCodes.INVALID_VALUE_LENGHT, String.valueOf(e.getPropertyKey() + " " + e.getMessage().toLowerCase()));
			}
		}
	}

	private static void validateTransferFundsToMerchantAuthRequestBean(TransferFundsToMerchantAuthRequestBean bean) throws WsMerchantValidationException {

		if (bean.getCheckCustomerName() != null && bean.getCheckCustomerName()) {

			if (StringUtil.isEmpty(bean.getCustomerName()))
				throw new WsMerchantValidationException(ResultCodes.MANDATORY_CUSTOMER_NAME);

			try {
				EntityValidator.validateField("customerName", bean.getCustomerName(), TransferFundsToMerchantAuthRequestBean.class);
			} catch (FormFieldValidationException e) {
				throw new WsMerchantValidationException(ResultCodes.INVALID_VALUE_LENGHT, String.valueOf(e.getPropertyKey() + " " + e.getMessage().toLowerCase()));
			}
		}
	}

	public static void validateSignature(MerchantRequestBean bean, String pin) throws WsMerchantValidationException {

		Field[] fields = DynamicFieldUtils.getClassFieldForBean(bean.getClass());

		LinkedList<String> names = new LinkedList<String>();

		for (Field field : fields) {
			if (field.getName().equals("signature"))
				continue;

			names.add(field.getName());
		}

		Collections.sort(names);

		StringBuilder sb = new StringBuilder();

		for (String string : names) {

			Object value = DynamicFieldUtils.getValue(bean, string);

			if (value == null)
				continue;

			sb.append(string);
			sb.append(DynamicFieldUtils.getValue(bean, string));
		}

		sb.append(pin);
		
		String digest = DigestUtils.sha256Hex(sb.toString());

		if (!bean.getSignature().equals(digest))
			throw new WsMerchantValidationException(ResultCodes.BAD_SIGNATURE);
	}

	private static void validateNulls(Field[] fields, MerchantRequestBean bean) throws WsMerchantValidationException {

		for (Field field : fields) {

			NotNull ann = field.getAnnotation(NotNull.class);

			if (ann == null)
				continue;

			if (DynamicFieldUtils.getValue(bean, field.getName()) == null)
				throw new WsMerchantValidationException(ResultCodes.MANDATORY_FIELD, field.getName());
		}
	}
}
