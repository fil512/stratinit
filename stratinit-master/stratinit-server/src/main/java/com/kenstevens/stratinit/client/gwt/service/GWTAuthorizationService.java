package com.kenstevens.stratinit.client.gwt.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.kenstevens.stratinit.client.gwt.callback.ServiceSecurityException;
import com.kenstevens.stratinit.client.gwt.model.Authorization;

@RemoteServiceRelativePath("authorization.rpc")
public interface GWTAuthorizationService extends RemoteService {
	public Authorization getAuthorization() throws ServiceSecurityException;
	public GWTResult<String> login(String username, String password);
	GWTNone logout();
}