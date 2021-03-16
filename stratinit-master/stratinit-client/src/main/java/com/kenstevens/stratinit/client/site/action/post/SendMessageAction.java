package com.kenstevens.stratinit.client.site.action.post;

import com.kenstevens.stratinit.client.api.IStatusReporter;
import com.kenstevens.stratinit.client.model.Mail;
import com.kenstevens.stratinit.client.site.PostAction;
import com.kenstevens.stratinit.client.site.command.post.SendMessageCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class SendMessageAction extends PostAction<SendMessageCommand> {
	@Autowired
	private IStatusReporter statusReporter;
	@Autowired
	private ActionFactory actionFactory;

	public SendMessageAction(Mail message) {
		super(new SendMessageCommand(message));
	}

	@Override
	public void postRequest() {
		String recipient = getCommand().getRecipient();
		statusReporter.reportResult("message to " + recipient + " sent.");
		// TODO need this? shouldn't we just refresh?
		if (recipient == null) {
			actionFactory.readMessageBoard();
		}
		actionFactory.getSentMessages();
	}
}