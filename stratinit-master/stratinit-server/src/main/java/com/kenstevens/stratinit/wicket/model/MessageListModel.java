package com.kenstevens.stratinit.wicket.model;

import java.util.List;

import org.apache.wicket.model.LoadableDetachableModel;

import com.kenstevens.stratinit.model.Mail;
import com.kenstevens.stratinit.wicket.provider.MessageListProvider;

public class MessageListModel extends LoadableDetachableModel<List<Mail>> {
	private static final long serialVersionUID = 1L;

	private final MessageListProvider messageListProvider;

	private final int gameId;

	public MessageListModel(MessageListProvider messageListProvider, int gameId) {
		this.messageListProvider = messageListProvider;
		this.gameId = gameId;
	}

	@Override
	protected List<Mail> load() {
		return messageListProvider.getMessages(gameId);
	}

}
