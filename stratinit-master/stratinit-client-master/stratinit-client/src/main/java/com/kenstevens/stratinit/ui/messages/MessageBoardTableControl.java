package com.kenstevens.stratinit.ui.messages;

import org.eclipse.swt.widgets.Table;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.model.Message;
import com.kenstevens.stratinit.model.MessageList;


@Scope("prototype")
@Component
public class MessageBoardTableControl extends MailboxTableControl  {
	public MessageBoardTableControl(Table table) {
		super(table);
	}

	protected MessageList getMessageList() {
		return db.getMessageBoard();
	}

	@Override
	protected String getPlayerName(Message message) {
		return message.getAuthor();
	}
}
