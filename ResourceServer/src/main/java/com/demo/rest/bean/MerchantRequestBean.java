package com.demo.rest.bean;

import java.lang.reflect.InvocationTargetException;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.beanutils.BeanUtils;

import com.google.gson.annotations.Expose;
import com.demo.common.domain.transaction.request.merchant.MerchantRequest;
import com.revoframework.module.cms.ApplicationContext;

/**
 * @author LinesCode
 *
 */
public abstract class MerchantRequestBean<T extends MerchantRequest> {

	@Expose
	@NotNull
	@Size(min = 64, max = 64)
	private String signature;

	public T buildEntity(Class<T> clazz) {

		try {

			T entity = clazz.newInstance();
			entity.setCid(ApplicationContext.getApplication().getCid());
			entity.setStatus(MerchantRequest.STATUS_PENDING);
			return entity;

		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Something happened");
		}
	}

	public void copyProperties(T entity) {

		try {
			BeanUtils.copyProperties(entity, this);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException("Something happened");
		}
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

}
