package com.kenstevens.stratinit.wicket.games;

import java.util.List;

import org.apache.wicket.model.LoadableDetachableModel;

import com.kenstevens.stratinit.model.Mail;

public class MessageListModel extends LoadableDetachableModel<List<Mail>> {
	private static final long serialVersionUID = 1L;

	private final MessageBoardPage messageBoardPage;

	public MessageListModel(MessageBoardPage messageBoardPage) {
		this.messageBoardPage = messageBoardPage;
	}

	@Override
	protected List<Mail> load() {
		return messageBoardPage.getMessages();
	}

}
