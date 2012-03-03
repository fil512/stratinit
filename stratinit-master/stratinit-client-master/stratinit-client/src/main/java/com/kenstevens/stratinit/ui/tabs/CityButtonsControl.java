package com.kenstevens.stratinit.ui.tabs;

import javax.annotation.PostConstruct;

import org.eclipse.swt.widgets.Display;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gwt.event.shared.HandlerManager;
import com.kenstevens.stratinit.event.NationListArrivedEvent;
import com.kenstevens.stratinit.event.NationListArrivedEventHandler;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.model.NationView;
import com.kenstevens.stratinit.model.SelectedCity;
import com.kenstevens.stratinit.site.action.ActionFactory;
import com.kenstevens.stratinit.ui.adapter.CancelCitySelectionAdapter;
import com.kenstevens.stratinit.ui.adapter.CedeCitySelectionAdapter;
import com.kenstevens.stratinit.ui.adapter.UpdateCitiesSelectionAdapter;
import com.kenstevens.stratinit.ui.image.ImageLibrary;
import com.kenstevens.stratinit.ui.shell.TopShell;
import com.kenstevens.stratinit.util.Spring;

@Scope("prototype")
@Component
public class CityButtonsControl {
	@Autowired
	private ActionFactory actionFactory;
	@Autowired
	private Data db;
	@Autowired
	private SelectedCity selectedCity;
	@Autowired
	private CedeWindow cedeWindow;
	@Autowired
	private TopShell topShell;
	@Autowired
	private ImageLibrary imageLibrary;
	@Autowired
	private Spring spring;
	@Autowired
	private HandlerManager handlerManager;
	
	private final CityButtons cityButtons;

	public CityButtonsControl(CityButtons cityButtons) {
		this.cityButtons = cityButtons;
		setInitialImages();
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void addObservers() {
		setButtonListeners();
		handlerManager.addHandler(NationListArrivedEvent.TYPE,
				new NationListArrivedEventHandler() {
					@Override
					public void dataArrived() {
						NationView ally = db.getAlly();
						if (ally == null) {
							cityButtons.getCedeButton().setEnabled(false);
						} else {
							cityButtons.getCedeButton().setEnabled(true);
						}

					}
				});
	}

	private void setButtonListeners() {
		cityButtons.getCedeButton().addSelectionListener(
				new CedeCitySelectionAdapter(selectedCity, spring,  cedeWindow, topShell));
		cityButtons.getCancelButton().addSelectionListener(
				new CancelCitySelectionAdapter(selectedCity, actionFactory));
		cityButtons.getUpdateButton().addSelectionListener(
				new UpdateCitiesSelectionAdapter(actionFactory));
	}

	public void setInitialImages() {
		Display display = Display.getDefault();
		if (display.isDisposed())
			return;
		display.asyncExec(new Runnable() {
			public void run() {

				cityButtons.getCedeButton().setImage(imageLibrary.getCede());
				cityButtons.getCancelButton().setImage(imageLibrary.getCancelMove());
				cityButtons.getUpdateButton().setImage(
						imageLibrary.getUpdate());
			}
		});
	}
}
