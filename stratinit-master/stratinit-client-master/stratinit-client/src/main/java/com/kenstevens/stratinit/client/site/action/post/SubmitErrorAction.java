package com.kenstevens.stratinit.client.site.action.post;

import com.kenstevens.stratinit.client.site.Action;
import com.kenstevens.stratinit.client.site.ErrorSubmitter;
import com.kenstevens.stratinit.client.site.command.SubmitErrorCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class SubmitErrorAction extends Action<SubmitErrorCommand> implements ErrorSubmitter {
	@Autowired
	private ActionFactory actionFactory;
	private final Exception exception;

	public SubmitErrorAction(Exception e) {
		this.exception = e;
	}

	protected SubmitErrorCommand buildCommand() {
		return new SubmitErrorCommand(exception);
	}

	@Override
	public void submitError(Exception e) {
		actionFactory.submitError(e);
	}
}