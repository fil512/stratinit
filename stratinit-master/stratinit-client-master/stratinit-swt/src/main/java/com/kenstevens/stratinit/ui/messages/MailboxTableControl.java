package com.kenstevens.stratinit.ui.messages;

import com.google.common.eventbus.Subscribe;
import com.kenstevens.stratinit.client.control.Controller;
import com.kenstevens.stratinit.client.event.MessageListArrivedEvent;
import com.kenstevens.stratinit.client.event.StratinitEventBus;
import com.kenstevens.stratinit.client.model.Data;
import com.kenstevens.stratinit.client.model.Message;
import com.kenstevens.stratinit.client.model.MessageList;
import com.kenstevens.stratinit.ui.tabs.TableControl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public abstract class MailboxTableControl extends TableControl implements
		Controller {

	protected final Table table;
	@Autowired
	protected Data db;
	@Autowired
	protected StratinitEventBus eventBus;
	private final Font boldTableFont;

	protected MailboxTableControl(Table table) {
		this.table = table;
		boldTableFont = getBoldTableFont(table, Display.getDefault());
	}

	@Subscribe
	public void handleMessageListArrivedEvent(MessageListArrivedEvent event) {
		updateTable(getMessageList());
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void addObservers() {
		eventBus.register(this);
	}

	protected abstract MessageList getMessageList();

	protected abstract String getPlayerName(Message message);

	protected void setBoldIfUnread(Message message, TableItem item) {
		if (!message.isRead()) {
			item.setFont(boldTableFont);
		}
	}

	protected void setRead(Message message, TableItem item) {
		message.setRead(true);
		item.setFont(table.getFont());
	}

	public void setContents() {
		updateTable(getMessageList());
	}

	private void updateTable(final MessageList messageList) {
		if (table.isDisposed())
			return;
		table.removeAll();
		for (int i = 0; i < messageList.size(); ++i) {
			Message message = messageList.get(i);
			String player = getPlayerName(message);
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(new String[] { player, message.getDateString(),
					message.getSubject() });
			if (message.getDate().after(db.getLastLoginTime())) {
				message.setRead(false);
			}
			setBoldIfUnread(message, item);
			item.setData(message);
		}
		table.redraw();
	}
}