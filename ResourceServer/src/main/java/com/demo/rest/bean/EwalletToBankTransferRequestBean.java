package com.demo.rest.bean;

import com.demo.common.domain.customer.Consumer;

public class EwalletToBankTransferRequestBean extends SwitchRequestBean {

	private static final long serialVersionUID = 8822801163525996436L;

	private TransactionBankBean transactionBankBean;
	private Consumer consumer;

	public TransactionBankBean getTransactionBankBean() {
		return transactionBankBean;
	}

	public void setTransactionBankBean(TransactionBankBean transactionBankBean) {
		this.transactionBankBean = transactionBankBean;
	}

	public Consumer getConsumer() {
		return consumer;
	}

	public void setConsumer(Consumer consumer) {
		this.consumer = consumer;
	}
	

}
