package com.demo.rest.bean;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.demo.common.bean.QuestionBean;
import com.demo.common.domain.customer.Address;
import com.demo.common.domain.customer.CustomerDueDiligence;
import com.demo.common.domain.customer.Gender;
import com.demo.common.domain.customer.SecurityQuestion;
import com.demo.common.domain.customer.Title;
import com.revoframework.domain.common.Country;
import com.revoframework.domain.common.Language;
import com.revoframework.domain.common.PhonePrefix;

/**
 * @author ibrahim karakuş
 *
 */
public class ProfileBean implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 7953913714778220580L;
	private Long id;
	private Language language;
	private String eMail;
	private String password;
	private String repeatPassword;
	private PhonePrefix mobilePrefix;
	private String mobile;
	private PhonePrefix homePrefix;
	private String phone;
	private String name;
	private String surname;

	private String midname;
	private Gender gender;
	private String dateOfBirth;
	private String documentExpireDate;
	private Title title;
	private Country nationality;
	private Country otherNationality;
	private Address residenceAddress;
	private Address preferredAddress;
	private Integer currentCddLevel;
	private Integer askedCddLevel;
	private String statusCdd;
	private String documentType;
	private String docNumber;
	private Country documentCountry;
	List<QuestionBean> questionsBeans1;
	List<QuestionBean> questionsBeans2;
	List<QuestionBean> questionsBeans3;
	private List<QuestionBean> listQuestionCDD1 = new ArrayList<QuestionBean>();
	private List<QuestionBean> listQuestionCDD2 = new ArrayList<QuestionBean>();
	private List<QuestionBean> listQuestionCDD3 = new ArrayList<QuestionBean>();
	private CustomerDueDiligence dueDiligence;
	private String productLimit;
	private String productName;	
	private Address residentialAddress;
	private List<File> documentCard;
	private List<File> documentBill;
	private String documentCardName;
	private String documentBillName;
	
	// step 2 Certifications and declarations
	private List<QuestionBean> listQuestion1 = new ArrayList<QuestionBean>();
	private List<QuestionBean> listTermsAndCondition = new ArrayList<QuestionBean>();	
	private List<QuestionBean> listPEP = new ArrayList<QuestionBean>();
		

	private SecurityQuestion securityQuestion_first;
	
	private SecurityQuestion securityQuestion_second;
	
	private String securityAnswer_first;
	
	private String securityAnswer_second;	

	public SecurityQuestion getSecurityQuestion_first() {
		return securityQuestion_first;
	}

	public void setSecurityQuestion_first(SecurityQuestion securityQuestion_first) {
		this.securityQuestion_first = securityQuestion_first;
	}

	public SecurityQuestion getSecurityQuestion_second() {
		return securityQuestion_second;
	}

	public void setSecurityQuestion_second(SecurityQuestion securityQuestion_second) {
		this.securityQuestion_second = securityQuestion_second;
	}

	public String getSecurityAnswer_first() {
		return securityAnswer_first;
	}

	public void setSecurityAnswer_first(String securityAnswer_first) {
		this.securityAnswer_first = securityAnswer_first;
	}

	public String getSecurityAnswer_second() {
		return securityAnswer_second;
	}

	public void setSecurityAnswer_second(String securityAnswer_second) {
		this.securityAnswer_second = securityAnswer_second;
	}

	public String getMidname() {
		return midname;
	}

	public void setMidname(String midname) {
		this.midname = midname;
	}

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public String geteMail() {
		return eMail;
	}

	public void seteMail(String eMail) {
		this.eMail = eMail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRepeatPassword() {
		return repeatPassword;
	}

	public void setRepeatPassword(String repeatPassword) {
		this.repeatPassword = repeatPassword;
	}

	public PhonePrefix getMobilePrefix() {
		return mobilePrefix;
	}

	public void setMobilePrefix(PhonePrefix mobilePrefix) {
		this.mobilePrefix = mobilePrefix;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public PhonePrefix getHomePrefix() {
		return homePrefix;
	}

	public void setHomePrefix(PhonePrefix homePrefix) {
		this.homePrefix = homePrefix;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public Title getTitle() {
		return title;
	}

	public void setTitle(Title title) {
		this.title = title;
	}

	public Country getNationality() {
		return nationality;
	}

	public void setNationality(Country nationality) {
		this.nationality = nationality;
	}

	public Country getOtherNationality() {
		return otherNationality;
	}

	public void setOtherNationality(Country otherNationality) {
		this.otherNationality = otherNationality;
	}

	public Address getResidenceAddress() {
		return residenceAddress;
	}

	public void setResidenceAddress(Address residenceAddress) {
		this.residenceAddress = residenceAddress;
	}

	public CustomerDueDiligence getDueDiligence() {
		return dueDiligence;
	}

	public void setDueDiligence(CustomerDueDiligence dueDiligence) {
		this.dueDiligence = dueDiligence;
	}

	public Integer getCurrentCddLevel() {
		return currentCddLevel;
	}

	public void setCurrentCddLevel(Integer currentCddLevel) {
		this.currentCddLevel = currentCddLevel;
	}

	public String getStatusCdd() {
		return statusCdd;
	}

	public void setStatusCdd(String statusCdd) {
		this.statusCdd = statusCdd;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getDocNumber() {
		return docNumber;
	}

	public void setDocNumber(String docNumber) {
		this.docNumber = docNumber;
	}

	public String getDocumentExpireDate() {
		return documentExpireDate;
	}

	public void setDocumentExpireDate(String documentExpireDate) {
		this.documentExpireDate = documentExpireDate;
	}

	public Country getDocumentCountry() {
		return documentCountry;
	}

	public void setDocumentCountry(Country documentCountry) {
		this.documentCountry = documentCountry;
	}

	public List<QuestionBean> getQuestionsBeans1() {
		return questionsBeans1;
	}

	public void setQuestionsBeans1(List<QuestionBean> questionsBeans1) {
		this.questionsBeans1 = questionsBeans1;
	}

	public List<QuestionBean> getQuestionsBeans2() {
		return questionsBeans2;
	}

	public void setQuestionsBeans2(List<QuestionBean> questionsBeans2) {
		this.questionsBeans2 = questionsBeans2;
	}

	public List<QuestionBean> getQuestionsBeans3() {
		return questionsBeans3;
	}

	public void setQuestionsBeans3(List<QuestionBean> questionsBeans3) {
		this.questionsBeans3 = questionsBeans3;
	}

	public Integer getAskedCddLevel() {
		return askedCddLevel;
	}

	public void setAskedCddLevel(Integer askedCddLevel) {
		this.askedCddLevel = askedCddLevel;
	}

	public boolean getCddToAccept() {
		return statusCdd.equals(CustomerDueDiligence.TO_ACCEPT) ? true : false;
	}

	public boolean getCddAccepted() {
		return statusCdd.equals(CustomerDueDiligence.ACCEPTED) ? true : false;
	}

	public boolean getCddRejected() {
		return statusCdd.equals(CustomerDueDiligence.REJECTED) ? true : false;
	}

	public String getProductLimit() {
		return productLimit;
	}

	public void setProductLimit(String productLimit) {
		this.productLimit = productLimit;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	
	public Address getResidentialAddress() {
		return residentialAddress;
	}

	public void setResidentialAddress(Address residentialAddress) {
		this.residentialAddress = residentialAddress;
	}
	public List<File> getDocumentCard() {
		return documentCard;
	}

	public void setDocumentCard(List<File> documentCard) {
		this.documentCard = documentCard;
	}
	public List<File> getDocumentBill() {
		return documentBill;
	}

	public void setDocumentBill(List<File> documentBill) {
		this.documentBill = documentBill;
	}
	
	public String getDocumentCardName() {
		return documentCardName;
	}

	public void setDocumentCardName(String documentCardName) {
		this.documentCardName = documentCardName;
	}

	public String getDocumentBillName() {
		return documentBillName;
	}

	public void setDocumentBillName(String documentBillName) {
		this.documentBillName = documentBillName;
	}

	public List<QuestionBean> getListQuestionCDD1() {
		return listQuestionCDD1;
	}

	public void setListQuestionCDD1(List<QuestionBean> listQuestionCDD1) {
		this.listQuestionCDD1 = listQuestionCDD1;
	}

	public List<QuestionBean> getListQuestionCDD2() {
		return listQuestionCDD2;
	}

	public void setListQuestionCDD2(List<QuestionBean> listQuestionCDD2) {
		this.listQuestionCDD2 = listQuestionCDD2;
	}

	public List<QuestionBean> getListQuestionCDD3() {
		return listQuestionCDD3;
	}

	public void setListQuestionCDD3(List<QuestionBean> listQuestionCDD3) {
		this.listQuestionCDD3 = listQuestionCDD3;
	}
	public List<QuestionBean> getListQuestion1() {
		return listQuestion1;
	}

	public void setListQuestion1(List<QuestionBean> listQuestion1) {
		this.listQuestion1 = listQuestion1;
	}


	public List<QuestionBean> getListTermsAndCondition() {
		return listTermsAndCondition;
	}

	public void setListTermsAndCondition(List<QuestionBean> listTermsAndCondition) {
		this.listTermsAndCondition = listTermsAndCondition;
	}


	public List<QuestionBean> getListPEP() {
		return listPEP;
	}

	public void setListPEP(List<QuestionBean> listPEP) {
		this.listPEP = listPEP;
	}


	public Address getPreferredAddress() {
		return preferredAddress;
	}

	public void setPreferredAddress(Address preferredAddress) {
		this.preferredAddress = preferredAddress;
	}
	


	
	
}
