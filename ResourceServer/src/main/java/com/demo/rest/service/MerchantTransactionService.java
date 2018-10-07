package com.demo.rest.service;

import com.demo.common.domain.customer.Consumer;
import com.demo.common.domain.transaction.request.merchant.MerchantRequestHistory;
import com.demo.common.domain.transaction.request.merchant.TransferFundsToCustomerRequest;
import com.demo.common.domain.transaction.request.merchant.TransferFundsToMerchantRequest;
import com.demo.common.exception.AccountNotGoodException;
import com.demo.common.exception.AvailableBalanceException;
import com.demo.common.exception.EwalletCurrencyNotFoundException;
import com.demo.common.exception.EwalletNotFoundBeneficiaryException;
import com.demo.common.exception.EwalletNotFoundException;
import com.demo.common.exception.ExceededLimitException;
import com.demo.rest.bean.ConfirmPaymentRequestBean;
import com.demo.rest.bean.GetTransferResultRequestBean;
import com.demo.rest.bean.GetTransferResultResponseBean;
import com.demo.rest.bean.TransferFundsToCustomerRequestBean;
import com.demo.rest.bean.TransferFundsToMerchantAuthRequestBean;
import com.demo.rest.exception.WsMerchantValidationException;
import com.revoframework.exception.DatabaseException;
import com.revoframework.exception.EmailException;
import com.revoframework.exception.NotFoundException;
import com.revoframework.exception.SmsException;

public interface MerchantTransactionService {

	public String transferFundsToMerchantAuth(TransferFundsToMerchantAuthRequestBean bean, TransferFundsToMerchantRequest merchantRequest, MerchantRequestHistory merchantHistory) throws WsMerchantValidationException, DatabaseException, SmsException, EwalletNotFoundException, AccountNotGoodException, EwalletNotFoundBeneficiaryException, EwalletCurrencyNotFoundException;

	public void confirmPayment(ConfirmPaymentRequestBean bean, TransferFundsToMerchantRequest merchantRequest, MerchantRequestHistory merchantHistory) throws WsMerchantValidationException, DatabaseException, NotFoundException, EwalletNotFoundException, ExceededLimitException, AvailableBalanceException, AccountNotGoodException, EmailException, EwalletCurrencyNotFoundException;

	public void transferFundsToCustomer(TransferFundsToCustomerRequestBean bean, TransferFundsToCustomerRequest merchantRequest, MerchantRequestHistory merchantHistory) throws WsMerchantValidationException, DatabaseException, NotFoundException, EwalletNotFoundException, ExceededLimitException, AvailableBalanceException, AccountNotGoodException, EmailException, EwalletNotFoundBeneficiaryException, EwalletCurrencyNotFoundException;

	public Consumer findMerchantCustomer(String email) throws DatabaseException;
	
	public void getTransferResult(GetTransferResultRequestBean requestBean, GetTransferResultResponseBean responseBean, MerchantRequestHistory merchantHistory) throws WsMerchantValidationException, DatabaseException, NotFoundException;
}
