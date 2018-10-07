package com.demo.rest.bean;

import java.io.Serializable;

public class BlobFileResponseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7953913714778220580L;

	private String documentType;
	private Long generic_blob_entity_id;

	public String getDocumentType() {
		return documentType;
	}
	public Long getGeneric_blob_entity_id() {
		return generic_blob_entity_id;
	}
	public void setGeneric_blob_entity_id(Long generic_blob_entity_id) {
		this.generic_blob_entity_id = generic_blob_entity_id;
	}
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

}
