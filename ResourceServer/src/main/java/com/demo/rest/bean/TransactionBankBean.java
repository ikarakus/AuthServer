/**
 * @author Ern
 */
package com.demo.rest.bean;

import com.demo.common.domain.transaction.request.BankCharges;
import com.revoframework.domain.common.Country;

/**
 * @author Ern
 *
 */
public class TransactionBankBean extends TransactionBean{

	private String beneficiaryName;
	private Country beneficiaryBankCountry;
	private Country beneficiaryCountry;
	private String targetBankname;
	private String targetBankAddress;
	private String targetBankAddress2;
	private String bicOrSwift; 
	private BankCharges bankCharges; 
	private boolean sepa;
	private boolean saveBeneficiary;
	private boolean iban;
//	private boolean ibic;
	private String bankAccountNumber;
	private boolean inEdit;
	private String idTemplate;
	
	
	public TransactionBankBean(){
		super();
		sepa = true;
	}
	 
	public String getBeneficiaryName() {
		return beneficiaryName;
	}
	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}

	public String getBicOrSwift() {
		return bicOrSwift;
	}
	public void setBicOrSwift(String bicOrSwift) {
		this.bicOrSwift = bicOrSwift;
	}
	 
	public boolean isSepa() {
		return sepa;
	}
	public void setSepa(boolean sepa) {
		this.sepa = sepa;
	}
	public String getTargetBankAddress() {
		return targetBankAddress;
	}

	public void setTargetBankAddress(String targetBankAddress) {
		this.targetBankAddress = targetBankAddress;
	}

	public String getTargetBankAddress2() {
		return targetBankAddress2;
	}

	public void setTargetBankAddress2(String targetBankAddress2) {
		this.targetBankAddress2 = targetBankAddress2;
	}

	public boolean isSaveBeneficiary() {
		return saveBeneficiary;
	}
	public void setSaveBeneficiary(boolean saveBeneficiary) {
		this.saveBeneficiary = saveBeneficiary;
	}
	public boolean isIban() {
		return iban;
	}
	public void setIban(boolean iban) {
		this.iban = iban;
	}
	public String getBankAccountNumber() {
		return bankAccountNumber;
	}
	public void setBankAccountNumber(String bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
	}
	public boolean isInEdit() {
		return inEdit;
	}
	public void setInEdit(boolean inEdit) {
		this.inEdit = inEdit;
	}
	public Country getBeneficiaryBankCountry() {
		return beneficiaryBankCountry;
	}
	public void setBeneficiaryBankCountry(Country beneficiaryBankCountry) {
		this.beneficiaryBankCountry = beneficiaryBankCountry;
	}
	public BankCharges getBankCharges() {
		return bankCharges;
	}
	public void setBankCharges(BankCharges bankCharges) {
		this.bankCharges = bankCharges;
	}
	public Country getBeneficiaryCountry() {
		return beneficiaryCountry;
	}
	public void setBeneficiaryCountry(Country beneficiaryCountry) {
		this.beneficiaryCountry = beneficiaryCountry;
	}
	public String getIdTemplate() {
		return idTemplate;
	}
	public void setIdTemplate(String idTemplate) {
		this.idTemplate = idTemplate;
	}
//	public boolean isIbic() {
//		return ibic;
//	}
//	public void setIbic(boolean ibic) {
//		this.ibic = ibic;
//	}

	public String getTargetBankname() {
		return targetBankname;
	}

	public void setTargetBankname(String targetBankname) {
		this.targetBankname = targetBankname;
	}
	 
	
	
}
