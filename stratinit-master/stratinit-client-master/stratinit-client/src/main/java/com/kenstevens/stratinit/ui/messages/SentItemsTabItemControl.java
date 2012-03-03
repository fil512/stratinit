package com.kenstevens.stratinit.ui.messages;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.control.TopLevelController;
import com.kenstevens.stratinit.model.Mail;
import com.kenstevens.stratinit.model.Message;
import com.kenstevens.stratinit.util.Spring;

@Scope("prototype")
@Component
public class SentItemsTabItemControl extends MessageViewerControl implements TopLevelController {
	private final Log logger = LogFactory.getLog(getClass());

	@Autowired
	private Spring spring;
	@Autowired
	private MessageWindow messageWindow;

	private final SentItemsTabItem sentItemsTabItem;

	private MailboxTableControl sentItemsTableControl;

	public SentItemsTabItemControl(SentItemsTabItem sentItemsTabItem) {
		super(sentItemsTabItem);
		this.sentItemsTabItem = sentItemsTabItem;

		setTableListeners();
		setButtonListeners();
	}

	@Override
	protected boolean isSent() {
		return true;
	}

	public final void setTableListeners() {
		sentItemsTabItem.getTable().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				TableItem item = (TableItem) event.item;
				if (item == null) {
					return;
				}
				Message message = (Message) item.getData();
				sentItemsTabItem.setMessage(message);
			}
		});
	}

	private final void setButtonListeners() {
		final Button forwardButton = sentItemsTabItem.getForwardButton();
		forwardButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				MessageBoxer messageBoxer = new MessageBoxer(getSelectedMessage(), true);
				messageWindow.openComposeWindow(messageBoxer.forward());
			}
		});
		sentItemsTabItem.getRefreshButton().addSelectionListener(
				new SelectionAdapter() {

					@Override
					public void widgetSelected(final SelectionEvent e) {
						try {
							actionFactory.getSentMessages();
						} catch (Exception e1) {
							logger.error(e1.getMessage(), e1);
						}
					}
				});
	}

	public void setControllers() {
		sentItemsTableControl = spring.autowire(new SentItemsTableControl( sentItemsTabItem.getTable() ));
	}

	public void setContents() {
		sentItemsTableControl.setContents();
	}

	// TODO not the best option here
	@Override
	protected Mail reply(MessageBoxer messageBoxer) {
		return messageBoxer.reply();
	}
}
