package com.kenstevens.stratinit.client.ui.messages;

import com.kenstevens.stratinit.client.model.Message;
import com.kenstevens.stratinit.client.model.MessageList;
import org.eclipse.swt.widgets.Table;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
