package com.demo.rest.contract;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.demo.common.domain.account.AccountBalanceHistory;
import com.demo.common.exception.EwalletNotFoundException;
import com.demo.common.exception.InvalidParameterException;
import com.demo.rest.bean.BalanceResponseBean;
import com.demo.rest.bean.BlobFileResponseBean;
import com.demo.rest.bean.EwalletResponseBean;
import com.demo.rest.bean.ProfileBean;
import com.demo.rest.bean.SwitchResponseBean;
import com.demo.rest.bean.TransferFundsToCustomerRequestBean;
import com.demo.rest.bean.TransferFundsToCustomerResponseBean;
import com.demo.rest.bean.UpdateSecurityRequestBean;
import com.revoframework.exception.ApplicationException;
import com.revoframework.exception.DatabaseException;
import com.revoframework.exception.NotFoundException;

@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public interface ISwitchController {
	
	@RequestMapping(value = "/Test", method = RequestMethod.GET)
	public ResponseEntity<String> sayHello(@RequestParam("name") String name);	
	
	/**
	 * getCustomerAccountSummary
	 * Returns information about customer's each ewallet account
	 * @param String customerId
	 * @return ResponseEntity<List<Ewallet>>
	 * @throws DatabaseException, NumberFormatException, InvalidParameterException
	 */
	@RequestMapping(value = "/Cobrander/Account", method = RequestMethod.GET) 
	public ResponseEntity<SwitchResponseBean<EwalletResponseBean>> getCustomerAccountSummary(@RequestParam("customerId") String customerId) throws DatabaseException, NumberFormatException, InvalidParameterException;
	
	/**
	 * getCustomerBalance
	 * Gets the available and total balances of customer's ewallet requested
	 * @param String customerId
	 * @param String accountNumber
	 * @return ResponseEntity<BigDecimal>
	 * @throws NumberFormatException, DatabaseException, InvalidParameterException, EwalletNotFoundException, ApplicationException
	 */
	@RequestMapping(value = "/Cobrander/Account/Balance", method = RequestMethod.GET)
	public ResponseEntity<SwitchResponseBean<BalanceResponseBean>> getCustomerBalance(@RequestParam("accountNumber") String accountNumber) throws NumberFormatException, DatabaseException, InvalidParameterException, EwalletNotFoundException, ApplicationException;
	
	/**
	 * sendFundstoCustomer
	 * This method will be used by the cobrander in order to make funds transfers
	 * from the merchant's corporate account to the end users account
	 * @param TransferFundsToCustomerRequestBean bean
	 * @return ResponseEntity<TransferFundsToCustomerResponseBean>
	 */
	@RequestMapping(value = "/Cobrander/Transfer/Customer", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TransferFundsToCustomerResponseBean> sendFundstoCustomer(TransferFundsToCustomerRequestBean bean);	

	/**
	 * depositCashtoCustomer
	 * This method will be used by the cobrander hardware (such as ATMs) in order to make direct transfers to the customers account. 
	 * The method should check the CID of the beneficiary customer and strictly disallow transfers to other Cobranders or CIDs.
	 * @return
	 */
	@RequestMapping(value = "/Cobrander/Transfer/Cash", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<DepositCashToCustomerResponseBean> depositCashtoCustomer(DepositCashToCustomerRequestBean bean);
	public ResponseEntity<?> depositCashToCustomer();
	
	/**
	 * GetCustomerDocuments
	 * Gets the list of documents along with the document types that belong to a customer. 
	 * The populated information should return identity/reference to the document image files for further download
	 * 
	 */
	@RequestMapping(value = "/Cobrander/Customer/Document", method = RequestMethod.GET)
	public ResponseEntity<?> getCustomerDocuments();
	
	/**
	 * GetCustomerKYC
	 * This method should get the screening data obtained from the external KYC system of a specific consumer
	 * 
	 */
	@RequestMapping(value = "/Cobrander/Customer/KYC", method = RequestMethod.GET)
	public ResponseEntity<?> getCustomerKYC();
	
	/**
	 * getBalance
	 * Gets the available and total balances of customer's ewallet requested
	 * @param String customerId
	 * @param String accountNumber
	 * @return ResponseEntity<BigDecimal>
	 * @throws NumberFormatException, DatabaseException, InvalidParameterException, EwalletNotFoundException, ApplicationException
	 */
	@RequestMapping(value = "/Members/Account/Balance", method = RequestMethod.GET)
	public ResponseEntity<SwitchResponseBean<BalanceResponseBean>> getBalance(@RequestParam("accountNumber") String accountNumber) throws NumberFormatException, DatabaseException, InvalidParameterException, EwalletNotFoundException, ApplicationException;

	/**
	 * getAccountSummary
	 * Returns information about customer's each ewallet account
	 * @param String customerId
	 * @return ResponseEntity<List<Ewallet>>
	 * @throws DatabaseException, NumberFormatException, InvalidParameterException
	 */
	@RequestMapping(value = "/Members/Account", method = RequestMethod.GET) 
	public ResponseEntity<SwitchResponseBean<EwalletResponseBean>> getAccountSummary(@RequestParam("customerId") String customerId) throws DatabaseException, NumberFormatException, InvalidParameterException;
	
	/**
	 * getAccountStatement
	 * Returns the customer cashflow between specified dates.
	 * @param accountNumber
	 * @param beginDate
	 * @param endDate
	 * @return ResponseEntity<List<AccountBalanceHistory>>
	 * @throws DatabaseException
	 * @throws InvalidParameterException
	 */
	@RequestMapping(value = "/Members/Statement", method = RequestMethod.GET)
	public ResponseEntity<List<AccountBalanceHistory>> getAccountStatement(@RequestParam("accountNumber") String accountNumber, @RequestParam("beginDate") String beginDate, @RequestParam("endDate") String endDate) throws DatabaseException, InvalidParameterException;
		
	/**
	 * sendFundstoMerchant
	 * used to transfer funds from the account of any customer to any Merchant defined in the system
	 * 
	 */
	@RequestMapping(value = "/Members/Transfer/Merchant", method = RequestMethod.POST)
	public ResponseEntity<?> sendFundstoMerchant();
	
	/**
	 * sendFundstoEWallet
	 * used to transfer funds from any merchant account to any customer account in the system
	 */
	@RequestMapping(value = "/Members/Transfer/Ewallet", method = RequestMethod.POST)
	public ResponseEntity<?> sendFundstoEWallet();
	
	/**
	 * getProfile
	 * Retrieves Basic Profile Information of the logged in Customer.
	 * @param customerId
	 * @return ResponseEntity<ProfileBean>
	 * @throws NumberFormatException
	 * @throws DatabaseException
	 * @throws InvalidParameterException
	 * @throws EwalletNotFoundException
	 * @throws NotFoundException
	 */
	@RequestMapping(value = "/Members/Profile", method = RequestMethod.GET)
	public ResponseEntity<SwitchResponseBean<ProfileBean>> getProfile(@RequestParam("customerId") String customerId);
	
	/**
	 * GetCustomerCDDDetails
	 * This method retrieves all the profile data of any retail consumer.
	 */
	@RequestMapping(value = "/Cobrander/Customer/Information", method = RequestMethod.GET)
	public  ResponseEntity<SwitchResponseBean<ProfileBean>> getCustomerCDDDetails(@RequestParam("customerId") String customerId);

	
	/**
	 * updateProfile
	 * Updates the profile information of the logged in Customer.
	 * @return
	 */
	@RequestMapping(value = "/Members/Profile", method = RequestMethod.PUT)
	public ResponseEntity<?> updateProfile();
	
	/**
	 * upgradeProduct
	 * This is method will be used for applying to the next product available.
	 * @return
	 */
	@RequestMapping(value = "/Members/Upgrade", method = RequestMethod.POST)
	public ResponseEntity<?> upgradeProduct();
	
	/**
	 * getDocuments
	 * Gets the list of document images  for the customer.
	 * 
	 */
	@RequestMapping(value = "/Members/Document/Info", method = RequestMethod.GET)
	public ResponseEntity<SwitchResponseBean<BlobFileResponseBean>> getDocuments(@RequestParam("customerId") String customerId)  throws NumberFormatException, DatabaseException, InvalidParameterException,NotFoundException;

	
	/**
	 * sendDocuments
	 * Updates the list of document images  for the customer.
	 * 
	 */
	@RequestMapping(value = "/Members/Document", method = RequestMethod.POST)
	public ResponseEntity<?> sendDocuments();
	
	/**
	 * updateSecurityInformation
	 * Updates the password, secret questions
	 * 
	 */
	@RequestMapping(value = "/Members/UpdateSecurity", method = RequestMethod.POST)
	public ResponseEntity<SwitchResponseBean<?>> updateSecurity(UpdateSecurityRequestBean bean);	
	
	/**
	 * submitCustomerApplication
	 * Receives the account opening information and sends the data to  the queue server
	 * 
	 */
	@RequestMapping(value = "/Cobrander/Customer/Application", method = RequestMethod.POST)
	public ResponseEntity<?> submitCustomerApplication();	
	
	/**
	 * submitCustomerApplicationDocument
	 * Receives the required account opening image files and sends the data to  the queue server
	 * 
	 */
	@RequestMapping(value = "/Cobrander/Customer/ApplicationDocument", method = RequestMethod.POST)
	public ResponseEntity<?> submitCustomerApplicationDocument();	
	
	
	
}
