package com.kenstevens.stratinit.site.server;

import com.kenstevens.stratinit.model.Account;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.httpinvoker.SimpleHttpInvokerRequestExecutor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;

@Service
public class BasicAuthenticationCommonsHttpInvokerRequestExecutor extends SimpleHttpInvokerRequestExecutor {
	@Autowired
	private Account account;

	@Override
	protected void prepareConnection(HttpURLConnection connection, int contentLength) throws IOException {
		String base64 = account.getUsername() + ":" + account.getPassword();
		connection.setRequestProperty("Authorization", "Basic " + new String(Base64.encodeBase64(base64.getBytes())));
		super.prepareConnection(connection, contentLength);
	}
}
