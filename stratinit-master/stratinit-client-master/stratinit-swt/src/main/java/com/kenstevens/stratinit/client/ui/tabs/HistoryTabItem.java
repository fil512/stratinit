package com.kenstevens.stratinit.client.ui.tabs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class HistoryTabItem extends Composite {
	private StyledText styledText;

	/**
	 * Create the composite
	 * @param tabFolder
	 * @param style
	 */
	public HistoryTabItem(Composite parent, int style) {
		super(parent, style);
		createContents();
	}

	private void createContents() {
		setLayout(new FormLayout());

		styledText = new StyledText (this, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL);

		final FormData fdText = new FormData();
		fdText.right = new FormAttachment(100, -5);
		fdText.bottom = new FormAttachment(100, -5);
		fdText.left = new FormAttachment(0,0);
		fdText.top = new FormAttachment(0, 0);
		styledText.setLayoutData(fdText);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public StyledText getStyledText() {
		return styledText;
	}
}
