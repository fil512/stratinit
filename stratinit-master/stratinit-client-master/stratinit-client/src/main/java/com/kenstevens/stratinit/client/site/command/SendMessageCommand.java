package com.kenstevens.stratinit.client.site.command;

import com.kenstevens.stratinit.client.model.Mail;
import com.kenstevens.stratinit.client.site.Command;
import com.kenstevens.stratinit.dto.SIMessage;
import com.kenstevens.stratinit.remote.Result;
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
	public Result<Integer> execute() {
        return stratInitServer.sendMessage(new SIMessage(message));
    }

	@Override
	public String getDescription() {
		return "Send Message to "+message.getRecipient();
	}

	@Override
	public void handleSuccess(Integer messageId) {
	}
}
