package com.kenstevens.stratinit.ui.tabs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.ui.shell.Window;

@Component
public class CedeWindow implements Window {
	private Shell dialog;
	private Label recipientLabel;
	private Button cedeButton;
	private Label cedeLabel;

	/**
	 * @wbp.parser.entryPoint
	 */
	public void open(Shell shell) {
		dialog = new Shell(shell);
		dialog.setMinimumSize(new Point(500, 40));
		dialog.setSize(307, 168);
		createContents(dialog);
		dialog.setTabList(new Control[]{cedeButton});
		dialog.open();
	}
	
	private void createContents(Shell shell) {
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		shell.setLayout(gridLayout);
		shell.setBounds(60, 60, 193, 253);
		shell.setText("Cede");

		cedeLabel = new Label(shell, SWT.NONE);
		cedeLabel.setAlignment(SWT.RIGHT);
		GridData gdCedeLabel = new GridData(SWT.LEFT, SWT.TOP, false, false);
		gdCedeLabel.widthHint = 229;
		cedeLabel.setLayoutData(gdCedeLabel);
		cedeLabel.setText("Give X to:");

		recipientLabel = new Label(shell, SWT.NONE);
		GridData gdRecLabel = new GridData(SWT.LEFT, SWT.TOP, false, false);
		gdRecLabel.widthHint = 229;
		recipientLabel.setLayoutData(gdRecLabel);

		cedeButton = new Button(shell, SWT.NONE);
		cedeButton.setToolTipText("Transfer ownership to the nation indicated above");
		cedeButton.setText("Cede");
		GridData gdCedeButton = new GridData(SWT.RIGHT, SWT.TOP, false, false);
		cedeButton.setLayoutData(gdCedeButton);
	}
	public Label getRecipientLabel() {
		return recipientLabel;
	}

	public Button getCedeButton() {
		return cedeButton;
	}

	public void close() {
		dialog.close();
	}
	public Label getCedeLabel() {
		return cedeLabel;
	}
}
