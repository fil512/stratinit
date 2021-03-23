package com.kenstevens.stratinit.client.ui.tabs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class CityButtons extends Composite {
	private Button cedeButton;
	private Button cancelButton;
	private Button updateButton;

	/**
	 * Create the composite
	 * @param parent
	 * @param style
	 */
	public CityButtons(Composite parent, int style) {
		super(parent, style);
		createContents();
	}

	private void createContents() {
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 8;
		setLayout(gridLayout);

		updateButton = new Button(this, SWT.NONE);
		GridData gridDataUpdateButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gridDataUpdateButton.widthHint = 36;
		gridDataUpdateButton.heightHint = 28;
		updateButton.setLayoutData(gridDataUpdateButton);
		updateButton.setToolTipText("Refresh city data");
		
		cedeButton = new Button(this, SWT.NONE);
		GridData gridDataCede = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gridDataCede.widthHint = 36;
		gridDataCede.heightHint = 28;
		cedeButton.setLayoutData(gridDataCede);
		cedeButton.setToolTipText("Give city to ally");
		
		cancelButton = new Button(this, SWT.NONE);
		GridData gridDataCancel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gridDataCancel.widthHint = 36;
		gridDataCancel.heightHint = 28;
		cancelButton.setLayoutData(gridDataCede);
		cancelButton.setToolTipText("Cancel waypoint");
	}

	public Button getCedeButton() {
		return cedeButton;
	}
	public Button getCancelButton() {
		return cancelButton;
	}
	public Button getUpdateButton() {
		return updateButton;
	}
}
