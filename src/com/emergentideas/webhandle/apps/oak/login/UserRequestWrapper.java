package com.emergentideas.webhandle.apps.oak.login;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.emergentideas.webhandle.assumptions.oak.interfaces.User;

public class UserRequestWrapper extends HttpServletRequestWrapper {
	
	protected HttpServletRequest innerRequest;
	protected User user;
	protected String authType;
	
	public UserRequestWrapper(HttpServletRequest req, User user, String authType)
	{
		super(req);
		innerRequest = req;
		this.user = user;
		this.authType = authType;
	}

	public HttpServletRequest getInnerRequest() {
		return innerRequest;
	}

	public void setInnerRequest(HttpServletRequest innerRequest) {
		this.innerRequest = innerRequest;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String getAuthType() {
		if(authType != null) {
			return authType;
		}
		
		return super.getAuthType();
	}

	public void setAuthType(String authType) {
		this.authType = authType;
	}

	@Override
	public Principal getUserPrincipal() {
		return new Principal() {
			@Override
			public String getName() {
				return user.getProfileName();
			}
		};
	}

	@Override
	public boolean isUserInRole(String role) {
		if(user == null) {
			return super.isUserInRole(role);
		}
		
		return user.getGroupNames().contains(role);
	}

	@Override
	public String getRemoteUser() {
		if(user != null) {
			return user.getProfileName();
		}
		return super.getRemoteUser();
	}
	
	
	
}
