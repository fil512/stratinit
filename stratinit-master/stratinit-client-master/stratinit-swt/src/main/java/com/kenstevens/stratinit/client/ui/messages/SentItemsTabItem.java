package com.kenstevens.stratinit.client.ui.messages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class SentItemsTabItem extends MessageViewer {

	private final Button forwardButton;

	public SentItemsTabItem(Composite parent, int style) {
		super(parent, style);
		
		forwardButton = new Button(buttonGrid, SWT.NONE);
		forwardButton.setLayoutData(new GridData());
		forwardButton.setText("Forward");
	}

	public Button getForwardButton() {
		return forwardButton;
	}
	
	@Override
	protected String getDirection() {
		return "To";
	}
}
