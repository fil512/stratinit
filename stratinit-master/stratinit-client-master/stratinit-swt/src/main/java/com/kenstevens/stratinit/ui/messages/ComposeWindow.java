package com.kenstevens.stratinit.ui.messages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;

public class ComposeWindow {

	private Button sendButton;
	private Combo recipientCombo;
	private Text subject;
	private Text body;
	private Shell dialog;

	/**
	 * Open the dialog
	 *
	 * @return the result
	 * @wbp.parser.entryPoint
	 */
	public void open(Shell shell) {
		dialog = new Shell(shell, SWT.RESIZE | SWT.CLOSE);
		dialog.setLayout(new FormLayout());
		createContents(dialog);

		dialog.open();
	}

	private void createContents(Shell shell) {
		shell.setSize(800, 600);
		shell.setText("Message");

		sendButton = new Button(shell, SWT.NONE);
		final FormData fdSendButton = new FormData();
		fdSendButton.bottom = new FormAttachment(100, -6);
		fdSendButton.right = new FormAttachment(0, 106);
		fdSendButton.left = new FormAttachment(0, 8);
		sendButton.setLayoutData(fdSendButton);
		sendButton.setText("Send");

		Label toLabel = new Label(shell, SWT.NONE);
		FormData fdToLabel = new FormData();
		fdToLabel.left = new FormAttachment(0, 35);
		toLabel.setLayoutData(fdToLabel);
		toLabel.setText("To:");

		recipientCombo = new Combo(shell, SWT.READ_ONLY);
		recipientCombo.setVisibleItemCount(12);
		final FormData fdRecipient = new FormData();
		fdRecipient.right = new FormAttachment(toLabel, 130, SWT.RIGHT);
		fdRecipient.bottom = new FormAttachment(0, 35);
		fdRecipient.top = new FormAttachment(0, 10);
		fdRecipient.left = new FormAttachment(toLabel, 6);
		recipientCombo.setLayoutData(fdRecipient);

		Label lblSubject = new Label(dialog, SWT.NONE);
		FormData fdLblSubject = new FormData();
		fdLblSubject.top = new FormAttachment(0, 46);
		fdLblSubject.left = new FormAttachment(sendButton, 0, SWT.LEFT);
		lblSubject.setLayoutData(fdLblSubject);
		lblSubject.setText("Subject:");

		subject = new Text(dialog, SWT.BORDER);
		FormData fdSubject = new FormData();
		fdSubject.top = new FormAttachment(recipientCombo, 6);
		fdSubject.right = new FormAttachment(100, -5);
		fdSubject.left = new FormAttachment(lblSubject, 2);
		subject.setLayoutData(fdSubject);
		fdToLabel.bottom = new FormAttachment(100, -525);

		body = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		FormData fdBody = new FormData();
		fdBody.bottom = new FormAttachment(sendButton, -6);
		fdBody.left = new FormAttachment(sendButton, 0, SWT.LEFT);
		fdBody.top = new FormAttachment(lblSubject, 22);
		fdBody.right = new FormAttachment(100, -5);
		body.setLayoutData(fdBody);
	}

	public Button getSendButton() {
		return sendButton;
	}

	public Combo getRecipientCombo() {
		return recipientCombo;
	}

	public Text getBody() {
		return body;
	}

	public void close() {
		dialog.close();
	}

	public void setTitle(String title) {
		dialog.setText(title);
	}

	public boolean confirm(String message) {
		MessageBox messageBox = new MessageBox(dialog, SWT.OK | SWT.CANCEL
				| SWT.ICON_WARNING);
		messageBox.setMessage(message);
		int button = messageBox.open();
		return button == SWT.OK;
	}

	public Text getSubject() {
		return subject;
	}
}
