package com.kenstevens.stratinit.client.gwt.callback;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * {@link AsyncCallback} switch recognises {@link ServiceSecurityException} and
 * handles it gracefully.
 *
 * @author See Wah Cheng
 * @created 5 Jun 2009
 */
public abstract class AutoErrorHandlingAsyncCallback<T> implements AsyncCallback<T> {
	private final LoginWindow loginWindow;

	public AutoErrorHandlingAsyncCallback(LoginWindow loginWindow) {
		this.loginWindow = loginWindow;
	}

	public final void onFailure(Throwable throwable) {
		if (throwable instanceof ServiceSecurityException) {
			loginWindow.open();
		} else {
			Window.alert(throwable.getMessage());
		}
	}
}