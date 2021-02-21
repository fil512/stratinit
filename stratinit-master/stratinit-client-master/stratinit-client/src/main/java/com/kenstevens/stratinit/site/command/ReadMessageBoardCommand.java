package com.kenstevens.stratinit.site.command;

import com.kenstevens.stratinit.dto.SIMessage;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.remote.SIResponseEntity;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.processor.MailProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class ReadMessageBoardCommand extends Command<List<SIMessage>> {
	@Autowired
	private MailProcessor mailProcessor;
	@Autowired
	private Data db;

	@Override
	public SIResponseEntity<List<SIMessage>> execute() {
		return stratInit.getAnnouncements();
	}

	@Override
	public String getDescription() {
		return "Read Message Board";
	}

	@Override
	public void handleSuccess(List<SIMessage> messages) {
		mailProcessor.process(db.getMessageBoard(), messages);
	}
}
