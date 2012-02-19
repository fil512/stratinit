package com.kenstevens.stratinit.server.remote.request;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.dao.MessageDao;
import com.kenstevens.stratinit.dto.SIMessage;
import com.kenstevens.stratinit.server.remote.helper.PlayerMessageList;

@Scope("prototype")
@Component
public class GetSentMailRequest extends PlayerRequest<List<SIMessage>> {
	@Autowired
	private MessageDao messageDao;
	@Autowired
	private PlayerMessageList playerMessageList;

	@Override
	protected List<SIMessage> execute() {
		return playerMessageList.messagesToSIMessages(messageDao.getSentMail(getNation()));
	}
}
