package com.kenstevens.stratinit.ui.messages;


import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.model.Mail;
import com.kenstevens.stratinit.site.action.ActionFactory;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.TableItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public abstract class MessageViewerControl {
	@Autowired
	private MessageWindow messageWindow;
	@Autowired
	protected Data db;

	@Autowired
	protected ActionFactory actionFactory;

	private final MessageViewer messageViewer;

	public MessageViewerControl(MessageViewer messageViewer) {
		this.messageViewer = messageViewer;
	}

	protected final Mail getSelectedMessage() {
		TableItem[] tableItems = messageViewer.getTable().getSelection();
		if (tableItems == null || tableItems.length == 0) {
			return null;
		}
		return (Mail)tableItems[0].getData();
	}

	protected boolean isSent() {
		return false;
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private final void setButtonListeners() {
		final Button replyButton = messageViewer.getReplyButton();
		replyButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				MessageBoxer messageBoxer = new MessageBoxer(getSelectedMessage(), isSent());
				messageWindow.openComposeWindow(reply(messageBoxer));
			}
		});
		final Button composeButton = messageViewer.getComposeButton();
		composeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				messageWindow.openComposeWindow(null);
			}
		});
	}

	protected abstract Mail reply(MessageBoxer messageBoxer);
}