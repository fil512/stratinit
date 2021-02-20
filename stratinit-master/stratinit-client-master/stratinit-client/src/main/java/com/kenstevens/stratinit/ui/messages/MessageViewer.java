package com.kenstevens.stratinit.ui.messages;

import com.kenstevens.stratinit.model.Message;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public abstract class MessageViewer extends Composite {

	private final Button composeButton;
	protected Button replyButton;
	protected Button refreshButton;
	protected StyledText body;
	protected Table table;
	protected Composite buttonGrid;
	private final Text subject;

	public MessageViewer(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());

		table = new Table(this, SWT.BORDER | SWT.SINGLE
				| SWT.FULL_SELECTION);
		final FormData fdTable = new FormData();
		fdTable.right = new FormAttachment(0, 375);
		fdTable.bottom = new FormAttachment(100, -39);
		fdTable.top = new FormAttachment(0, 5);
		fdTable.left = new FormAttachment(0, 5);
		table.setLayoutData(fdTable);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		final TableColumn fromTableColumn = new TableColumn(table, SWT.NONE);
		fromTableColumn.setWidth(100);
		fromTableColumn.setText(getDirection());

		final TableColumn dateTableColumn = new TableColumn(table, SWT.NONE);
		dateTableColumn.setWidth(98);
		dateTableColumn.setText("Date");

		final TableColumn summaryTableColumn = new TableColumn(table, SWT.NONE);
		summaryTableColumn.setWidth(171);
		summaryTableColumn.setText("Subject");

		body = new StyledText(this, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		body.setIndent(10);
		final FormData fdMessage = new FormData();
		fdMessage.left = new FormAttachment(table, 6);
		fdMessage.top = new FormAttachment(0, 31);
		body.setLayoutData(fdMessage);

		buttonGrid = new Composite(this, SWT.NONE);
		fdMessage.right = new FormAttachment(buttonGrid, 0, SWT.RIGHT);
		fdMessage.bottom = new FormAttachment(buttonGrid);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 10;
		buttonGrid.setLayout(gridLayout);
		final FormData fdComposite = new FormData();
		fdComposite.bottom = new FormAttachment(100, -5);
		fdComposite.right = new FormAttachment(100, -5);
		fdComposite.top = new FormAttachment(table, 0, SWT.BOTTOM);
		fdComposite.left = new FormAttachment(table, 0, SWT.LEFT);
		buttonGrid.setLayoutData(fdComposite);

		refreshButton = new Button(buttonGrid, SWT.NONE);
		refreshButton.setText("Update");

		composeButton = new Button(buttonGrid, SWT.NONE);
		composeButton.setText("Compose");

		replyButton = new Button(buttonGrid, SWT.NONE);
		replyButton.setToolTipText("Reply to Selected Message");
		replyButton.setText("Reply");

		subject = new Text(this, SWT.BORDER | SWT.READ_ONLY);
		FormData fdSubject = new FormData();
		fdSubject.bottom = new FormAttachment(table, 23);
		fdSubject.top = new FormAttachment(table, 0, SWT.TOP);
		fdSubject.left = new FormAttachment(table, 6);
		fdSubject.right = new FormAttachment(100, -5);
		subject.setLayoutData(fdSubject);
	}

	protected abstract String getDirection();

	public Table getTable() {
		return table;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public Button getRefreshButton() {
		return refreshButton;
	}
	public Button getComposeButton() {
		return composeButton;
	}
	public void setMessage(Message message) {
		this.subject.setText(message.getSubject());
		this.body.setText(message.getBody());
	}
	public Button getReplyButton() {
		return replyButton;
	}
}