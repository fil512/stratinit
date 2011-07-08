package com.kenstevens.stratinit.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.security.AuthenticationException;
import org.springframework.security.ui.AuthenticationEntryPoint;

/**
 * A dummy {@link AuthenticationEntryPoint} implementation.
 * 
 * @author See Wah Cheng
 * @created 6 Jan 2009
 */
public class DummyEntryPoint implements AuthenticationEntryPoint {

	public void commence(ServletRequest request, ServletResponse response, AuthenticationException e)
			throws IOException, ServletException {

		throw new IllegalStateException("This implementation is a dummy class, created purely so that "
				+ "spring security namespace tags can be used in application context, and this method should "
				+ "never be called");
	}

}
