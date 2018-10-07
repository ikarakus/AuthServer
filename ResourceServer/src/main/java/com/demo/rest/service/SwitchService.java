package com.demo.rest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.rest.entity.OauthUriScoreMap;
import com.revoframework.exception.DatabaseException;
import com.revoframework.service.RevoGenericService;

@Service
public class SwitchService {

	@Autowired
	private RevoGenericService genericService;
	
	public List<OauthUriScoreMap> getOauthUriScoreMapList() throws DatabaseException {
		return genericService.findAll(OauthUriScoreMap.class);
	}

}
