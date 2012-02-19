package com.kenstevens.stratinit.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.model.Account;
import com.kenstevens.stratinit.ui.window.WindowDirector;

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
