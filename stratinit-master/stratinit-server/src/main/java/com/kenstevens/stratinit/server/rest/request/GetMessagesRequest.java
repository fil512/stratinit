package com.kenstevens.stratinit.server.rest.request;

import com.kenstevens.stratinit.dao.MessageDao;
import com.kenstevens.stratinit.dto.SIMessage;
import com.kenstevens.stratinit.server.rest.svc.PlayerMessageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class GetMessagesRequest extends PlayerRequest<List<SIMessage>> {
	@Autowired
	private MessageDao messageDao;
	@Autowired
	private PlayerMessageList playerMessageList;

	@Override
	protected List<SIMessage> execute() {
		return playerMessageList.messagesToSIMessages(messageDao.getMessages(getNation()));
	}
}
