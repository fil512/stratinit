package com.kenstevens.stratinit.client.site.command.post;

import com.kenstevens.stratinit.client.api.IStatusReporter;
import com.kenstevens.stratinit.client.site.PostCommand;
import com.kenstevens.stratinit.client.util.StackTraceHelper;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.ErrorJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class SubmitErrorCommand extends PostCommand<Integer, ErrorJson> {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	IStatusReporter statusReporter;

	private final Exception exception;

	public SubmitErrorCommand(String username, Integer gameId, Exception exception) {
		super(new ErrorJson("Stratinit Player Exception " + username + " " +gameId, StackTraceHelper.getStackTrace(exception)), buildDescription(exception));
		this.exception = exception;
	}

	@Override
	public Result<Integer> executePost(ErrorJson request) {
		Result<Integer> retval;
		try {
			retval = stratInitServer.submitError(request);
		} catch (Exception e) {
			logger.error(exception.getMessage(), exception);
			logger.error(e.getMessage(), e);
			retval = new Result<>(exception.getMessage(), false, -1);
		}
		return retval;
	}

	public static String buildDescription(Exception exception) {
		return "Submitting " + exception.getMessage() + " Error.";
	}

	@Override
	public void handleSuccess(Integer errno) {
		statusReporter.reportError("Error #"+errno+" logged.  When reporting this error, please make reference to this error number and what you were doing at the time.  Thanks!");
	}
}
