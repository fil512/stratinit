package com.kenstevens.stratinit.ui.messages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class MessageBoardTabItem extends MessageViewer {

	private final Button postButton;

	/**
	 * Create the composite
	 * 
	 * @param parent
	 * @param style
	 */
	public MessageBoardTabItem(Composite parent, int style) {
		super(parent, style);

		postButton = new Button(buttonGrid, SWT.NONE);
		postButton.setLayoutData(new GridData());
		postButton.setText("Post");
	}

	public Button getPostButton() {
		return postButton;
	}
	
	@Override
	protected String getDirection() {
		return "From";
	}
}
