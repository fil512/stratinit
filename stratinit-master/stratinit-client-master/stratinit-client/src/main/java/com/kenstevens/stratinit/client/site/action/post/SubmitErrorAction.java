package com.kenstevens.stratinit.client.site.action.post;

import com.kenstevens.stratinit.client.site.ErrorSubmitter;
import com.kenstevens.stratinit.client.site.PostAction;
import com.kenstevens.stratinit.client.site.command.post.SubmitErrorCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class SubmitErrorAction extends PostAction<SubmitErrorCommand> implements ErrorSubmitter {
	@Autowired
	private ActionFactory actionFactory;

	public SubmitErrorAction(String username, Integer gameId, Exception exception) {
		super(new SubmitErrorCommand(username, gameId, exception));
	}

	@Override
	public void submitError(Exception e) {
		actionFactory.submitError(e);
	}
}