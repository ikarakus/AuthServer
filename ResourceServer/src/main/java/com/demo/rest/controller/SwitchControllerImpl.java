package com.demo.rest.controller;

import static com.demo.common.domain.transaction.request.merchant.MerchantRequest.STATUS_ERROR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import com.demo.common.domain.account.Account;
import com.demo.common.domain.account.AccountBalanceHistory;
import com.demo.common.domain.account.Ewallet;
import com.demo.common.domain.customer.Consumer;
import com.demo.common.domain.customer.Customer;
import com.demo.common.domain.customer.CustomerDueDiligence;
import com.demo.common.domain.customer.CustomerDueDiligenceBlobEntity;
import com.demo.common.domain.customer.SecurityAnswer;
import com.demo.common.domain.customer.wl.WhiteLabeler;
import com.demo.common.domain.transaction.request.merchant.MerchantRequestHistory;
import com.demo.common.domain.transaction.request.merchant.TransferFundsToCustomerRequest;
import com.demo.common.exception.EwalletNotFoundException;
import com.demo.common.exception.InvalidParameterException;
import com.demo.common.service.AccountService;
import com.demo.common.service.CustomerService;
import com.demo.common.service.ewallet.EwalletCurrencyService;
import com.demo.common.service.ewallet.EwalletService;
import com.demo.rest.bean.BalanceResponseBean;
import com.demo.rest.bean.BlobFileResponseBean;
import com.demo.rest.bean.EwalletResponseBean;
import com.demo.rest.bean.ProfileBean;
import com.demo.rest.bean.ResultCodes;
import com.demo.rest.bean.SwitchResponseBean;
import com.demo.rest.bean.TransferFundsToCustomerRequestBean;
import com.demo.rest.bean.TransferFundsToCustomerResponseBean;
import com.demo.rest.bean.UpdateSecurityRequestBean;
import com.demo.rest.contract.ISwitchController;
import com.demo.rest.service.MerchantTransactionService;
import com.demo.rest.service.ProfileService;
import com.demo.rest.util.ServiceUtil;
import com.demo.rest.util.SwitchHelper;
import com.revoframework.domain.common.Currency;
import com.revoframework.domain.common.User;
import com.revoframework.exception.ApplicationException;
import com.revoframework.exception.DatabaseException;
import com.revoframework.exception.NotFoundException;
import com.revoframework.exception.PasswordStrengthException;
import com.revoframework.service.RevoGenericService;
import com.revoframework.service.UserService;

@ComponentScan({"com.revoframework.service","com.revoframework.dao","com.demo.common.service","com.demo.rest.service"})
@ImportResource("classpath:database-config.xml")
@Transactional
@RestController
public class SwitchControllerImpl implements ISwitchController {
		
	public static final String APP_CID = WhiteLabeler.DEMO;

	@Autowired
	protected RevoGenericService genericService;
	
	@Autowired
	private EwalletService ewalletService;
	
	@Autowired
	protected EwalletCurrencyService ewalletCurrencyService;	

	@Autowired
	protected AccountService accountService;
	
	@Autowired
	private CustomerService customerService;
		
	@Autowired
	@Qualifier("merchantTransactionServiceImpl")
	private MerchantTransactionService merchantService;		
	
	@Autowired
	protected UserService userService;	
	
	@Autowired
	private ProfileService profileService;
	
	
	
	private Ewallet findEwallet(String customerId, String currencyCode) throws DatabaseException, InvalidParameterException, NotFoundException, EwalletNotFoundException {
		Customer customer = getCustomer(customerId);
		Currency currency = getCurrency(currencyCode);	
		return ewalletService.findMyEwallet(customer, currency);
	}
	
	private Customer getCustomer(String id) throws NumberFormatException, DatabaseException, InvalidParameterException {
		ServiceUtil.checkStringParameter("Customer id", id);
		return genericService.get(Customer.class, Long.valueOf(id));
	}
	
	private Consumer getConsumer(String id) throws NumberFormatException, DatabaseException, InvalidParameterException {
		ServiceUtil.checkStringParameter("Consumer id", id);
		return genericService.get(Consumer.class, Long.valueOf(id));
	}	
	
	private User getUser(String id) throws NumberFormatException, DatabaseException, InvalidParameterException {
		ServiceUtil.checkStringParameter("User id", id);
		return genericService.get(User.class, Long.valueOf(id));	
	}

	private Currency getCurrency(String code) throws DatabaseException, InvalidParameterException, NotFoundException {
		ServiceUtil.checkStringParameter("Currency code", code);
		return genericService.get(Currency.class, code);
	}
	
	private Account getAccount(String accountNumber) throws DatabaseException, InvalidParameterException {
		ServiceUtil.checkStringParameter("Account number", accountNumber);
		return genericService.get(Account.class, accountNumber);
	}	

	@Override
	public ResponseEntity<SwitchResponseBean<EwalletResponseBean>> getCustomerAccountSummary(String customerId) throws DatabaseException, NumberFormatException, InvalidParameterException {
		return getAccountSummary(customerId);
	}

	@Override
	public ResponseEntity<SwitchResponseBean<BalanceResponseBean>> getCustomerBalance(String accountNumber) throws NumberFormatException, DatabaseException, InvalidParameterException, EwalletNotFoundException, ApplicationException {
		return getBalance(accountNumber);
	}
	
	@Override
	public ResponseEntity<TransferFundsToCustomerResponseBean> sendFundstoCustomer(TransferFundsToCustomerRequestBean bean) {
		MerchantRequestHistory merchantHistory = new MerchantRequestHistory();
		merchantHistory.setRequestType(MerchantRequestHistory.TX_TO_CUSTOMER);
		TransferFundsToCustomerResponseBean responseBean = null;
		TransferFundsToCustomerRequest merchantRequest = null;
		
		try {
			
			responseBean = new TransferFundsToCustomerResponseBean();
			merchantRequest = bean.buildEntity(TransferFundsToCustomerRequest.class);
			
			genericService.save(merchantRequest);

		//	saveMerchantRequestHistory(merchantHistory, bean, merchantRequest);

			responseBean.setRequestId(merchantRequest.getId());
			
			// per fargli prendere il requestType, stronzata, viton
			genericService.refresh(merchantRequest);
			
			merchantService.transferFundsToCustomer(bean, merchantRequest, merchantHistory);
			
		//	fillResponse(responseBean, ResultCodes.OK, null);
			
		} catch (Exception e) {
		//	handleException(e, bean, responseBean);
			merchantRequest.setResultCode(responseBean.getResultCode());
			merchantRequest.setStatus(STATUS_ERROR);
		//	errorStatusAndResultCodeMerReqHistory(merchantHistory, merchantRequest.getResultCode());
		}

		try {
			genericService.update(merchantRequest);
		} catch (DatabaseException e) {
		//	handleException(e, bean, responseBean);
	//		logger.error("Unexpected error", e);
	//		logger.error(merchantHistory.getSerialization());
		}

		try {
			genericService.update(merchantHistory);
		} catch (Exception e) {
	//		logger.error("Unexpected non-blocking error", e);
	//		logger.error(merchantHistory.getSerialization());
		}

		return new ResponseEntity<TransferFundsToCustomerResponseBean>(responseBean, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> depositCashToCustomer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<?> getCustomerDocuments() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ResponseEntity<?> getCustomerKYC() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<SwitchResponseBean<BalanceResponseBean>> getBalance(String accountNumber) throws NumberFormatException,
			DatabaseException, InvalidParameterException, EwalletNotFoundException, ApplicationException {
	Ewallet ewallet = ewalletService.findMyEwallet(accountNumber,APP_CID);
		if (ewallet != null) {
			List<BalanceResponseBean> balanceList=  new ArrayList<>();
			SwitchResponseBean<BalanceResponseBean> switchResponseBean=  new SwitchResponseBean<BalanceResponseBean>();
			BalanceResponseBean balanceResponse=  new BalanceResponseBean();
			balanceResponse.setAvailableBalance(ewallet.getAvailableAmount());
			balanceResponse.setBalance(ewallet.getBalance());
			balanceList.add(balanceResponse);
			switchResponseBean.setResponseBody(balanceList);
			return new ResponseEntity<SwitchResponseBean<BalanceResponseBean>>(switchResponseBean, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Override
	public ResponseEntity<SwitchResponseBean<EwalletResponseBean>> getAccountSummary(String customerId) throws DatabaseException, NumberFormatException, InvalidParameterException {
		Customer customer = getCustomer(customerId);
		List<Ewallet> ewalletList = ewalletService.findAllMyEwallet(customer);
		List<EwalletResponseBean> ewalletBeanList=  new ArrayList<>();
		SwitchResponseBean<EwalletResponseBean> switchResponseBean=  new SwitchResponseBean<EwalletResponseBean>();	
		for (Ewallet ewallet : ewalletList) {
			EwalletResponseBean ewalletBean= new EwalletResponseBean();
			ewalletBean.setAccNumber(ewallet.getNumber());
			ewalletBean.setAccountType(ewallet.getAccountType());
			ewalletBean.setCurrencyCode(ewallet.getCurrency().getCode());
			ewalletBeanList.add(ewalletBean);	
		}		
		switchResponseBean.setResponseBody(ewalletBeanList);		
		return new ResponseEntity<SwitchResponseBean<EwalletResponseBean>> (switchResponseBean, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<AccountBalanceHistory>> getAccountStatement(String accountNumber, String beginDate, String endDate) throws DatabaseException, InvalidParameterException {
		Account account = getAccount(accountNumber);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("response_status", null);
	//	map.put("data", accountService.getAccountBalanceHistoryList(account, java.time.LocalDate.parse(beginDate), java.time.LocalDate.parse(endDate)));
		return new ResponseEntity (map, HttpStatus.OK);
//		return new ResponseEntity<List<AccountBalanceHistory>>(accountService.getAccountBalanceHistoryList(account, java.time.LocalDate.parse(beginDate), java.time.LocalDate.parse(endDate)), HttpStatus.OK);
//		return null;
	}

	@Override
	public ResponseEntity<?> sendFundstoMerchant() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<?> sendFundstoEWallet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<SwitchResponseBean<ProfileBean>> getProfile(String customerId) {
		Consumer consumer=null;
		ProfileBean profileBean = new ProfileBean();
		SwitchResponseBean<ProfileBean> switchResponseBean=  new SwitchResponseBean<ProfileBean>();
		List<ProfileBean> profileList=  new ArrayList<>();
		try {
			consumer = getConsumer(customerId);
			profileService.fillGeneralInfo(profileBean,consumer);	
			profileList.add(profileBean);
			switchResponseBean.setResponseBody(profileList);	
			SwitchHelper.fillResponse(switchResponseBean, ResultCodes.OK, null);
		} catch (NumberFormatException | DatabaseException | InvalidParameterException | EwalletNotFoundException | NotFoundException e) {
			SwitchHelper.handleException(switchResponseBean, e);
		}
		return new ResponseEntity<SwitchResponseBean<ProfileBean>> (switchResponseBean, HttpStatus.OK);
	}
	
	
	@Override
	public ResponseEntity<SwitchResponseBean<ProfileBean>> getCustomerCDDDetails(String customerId) {		
		Consumer consumer=null;
		ProfileBean profileBean = new ProfileBean();
		SwitchResponseBean<ProfileBean> switchResponseBean=  new SwitchResponseBean<ProfileBean>();
		List<ProfileBean> profileList=  new ArrayList<>();
		try {
			consumer = getConsumer(customerId);
			profileService.fillGeneralInfo(profileBean,consumer);	
			profileService.fillProfileDetailInfo(profileBean,consumer);
			profileList.add(profileBean);
			switchResponseBean.setResponseBody(profileList);	
			SwitchHelper.fillResponse(switchResponseBean, ResultCodes.OK, null);
			} catch (NumberFormatException | DatabaseException | InvalidParameterException | EwalletNotFoundException | NotFoundException e) {
				SwitchHelper.handleException(switchResponseBean,e);
			}
		
		return new ResponseEntity<SwitchResponseBean<ProfileBean>> (switchResponseBean, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> updateProfile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<?> upgradeProduct() {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public ResponseEntity<SwitchResponseBean<BlobFileResponseBean>> getDocuments(String customerId) throws NumberFormatException, DatabaseException, InvalidParameterException, NotFoundException {
		Consumer consumer = getConsumer(customerId);
		List<BlobFileResponseBean> blobFileList=  new ArrayList<>();
		SwitchResponseBean<BlobFileResponseBean> switchResponseBean=  new SwitchResponseBean<BlobFileResponseBean>();
		if (consumer!=null) {
			CustomerDueDiligence customerDueDiligence = profileService.getMyCdd(consumer);
			List<CustomerDueDiligenceBlobEntity> customerDDBEList = profileService.findNotDeleteCDDBE(customerDueDiligence);
			for (CustomerDueDiligenceBlobEntity customerDueDiligenceBlobEntity : customerDDBEList) {
				BlobFileResponseBean blobFileBean= new BlobFileResponseBean();
				blobFileBean.setDocumentType(customerDueDiligenceBlobEntity.getDocumentType());
				blobFileBean.setGeneric_blob_entity_id(customerDueDiligenceBlobEntity.getId());
				blobFileList.add(blobFileBean);
			}
		}
		
		switchResponseBean.setResponseBody(blobFileList);		
		return new ResponseEntity<SwitchResponseBean<BlobFileResponseBean>> (switchResponseBean, HttpStatus.OK);

	}

	@Override
	public ResponseEntity<?> sendDocuments() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<SwitchResponseBean<?>> updateSecurity(UpdateSecurityRequestBean bean) {
		SwitchResponseBean<ProfileBean> switchResponseBean =  new SwitchResponseBean<ProfileBean>();
		Consumer consumer;
		try {
			consumer = getConsumer(bean.getCustomerId());

			if (bean.getNewPassword() != null)
				userService.changePassword(consumer.getUser(), bean.getNewPassword());	
			
			if (bean.getFirstSecurityQuestionId() != null && bean.getSecondSecurityQuestionId() != null) {
				ArrayList<SecurityAnswer> securityAnswerList = new ArrayList<SecurityAnswer>();
				securityAnswerList.add(SwitchHelper.createSecurityAnswer(bean.getFirstSecurityQuestionId(), bean.getFirstSecurityQuestionAnswer()));
				securityAnswerList.add(SwitchHelper.createSecurityAnswer(bean.getSecondSecurityQuestionId(), bean.getSecondSecurityQuestionAnswer()));
				profileService.saveSecurityQuestionList(consumer, securityAnswerList);
			}
			SwitchHelper.fillResponse(switchResponseBean, ResultCodes.OK, null);
		} catch (NumberFormatException | DatabaseException | InvalidParameterException | PasswordStrengthException | NotFoundException e) {
			SwitchHelper.handleException(switchResponseBean,e);
		}	
		return new ResponseEntity<SwitchResponseBean<?>> (switchResponseBean, HttpStatus.OK);	
	}

	@Override
	public ResponseEntity<?> submitCustomerApplication() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<?> submitCustomerApplicationDocument() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ResponseEntity<String> sayHello(String name) {
		return new ResponseEntity<String>("Hello " + name, HttpStatus.OK);
	}
	
}
