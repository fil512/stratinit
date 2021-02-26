package com.kenstevens.stratinit.site.command;

import com.kenstevens.stratinit.model.Account;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.shell.StatusReporter;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.util.StackTraceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class SubmitErrorCommand extends Command<Integer> {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	Account account;
	@Autowired
	Data data;
	@Autowired
	StatusReporter statusReporter;

	private final Exception exception;

	public SubmitErrorCommand(Exception e) {
		this.exception = e;
	}

	@Override
	public Result<Integer> execute() {
		Result<Integer> retval = null;
		try {
            String subject = "Stratinit Player Exception "
                    + account.getUsername() + " " + data.getSelectedGameId();
            retval = stratInitServer.submitError(subject, StackTraceHelper.getStackTrace(exception));
        } catch (Exception e) {
			logger.error(exception.getMessage(), exception);
			logger.error(e.getMessage(), e);
			retval = new Result<Integer>(exception.getMessage(), false, -1);
		}
		return retval;
	}

	@Override
	public String getDescription() {
		return "Submitting " + exception.getMessage() + " Error.";
	}

	@Override
	public void handleSuccess(Integer errno) {
		statusReporter.reportError("Error #"+errno+" logged.  When reporting this error, please make reference to this error number and what you were doing at the time.  Thanks!");
	}
}
