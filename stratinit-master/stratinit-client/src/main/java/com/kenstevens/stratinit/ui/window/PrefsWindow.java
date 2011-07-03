package com.kenstevens.stratinit.ui.window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.ui.shell.Window;

@Component
public class PrefsWindow implements Window {
	private Button liberatorOrConquererButton;
	private Button showBuildingButton;
	private Button playSoundsButton;
	private Button switchMouseButton;
	private Button showFOWButton;
	
	private Shell dialog;

	/**
	 * Open the window
	 * @wbp.parser.entryPoint
	 */
	public void open(Shell shell) {
		dialog = new Shell(shell);
		createContents(dialog);
		dialog.open();
	}

	/**
	 * Create contents of the window
	 */
	protected void createContents(Shell dialog) {
		dialog.setSize(169, 168);
		dialog.setText("Preferences");
		dialog.setLayout(new GridLayout());

		showBuildingButton = new Button(dialog, SWT.CHECK);
		showBuildingButton.setText("Show Building");
		playSoundsButton = new Button(dialog, SWT.CHECK);
		playSoundsButton.setToolTipText("Play intro music and sound effects.");
		playSoundsButton.setText("Play sounds");

		liberatorOrConquererButton = new Button(dialog, SWT.CHECK);
		liberatorOrConquererButton.setToolTipText("Check for trumpets, uncheck for screams when you capture a city");
		liberatorOrConquererButton.setText("Liberator vs Conquerer");

		switchMouseButton = new Button(dialog, SWT.CHECK);
		switchMouseButton.setToolTipText("Switch the meaning of right and left mouse buttons on the map.");
		switchMouseButton.setText("Switch mouse buttons");

		showFOWButton = new Button(dialog, SWT.CHECK);
		showFOWButton.setToolTipText("Display // on sectors that are outside your line of sight.");
		showFOWButton.setText("Show Fog of War");}


	public Button getShowBuildingButton() {
		return showBuildingButton;
	}

	public Button getPlaySoundsButton() {
		return playSoundsButton;
	}
	public Button getLiberatorButton() {
		return liberatorOrConquererButton;
	}
	public Button getSwitchMouseButton() {
		return switchMouseButton;
	}

	public Button getShowFOWButton() {
		return showFOWButton;
	}

	public boolean isDisposed() {
		return dialog.isDisposed();
	}
}
