package com.kenstevens.stratinit.server.remote;

import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.context.SecurityContextHolder;

public class AuthenticationHelper {

	private final class MockAuthentication implements Authentication {
		private static final long serialVersionUID = 1L;
		private final String username;

		private MockAuthentication(String username) {
			this.username = username;
		}

		@Override
		public GrantedAuthority[] getAuthorities() {
			return new GrantedAuthority[0];
		}

		@Override
		public Object getCredentials() {
			return null;
		}

		@Override
		public Object getDetails() {
			return null;
		}

		@Override
		public Object getPrincipal() {
			return null;
		}

		@Override
		public boolean isAuthenticated() {
			return false;
		}

		@Override
		public void setAuthenticated(boolean isAuthenticated) {
		}

		@Override
		public String getName() {
			return username;
		}
	}

	public
	void setAuthentication(final String username) {
		Authentication authentication = new MockAuthentication(username);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
