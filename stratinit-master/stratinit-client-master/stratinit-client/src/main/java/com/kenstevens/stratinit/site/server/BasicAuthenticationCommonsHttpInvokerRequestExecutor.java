package com.kenstevens.stratinit.site.server;

import com.kenstevens.stratinit.model.Account;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.methods.PostMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.httpinvoker.CommonsHttpInvokerRequestExecutor;
import org.springframework.remoting.httpinvoker.HttpInvokerClientConfiguration;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class BasicAuthenticationCommonsHttpInvokerRequestExecutor extends CommonsHttpInvokerRequestExecutor {
	@Autowired
	private Account account;

	@Override
	protected PostMethod createPostMethod(HttpInvokerClientConfiguration config) throws IOException {
		PostMethod postMethod = super.createPostMethod(config);

		String base64 = account.getUsername() + ":" + account.getPassword();
		postMethod.setRequestHeader("Authorization", "Basic " + new String(Base64.encodeBase64(base64.getBytes())));
		return postMethod;
	}
}
