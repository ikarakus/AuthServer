package com.demo.rest.bean;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.demo.common.domain.transaction.request.merchant.MerchantRequest;

/**
 * @author LinesCode
 *
 */
public class GetTransferResultRequestBean extends MerchantRequestBean<MerchantRequest> {

	@NotNull
	@Size(min = 1, max = 12)
	private String merchantCode;

	@NotNull
	@Size(min = 1, max = 255)
	private String merchantTransactionId;

	public String getMerchantCode() {
		return merchantCode;
	}

	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}

	public String getMerchantTransactionId() {
		return merchantTransactionId;
	}

	public void setMerchantTransactionId(String merchantTransactionId) {
		this.merchantTransactionId = merchantTransactionId;
	}
}
