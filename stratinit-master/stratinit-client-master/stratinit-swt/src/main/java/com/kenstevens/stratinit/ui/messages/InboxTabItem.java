package com.kenstevens.stratinit.ui.messages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class InboxTabItem extends MessageViewer {

	private final Button forwardButton;


	/**
	 * Create the composite
	 * @param parent
	 * @param style
	 */
	public InboxTabItem(Composite parent, int style) {
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
		return "From";
	}
}
