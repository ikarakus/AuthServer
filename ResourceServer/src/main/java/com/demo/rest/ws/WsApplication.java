package com.demo.rest.ws;

import java.util.List;

import com.demo.common.domain.customer.wl.WhiteLabeler;
import com.revoframework.module.cms.GenericApplication;
import com.revoframework.module.cms.UserSessionFactory;

public class WsApplication extends GenericApplication {

	@Override
	public void findWhiteLabelerConf() {
		setCid(WhiteLabeler.DEMO);
	}

	@Override
	public UserSessionFactory getUserSessionFactory() {
		return null;
	}

	@Override
	public List buildGui() {
		return null;
	}

}
