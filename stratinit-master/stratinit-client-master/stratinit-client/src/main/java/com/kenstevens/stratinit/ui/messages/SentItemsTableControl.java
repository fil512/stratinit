package com.kenstevens.stratinit.ui.messages;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.model.Message;
import com.kenstevens.stratinit.model.MessageList;


@Scope("prototype")
@Component
public class SentItemsTableControl extends MailboxTableControl  {
	public SentItemsTableControl(Table table) {
		super(table);
	}

	protected MessageList getMessageList() {
		return db.getSentItems();
	}

	@Override
	protected void setBoldIfUnread(Message message, TableItem item) {
		// Do nothing
	}

	@Override
	protected String getPlayerName(Message message) {
		return message.getRecipient();
	}
}
