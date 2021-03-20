package com.kenstevens.stratinit.client.ui;

import com.kenstevens.stratinit.client.model.Account;
import com.kenstevens.stratinit.client.ui.window.WindowDirector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NewbHelper {
	@Autowired
	private Account account;
	@Autowired
	private WindowDirector windowDirector;

	public void openNextWindow() {
		if (account.getUsername().isEmpty()) {
			windowDirector.accountSettingsWindow();
		} else {
			windowDirector.chooseGameWindow();
		}
	}
}
