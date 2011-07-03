package com.kenstevens.stratinit.site.command;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.model.Account;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.site.Command;

@Scope("prototype")
@Component
public class SubmitErrorCommand extends Command<None> {
	private final Logger logger = Logger.getLogger(getClass());
	@Autowired
	Account account;
	@Autowired
	Data data;

	private final Exception exception;

	public SubmitErrorCommand(Exception e) {
		this.exception = e;
	}

	@Override
	public Result<None> execute() {
		Result<None> retval = null;
		try {
			String subject = "Stratinit Player Exception "
					+ account.getUsername() + " " + data.getSelectedGameId();
			retval  = stratInit.submitError(subject, exception);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return retval;
	}

	@Override
	public String getDescription() {
		return "Submitting " + exception.getMessage() + " Error.";
	}

	@Override
	public void handleSuccess(None result) {
	}
}
