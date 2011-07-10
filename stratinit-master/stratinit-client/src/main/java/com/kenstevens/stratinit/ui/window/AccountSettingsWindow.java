package com.kenstevens.stratinit.ui.window;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.model.Account;
import com.kenstevens.stratinit.ui.shell.Window;
import com.kenstevens.stratinit.util.AccountPersister;
import com.kenstevens.stratinit.util.XMLException;

@Component
public class AccountSettingsWindow implements Window {
	private final Log logger = LogFactory.getLog(getClass());

	private Text username;
	private Text password;
	private Shell dialog;
	@Autowired
	private Account account;
	@Autowired
	private AccountPersister accountPersister;
	@Autowired
	private WindowDirector windowDirector;

	private Button saveButton;
	private Button cancelButton;

	/**
	 * Open the window
	 */
	public void open(Shell shell) {
		dialog = new Shell(shell);
		createContents(dialog);
		dialog.open();
	}

	/**
	 * Create contents of the window
	 */
	protected void createContents(Shell shell) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		shell.setLayout(gridLayout);
		shell.setSize(206, 168);
		shell.setText("Account Settings");

		final Label usernameLabel = new Label(shell, SWT.NONE);
		usernameLabel.setText("Username");

		username = new Text(shell, SWT.BORDER);
		final GridData gdUsername = new GridData(SWT.FILL, SWT.CENTER, false, false);
		gdUsername.widthHint = 108;
		username.setLayoutData(gdUsername);

		final Label passwordLabel = new Label(shell, SWT.NONE);
		passwordLabel.setText("Password");

		password = new Text(shell, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		new Label(dialog, SWT.NONE);

		saveButton = new Button(dialog, SWT.NONE);
		saveButton.setLayoutData(new GridData());
		saveButton.setText("Save");
		new Label(shell, SWT.NONE);

		cancelButton = new Button(shell, SWT.NONE);
		cancelButton.setLayoutData(new GridData());
		cancelButton.setText("Cancel");
		setButtonListeners();
	}

	private void setButtonListeners() {
		saveButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				try {
					saveAndClose();
				} catch (XMLException e1) {
					logger.error(e1.getMessage(), e1);
				}
			}
		});
		cancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				try {
					dialog.close();
				} catch (Exception e1) {
					logger.error(e1.getMessage(), e1);
				}
			}
		});
		password.addListener(SWT.DefaultSelection, new Listener() {
			public void handleEvent(Event e) {
				try {
					saveAndClose();
				} catch (XMLException e1) {
					logger.error(e1.getMessage(), e1);
				}
			}
		});
	}

	public void setContents() {
		if (account.getUsername() != null) {
			username.setText(account.getUsername());
		}
		if (account.getPassword() != null) {
			password.setText(account.getPassword());
		}
	}

	private void saveAndClose() throws XMLException {
		account.setUsername(username.getText());
		account.setPassword(password.getText());
		accountPersister.save();
		dialog.close();
		windowDirector.chooseGameWindow();
	}
}
