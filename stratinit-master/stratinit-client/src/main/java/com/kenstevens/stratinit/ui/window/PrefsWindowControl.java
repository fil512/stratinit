package com.kenstevens.stratinit.ui.window;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gwt.event.shared.HandlerManager;
import com.kenstevens.stratinit.audio.WavPlayer;
import com.kenstevens.stratinit.event.WorldArrivedEvent;
import com.kenstevens.stratinit.model.Account;

@Scope("prototype")
@Component
public class PrefsWindowControl {
	private Logger logger = Logger.getLogger(getClass());
	@Autowired
	private Account account;
	@Autowired
	private WavPlayer wavPlayer;
	@Autowired
	private HandlerManager handlerManager;

	private final PrefsWindow prefsWindow;

	public PrefsWindowControl(PrefsWindow window) {
		this.prefsWindow = window;
		setButtonListeners();
	}


	private void setButtonListeners() {
		final Button showBuildingButton = prefsWindow.getShowBuildingButton();
		showBuildingButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				try {
					account.getPreferences().setShowBuilding(
							showBuildingButton.getSelection());
					handlerManager.fireEvent(new WorldArrivedEvent());
				} catch (Exception e1) {
					logger.error(e1.getMessage(), e1);
				}
			}
		});

		final Button playSoundsButton = prefsWindow.getPlaySoundsButton();
		playSoundsButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				try {
					account.getPreferences().setPlaySounds(
							playSoundsButton.getSelection());
				} catch (Exception e1) {
					logger.error(e1.getMessage(), e1);
				}
			}
		});
		final Button liberatorButton = prefsWindow.getLiberatorButton();
		liberatorButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				try {
					boolean liberator = liberatorButton.getSelection();
					account.getPreferences().setLiberator(liberator);
					wavPlayer.playFanfare();
				} catch (Exception e1) {
					logger.error(e1.getMessage(), e1);
				}
			}
		});

		final Button switchMouseButton = prefsWindow.getSwitchMouseButton();
		switchMouseButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				try {
					account.getPreferences().setSwitchMouse(
							switchMouseButton.getSelection());
				} catch (Exception e1) {
					logger.error(e1.getMessage(), e1);
				}
			}
		});

		final Button canvasScrollButton = prefsWindow.getCanvasScrollButton();
		canvasScrollButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				try {
					account.getPreferences().setCanvasScroll(
							canvasScrollButton.getSelection());
				} catch (Exception e1) {
					logger.error(e1.getMessage(), e1);
				}
			}
		});

		final Button showFOWButton = prefsWindow.getShowFOWButton();
		showFOWButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				try {
					account.getPreferences().setShowFOW(
							showFOWButton.getSelection());
					handlerManager.fireEvent(new WorldArrivedEvent());
				} catch (Exception e1) {
					logger.error(e1.getMessage(), e1);
				}
			}
		});

	}

	public void setControllers() {
	}

	public void setContents() {
		Button showBuildingButton = prefsWindow.getShowBuildingButton();
		showBuildingButton.setSelection(account.getPreferences()
				.isShowBuilding());
		Button playSoundsButton = prefsWindow.getPlaySoundsButton();
		playSoundsButton.setSelection(account.getPreferences().isPlaySounds());
		Button liberatorButton = prefsWindow.getLiberatorButton();
		liberatorButton.setSelection(account.getPreferences().isLiberator());
		Button switchMouseButton = prefsWindow.getSwitchMouseButton();
		switchMouseButton.setSelection(account.getPreferences().isSwitchMouse());
		Button canvasScrollButton = prefsWindow.getCanvasScrollButton();
		canvasScrollButton.setSelection(account.getPreferences().isCanvasScroll());
		Button showFOWButton = prefsWindow.getShowFOWButton();
		showFOWButton.setSelection(account.getPreferences().isShowFOW());
	}
}
