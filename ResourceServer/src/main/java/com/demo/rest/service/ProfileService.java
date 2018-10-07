package com.demo.rest.service;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.common.bean.QuestionBean;
import com.demo.common.domain.account.product.Product;
import com.demo.common.domain.customer.Address;
import com.demo.common.domain.customer.Consumer;
import com.demo.common.domain.customer.Customer;
import com.demo.common.domain.customer.CustomerDueDiligence;
import com.demo.common.domain.customer.CustomerDueDiligenceBlobEntity;
import com.demo.common.domain.customer.Question;
import com.demo.common.domain.customer.QuestionAnswer;
import com.demo.common.domain.customer.SecurityAnswer;
import com.demo.common.domain.customer.SecurityQuestion;
import com.demo.common.exception.EwalletNotFoundException;
import com.demo.common.service.CustomerService;
import com.demo.rest.bean.ProfileBean;
import com.revoframework.dao.OrderBy;
import com.revoframework.domain.common.Currency;
import com.revoframework.exception.DatabaseException;
import com.revoframework.exception.NotFoundException;
import com.revoframework.util.NewDateUtils;


@Service
public class ProfileService extends CustomerService {

	@Autowired
	private CustomerService customerService;
	
	private Consumer consumer;
	private Address preferredAddress;
	private Address residentialAddress;
	
	
	public Consumer getConsumer() {
		return consumer;
	}

	public void setConsumer(Consumer consumer) {
		this.consumer = consumer;
	}

	public Address getPreferredAddress() {
		return preferredAddress;
	}

	public void setPreferredAddress(Address preferredAddress) {
		this.preferredAddress = preferredAddress;
	}

	public Address getResidentialAddress() {
		return residentialAddress;
	}

	public void setResidentialAddress(Address residentialAddress) {
		this.residentialAddress = residentialAddress;
	}
	
		
	
	
	private QuestionAnswer getAnswerByQuestion(Customer customer,Question question) throws DatabaseException{
		
		try {
			Criterion[] cri = {
					Restrictions.eq("question.id", question.getId()),
					Restrictions.eq("customer", customer)
			};
			
			return genericService.findFirst(QuestionAnswer.class, cri);
									
		} catch (NotFoundException e) {
			return null;
		}	
	}
	
	
	private void fillSecurityQuestions(ProfileBean profileBean,Consumer consumer) throws DatabaseException{
		
			Criterion[] cri = {
					Restrictions.eq("customer", consumer)
			};
			
			List<SecurityAnswer> securityAnswerList=genericService.findAllNoPaging(SecurityAnswer.class, cri);
			
			if (securityAnswerList.size()>0) {
				profileBean.setSecurityAnswer_first(securityAnswerList.get(0).getAnswer());
				profileBean.setSecurityQuestion_first(securityAnswerList.get(0).getSecurityQuestion());
				if (securityAnswerList.size()>1) {
					profileBean.setSecurityAnswer_second(securityAnswerList.get(1).getAnswer());
					profileBean.setSecurityQuestion_second(securityAnswerList.get(1).getSecurityQuestion());
				}
			}
		
	}


	private void fillCertificateQuestions(ProfileBean profileBean,Consumer consumer) throws DatabaseException { 
		
		QuestionAnswer answer=null;
		
		if (profileBean.getListQuestion1() == null || profileBean.getListQuestion1().size() == 0) {
			
			List<QuestionBean> beans = new ArrayList<QuestionBean>();
			List<QuestionBean> beans2 = new ArrayList<QuestionBean>();
			List<QuestionBean> beansPEP = new ArrayList<QuestionBean>();


			Criterion[] criterions = { Restrictions.eq("set", Question.SET_CONSUMER_SIGN_CERTIFICATIONS_AND_DECLARATIONS), Restrictions.isNull("parentQuestion") };

			List<Question> questions = getGenericService().findAllNoPaging(Question.class, criterions, new OrderBy("priority", true));

			for (Question question : questions) {
				QuestionBean bean = customerService.convertQuestionToQuestionBean(question);
				 answer = getAnswerByQuestion(consumer,question);
				 if (answer!=null)
					 bean.setValue(answer.getBooleanAnswer());
				beans.add(bean);
			}
			profileBean.setListQuestion1(beans);
			
			Criterion[] criterionsPEP = { Restrictions.eq("set", Question.SET_CONSUMER_SIGN_POLITICALLY_EXPOSED_PERSON), Restrictions.isNull("parentQuestion") };

			List<Question> questionsPEP = getGenericService().findAllNoPaging(Question.class, criterionsPEP, new OrderBy("priority", true));

			for (Question question : questionsPEP) {
				QuestionBean bean = customerService.convertQuestionToQuestionBean(question);
				 answer = getAnswerByQuestion(consumer,question);
				 if (answer!=null)
					 bean.setValue(answer.getBooleanAnswer());
				beansPEP.add(bean);
			}
			profileBean.setListPEP(beansPEP);


			Criterion[] criterions2 = { Restrictions.eq("set", Question.SET_CONSUMER_SIGN_TERMS_AND_CONDITINS), Restrictions.isNull("parentQuestion") };

			List<Question> questions2 = getGenericService().findAllNoPaging(Question.class, criterions2, new OrderBy("priority", true));

			for (Question question : questions2) {
				QuestionBean bean = customerService.convertQuestionToQuestionBean(question);
				 answer = getAnswerByQuestion(consumer,question);
				 if (answer!=null)
					 bean.setValue(answer.getBooleanAnswer());
				 beans2.add(bean);
			}
			profileBean.setListTermsAndCondition(beans2);


		}
	}
	
	public void fillCDDQuestions(ProfileBean profileBean,Consumer consumer) throws DatabaseException { 
		
		QuestionAnswer answer=null;
			
			Criterion[] criterions = {
				Restrictions.eq("set", Question.SET_CONSUMER_CDD_EMPLOYMENT_DETAILS),
				Restrictions.isNull("parentQuestion")
			};
			
			List<Question> questions = getGenericService().findAllNoPaging(Question.class, criterions, new OrderBy("priority", true));
			List<QuestionBean> beansCDD = new ArrayList<QuestionBean>();
			
			for (Question question : questions) {
				QuestionBean bean = customerService.convertQuestionToQuestionBean(question);	
				 answer = getAnswerByQuestion(consumer,question);
				 if (answer!=null){
					 if (question.getType().equals(Question.TYPE_COMBO))
					 bean.setValue(answer.getQuestionOption().getId());
					 else
						 bean.setValue(answer.getStringAnswer());
				 }
				beansCDD.add(bean);
			}
			profileBean.setListQuestionCDD1(beansCDD);
			
			Criterion[] criterions2 = {
					Restrictions.eq("set", Question.SET_CONSUMER_CDD_ESTIMATED_LOADING_FUNDS),
					Restrictions.isNull("parentQuestion")
			};
			
			questions = getGenericService().findAllNoPaging(Question.class, criterions2, new OrderBy("priority", true));
			beansCDD = new ArrayList<QuestionBean>();
			
			for (Question question : questions) {
				QuestionBean bean = customerService.convertQuestionToQuestionBean(question);	
				 answer = getAnswerByQuestion(consumer,question);
				 if (answer!=null)
					 bean.setValue(answer.getQuestionOption().getId());
				beansCDD.add(bean);
			}
			profileBean.setListQuestionCDD2(beansCDD);
			
			Criterion[] criterions3 = {
					Restrictions.eq("set", Question.SET_CONSUMER_CDD_CERTIFICATIONS_AND_DECLARATIONS),
					Restrictions.isNull("parentQuestion")
			};
			
			questions = getGenericService().findAllNoPaging(Question.class, criterions3, new OrderBy("priority", true));
			beansCDD = new ArrayList<QuestionBean>();
			
			for (Question question : questions) {
				QuestionBean bean = customerService.convertQuestionToQuestionBean(question);	
				 answer = getAnswerByQuestion(consumer,question);
				 if (answer!=null)
					 bean.setValue(answer.getBooleanAnswer());
				beansCDD.add(bean);
			}
			profileBean.setListQuestionCDD3(beansCDD);				
		
	}
	
	public CustomerDueDiligence getMyCdd(Customer customer) throws DatabaseException {
		try {
			return customerService.findMyCdd(customer);
		} catch (NotFoundException e) {
			
			return null;
					
		}
	}

	public void fillGeneralInfo(ProfileBean profileBean, Consumer consumer) throws DatabaseException, EwalletNotFoundException, NotFoundException  { 
		
		Integer currenctCddLevel = 0;
		Integer askedCddLevel = 0;
		String cddStatus = null;
		
		CustomerDueDiligence customerDueDiligence  = null;
		List<CustomerDueDiligenceBlobEntity> customerDDBEList=null;
		
		try {
			customerDueDiligence = findMyCdd(consumer);
			customerDDBEList = findNotDeleteCDDBE(customerDueDiligence);
			
			for (CustomerDueDiligenceBlobEntity customerDueDiligenceBlobEntity : customerDDBEList) {
				if (customerDueDiligenceBlobEntity.getDocumentType().equals(CustomerDueDiligenceBlobEntity.DOCUMENT_CARD))				
					profileBean.setDocumentCardName(customerDueDiligenceBlobEntity.getBlobEntity().getFileName());
					else if (customerDueDiligenceBlobEntity.getDocumentType().equals(CustomerDueDiligenceBlobEntity.UTILITY_BILL))
						profileBean.setDocumentBillName(customerDueDiligenceBlobEntity.getBlobEntity().getFileName());
			}
			
			
			currenctCddLevel = customerDueDiligence.getCurrentCddLevel();
			askedCddLevel = customerDueDiligence.getAskedCddLevel();
			cddStatus = customerDueDiligence.getStatus();			
			
		} catch (NotFoundException e) {
			currenctCddLevel = 0;
			askedCddLevel = 0;
			cddStatus = "Not exist";
			
			customerDueDiligence = new CustomerDueDiligence();
			customerDueDiligence.setCustomer(consumer);
		}
		
		
		profileBean.setDueDiligence(customerDueDiligence);
		
		profileBean.setCurrentCddLevel(currenctCddLevel);
		profileBean.setAskedCddLevel(askedCddLevel);
		profileBean.setStatusCdd(cddStatus);	
		profileBean.setDocumentExpireDate(NewDateUtils.format(consumer.getDocumentExpireDate(), NewDateUtils.DATE_PATTERN));
		profileBean.setDateOfBirth(NewDateUtils.format(consumer.getBirthdate(), NewDateUtils.DATE_PATTERN));
		profileBean.setMobilePrefix(consumer.getMobilePrefix());
		profileBean.setMobile(consumer.getMobileNumber());
		profileBean.seteMail(consumer.getEmail());
		profileBean.setMobilePrefix(consumer.getMobilePrefix());
	//	profileBean.setDocumentType(genericService.findFirst(DocumentTypeI18n.class, new String[] { "parent", "language" }, consumer.getDocumentType(), consumer.getUser().getLanguage()).getName());
	
		
		profileBean.setProductLimit(productService.findMyCddLimit(consumer, currenctCddLevel));
		Address primaryAddress=customerService.findPrimaryAddress(consumer);	
		boolean hasPrimaryAddress=true;
		
		if (primaryAddress==null){
			hasPrimaryAddress=false;
			primaryAddress=new Address();
			primaryAddress.setCustomer(consumer);
		}
		profileBean.setResidentialAddress(primaryAddress);
		Address preferredAddress=customerService.findPreferredAddress(consumer);
		if (preferredAddress==null){
			preferredAddress=new Address();
		    preferredAddress.setCustomer(consumer);
		}
		profileBean.setPreferredAddress(preferredAddress);

		if (hasPrimaryAddress){
			Product currentProduct = productService.getMyCurrentEwalletProduct(consumer,Currency.EUR);		
			if (currentProduct!=null)		
				profileBean.setProductName(currentProduct.getName());
		}
	}
	
	public void fillProfileDetailInfo(ProfileBean profileBean,Consumer consumer) throws DatabaseException { 
		fillSecurityQuestions(profileBean,consumer);
		fillCDDQuestions(profileBean,consumer);
		fillCertificateQuestions(profileBean,consumer);
	}
	
	
	public List<CustomerDueDiligenceBlobEntity> findNotDeleteCDDBE (CustomerDueDiligence customerDueDiligence) throws DatabaseException{
		
		Criterion[] criCDDBE = {
				Restrictions.eq("customerDiligence", customerDueDiligence),
				Restrictions.eq("deleted", false)
			};
		
		return genericService.findAllNoPaging(CustomerDueDiligenceBlobEntity.class, criCDDBE);
	}
	
	public void saveSecurityQuestionList(Consumer consumer, ArrayList<SecurityAnswer> securityAnswerList) throws DatabaseException, NotFoundException{
		if (!securityAnswerList.isEmpty()) {
			deleteSecurityQuestionList(consumer);
			for (SecurityAnswer securityAnswer : securityAnswerList) {
				saveSecurityQuestion(consumer, securityAnswer.getSecurityQuestion().getId().toString(), securityAnswer.getAnswer());
			}			
		}
	}
	
	private void deleteSecurityQuestionList(Consumer consumer) throws DatabaseException{
		
		Criterion[] cri = {
				Restrictions.eq("customer", consumer)
		};
			if (genericService.exist(SecurityAnswer.class, cri)) {
				List<SecurityAnswer> securityAnswerList = genericService.findAllNoPaging(SecurityAnswer.class, cri);
				
				for (SecurityAnswer securityAnswer : securityAnswerList) {
					getGenericService().delete(securityAnswer);
				}
				
			}
	}	
	
	private void saveSecurityQuestion(Consumer consumer, String question_id, String answer) throws DatabaseException, NotFoundException{
		
		SecurityQuestion securityQuestion = genericService.get(SecurityQuestion.class,Integer.parseInt(question_id));
		SecurityAnswer	securityAnswer=null;
		Criterion[] cri = {
				Restrictions.eq("securityQuestion", securityQuestion),
				Restrictions.eq("customer", consumer)
		};
		if (genericService.exist(SecurityAnswer.class, cri))
			securityAnswer=genericService.findFirst(SecurityAnswer.class, cri);
		else
			securityAnswer=new SecurityAnswer();
			
		securityAnswer.setSecurityQuestion(securityQuestion);
		securityAnswer.setAnswer(answer);
		securityAnswer.setCustomer(consumer);
		getGenericService().saveOrUpdate(securityAnswer);
		
	}	

}
