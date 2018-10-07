package com.demo.rest.service;

import static com.demo.common.domain.transaction.request.merchant.MerchantRequest.STATUS_PENDING;
import static com.demo.common.domain.transaction.request.merchant.MerchantRequest.STATUS_PROCESSED;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.common.DemoCommonMessages;
import com.demo.common.domain.SmsType;
import com.demo.common.domain.account.Account;
import com.demo.common.domain.account.Ewallet;
import com.demo.common.domain.account.LoadUnloadChannel;
import com.demo.common.domain.account.fee.MerchantFeeByRange;
import com.demo.common.domain.account.fee.MerchantFeeByVolume;
import com.demo.common.domain.account.fee.MerchantFeeByVolumeType;
import com.demo.common.domain.account.fee.MerchantFeeMapped;
import com.demo.common.domain.customer.Consumer;
import com.demo.common.domain.customer.Customer;
import com.demo.common.domain.customer.MerchantProgramme;
import com.demo.common.domain.customer.wl.CoBrander;
import com.demo.common.domain.transaction.Transaction;
import com.demo.common.domain.transaction.TransactionMerchantDetails;
import com.demo.common.domain.transaction.TransactionStatus;
import com.demo.common.domain.transaction.TransactionSubType;
import com.demo.common.domain.transaction.TransactionType;
import com.demo.common.domain.transaction.request.merchant.MerchantRequest;
import com.demo.common.domain.transaction.request.merchant.MerchantRequestChannel;
import com.demo.common.domain.transaction.request.merchant.MerchantRequestHistory;
import com.demo.common.domain.transaction.request.merchant.TransferFundsToCustomerRequest;
import com.demo.common.domain.transaction.request.merchant.TransferFundsToMerchantRequest;
import com.demo.common.exception.AccountNotGoodException;
import com.demo.common.exception.AucValidationException;
import com.demo.common.exception.AvailableBalanceException;
import com.demo.common.exception.CardNotFoundException;
import com.demo.common.exception.EwalletCurrencyNotFoundException;
import com.demo.common.exception.EwalletNotFoundBeneficiaryException;
import com.demo.common.exception.EwalletNotFoundException;
import com.demo.common.exception.ExceededLimitException;
import com.demo.common.service.AccountService;
import com.demo.common.service.CustomerService;
import com.demo.common.service.ewallet.EwalletCurrencyService;
import com.demo.common.service.ewallet.EwalletService;
import com.demo.rest.bean.ConfirmPaymentRequestBean;
import com.demo.rest.bean.GetTransferResultRequestBean;
import com.demo.rest.bean.GetTransferResultResponseBean;
import com.demo.rest.bean.ResultCodes;
import com.demo.rest.bean.TransferFundsToCustomerRequestBean;
import com.demo.rest.bean.TransferFundsToMerchantAuthRequestBean;
import com.demo.rest.exception.WsMerchantValidationException;
import com.demo.rest.ws.WsContextHolder;
import com.revoframework.bean.Message;
import com.revoframework.domain.common.User;
import com.revoframework.domain.common.firewall.Rule;
import com.revoframework.domain.common.system.SystemConfig;
import com.revoframework.exception.DatabaseException;
import com.revoframework.exception.EmailException;
import com.revoframework.exception.NotFoundException;
import com.revoframework.exception.SmsException;
import com.revoframework.module.cms.ApplicationContext;
import com.revoframework.module.firewall.FirewallManager;
import com.revoframework.service.RevolutionModel;
import com.revoframework.util.CryptoLibrary;
import com.revoframework.util.NewDateUtils;

@Service("merchantTransactionServiceImpl")
public class MerchantTransactionServiceImpl extends AccountService implements MerchantTransactionService {
	
	@Autowired
	private EwalletService ewalletService;
	
	@Autowired
	private EwalletCurrencyService ewalletCurrencyService;
	
	@Autowired
	private CustomerService customerService;
	
	private Logger logger = Logger.getLogger(MerchantTransactionServiceImpl.class);

	@Override
	public String transferFundsToMerchantAuth(TransferFundsToMerchantAuthRequestBean bean, TransferFundsToMerchantRequest merchantRequest, MerchantRequestHistory merchantHistory)
			throws WsMerchantValidationException, DatabaseException, SmsException, EwalletNotFoundException, AccountNotGoodException, EwalletNotFoundBeneficiaryException, EwalletCurrencyNotFoundException {
		
		bean.copyProperties(merchantRequest);

		MerchantTransactionValidator.validate(bean);

		MerchantProgramme merchant = findMerchantProgramme(bean.getMerchantCode());
		if (merchant == null)
			throw new WsMerchantValidationException(ResultCodes.MERCHANT_NOT_FOUND);
		
		merchantRequest.setMerchant(merchant);

		WsContextHolder.getContext().setMerchantProgramme(merchant);

		MerchantTransactionValidator.validateSignature(bean, merchant.getClearPin());

		ewalletCurrencyService.hasEwallet(merchant.getCustomer(), bean.getCurrency());
		Ewallet merchantEwallet = ewalletService.findMyEwallet(merchant.getCustomer(), bean.getCurrency());
		checkAccountStatus(merchantEwallet);

		Consumer consumer = findMerchantCustomer(bean.getCustomerUserName());

		if (consumer == null)
			throw new WsMerchantValidationException(ResultCodes.CUSTOMER_NOT_FOUND);
		
		if (bean.getCheckCustomerName() != null && bean.getCheckCustomerName())
			checkCustomerName(consumer, bean.getCustomerName());
		
		WsContextHolder.getContext().setCustomerUserName(bean.getCustomerUserName());

		if (FirewallManager.getInstance().checkRequest(WsContextHolder.getContext().getHttpServletRequest()).equals(Rule.DENY))
			throw new WsMerchantValidationException(ResultCodes.BLOCKED_CUSTOMER);

		if (!CryptoLibrary.getInstance().checkPassword(consumer.getUser().getPassword(), bean.getCustomerPassword()))
			throw new WsMerchantValidationException(ResultCodes.CUSTOMER_BAD_CREDENTIALS);

		ewalletCurrencyService.hasEwallet(consumer, bean.getCurrency());
		Ewallet customerEwallet = ewalletService.findBeneficiaryEwallet(consumer, bean.getCurrency());
		checkAccountStatus(customerEwallet);

		String token = UUID.randomUUID().toString();
		merchantRequest.setToken(token);
		
		String otp;
		if (RevolutionModel.getInstance().getSystemConfig(SystemConfig.PRODUCTION_MODE).getValueAsBool()) {
			
			customerService.sendAucViaSMS(consumer, SmsType.WS_AUC_TRANSFER_TO_MERCHANT);
			otp = consumer.getAuc();
			
		} else {
			otp = "262309";
		}
		
		merchantRequest.setOtp(otp);

		merchantRequest.setAccount(customerEwallet);
		merchantRequest.setResultCode(ResultCodes.OK);
		merchantRequest.setStatus(STATUS_PENDING);
		setStatusAndResultCodeToHistory(merchantHistory, merchantRequest.getStatus(), merchantRequest.getResultCode());
		return token;
	}

	@Override
	public void confirmPayment(ConfirmPaymentRequestBean bean, TransferFundsToMerchantRequest merchantRequest, MerchantRequestHistory merchantHistory) throws WsMerchantValidationException, DatabaseException, NotFoundException, EwalletNotFoundException,
			ExceededLimitException, AvailableBalanceException, AccountNotGoodException, EmailException, EwalletCurrencyNotFoundException {

//		MerchantTransactionValidator.validate(bean);

		MerchantProgramme merchant = merchantRequest.getMerchant();
		WsContextHolder.getContext().setMerchantProgramme(merchant);
		
		MerchantTransactionValidator.validateSignature(bean, merchant.getClearPin());
		

		if(!getCustomerStatus(merchantRequest)){
			if (!merchantRequest.getOtp().equals(bean.getOtp()))
			{
				throw new WsMerchantValidationException(ResultCodes.WRONG_OTP);
			}
		}
		else{
				
			try {
					Account customerAccount = merchantRequest.getAccount();
					Customer customer = customerAccount.getCustomer();
					customerService.verifyAucTOTP(bean.getOtp(), customer.getUser());
					
				} catch (AucValidationException e) {
					throw new WsMerchantValidationException(ResultCodes.WRONG_OTP);
				}
			
			}
			

		transferFunds(merchantRequest);

		merchantRequest.setStatus(STATUS_PROCESSED);
		setStatusAndResultCodeToHistory(merchantHistory, merchantRequest.getStatus(), merchantRequest.getResultCode());
	}

	private boolean getCustomerStatus(TransferFundsToMerchantRequest merchantRequest) {
		 
		Account customerAccount = merchantRequest.getAccount();
		Customer customer = customerAccount.getCustomer();
		User user = customer.getUser();
		
		boolean isTrue=false;
		
		if(user.getTotpEnabled()){
			isTrue=user.getTotpActivated();	
		}
		
		return isTrue;
	}

	@Override
	public void transferFundsToCustomer(TransferFundsToCustomerRequestBean bean, TransferFundsToCustomerRequest merchantRequest, MerchantRequestHistory merchantHistory)
			throws WsMerchantValidationException, DatabaseException, NotFoundException, EwalletNotFoundException, ExceededLimitException, AvailableBalanceException,
			AccountNotGoodException, EmailException, EwalletNotFoundBeneficiaryException, EwalletCurrencyNotFoundException {

		bean.copyProperties(merchantRequest);

		MerchantTransactionValidator.validate(bean);

		MerchantProgramme merchant = findMerchantProgramme(bean.getMerchantCode());
		if (merchant == null)
			throw new WsMerchantValidationException(ResultCodes.MERCHANT_NOT_FOUND);

		merchantRequest.setMerchant(merchant);

		WsContextHolder.getContext().setMerchantProgramme(merchant);

		MerchantTransactionValidator.validateSignature(bean, merchant.getClearPin());

		Consumer consumer = findMerchantCustomer(bean.getCustomerUserName());

		if (consumer == null)
			throw new WsMerchantValidationException(ResultCodes.CUSTOMER_NOT_FOUND);

		if (bean.getCheckCustomerName() != null && bean.getCheckCustomerName())
			checkCustomerName(consumer, bean.getCustomerName());
		
		ewalletCurrencyService.hasEwallet(consumer, bean.getCurrency());
		Ewallet customerEwallet = ewalletService.findBeneficiaryEwallet(consumer, bean.getCurrency());
		merchantRequest.setAccount(customerEwallet);
		
		transferFunds(merchantRequest);

		merchantRequest.setResultCode(ResultCodes.OK);
		merchantRequest.setStatus(STATUS_PROCESSED);
		setStatusAndResultCodeToHistory(merchantHistory, merchantRequest.getStatus(), merchantRequest.getResultCode());
	}

	@Override
	public void getTransferResult(GetTransferResultRequestBean requestBean, GetTransferResultResponseBean responseBean, MerchantRequestHistory merchantHistory) throws WsMerchantValidationException, DatabaseException, NotFoundException {
		
		MerchantTransactionValidator.validate(requestBean);

		MerchantProgramme merchant = findMerchantProgramme(requestBean.getMerchantCode());
		if (merchant == null)
			throw new WsMerchantValidationException(ResultCodes.MERCHANT_NOT_FOUND);
		
		WsContextHolder.getContext().setMerchantProgramme(merchant);
		
		MerchantTransactionValidator.validateSignature(requestBean, merchant.getClearPin());

		Criterion[] requestCriterions = {
			Restrictions.eq("merchantCode", requestBean.getMerchantCode()),
			Restrictions.eq("merchantTransactionId", requestBean.getMerchantTransactionId())
		};

		if (!genericService.exist(MerchantRequest.class, requestCriterions))
			throw new WsMerchantValidationException(ResultCodes.TRANSFER_NOT_FOUND);
		
		MerchantRequest request = genericService.findFirst(MerchantRequest.class, requestCriterions);
		merchantHistory.setMerchantRequest(request);
		
		System.out.println("id: " + request.getId());
		System.out.println("status: " + request.getStatus());

		if (request.getStatus().equals(STATUS_PENDING))
			responseBean.setTransferResultCode(ResultCodes.PENDING_PAYMENT);
		else
			responseBean.setTransferResultCode(request.getResultCode());
		
		setStatusAndResultCodeToHistory(merchantHistory, request.getStatus(), responseBean.getTransferResultCode());
		
		System.out.println("Result code: " + responseBean.getResultCode());
	}

	private void transferFunds(MerchantRequest merchantRequest) throws DatabaseException, NotFoundException, EwalletNotFoundException, ExceededLimitException,
			AvailableBalanceException, AccountNotGoodException, WsMerchantValidationException, EwalletCurrencyNotFoundException {

		String merchantTransactionId = merchantRequest.getMerchantTransactionId();
		MerchantProgramme merchant = merchantRequest.getMerchant();

		if (genericService.exist(TransactionMerchantDetails.class, new String [] {"programme", "merchantTransactionId"}, new Object[] {merchant, merchantTransactionId}))
			throw new WsMerchantValidationException(ResultCodes.DUPLICATE_MERCHANT_TRANSACTION_ID);
		
		String currency = merchantRequest.getCurrency();
		
		ewalletCurrencyService.hasEwallet(merchantRequest.getMerchant().getCustomer(), merchantRequest.getCurrency());
		Ewallet merchantEwallet = ewalletService.findMyEwallet(merchantRequest.getMerchant().getCustomer(), merchantRequest.getCurrency());
		Account customerEwallet = merchantRequest.getAccount();
		BigDecimal amount = merchantRequest.getAmount();

		checkAccountStatus(merchantEwallet);
		checkAccountStatus(customerEwallet);

		MerchantRequestChannel requestChannel = genericService.findFirst(MerchantRequestChannel.class, "merchantRequestType", merchantRequest.getRequestType());

		LoadUnloadChannel merchantChannel = requestChannel.getMerchantChannel();
		LoadUnloadChannel customerChannel = requestChannel.getCustomerChannel();

		// cerco le due fee usando l'ewallet merchant, le fee sono configurate solo sul merchant
		MerchantFeeMapped merchantFee = findMerchantFee(merchantEwallet, merchantChannel, amount);
		MerchantFeeMapped customerFee = findMerchantFee(merchantEwallet, customerChannel, amount);

		if (merchantFee instanceof MerchantFeeByVolume)
			merchantFee = findExactVolumeFee(merchantEwallet, merchantChannel, (MerchantFeeByVolume) merchantFee);
		
		if (customerFee instanceof MerchantFeeByVolume)
			customerFee = findExactVolumeFee(merchantEwallet, merchantChannel, (MerchantFeeByVolume) customerFee);
		
		BigDecimal merchantFeeAmount = calculateMerchantFee(merchantFee, amount);
		BigDecimal customerFeeAmount = calculateMerchantFee(customerFee, amount);

		TransactionType merchantType = null;
		TransactionType customerType = null;

		String merchantDescription = null;
		String customerDescription = null;

		String merchantLanguage = merchant.getCustomer().getContactPerson().getUser().getLanguage().getCode();
		String customerLanguage = customerEwallet.getCustomer().getUser().getLanguage().getCode();

		if (merchantChannel.getType().equals(TransactionSubType.TYPE_UNLOAD)) {
			checkAvailableFunds(amount.add(merchantFeeAmount), merchantEwallet);

			merchantType = new TransactionType();
			merchantType.setCode(TransactionType.U);

			customerType = new TransactionType();
			customerType.setCode(TransactionType.L);

			merchantDescription = Message.getInstance().getLocalizedMessage(DemoCommonMessages.PMX_TRANSACTION_paymenToCustomer, merchantLanguage);
			customerDescription = Message.getInstance().getLocalizedMessage(DemoCommonMessages.PMX_TRANSACTION_paymenFromMerchant, customerLanguage);

		} else {
			// se non è quello del merchant è quello del consumer
			checkAvailableFunds(amount.add(customerFeeAmount), customerEwallet);

			merchantType = new TransactionType();
			merchantType.setCode(TransactionType.L);

			customerType = new TransactionType();
			customerType.setCode(TransactionType.U);

			merchantDescription = Message.getInstance().getLocalizedMessage(DemoCommonMessages.PMX_TRANSACTION_paymenFromCustomer, merchantLanguage);
			customerDescription = Message.getInstance().getLocalizedMessage(DemoCommonMessages.PMX_TRANSACTION_paymenToMerchant, customerLanguage);
		}

		checkLimitsFull(merchantEwallet, merchantChannel, amount);
		checkLimitsFull(customerEwallet, customerChannel, amount);

		TransactionStatus status = new TransactionStatus();
		status.setCode(TransactionStatus.SETTLED);

		String description = merchantRequest.getDescription();

		try {
			
			{
				StringBuilder sb = new StringBuilder();
				sb.append(merchantDescription)
				.append(" ")
				.append(customerEwallet.getCustomer().getToString())
				.append("<br>")
				.append(Message.getInstance().getLocalizedMessage(DemoCommonMessages.PMX_TRANSACTION_descriptionFragment, merchantLanguage))
				.append(" ")
				.append(merchantRequest.getDescription());

				String descriptionI18n = sb.toString();
				
				Transaction transaction = transactionService.createAndSaveTransactionFroMrc(merchantEwallet, customerEwallet, customerEwallet.getCustomer().getToString(), amount,
						amount, description, descriptionI18n, currency, currency, status, merchantType, merchantChannel);

				merchantRequest.setTransaction(transaction);
				
				if (merchantFee != null) {

					if (merchantFee.getReportingOnly())
						saveTransactionMerchantDetails(merchant, merchantFee, amount, merchantRequest.getMerchantCustomerId(), merchantRequest.getMerchantTransactionId(), transaction);
					else {

						// per ora gli passo l'overrideAmount, questo metodo non supporta la transaction fee
						// TODO rimuovere quando applyFee funzionerà a dovere
						// viton 30/07/2016
						transactionService.applyFeeMrc(merchantEwallet, null, merchantFee, merchantFeeAmount, transaction);
						saveTransactionMerchantDetails(merchant, null, null, merchantRequest.getMerchantCustomerId(), merchantRequest.getMerchantTransactionId(), transaction);
					}
					
				} else {
					saveTransactionMerchantDetails(merchant, null, null, merchantRequest.getMerchantCustomerId(), merchantRequest.getMerchantTransactionId(), transaction);
				}
			}

			{
				StringBuilder sb = new StringBuilder();
				sb.append(customerDescription)
				.append(" ")
				.append(merchant.getName())
				.append("<br>")
				.append(Message.getInstance().getLocalizedMessage(DemoCommonMessages.PMX_TRANSACTION_descriptionFragment, customerLanguage))
				.append(" ")
				.append(merchantRequest.getDescription());

				String descriptionI18n = sb.toString();

				Transaction transaction = transactionService.createAndSaveTransactionFroMrc(customerEwallet, merchantEwallet, merchant.getName(), amount, amount, description,
						descriptionI18n, currency, currency, status, customerType, customerChannel);

				// TODO come sopra
				transactionService.applyFeeMrc(customerEwallet, null, customerFee, customerFeeAmount, transaction);
			}
			
		} catch (CardNotFoundException e) {
			// impossibile solo una grossa anomalia la potrebbe causare, viton 22/07/2016
			logger.error("Should not happen", e);
			throw new RuntimeException(e);
		}
	}
	
	private void saveTransactionMerchantDetails(MerchantProgramme merchantProgramme, MerchantFeeMapped fee, BigDecimal txAmount, String merchantCustomerId, String merchantTransactionId, Transaction transaction)
			throws DatabaseException {
		
		// inutile creare un record del genere
		if (fee == null && merchantCustomerId == null && merchantTransactionId == null)
			return;
		
		TransactionMerchantDetails tmd = new TransactionMerchantDetails();
		tmd.setCid(merchantProgramme.getCid());
		
		if (fee != null) {
			tmd.setFeeAmount(calculateFeeValue(fee, txAmount));
			tmd.setFee(fee);
		}
		
		tmd.setMerchantCustomerId(merchantCustomerId);
		tmd.setMerchantTransactionId(merchantTransactionId);
		tmd.setProgramme(merchantProgramme);
		tmd.setTransaction(transaction);
		
		genericService.save(tmd);
	}
	
	private void checkCustomerName(Consumer consumer, String customerName) throws WsMerchantValidationException {
		
		LinkedList<String> customerNameTokens = new LinkedList<String>();
		LinkedList<String> consumerTokens = new LinkedList<String>();

		StringTokenizer st = new StringTokenizer(customerName, " ");
		
		while (st.hasMoreElements()) {
			customerNameTokens.add((String) st.nextElement());
		}
		
		st = new StringTokenizer(consumer.getFullName(), " ");
		
		while (st.hasMoreElements()) {
			consumerTokens.add((String) st.nextElement());
		}

		if (consumerTokens.size() != customerNameTokens.size())
			throw new WsMerchantValidationException(ResultCodes.WRONG_CUSTOMER_NAME);

		Collections.sort(customerNameTokens);
		Collections.sort(consumerTokens);
		
		for (int i = 0; i < consumerTokens.size(); i++) {
			String s = customerNameTokens.get(i);
			String s2 = consumerTokens.get(i);
			
			if (!s.trim().equalsIgnoreCase(s2.trim()))
				throw new WsMerchantValidationException(ResultCodes.WRONG_CUSTOMER_NAME);
		}
	}

	/**
	 * Cerco le fee speciali del merchant
	 * da ora anche byVolume, 13/03/2017
	 * 
	 * @param merchantEwallet
	 * @param channel
	 * @param amount
	 * @return
	 * @throws DatabaseException
	 * @throws NotFoundException
	 */
	private MerchantFeeMapped findMerchantFee(Ewallet merchantEwallet, LoadUnloadChannel channel, BigDecimal amount) throws DatabaseException, NotFoundException {
		MerchantFeeMapped fee = findTransactionFeeInternal(MerchantFeeByRange.class, merchantEwallet, channel, null, null, amount.floatValue());
		
		if (fee == null)
			fee = findTransactionFeeInternal(MerchantFeeByVolume.class, merchantEwallet, channel, null, null, null);
		
		return fee; 
	}
	
	private MerchantFeeByVolume findExactVolumeFee(Ewallet merchantEwallet, LoadUnloadChannel merchantChannel, MerchantFeeByVolume fee) throws DatabaseException, NotFoundException {
		
		Long count = countMerchantTransactions(merchantEwallet, merchantChannel, fee);
		
		if (count < fee.getFromAmount() || count > fee.getToAmount()) {
			
			Criterion[] criterions = {
				Restrictions.lt("fromAmount", count.floatValue()),
				Restrictions.ge("toAmount", count.floatValue())
			};
			
			MerchantFeeByVolume fee2 = findTransactionFeeInternal(MerchantFeeByVolume.class, merchantEwallet, fee.getFeeType(), null, null, null, criterions);
			return fee2;
		}
		
		return fee;
	}
	
	private Long countMerchantTransactions(Ewallet merchantEwallet, LoadUnloadChannel txChannel, MerchantFeeByVolume fee) throws DatabaseException {
		
		LocalDateTime dateFrom;
		LocalDateTime datoTill;
		
		if (fee.getFrom() != null) {
			
			dateFrom = NewDateUtils.resetTime(fee.getFrom());
			datoTill = NewDateUtils.resetTime(NewDateUtils.addDays(fee.getTill(), 1));
			
		} else {
			
			datoTill = NewDateUtils.resetTime(NewDateUtils.addDays(LocalDateTime.now(), 1));
			
			MerchantFeeByVolumeType type = fee.getType();
			
			if (type.isDaily())
				dateFrom = NewDateUtils.resetTime(LocalDateTime.now());
			else if (type.isWeekly())
				dateFrom = NewDateUtils.resetTime(NewDateUtils.firstDayOfWeek(LocalDateTime.now()));
			else if (type.isMonthly())
				dateFrom = NewDateUtils.resetTime(NewDateUtils.firstDayOfMonth(LocalDateTime.now()));
			else // yearly
				dateFrom = NewDateUtils.resetTime(NewDateUtils.firstDayOfYear(LocalDateTime.now()));
		}
		
		Criterion[] criterions = {
			Restrictions.eq("transactionSubType", txChannel),
			Restrictions.eq("transactionStatus.code", TransactionStatus.SETTLED),
			Restrictions.eq("account", merchantEwallet),
			Restrictions.ge("date", dateFrom),
			Restrictions.lt("date", datoTill)
		};
		
		return genericService.count(Transaction.class, criterions);
	}
	
	private BigDecimal calculateMerchantFee(MerchantFeeMapped merchantFee, BigDecimal amount) {
		if (merchantFee == null)
			return BigDecimal.ZERO;
		
		return calculateFeeValue(merchantFee, amount);
	}

	/**
	 * Metodo unico per cercare il merchant
	 * 
	 * @param merchantCode
	 * @return
	 * @throws DatabaseException
	 * @throws WsMerchantValidationException 
	 */
	private MerchantProgramme findMerchantProgramme(String merchantCode) throws DatabaseException, WsMerchantValidationException {

		MerchantProgramme merchantProgramme = genericService.get(MerchantProgramme.class, merchantCode);
		
		if (!merchantProgramme.isAuthorized())
			throw new WsMerchantValidationException(ResultCodes.MERCHANT_NOT_ENABLED);

		return merchantProgramme;
	}

	/**
	 * Metodo unico per cercare il consumer
	 * 
	 * @param email
	 * @return
	 * @throws DatabaseException
	 */
	@Override
	public Consumer findMerchantCustomer(String email) throws DatabaseException {

		String CID = ApplicationContext.getApplication().getCid();
		
		if (genericService.exist(CoBrander.class, "company", WsContextHolder.getContext().getMerchantProgramme().getCustomer())) {
			
			try {
				
				CoBrander coBrander = genericService.findFirst(CoBrander.class, "company", WsContextHolder.getContext().getMerchantProgramme().getCustomer());
				CID = coBrander.getCid();

			} catch (NotFoundException e) {
				throw new RuntimeException("should not happen", e);
			}
		}
		
		Criterion[] criterions = { Restrictions.eq("email", email.toLowerCase()), Restrictions.eq("cid", CID) };

		// non lo faccio andare in errore
		if (!genericService.exist(Consumer.class, criterions))
			return null;

		try {
			return genericService.findFirst(Consumer.class, criterions);
		} catch (NotFoundException e) {
			throw new RuntimeException("should not happen", e);
		}
	}
	
	private void setStatusAndResultCodeToHistory(MerchantRequestHistory merchantHistory, String status, Integer resultCode) {
		
		merchantHistory.setResultCode(resultCode);
		merchantHistory.setStatus(status);
	}
 
}
