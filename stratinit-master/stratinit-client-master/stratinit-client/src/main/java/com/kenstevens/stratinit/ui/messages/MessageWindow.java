package com.kenstevens.stratinit.ui.messages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.main.ClientConstants;
import com.kenstevens.stratinit.model.Mail;
import com.kenstevens.stratinit.shell.StratInitWindow;
import com.kenstevens.stratinit.util.Spring;

@Component
public class MessageWindow implements StratInitWindow {
	private MessageBoardTabItem messageBoardTabItem;
	private Shell dialog;
	private InboxTabItem inboxTabItem;
	private SentItemsTabItem sentItemsTabItem;

	@Autowired
	private Spring spring;
	private TabFolder tabFolder;

	/**
	 * @wbp.parser.entryPoint
	 */
	public void open(Shell shell) {
		dialog = new Shell(shell, SWT.RESIZE | SWT.CLOSE);
		dialog.setLayout(new FormLayout());
		createContents(dialog);
		dialog.open();
	}

	/**
	 * Create contents of the dialog
	 */
	protected void createContents(Shell shell) {
		shell.setSize(1080, 741);
		shell.setText("Messages");

		tabFolder = new TabFolder(shell, SWT.NONE);
		final FormData fdTabFolder = new FormData();
		fdTabFolder.right = new FormAttachment(100, 2);
		fdTabFolder.bottom = new FormAttachment(100, 2);
		fdTabFolder.top = new FormAttachment(0, 0);
		fdTabFolder.left = new FormAttachment(0, 0);
		tabFolder.setLayoutData(fdTabFolder);

		TabItem mTabItem = new TabItem(tabFolder, SWT.NONE);
		mTabItem.setText(ClientConstants.MESSAGE_BOARD);

		messageBoardTabItem = new MessageBoardTabItem(tabFolder, SWT.NONE);
		mTabItem.setControl(messageBoardTabItem);

		TabItem iTabItem = new TabItem(tabFolder, SWT.NONE);
		iTabItem.setText(ClientConstants.INBOX);

		inboxTabItem = new InboxTabItem(tabFolder, SWT.NONE);
		iTabItem.setControl(inboxTabItem);

		TabItem sTabItem = new TabItem(tabFolder, SWT.NONE);
		sTabItem.setText(ClientConstants.SENT_ITEMS);

		sentItemsTabItem = new SentItemsTabItem(tabFolder, SWT.NONE);
		sTabItem.setControl(sentItemsTabItem);
	}

	public InboxTabItem getInboxTabItem() {
		return inboxTabItem;
	}

	public SentItemsTabItem getSentItemsTabItem() {
		return sentItemsTabItem;
	}

	public MessageBoardTabItem getMessageBoardTabItem() {
		return messageBoardTabItem;
	}

	public void openComposeWindow(Mail template) {
		ComposeWindow composeWindow = new ComposeWindow();
		composeWindow.open(dialog);
		ComposeWindowControl composeController = spring
				.autowire(new ComposeWindowControl(composeWindow));
		composeController.setControllers();
		composeController.setContents();
		if (template != null) {
			composeWindow.getBody().setText(template.getBody());
			composeWindow.getSubject().setText(template.getSubject());
			Combo playerCombo = composeWindow.getRecipientCombo();
			if (template.isPost()) {
				int playerIndex = playerCombo
						.indexOf(ClientConstants.MESSAGE_BOARD);
				playerCombo.select(playerIndex);
			} else if (template.getTo() != null) {
				String recipient = template.getTo().toString();
				int playerIndex = playerCombo.indexOf(recipient);
				if (playerIndex == -1) {
					playerIndex = playerCombo
							.indexOf(ClientConstants.ALLY_PREFIX + recipient);
				}
				if (playerIndex != -1) {
					playerCombo.select(playerIndex);
				}
			}
			if (!composeWindow.getBody().getText().isEmpty()) {
				composeWindow.getBody().setSelection(
						composeWindow.getBody().getCharCount());
				composeWindow.getBody().setFocus();
			} else {
				composeWindow.getSubject().setFocus();
			}
		} else {
			composeWindow.getSubject().setFocus();
		}
	}

	public TabFolder getTabFolder() {
		return tabFolder;
	}
}
