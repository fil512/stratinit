package com.kenstevens.stratinit.site.action;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.model.Mail;
import com.kenstevens.stratinit.shell.StatusReporter;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.command.SendMessageCommand;
import com.kenstevens.stratinit.util.Spring;

@Scope("prototype")
@Component
public class SendMessageAction extends Action {
	private final Mail message;
	@Autowired
	private Spring spring;
	private SendMessageCommand sendMessageCommand;
	@Autowired
	private StatusReporter statusReporter;
	@Autowired
	private ActionFactory actionFactory;

	public SendMessageAction(Mail message) {
		this.message = message;
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void initialize() {
		sendMessageCommand = spring.autowire(new SendMessageCommand( message ));
	}

	@Override
	public Command<? extends Object> getCommand() {
		return sendMessageCommand;
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