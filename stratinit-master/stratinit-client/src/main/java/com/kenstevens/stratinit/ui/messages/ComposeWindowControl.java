package com.kenstevens.stratinit.ui.messages;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.control.Controller;
import com.kenstevens.stratinit.main.ClientConstants;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.model.Mail;
import com.kenstevens.stratinit.site.action.ActionFactory;
import com.kenstevens.stratinit.ui.shell.StatusReporter;

@Scope("prototype")
@Component
public class ComposeWindowControl implements Controller {
	private final Log logger = LogFactory.getLog(getClass());

	@Autowired
	private ActionFactory actionFactory;
	@Autowired
	private Data db;
	@Autowired
	private StatusReporter statusReporter;

	private final ComposeWindow composeWindow;

	public ComposeWindowControl(ComposeWindow composeWindow) {
		this.composeWindow = composeWindow;
		setButtonListeners();
	}

	public void setContents() {
		final String[] recipients = db.getNationList().getPlayerNames();
		if (recipients == null) {
			return;
		}
		Combo playerCombo = composeWindow.getRecipientCombo();
		if (playerCombo.isDisposed())
			return;

		playerCombo.removeAll();
		playerCombo.add("");
		playerCombo.add(ClientConstants.MESSAGE_BOARD);
		for (String recipient : recipients) {
			playerCombo.add(recipient);
		}
		playerCombo.select(0);
		playerCombo.redraw();
	}

	private void setButtonListeners() {
		composeWindow.getSendButton().addSelectionListener(
				new SelectionAdapter() {

					@Override
					public void widgetSelected(final SelectionEvent e) {
						if (composeWindow.getRecipientCombo()
									.getText().isEmpty()) {
							statusReporter.reportError("Select a recipient for the message.");
							return;
						}
						try {
							sendMessage();
						} catch (Exception e1) {
							logger.error(e1.getMessage(), e1);
						}
					}
				});
		composeWindow.getRecipientCombo().addSelectionListener(
				new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent e) {
						try {
							String playerName = composeWindow.getRecipientCombo()
									.getText();
							composeWindow.setTitle("Message to " + playerName);
						} catch (Exception e1) {
							logger.error(e1.getMessage(), e1);
						}
					}
				});
	}

	public void setControllers() {
	}

	private void sendMessage() {
		Mail message = new Mail();
		message.setSubject(composeWindow.getSubject().getText());
		message.setBody(composeWindow.getBody().getText());
		message.setDate(new Date());
		String recipient = composeWindow.getRecipientCombo()
				.getText();
		if (recipient.startsWith(ClientConstants.ALLY_PREFIX)) {
			recipient = recipient.substring(ClientConstants.ALLY_PREFIX.length());
		}
		if (!recipient.equals(ClientConstants.MESSAGE_BOARD)) {
			message.setTo(db.getNationList().getNation(recipient));
		}
		// TODO SEC this needs to be overridden on server
		message.setFrom(db.getNation());
		actionFactory.sendMessage(message);
		composeWindow.close();
	}
}
