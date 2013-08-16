package com.emergentideas.webhandle.apps.oak.login;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

import com.emergentideas.webhandle.assumptions.oak.interfaces.AuthenticationService;
import com.emergentideas.webhandle.assumptions.oak.interfaces.User;

import static junit.framework.Assert.*;

public class UserInformationSourceInterceptorTest {
	
	@InjectMocks
	protected UserInformationSourceInterceptor inter = new UserInformationSourceInterceptor();

	@Mock
	protected AuthenticationService authenticationService;
	
	
	protected String basicAuthHeader = "Basic dXNlcm5hbWU6cGFzc3dvcmQ="; // username:password
	protected String unauthorizedAuthHeader = "Basic dXNlcm5hbWUyOnBhc3N3b3Jk"; // username2:password
	
	
	public UserInformationSourceInterceptorTest() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testBasicDecrypt() throws Exception {
		String[] cred = inter.decryptBasicAuthHeader(basicAuthHeader);
		assertEquals("username", cred[0]);
		assertEquals("password", cred[1]);
	}
	
	@Test
	public void testFindUser() throws Exception {
		when(authenticationService.isAuthenticated(anyString(), anyString())).thenReturn(false);
		
		when(authenticationService.isAuthenticated("username", "password")).thenReturn(true);
		
		assertTrue(authenticationService.isAuthenticated("username", "password"));
		assertFalse(authenticationService.isAuthenticated("username", "password2"));
		
		when(authenticationService.getUserByProfileName(anyString())).thenReturn(null);
		OakUser user = new OakUser();
		user.setProfileName("username");
		when(authenticationService.getUserByProfileName("username")).thenReturn(user);
		
		
		
		
		User foundUser = inter.getUserFromBasicAuth(basicAuthHeader);
		assertEquals("username", foundUser.getProfileName());
		
		assertNull(inter.getUserFromBasicAuth(unauthorizedAuthHeader));
		
	}
}
