package com.kenstevens.stratinit.ui.messages;

import javax.annotation.PostConstruct;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.control.Controller;
import com.kenstevens.stratinit.main.ClientConstants;
import com.kenstevens.stratinit.site.action.ActionFactory;
import com.kenstevens.stratinit.util.Spring;

@Scope("prototype")
@Component
public class MessageWindowControl implements Controller {
	@Autowired
	private Spring spring;
	@Autowired
	private ActionFactory actionFactory;
	@Autowired
	private MessageWindow messageWindow;
	private InboxTabItemControl inboxTabItemController;
	private SentItemsTabItemControl sentItemsTabItemController;
	private MessageBoardTabItemControl messageBoardController;

	@SuppressWarnings("unused")
	@PostConstruct
	private void addObservers() {
		addTabFolderListeners();
	}

	private void addTabFolderListeners() {
		final TabFolder tabFolder = messageWindow.getTabFolder();
		tabFolder.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				TabItem[] tabItem = tabFolder.getSelection();
				if (tabItem.length == 1) {
					if (tabItem[0].getText().equals(ClientConstants.MESSAGE_BOARD)) {
						actionFactory.readMessageBoard();
					} else if (tabItem[0].getText().equals(ClientConstants.INBOX)) {
						actionFactory.readMessages();
					} else if (tabItem[0].getText().equals(ClientConstants.SENT_ITEMS)) {
						actionFactory.getSentMessages();
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
	}

	public void setControllers() {
		inboxTabItemController = spring.autowire(new InboxTabItemControl( messageWindow.getInboxTabItem() ));
		inboxTabItemController.setControllers();

		sentItemsTabItemController = spring.autowire(new SentItemsTabItemControl( messageWindow
						.getSentItemsTabItem() ));
		sentItemsTabItemController.setControllers();


		messageBoardController = spring.autowire(new MessageBoardTabItemControl( messageWindow
								.getMessageBoardTabItem() ));
		messageBoardController.setControllers();
	}

	public void setContents() {
		inboxTabItemController.setContents();
		sentItemsTabItemController.setContents();
		messageBoardController.setContents();
	}

	public void setTab(int tab) {
		TabFolder tabFolder = messageWindow.getTabFolder();
		tabFolder.setSelection(tab);
	}
}
