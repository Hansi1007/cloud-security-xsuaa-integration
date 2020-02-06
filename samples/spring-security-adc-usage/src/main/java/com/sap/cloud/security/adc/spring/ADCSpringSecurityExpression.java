package com.sap.cloud.security.adc.spring;

import com.sap.cloud.security.adc.client.OpenPolicyAgentRequest;
import com.sap.cloud.security.adc.client.ADCService;
import com.sap.cloud.security.xsuaa.token.SpringSecurityContext;
import com.sap.cloud.security.xsuaa.token.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * TODO: extract as library
 */
public class ADCSpringSecurityExpression extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

	private ADCService service;
	private URI adcUri;

	private Logger logger = LoggerFactory.getLogger(getClass());

	public ADCSpringSecurityExpression(Authentication authentication) {
		super(authentication);
		setTrustResolver(new AuthenticationTrustResolverImpl());
	}

	public ADCSpringSecurityExpression withOpenPolicyAgentService(ADCService service) {
		this.service = service;
		return this;
	}

	public ADCSpringSecurityExpression withAdcUri(URI adcUri) {
		this.adcUri = adcUri;
		return this;
	}

	//    public String getScopeExpression(String localScope) {
	//        // http://docs.spring.io/spring-security/oauth/apidocs/org/springframework/security/oauth2/provider/expression/OAuth2SecurityExpressionMethods.html
	//        return "#oauth2.hasScope('" + getGlobalScope(localScope) + "')";
	//    }

	public boolean readAll(String action) {
		// TODO IAS Support

		Token token = SpringSecurityContext.getToken();

		OpenPolicyAgentRequest request = new OpenPolicyAgentRequest("readAll", token.getEmail()) // TODO update to getUserName()?
						.withAction(action);
		boolean isAuthorized = checkAuthorization(request);
		logger.info("Has user {} authorization with policy '{}' and action '{}' ? {}", token.getEmail(), "readAll", action, isAuthorized);

		return isAuthorized;
	}

	private boolean checkAuthorization(OpenPolicyAgentRequest request) {
		URI adcUri = expandPath(this.adcUri, "/v1/data/rbac/allow");

		try {
			return service.isUserAuthorized(adcUri, request).getResult();
		} catch (Exception e) { // TODO improve
			logger.error("Error accessing ADC service.", e);
		}
		return false;
	}

	@Override public void setFilterObject(Object o) {
	}

	@Override public Object getFilterObject() {
		return null;
	}

	@Override public void setReturnObject(Object o) {
	}

	@Override public Object getReturnObject() {
		return null;
	}

	@Override public Object getThis() {
		return null;
	}

	// TODO replace with UriUtil.expandPath
	private URI expandPath(URI uri, String pathToAppend) {
		try {
			String newPath = uri.getPath() + pathToAppend;
			return new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(),
					newPath, uri.getQuery(), uri.getFragment());
		} catch (URISyntaxException e) {
			logger.error("Could not set path {} in given uri {}", pathToAppend, uri);
			throw new IllegalStateException(e);
		}
	}
}
