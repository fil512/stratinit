package com.kenstevens.stratinit.client.site.action.post;

import com.kenstevens.stratinit.client.model.Mail;
import com.kenstevens.stratinit.client.shell.StatusReporter;
import com.kenstevens.stratinit.client.site.Action;
import com.kenstevens.stratinit.client.site.command.SendMessageCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class SendMessageAction extends Action<SendMessageCommand> {
	private final Mail message;
	@Autowired
	private StatusReporter statusReporter;
	@Autowired
	private ActionFactory actionFactory;

	public SendMessageAction(Mail message) {
		this.message = message;
	}

	protected SendMessageCommand buildCommand() {
		return new SendMessageCommand(message);
	}

	@Override
	public void postRequest() {
		String recipient = message.getRecipient();
		statusReporter.reportResult("message to " + recipient + " sent.");
		// TODO need this? shouldn't we just refresh?
		if (message.getTo() == null) {
			actionFactory.readMessageBoard();
		}
		actionFactory.getSentMessages();
	}
}