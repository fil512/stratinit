package com.kenstevens.stratinit.client.site.command.post;

import com.kenstevens.stratinit.client.model.Data;
import com.kenstevens.stratinit.client.model.Mail;
import com.kenstevens.stratinit.client.model.NationView;
import com.kenstevens.stratinit.client.site.PostCommand;
import com.kenstevens.stratinit.dto.SIMessage;
import com.kenstevens.stratinit.remote.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class SendMessageCommand extends PostCommand<Integer, SIMessage> {
	@Autowired
	Data db;

	public SendMessageCommand(Mail message) {
		super(new SIMessage(message), buildDescription(message));
	}

	@Override
	public Result<Integer> executePost(SIMessage request) {
        return stratInitServer.sendMessage(request);
    }

	public static String buildDescription(Mail message) {
		return "Send Message to "+message.getRecipient();
	}

	@Override
	public void handleSuccess(Integer messageId) {
	}

	public String getRecipient() {
		NationView nation = db.getNation(getRequest().toNationId);
		return nation == null ? "everyone" : nation.getName();
	}
}
