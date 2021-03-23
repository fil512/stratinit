package com.kenstevens.stratinit.client.site.command.get;

import com.kenstevens.stratinit.client.model.Data;
import com.kenstevens.stratinit.client.site.GetCommand;
import com.kenstevens.stratinit.client.site.processor.MailProcessor;
import com.kenstevens.stratinit.dto.SIMessage;
import com.kenstevens.stratinit.remote.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class ReadMessageBoardCommand extends GetCommand<List<SIMessage>> {
	@Autowired
	private MailProcessor mailProcessor;
	@Autowired
	private Data db;

	@Override
	public Result<List<SIMessage>> execute() {
        return stratInitServer.getAnnouncements();
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
