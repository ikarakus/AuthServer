package com.demo.oauth2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;

import com.demo.oauth2.service.AccountUserDetailsService;

@EnableAuthorizationServer
@Configuration
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

//	private final AuthenticationManager authenticationManager;
//	private final AppConfig appConfig;

	@Autowired
    private AuthenticationManager authenticationManager;
	
	@Autowired
	private AppConfig appConfig;
	
	@Autowired
    private AccountUserDetailsService userDetailsService;
	
	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

//	@Autowired
//	public AuthServerOAuth2Config(AuthenticationManager authenticationManager, AppConfig appConfig) {
//		this.authenticationManager = authenticationManager;
//		this.appConfig = appConfig;
//	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.jdbc(appConfig.dataSource());
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		/*
		 * Allow our tokens to be delivered from our token access point as well as for
		 * tokens to be validated from this point
		 */
//		security.passwordEncoder(passwordEncoder);
		security.checkTokenAccess("permitAll()");
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.authenticationManager(authenticationManager).tokenStore(appConfig.tokenStore()).reuseRefreshTokens(false).userDetailsService(userDetailsService); 
																									
	}
}
