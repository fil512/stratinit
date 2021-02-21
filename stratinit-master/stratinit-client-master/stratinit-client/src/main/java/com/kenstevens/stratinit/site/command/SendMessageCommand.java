package com.kenstevens.stratinit.site.command;

import com.kenstevens.stratinit.dto.SIMessage;
import com.kenstevens.stratinit.model.Mail;
import com.kenstevens.stratinit.remote.SIResponseEntity;
import com.kenstevens.stratinit.site.Command;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class SendMessageCommand extends Command<Integer> {
	private final Mail message;

	public SendMessageCommand(Mail message) {
		this.message = message;
	}

	@Override
	public SIResponseEntity<Integer> execute() {
		return stratInit.sendMessage(new SIMessage(message));
	}

	@Override
	public String getDescription() {
		return "Send Message to "+message.getRecipient();
	}

	@Override
	public void handleSuccess(Integer messageId) {
	}
}
