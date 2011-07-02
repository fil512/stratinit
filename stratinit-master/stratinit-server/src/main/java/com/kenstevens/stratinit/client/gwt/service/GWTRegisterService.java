package com.kenstevens.stratinit.client.gwt.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("register.rpc")
public interface GWTRegisterService extends RemoteService {

	GWTResult<GWTNone> register(String username, String password, String email);

	GWTResult<GWTNone> forgottenPassword(String username, String email);
}