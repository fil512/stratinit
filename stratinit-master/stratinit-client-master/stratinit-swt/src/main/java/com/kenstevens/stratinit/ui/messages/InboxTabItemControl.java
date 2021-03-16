package com.kenstevens.stratinit.ui.messages;

import com.kenstevens.stratinit.client.control.TopLevelController;
import com.kenstevens.stratinit.client.model.Mail;
import com.kenstevens.stratinit.client.model.Message;
import com.kenstevens.stratinit.client.site.action.post.ActionFactory;
import com.kenstevens.stratinit.client.util.Spring;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.TableItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class InboxTabItemControl extends MessageViewerControl implements TopLevelController {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private MessageWindow messageWindow;
	@Autowired
	private ActionFactory actionFactory;
	@Autowired
	private Spring spring;

	private final InboxTabItem inboxTabItem;
	private MailboxTableControl inboxTableControl;

	public InboxTabItemControl(InboxTabItem inboxTabItem) {
		super(inboxTabItem);
		this.inboxTabItem = inboxTabItem;
		setButtonListeners();
		setTableListeners();
	}


	public final void setTableListeners() {
		inboxTabItem.getTable().addListener(SWT.Selection, event -> {
			TableItem item = (TableItem) event.item;
			if (item == null) {
				return;
			}
			Message message = (Message) item.getData();
			inboxTabItem.setMessage(message);
			inboxTableControl.setRead(message, item);
		});
	}

	private void setButtonListeners() {
		inboxTabItem.getRefreshButton().addSelectionListener(
				new SelectionAdapter() {

					@Override
					public void widgetSelected(final SelectionEvent e) {
						try {
							// TODO * split these by tab
							actionFactory.readMessages();
							actionFactory.readMessageBoard();
							actionFactory.getSentMessages();
						} catch (Exception e1) {
							logger.error(e1.getMessage(), e1);
						}
					}
				});
		final Button forwardButton = inboxTabItem.getForwardButton();
		forwardButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				MessageBoxer messageBoxer = new MessageBoxer(getSelectedMessage(), false);
				messageWindow.openComposeWindow(messageBoxer.forward());
			}
		});

	}

	public void setControllers() {
		inboxTableControl = spring.autowire(new InboxTableControl( inboxTabItem.getTable() ));
	}

	public void setContents() {
		inboxTableControl.setContents();
	}

	@Override
	protected Mail reply(MessageBoxer messageBoxer) {
		return messageBoxer.reply();
	}
}
