package com.kenstevens.stratinit.server.remote;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.common.collect.Lists;

public class AuthenticationHelper {

	private final class MockAuthentication implements Authentication {
		private static final long serialVersionUID = 1L;
		private final String username;

		private MockAuthentication(String username) {
			this.username = username;
		}

		@Override
		public Collection<GrantedAuthority> getAuthorities() {
			Collection<GrantedAuthority> retval = Lists.newArrayList();
			retval.add(new GrantedAuthorityImpl("ROLE_ADMIN"));
			return retval;
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
