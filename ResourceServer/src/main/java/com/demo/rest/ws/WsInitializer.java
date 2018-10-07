package com.demo.rest.ws;

import org.springframework.transaction.annotation.Transactional;

public class WsInitializer {

	@Transactional
	private void init() {

//		FirewallManager.getInstance().addGenericFirewallEventHandler(new WsLoginFailureHandler());
		System.out.println("WsInitializer init done");
	}

}
