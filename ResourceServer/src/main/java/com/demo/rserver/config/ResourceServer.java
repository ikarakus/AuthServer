package com.demo.rserver.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;

import com.demo.rest.entity.OauthUriScoreMap;
import com.demo.rest.service.SwitchService;


@EnableResourceServer
@ComponentScan({"com.demo.rest.controller","com.demo.rserver.config"})
@Configuration
public class ResourceServer extends ResourceServerConfigurerAdapter {
	
	private final String AS_URL = "http://localhost:8181/DemoWS_Auth";
	
	@Autowired
	private SwitchService switchService;

	@Bean
	public RemoteTokenServices remoteTokenServices() {
		final RemoteTokenServices tokenServices = new RemoteTokenServices();
		tokenServices.setCheckTokenEndpointUrl(AS_URL + "/oauth/check_token");
		tokenServices.setClientId("resource");
		tokenServices.setClientSecret("resource");
		return tokenServices;
	}

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.tokenServices(remoteTokenServices());
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();

		List<OauthUriScoreMap> uriScoreList = switchService.getOauthUriScoreMapList();
		if (uriScoreList != null && !uriScoreList.isEmpty()) {
			/* if uri has multiple scopes, helperMap concatenates the scopes with "or" keyword */
			Map<String, String> helperMap = new HashMap<>();
			for (OauthUriScoreMap scoreMap : uriScoreList) {
				String uri = scoreMap.getUri().trim();
				String scope = scoreMap.getScope().trim();
				if (helperMap.get(uri) == null) {
					helperMap.put(uri, "#oauth2.hasScope('" + scope + "')");
				} else {
					helperMap.put(uri, helperMap.get(uri) + " or #oauth2.hasScope('" + scope + "')");
				}
			}
			for (OauthUriScoreMap scoreMap : uriScoreList) {
				String uri = scoreMap.getUri().trim();
				String scope = helperMap.get(uri);
				http.authorizeRequests().antMatchers(scoreMap.getVerb(), uri).access(scope);
			}
		}
		http.authorizeRequests().anyRequest().authenticated();		
//		http.authorizeRequests().antMatchers(HttpMethod.GET, "/Cobrander/Customer/Information").access("#oauth2.hasScope('EXTERNAL_COMPLIANCE2')");	
	
	}

}
