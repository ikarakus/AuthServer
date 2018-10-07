package com.demo.rest.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.List;
import java.util.UUID;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.common.domain.account.Account;
import com.demo.common.domain.account.Ewallet;
import com.demo.common.domain.account.LoadUnloadChannel;
import com.demo.common.domain.customer.Consumer;
import com.demo.common.domain.customer.Customer;
import com.demo.common.domain.customer.CustomerDueDiligence;
import com.demo.common.domain.customer.wl.CoBrander;
import com.demo.common.domain.customer.wl.WhiteLabeler;
import com.demo.common.domain.transaction.request.BankCharges;
import com.demo.common.domain.transaction.request.IncomePaymentRequest;
import com.demo.common.domain.transaction.request.NonSepaTransfer;
import com.demo.common.domain.transaction.request.PaymentOrder;
import com.demo.common.domain.transaction.request.PaymentTemplate;
import com.demo.common.domain.transaction.request.SepaTransfer;
import com.demo.common.domain.transaction.request.TransactionRequestStatus;
import com.demo.common.exception.AccountNotGoodBeneficiaryException;
import com.demo.common.exception.AccountNotGoodException;
import com.demo.common.exception.AvailableBalanceException;
import com.demo.common.exception.CardNotFoundException;
import com.demo.common.exception.CustomerNotActiveException;
import com.demo.common.exception.CustomerNotFoundException;
import com.demo.common.exception.EwalletNotFoundBeneficiaryException;
import com.demo.common.exception.EwalletNotFoundException;
import com.demo.common.exception.ExceededLimitBeneficiaryException;
import com.demo.common.exception.ExceededLimitException;
import com.demo.common.exception.IbanNotUsableException;
import com.demo.common.exception.IbanNotValidException;
import com.demo.common.exception.PreviewAccountException;
import com.demo.common.exception.TransactionRequestAlreadyApprovedException;
import com.demo.common.exception.TransferNotAuthorizableException;
import com.demo.common.exception.TransferToSameEwalletException;
import com.demo.common.model.alb.AlbQueueNames;
import com.demo.common.model.alb.TransactionResult;
import com.demo.common.rabbitmq.alb.TransactionClearancePublisher;
import com.demo.common.service.AccountService;
import com.demo.common.service.CustomerService;
import com.demo.common.service.TransactionRequestService;
import com.demo.common.service.TransactionService;
import com.demo.common.service.TransactionService.TransferEwalletToAccountResultBean;
import com.demo.common.service.alb.AlbService;
import com.demo.common.service.ewallet.EwalletService;
import com.demo.common.service.factory.TransactionRequestFactory;
import com.demo.rest.bean.TransactionBankBean;
import com.demo.rest.bean.TransactionEwalletBean;
import com.revoframework.bean.PagedListHolder;
import com.revoframework.dao.OrderBy;
import com.revoframework.domain.common.Country;
import com.revoframework.domain.common.Currency;
import com.revoframework.exception.ApplicationException;
import com.revoframework.exception.DatabaseException;
import com.revoframework.exception.EmailException;
import com.revoframework.exception.EntityValidationException;
import com.revoframework.exception.NotFoundException;
import com.revoframework.exception.ValidationException;
import com.revoframework.module.cms.ApplicationContext;
import com.revoframework.service.RevoGenericService;
import com.revoframework.util.NumberUtil;
import com.revoframework.web.bean.JsonBean;

/**
 * @author Ern
 *
 */
@Service
public class TransferService extends CustomerService{
 
	@Autowired
	private RevoGenericService genericService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private TransactionRequestService transactionRequestService;
	
	@Autowired
	private TransactionRequestFactory transactionRequestFactory;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private AlbService albService;
	
	@Autowired 
	private EwalletService ewalletService;
	
	public TransactionBankBean revertPaymentTemplate(String idTemplate) throws NumberFormatException, DatabaseException { 
		
		PaymentTemplate paymentTemplate = genericService.get(PaymentTemplate.class,Long.parseLong(idTemplate));
		if(paymentTemplate==null){
			return new TransactionBankBean();
		}
			
		
		TransactionBankBean payment = new TransactionBankBean();
		if (paymentTemplate.getType().equals(PaymentTemplate.TYPE_SEPA))
			payment.setSepa(true);
		else
			payment.setSepa(false);

		payment.setIban(paymentTemplate.getIsIban());
		payment.setBankAccountNumber(paymentTemplate.getBankAccountNumber());
//		payment.setIbic(paymentTemplate.getIsBic());
		payment.setBicOrSwift(paymentTemplate.getBic());
		payment.setCurrency(paymentTemplate.getCurrency().getCode());
		payment.setBeneficiaryName(paymentTemplate.getBeneficiaryName());
		payment.setTargetBankname(paymentTemplate.getBeneficiaryBankName());
		payment.setBeneficiaryBankCountry(paymentTemplate.getBeneficiaryBankCountry());
		payment.setTargetBankAddress(paymentTemplate.getBankAddress());
		payment.setTargetBankAddress2(paymentTemplate.getBankAddress2());
		
		return payment;
	}

	public List<PaymentTemplate> findPaymentTemplate(Consumer consumerSession,boolean sepa,boolean all) throws DatabaseException{
		Criterion[] criterions=null;
		
		if(all){
			 criterions = new  Criterion[1]; 
			 criterions[0]=Restrictions.eq("owner", consumerSession);
		}else if(sepa){
			criterions = new  Criterion[2]; 
			criterions[0]=Restrictions.eq("type", PaymentTemplate.TYPE_SEPA);
			criterions[1]=Restrictions.eq("owner", consumerSession); 
		}else{
			criterions = new  Criterion[2]; 
			criterions[0]=Restrictions.eq("type", PaymentTemplate.TYPE_NON_SEPA);
			criterions[1]=Restrictions.eq("owner", consumerSession); 
		}
		
		
		OrderBy orderBy = new OrderBy("beneficiaryName",true);
		
		List<PaymentTemplate> templateList = genericService.findAllNoPaging(PaymentTemplate.class,criterions,orderBy);
		
		return templateList;
	}
	
	public TransactionEwalletBean findMyEwalletBean(Consumer consumerSession,String currency) throws DatabaseException,EwalletNotFoundException{
		TransactionEwalletBean transactionEwalletBean = new TransactionEwalletBean();
		Ewallet ewallet = ewalletService.findMyEwallet(consumerSession, Currency.EUR);
		transactionEwalletBean.setAccountNumber(ewallet.getToString());
		transactionEwalletBean.setCurrentBalance(ewallet.getBalance());
		transactionEwalletBean.setCurrentAvailableBalance(ewallet.getAvailableAmount());
		transactionEwalletBean.setCurrency(ewallet.getCurrency().getCode());
		try {
			Account account = genericService.findFirst(Account.class, "customer", consumerSession);
			transactionEwalletBean.setMensilyLoadTransaction(NumberUtil.formatCurrencyNoEuroWithUserLocale(transactionService.getTotalAmountOfMonthlyLoads(account).doubleValue()));  
			transactionEwalletBean.setMensilyUnLoadTransaction(NumberUtil.formatCurrencyNoEuroWithUserLocale(transactionService.getTotalAmountOfMonthlyUnloads(account).doubleValue()));
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return transactionEwalletBean;
	}
	
	
	public TransactionEwalletBean createTransactionEwalletBean(JsonBean jb,Consumer consumerSession) 
			throws DatabaseException, EwalletNotFoundException, CustomerNotFoundException, ApplicationException, 
			TransferToSameEwalletException, AccountNotGoodBeneficiaryException{
		
		TransactionEwalletBean eWalletBean = new TransactionEwalletBean();
		
		Account beneficiaryAccount;
		beneficiaryAccount =  ewalletService.findBeneficiaryEwallet(jb.findItem("toEmailAccount").getValue(),consumerSession.getCid(),Currency.EUR);
		Ewallet ewalletSource = ewalletService.findMyEwallet(consumerSession, Currency.EUR);
		
		if(jb.findItem("toEmailAccount").getValue().equals(consumerSession.getUser().getEmail()) ||
				jb.findItem("toEmailAccount").getValue().equals(ewalletSource.getNumber())){
			throw new TransferToSameEwalletException();
		}
		
		DecimalFormatSymbols symbols = new DecimalFormatSymbols(ApplicationContext.getApplication().getUserLocale());	
		String pattern = "#,##0.0#";
		DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
		decimalFormat.setParseBigDecimal(true);
		BigDecimal balance = BigDecimal.ZERO;
		// parse the string
		try {
			balance = (BigDecimal) decimalFormat.parse(jb.findItem("balance").getValue());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
		Customer customer = beneficiaryAccount.getCustomer();
		eWalletBean.setAccountNumber(jb.findItem("accountNumber").getValue());		
		eWalletBean.setCurrentBalance(balance);
		eWalletBean.setFullNameTo(customer.getToString());
		BigDecimal amount=  BigDecimal.ZERO;
		try {
			amount=  NumberUtil.parseBigDecimalCurrency(jb.findItem("amount").getValue());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		eWalletBean.setAmount(amount); 
		eWalletBean.setCurrency(jb.findItem("currency").getValue());
		eWalletBean.setDescription(jb.findItem("description").getValue());
		eWalletBean.setRecipientEmail(jb.findItem("toEmailAccount").getValue());
		
		return eWalletBean;
	}
	
public TransactionEwalletBean createPaymentRequest(JsonBean jb,Consumer consumerSession) throws  EwalletNotFoundException, DatabaseException{
		
		TransactionEwalletBean eWalletBean = new TransactionEwalletBean();
		
		 
		if(jb.findItem("toEmailAccount").getValue().equals(consumerSession.getUser().getEmail())){
			throw new EwalletNotFoundException(consumerSession);
		}
		
		DecimalFormatSymbols symbols = new DecimalFormatSymbols(ApplicationContext.getApplication().
				getUserLocale());
		String pattern = "#,##0.0#";
		DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
		decimalFormat.setParseBigDecimal(true);
		BigDecimal balance = BigDecimal.ZERO;
		// parse the string
		try {
			balance = (BigDecimal) decimalFormat.parse(jb.findItem("balance").getValue());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		 
		eWalletBean.setAccountNumber(jb.findItem("accountNumber").getValue());		
		eWalletBean.setCurrentBalance(balance);
		eWalletBean.setFullNameTo(jb.findItem("fullNameTo").getValue());
		BigDecimal amount=  BigDecimal.ZERO;
		try {
			amount = NumberUtil.parseBigDecimalCurrency(jb.findItem("amount").getValue());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		eWalletBean.setAmount(amount); 
		eWalletBean.setCurrency(jb.findItem("currency").getValue());
		eWalletBean.setDescription(jb.findItem("description").getValue());
		eWalletBean.setRecipientEmail(jb.findItem("toEmailAccount").getValue());
		
		return eWalletBean;
	}
	
	@Deprecated
	public TransactionEwalletBean createTransactionEwalletBeanForAccountNotExist(JsonBean jb,Consumer consumerSession) throws DatabaseException{
		
		TransactionEwalletBean eWalletBean = new TransactionEwalletBean(); 
		 
		DecimalFormatSymbols symbols = new DecimalFormatSymbols(ApplicationContext.getApplication().getUserLocale());	
		String pattern = "#,##0.0#";
		DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
		decimalFormat.setParseBigDecimal(true);
		BigDecimal balance = BigDecimal.ZERO;
		// parse the string
		try {
			balance = (BigDecimal) decimalFormat.parse(jb.findItem("balance").getValue());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
		eWalletBean.setAccountNumber(jb.findItem("accountNumber").getValue());
		eWalletBean.setCurrentBalance(balance); 
		BigDecimal amount=  BigDecimal.ZERO;
		try {
			amount = NumberUtil.parseBigDecimalCurrency(jb.findItem("amount").getValue());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		eWalletBean.setAmount(amount); 
		eWalletBean.setCurrency(jb.findItem("currency").getValue());
		eWalletBean.setDescription(jb.findItem("description").getValue());
		eWalletBean.setRecipientEmail(jb.findItem("toEmailAccount").getValue());
		
		return eWalletBean;
	}
	
	public void checkTransactionForEwallet(TransactionEwalletBean eWalletBean, boolean sendToAccountNotExist, Consumer consumer,String emailBenefeciary) 
			throws DatabaseException, NotFoundException, AccountNotGoodException, CustomerNotFoundException, EwalletNotFoundException, ApplicationException, 
			ExceededLimitException, AvailableBalanceException, ExceededLimitBeneficiaryException, PreviewAccountException   {  
		Account accountSouce =  ewalletService.findMyEwallet(consumer,Currency.EUR);
		
		Criterion[] crit = { Restrictions.sqlRestriction("{alias}.CUSTOMER_ID="+consumer.getId())};
		
		CustomerDueDiligence cdd = genericService.findFirst(CustomerDueDiligence.class,crit);
		
		if(cdd.getCurrentCddLevel()==0){
		throw new PreviewAccountException();	
		}		
		
		LoadUnloadChannel channelUnload = genericService.get(LoadUnloadChannel.class, LoadUnloadChannel.UTOE);
		LoadUnloadChannel channelLoad = genericService.get(LoadUnloadChannel.class, LoadUnloadChannel.LFRE);
		
		accountService.checkBalanceAndLimit(accountSouce, channelUnload, accountSouce.getCurrency(), eWalletBean.getAmount()); 
		if (!sendToAccountNotExist){
			try {
				Account accountTarget =  ewalletService.findBeneficiaryEwallet(emailBenefeciary,consumer.getCid(),Currency.EUR);	
				accountService.checkBalanceAndLimit(accountTarget, channelLoad, accountSouce.getCurrency(), eWalletBean.getAmount());
			} catch (ExceededLimitException | AvailableBalanceException | AccountNotGoodBeneficiaryException e) {
				throw new ExceededLimitBeneficiaryException(e.getMessage());
			}
		} 
	}
	 
	public void creaTransactionForEwallet(TransactionEwalletBean eWalletBean, boolean sendToAccountNotExist, Consumer consumer) 
			throws DatabaseException, NotFoundException, ExceededLimitException, AvailableBalanceException, CustomerNotFoundException, EwalletNotFoundException, ApplicationException, 
			ValidationException, AccountNotGoodException, ExceededLimitBeneficiaryException, AccountNotGoodBeneficiaryException, EmailException, EwalletNotFoundBeneficiaryException, TransferToSameEwalletException , CardNotFoundException, EntityValidationException {  
		 
		if (sendToAccountNotExist)
			transactionRequestService.transaferFundsToNonexistentConsumer(consumer, eWalletBean.getDescription(),
					eWalletBean.getAmount(), eWalletBean.getCurrency(), eWalletBean.getFullNameTo(),
					eWalletBean.getRecipientEmail(), consumer.getUser().getLanguage().getCode());
		else
			// get customer from email 		
			transactionService.transferFundsToAnotherEwallet(consumer, eWalletBean.getRecipientEmail(),
					eWalletBean.getCurrency(), eWalletBean.getAmount(), eWalletBean.getDescription(), consumer);			
	}
	
	//Iban girince çalışan method banka ad bic/swift bulan - sepa detatları da set ediyor
	public TransactionBankBean createTransactionBankBean(JsonBean jb,boolean sepa,
			boolean saveBeneficiary,
			boolean accocuntNumberTypeIban,
			boolean banckCodeType,Consumer consumer) throws DatabaseException, EwalletNotFoundException{
		Ewallet ewallet = ewalletService.findMyEwallet(consumer, Currency.EUR);  
		TransactionBankBean bankBean = new TransactionBankBean();
		bankBean.setAccountNumber(ewallet.getToString());
		bankBean.setCurrentBalance(ewallet.getBalance());
		bankBean.setCurrentAvailableBalance(ewallet.getAvailableAmount());
		bankBean.setCurrency(ewallet.getCurrency().getCode());
		bankBean.setInEdit(false);
		bankBean.setSepa(sepa);
		bankBean.setSaveBeneficiary(saveBeneficiary);
		bankBean.setIban(accocuntNumberTypeIban);
//		bankBean.setIbic(banckCodeType);
		if(jb.hasValue("targetName"))
			bankBean.setBeneficiaryName(jb.findItem("targetName").getValue());
		if(jb.hasValue("bankAccountNumber"))
			bankBean.setBankAccountNumber(jb.findItem("bankAccountNumber").getValue());
		
		BigDecimal amount=  BigDecimal.ZERO;
		try {
			if(jb.hasValue("amount")){
				amount = NumberUtil.parseBigDecimalCurrency(jb.findItem("amount").getValue());
				bankBean.setAmount(amount); 
			}else
				bankBean.setAmount(null);
		} catch (Exception e) {
			bankBean.setAmount(null); 
		}
		
		if(jb.hasValue("targetBankname"))
			bankBean.setTargetBankname(jb.findItem("targetBankname").getValue());	
		if(jb.hasValue("bic"))	
			bankBean.setBicOrSwift(jb.findItem("bic").getValue());
		if(jb.hasValue("description"))
			bankBean.setDescription(jb.findItem("description").getValue());
		  
		try {	
			if(jb.hasValue("targetBankCountry") &&  jb.findItem("targetBankCountry").getValue().length() >0){
				bankBean.setBeneficiaryBankCountry(genericService.get(Country.class, jb.findItem("targetBankCountry").getValue())); 
				if(!sepa){    
					bankBean.setCurrency(jb.findItem("currency").getValue());  
					bankBean.setBankCharges(genericService.get(BankCharges.class, Integer.parseInt(jb.findItem("bankCharges").getValue()))); 
					if(jb.hasValue("targetBankAddress"))
						bankBean.setTargetBankAddress(jb.findItem("targetBankAddress").getValue());
					if(jb.hasValue("targetBankAddress2"))
						bankBean.setTargetBankAddress2(jb.findItem("targetBankAddress2").getValue());
				} 
			}
		} catch (Exception e) {
			 
		} 
		
		return bankBean;
	}
	
	public void creaTransactionForBankTransfer(TransactionBankBean transactionBankBean,Consumer consumer) 
			throws DatabaseException, NotFoundException, ExceededLimitException, AvailableBalanceException, TransferNotAuthorizableException, 
			ApplicationException, TransactionRequestAlreadyApprovedException, IbanNotValidException, ValidationException, EwalletNotFoundException, 
			AccountNotGoodException, IbanNotUsableException, CustomerNotActiveException, EmailException, CardNotFoundException, EntityValidationException { 
		
		PaymentOrder paymentOrder = fillPaymentOrder(transactionBankBean,consumer); 
		
		try {
			transactionRequestService.validateTransactionRequest(paymentOrder);		 
			transactionRequestService.handleTransactionRequestWithAuthorizations(paymentOrder, consumer, TransactionRequestStatus.AUTHORIZED);
		} catch (AccountNotGoodBeneficiaryException | ExceededLimitBeneficiaryException | TransferToSameEwalletException e) {
			// ignoro, si può verificare solo per i transfer to eW
			//I do not know, it can only happen for the transfer to eW
		}
		
		//if(transactionBankBean.isSaveBeneficiary())
		//	transactionRequestService.generateAndSavePaymentTemplate(paymentOrder); 
	}
	
	public void validateTransferBank(TransactionBankBean transactionBankBean,Consumer consumer ) 
			throws DatabaseException, NotFoundException, IbanNotValidException, EwalletNotFoundException, ValidationException, ApplicationException, 
			AccountNotGoodException, IbanNotUsableException, AvailableBalanceException, ExceededLimitException, EntityValidationException, PreviewAccountException{
		
		PaymentOrder paymentOrder = fillPaymentOrder(transactionBankBean,consumer);
		
        Criterion[] crit = { Restrictions.sqlRestriction("{alias}.CUSTOMER_ID="+consumer.getId())};
		
		CustomerDueDiligence cdd = genericService.findFirst(CustomerDueDiligence.class,crit);
		
		if(cdd.getCurrentCddLevel()==0){
		throw new PreviewAccountException();	
		}
		
		
		try {
			transactionRequestService.validateTransactionRequest(paymentOrder);
		} catch (AccountNotGoodBeneficiaryException | ExceededLimitBeneficiaryException e) {
			// ignoro, si può verificare solo per i transfer to eW
		}	
	}
	
	
	private PaymentOrder fillPaymentOrder(TransactionBankBean transactionBankBean,Consumer consumer) throws DatabaseException, NotFoundException, IbanNotValidException, EwalletNotFoundException, IbanNotUsableException, ApplicationException {
		 
		PaymentOrder paymentOrder = null;
		if(transactionBankBean.isSepa()){
			paymentOrder = transactionRequestFactory.createSepaTransfer(consumer, consumer.getUser(), transactionBankBean.getAmount(), transactionBankBean.getDescription(), transactionBankBean.getBeneficiaryName(), transactionBankBean.getBicOrSwift(), transactionBankBean.getBankAccountNumber(), transactionBankBean.getTargetBankname(), transactionBankBean.getBeneficiaryBankCountry().getKey());			 
		}else{
			paymentOrder = transactionRequestFactory.createNonSepaTransfer(consumer, consumer.getUser(), transactionBankBean.getCurrency(), transactionBankBean.getAmount(), transactionBankBean.getDescription(), transactionBankBean.getBeneficiaryName(),  transactionBankBean.getBicOrSwift(), transactionBankBean.getTargetBankAddress(),  transactionBankBean.getTargetBankAddress2(), transactionBankBean.getBankAccountNumber(), transactionBankBean.isIban(), transactionBankBean.getTargetBankname(), transactionBankBean.getBeneficiaryBankCountry().getKey(), transactionBankBean.getBankCharges().getKey());			 
		}  
		  
		return paymentOrder;
	}
	
	public void findBankName(TransactionBankBean transactionBankBean) throws IbanNotValidException, IbanNotUsableException, DatabaseException, ApplicationException, NotFoundException {
		 
		PaymentOrder paymentOrder = null;
		if(transactionBankBean.isSepa()){
			paymentOrder = new SepaTransfer();			
		}else{
			paymentOrder = new NonSepaTransfer();
			((NonSepaTransfer)paymentOrder).setBankCode(transactionBankBean.getBicOrSwift());
			((NonSepaTransfer)paymentOrder).setIsIban(transactionBankBean.isIban());
		}
		paymentOrder.setBankAccountNumber(transactionBankBean.getBankAccountNumber());
		 
		transactionRequestService.autoCompleteIbanFieldsForPaymentOrder(paymentOrder);

		transactionBankBean.setTargetBankname(paymentOrder.getTargetBankname());
		if(transactionBankBean.isSepa()){
			transactionBankBean.setBicOrSwift(((SepaTransfer)paymentOrder).getBic());			
		}else{
			transactionBankBean.setBicOrSwift(((NonSepaTransfer)paymentOrder).getBankCode());			
		}		
		if(paymentOrder.getTargetBankCountry() != null) {
			transactionBankBean.setBeneficiaryBankCountry(paymentOrder.getTargetBankCountry());
			if(!transactionBankBean.isSepa()){
				transactionBankBean.setIban(((NonSepaTransfer)paymentOrder).getIsIban());
//				transactionBankBean.setIbic(((NonSepaTransfer)paymentOrder).getIsBic());				
			}
		}
	}
	
	
	
	
	public PagedListHolder<IncomePaymentRequest> findLastIncomePaymentRequest(Consumer consumer,int currentPage,int maxRow){
		PagedListHolder<IncomePaymentRequest> result=null;
		
		try {
			Account account = genericService.findFirst(Account.class, "customer", consumer);
			
			Criterion[] criterion= {
					Restrictions.eq("account", account) 
			}; 
			 
			OrderBy orderBy = new OrderBy("dateAdd", true);  
			result = genericService.findAll(IncomePaymentRequest.class, criterion, orderBy, currentPage, maxRow);
		} catch (DatabaseException | NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	public void transferFundsToWhiteLabelerAccount(Consumer consumer, BigDecimal amount)
			throws DatabaseException, EwalletNotFoundException, NotFoundException, ExceededLimitException,
			AvailableBalanceException, ExceededLimitBeneficiaryException, AccountNotGoodException,
			AccountNotGoodBeneficiaryException, EmailException, CardNotFoundException, TransferToSameEwalletException {
		
		genericService.refresh(consumer);
		
		WhiteLabeler wl = customerService.findWhiteLabelerByCid(consumer.getCid());
		
		Ewallet sourceEwallet = ewalletService.findMyEwallet(consumer, Currency.EUR);
		Ewallet targetEwallet = ewalletService.findMyEwallet(wl.getCompany(), sourceEwallet.getCurrency());
		
		String description = "insert required transaction here";
		
		TransferEwalletToAccountResultBean resultBean = transactionService.createTrasferEwalletToAccount(sourceEwallet, targetEwallet, null, amount, description, null, false, consumer);
		
		if (wl.getCid().equals(CoBrander.ALBPAY)) {
			String message = TransactionClearancePublisher.serialize(sourceEwallet.getNumber(), TransactionResult.DONE, resultBean.getCreditTransaction().getDate(), resultBean.getCreditTransaction().getId());
			
			String messageId = UUID.randomUUID().toString();
			albService.saveMessage(AlbQueueNames.TRANSACTION_CLEARANCE, messageId, message, false);
			TransactionClearancePublisher.publish(message, messageId);
		}
	}
	public List<PaymentTemplate> findPaymentTemplate(Consumer consumerSession, boolean sepa) throws DatabaseException {
		Criterion[] criterions = null;

		if (sepa) {
			criterions = new Criterion[2];
			criterions[0] = Restrictions.eq("type", PaymentTemplate.TYPE_SEPA);
			criterions[1] = Restrictions.eq("owner", consumerSession);
		} else {
			criterions = new Criterion[2];
			criterions[0] = Restrictions.eq("type", PaymentTemplate.TYPE_NON_SEPA);
			criterions[1] = Restrictions.eq("owner", consumerSession);
		}

		OrderBy orderBy = new OrderBy("beneficiaryName", true);

		List<PaymentTemplate> templateList = genericService.findAllNoPaging(PaymentTemplate.class, criterions, orderBy);

		return templateList;
	}
	public TransactionBankBean createTransactionBankBean(JsonBean jb, boolean sepa, boolean saveBeneficiary, boolean accocuntNumberTypeIban, Consumer consumer, String eWalletcurrency) throws DatabaseException, EwalletNotFoundException {

		String currency = jb.findItem("currency").getValue();
		
		 
		
		
		
		if (currency == null)
			currency = eWalletcurrency;
		
		Ewallet ewallet = ewalletService.findMyEwallet(consumer, currency);
		TransactionBankBean bankBean = new TransactionBankBean();
		bankBean.setAccountNumber(ewallet.getToString());
		bankBean.setCurrentBalance(ewallet.getBalance());
		bankBean.setCurrentAvailableBalance(ewallet.getAvailableAmount());
		bankBean.setCurrency(currency);
		bankBean.setInEdit(false);
		bankBean.setSepa(sepa);
		bankBean.setSaveBeneficiary(saveBeneficiary);
		bankBean.setIban(accocuntNumberTypeIban);
		// bankBean.setIbic(banckCodeType);

		if (jb.hasValue("targetName"))
			bankBean.setBeneficiaryName(jb.findItem("targetName").getValue());
		if (jb.hasValue("bankAccountNumber"))
			bankBean.setBankAccountNumber(jb.findItem("bankAccountNumber").getValue());

		BigDecimal amount = BigDecimal.ZERO;
		try {
			if (jb.hasValue("amount")) {
				amount = NumberUtil.parseBigDecimalCurrency(jb.findItem("amount").getValue());
				bankBean.setAmount(amount);
			} else
				bankBean.setAmount(null);
		} catch (Exception e) {
			bankBean.setAmount(null);
		}

		if (jb.hasValue("targetBankname"))
			bankBean.setTargetBankname(jb.findItem("targetBankname").getValue());
		if (jb.hasValue("bic"))
			bankBean.setBicOrSwift(jb.findItem("bic").getValue());
		if (jb.hasValue("description"))
			bankBean.setDescription(jb.findItem("description").getValue());

		try {
			if (jb.hasValue("targetBankCountry") && jb.findItem("targetBankCountry").getValue().length() > 0) {
				bankBean.setBeneficiaryBankCountry(genericService.get(Country.class, jb.findItem("targetBankCountry").getValue()));
				if (!sepa) {
					bankBean.setBankCharges(genericService.get(BankCharges.class, Integer.parseInt(jb.findItem("bankCharges").getValue())));
					if (jb.hasValue("targetBankAddress"))
						bankBean.setTargetBankAddress(jb.findItem("targetBankAddress").getValue());
					if (jb.hasValue("targetBankAddress2"))
						bankBean.setTargetBankAddress2(jb.findItem("targetBankAddress2").getValue());
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return bankBean;
	}
	
	
	public TransactionEwalletBean createTransactionEwalletBeanForAccountNotExist(JsonBean jb) throws DatabaseException {

		TransactionEwalletBean eWalletBean = new TransactionEwalletBean();

		DecimalFormatSymbols symbols = new DecimalFormatSymbols(ApplicationContext.getApplication().getUserLocale());
		String pattern = "#,##0.0#";
		DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
		decimalFormat.setParseBigDecimal(true);
		BigDecimal balance = BigDecimal.ZERO;
		// parse the string
		try {
			balance = (BigDecimal) decimalFormat.parse(jb.findItem("balance").getValue());
		} catch (ParseException e) {
			e.printStackTrace();
		}

		eWalletBean.setAccountNumber(jb.findItem("accountNumber").getValue());
		eWalletBean.setCurrentBalance(balance);
		BigDecimal amount = BigDecimal.ZERO;
		try {
			amount = NumberUtil.parseBigDecimalCurrency(jb.findItem("amount").getValue());
		} catch (ParseException e) {
			logger.error(e);
		} 

 
		eWalletBean.setAmount(amount);
		eWalletBean.setCurrency(jb.findItem("currency").getValue());
		eWalletBean.setDescription(jb.findItem("description").getValue());
		eWalletBean.setRecipientEmail(jb.findItem("toEmailAccount").getValue());

		return eWalletBean;
	}
}