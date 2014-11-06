package com.emergentideas.webhandle.apps.oak.login;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.emergentideas.webhandle.ExceptionHandler;
import com.emergentideas.webhandle.Location;
import com.emergentideas.webhandle.Wire;
import com.emergentideas.webhandle.assumptions.oak.RequestMessages;
import com.emergentideas.webhandle.exceptions.UnauthorizedAccessException;
import com.emergentideas.webhandle.exceptions.UserRequiredException;
import com.emergentideas.webhandle.output.Show;
import com.emergentideas.webhandle.output.Template;
import com.emergentideas.webhandle.output.Wrap;

public class LoginExceptionHandle {

	protected LoginHandle loginHandle;
	
	@ExceptionHandler(UserRequiredException.class)
	@Template
	@Wrap("public_page")
	public Object caughtUserRequiredException(Location location, RequestMessages messages, Exception exception) throws UnsupportedEncodingException {
		messages.getInfoMessages().add("It looks like you've got to log in to access that page.");
		messages.persistMessages();
		
		
		String url = ((UserRequiredException)exception).getRequestedURL();
		
		
		return new Show("/login?forward=" + URLEncoder.encode(url, "UTF-8"));
	}
	
	
	@ExceptionHandler(UnauthorizedAccessException.class)
	@Template
	@Wrap("public_page")
	public Object caughtUnauthorizedAccessException() {
		return "login/accessDenied";
	}

	public LoginHandle getLoginHandle() {
		return loginHandle;
	}

	@Wire
	public void setLoginHandle(LoginHandle loginHandle) {
		this.loginHandle = loginHandle;
	}
	
	
}
