package com.kenstevens.stratinit.ui.messages;

import org.eclipse.swt.widgets.Table;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.model.Message;
import com.kenstevens.stratinit.model.MessageList;

@Scope("prototype")
@Component
public class InboxTableControl extends MailboxTableControl {
	public InboxTableControl(Table table) {
		super(table);
	}

	protected MessageList getMessageList() {
		return db.getInbox();
	}

	@Override
	protected String getPlayerName(Message message) {
		return message.getAuthor();
	}
}
