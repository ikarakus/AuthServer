/**
 * @author Ern
 */
package com.demo.rest.bean;


/**
 * @author Ern
 *
 */
public class TransactionEwalletBean extends TransactionBean{

	private String recipientEmail ;
	private String fullNameTo ;
	private boolean accountNotExist;

	
	public TransactionEwalletBean(){
		super();
	}
	
	public String getRecipientEmail() {
		return recipientEmail;
	}

	public void setRecipientEmail(String recipientEmail) {
		this.recipientEmail = recipientEmail;
	}

	public String getFullNameTo() {
		return fullNameTo;
	}

	public void setFullNameTo(String fullNameTo) {
		this.fullNameTo = fullNameTo;
	}

	public boolean isAccountNotExist() {
		return accountNotExist;
	}

	public void setAccountNotExist(boolean accountNotExist) {
		this.accountNotExist = accountNotExist;
	}
 
	
	
}
