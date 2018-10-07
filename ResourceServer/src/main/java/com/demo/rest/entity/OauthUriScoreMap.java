package com.demo.rest.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "OAUTH_URI_SCOPE_MAP")
public class OauthUriScoreMap implements Serializable{

	private static final long serialVersionUID = 3995104306170167378L;

	@Id
	@Column(name = "id", length = 4)
	private String id;

	@Column(name = "uri", length = 255)
	private String uri;
	
	@Column(name = "scope", length = 255)
	private String scope;
	
	@Column(name = "method_name", length = 255)
	private String methodName;
	
	@Column(name = "verb", length = 10)
	private String verb;
	
	@Column(name = "resource_owner", length = 10)
	private String resourceOwner;

	@Column(name = "otp_required")
	private Boolean otpRequired;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getVerb() {
		return verb;
	}

	public void setVerb(String verb) {
		this.verb = verb;
	}

	public String getResourceOwner() {
		return resourceOwner;
	}

	public void setResourceOwner(String resourceOwner) {
		this.resourceOwner = resourceOwner;
	}

	public Boolean getOtpRequired() {
		return otpRequired;
	}

	public void setOtpRequired(Boolean otpRequired) {
		this.otpRequired = otpRequired;
	}
	
}
