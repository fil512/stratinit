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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class MessageBoardTabItemControl extends MessageViewerControl implements TopLevelController {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private MessageWindow messageWindow;
	@Autowired
	private ActionFactory actionFactory;
	@Autowired
	private Spring spring;

	private final MessageBoardTabItem messageBoardTabItem;

	private MailboxTableControl messageBoardTableControl;

	public MessageBoardTabItemControl(MessageBoardTabItem messageWindow) {
		super(messageWindow);
		this.messageBoardTabItem = messageWindow;
		setButtonListeners();
		setTableListeners();
	}


	// TODO move to superclass
	public final void setTableListeners() {
		messageBoardTabItem.getTable().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				TableItem item = (TableItem) event.item;
				if (item == null) {
					return;
				}
				Message message = (Message) item.getData();
				messageBoardTabItem.setMessage(message);
				messageBoardTableControl.setRead(message, item);
			}
		});
	}

	// TODO move to superclass
	private final void setButtonListeners() {
		messageBoardTabItem.getRefreshButton().addSelectionListener(
				new SelectionAdapter() {

					@Override
					public void widgetSelected(final SelectionEvent e) {
						try {
							actionFactory.readMessageBoard();
						} catch (Exception e1) {
							logger.error(e1.getMessage(), e1);
						}
					}
				});

		final Button postButton = messageBoardTabItem.getPostButton();
		postButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				messageWindow.openComposeWindow(MessageBoxer.newPost());
			}
		});
	}

	public void setControllers() {
		messageBoardTableControl = spring.autowire(new MessageBoardTableControl( messageBoardTabItem.getTable() ));
	}

	public void setContents() {
		messageBoardTableControl.setContents();
	}

	@Override
	protected Mail reply(MessageBoxer messageBoxer) {
		return messageBoxer.replyAsPost();
	}
}
