package com.kenstevens.stratinit.site.command;

import com.kenstevens.stratinit.dto.SIMessage;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.processor.MailProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class GetSentMailCommand extends Command<List<SIMessage>> {
	@Autowired
	private MailProcessor mailProcessor;
	@Autowired
	private Data db;

	@Override
	public Result<List<SIMessage>> execute() {
		return stratInit.getSentMail();
	}

	@Override
	public String getDescription() {
		return "Get Sent Mail";
	}

	@Override
	public void handleSuccess(List<SIMessage> messages) {
		mailProcessor.process(db.getSentItems(), messages);
	}
}
