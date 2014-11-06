package com.emergentideas.webhandle.apps.oak.login;

import java.io.UnsupportedEncodingException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import com.emergentideas.webhandle.Location;
import com.emergentideas.webhandle.ParameterMarshal;
import com.emergentideas.webhandle.PreRequest;
import com.emergentideas.webhandle.ValueSource;
import com.emergentideas.webhandle.assumptions.oak.Constants;
import com.emergentideas.webhandle.assumptions.oak.interfaces.AuthenticationService;
import com.emergentideas.webhandle.assumptions.oak.interfaces.User;

public class UserInformationSourceInterceptor {
	
	@Resource
	protected AuthenticationService authenticationService;
	
	@PreRequest
	public void setupUserInformationSource(ParameterMarshal marshal, Location location, HttpServletRequest request) throws Exception {
		User user = null;
		String authType = null;
		
		user = getUserFromBasicAuth(request.getHeader("Authorization"));
		if(user != null) {
			authType = HttpServletRequest.BASIC_AUTH;
			
			// This should put it into the request location object
			location.put(Constants.CURRENT_USER_OBJECT, user);
		}
		
		if(user == null) {
			// This should check for the user in the session location
			user = (User)location.get(Constants.CURRENT_USER_OBJECT);
			authType = HttpServletRequest.FORM_AUTH;
		}
		
		ValueSource<?> vs = new UserInformationValueSource(user);
		marshal.addSource(Constants.USER_INFORMATION_SOURCE_NAME, vs);
		
		if(user != null) {
			UserRequestWrapper wrapper = new UserRequestWrapper(request, user, authType);
			marshal.getContext().setFoundParameter(HttpServletRequest.class, wrapper);
		}
	}
	
	protected User getUserFromBasicAuth(String headerValue) throws UnsupportedEncodingException {
		if(isBasicAuthHeader(headerValue)) {
			String[] credentials = decryptBasicAuthHeader(headerValue);
			if(credentials != null) {
				if(isUserAuthenticated(credentials[0], credentials[1])) {
					return getUser(credentials[0]);
				}
			}
		}
		
		return null;
	}
	
	protected boolean isBasicAuthHeader(String headerValue) {
		return StringUtils.isNotBlank(headerValue) && headerValue.startsWith("Basic ");
	}
	
	protected User getUser(String name) {
		return authenticationService.getUserByProfileName(name);
	}
	
	protected boolean isUserAuthenticated(String name, String password) {
		if(authenticationService != null) {
			try {
				return authenticationService.isAuthenticated(name, password);
			}
			catch(UnsupportedOperationException e) {
				// May be a problem with there being no db. Just return false
				return false;
			}
		}
		return false;
	}
	
	/**
	 * Takes an Authorization header value, strips the Basic content type and decrypts the
	 * user name and password.
	 * @param headerValue
	 * @return a two member array [user, pass] or null if a problem occurred. 
	 */
	protected String[] decryptBasicAuthHeader(String headerValue) throws UnsupportedEncodingException {
		headerValue = headerValue.substring(6);
		String credentials = new String(Base64.decodeBase64(headerValue.getBytes("UTF-8")), "UTF-8");
		
		int sep = credentials.indexOf(":");
		if(sep > 1) {
			String name = credentials.substring(0, sep);
			String pass = credentials.substring(sep + 1);
			return new String[] { name, pass };
		}
		
		return null;
	}

}
