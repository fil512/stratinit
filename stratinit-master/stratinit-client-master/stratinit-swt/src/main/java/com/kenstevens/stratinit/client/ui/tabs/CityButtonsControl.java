package com.kenstevens.stratinit.client.ui.tabs;

import com.google.common.eventbus.Subscribe;
import com.kenstevens.stratinit.client.event.NationListArrivedEvent;
import com.kenstevens.stratinit.client.event.StratinitEventBus;
import com.kenstevens.stratinit.client.model.Data;
import com.kenstevens.stratinit.client.model.NationView;
import com.kenstevens.stratinit.client.model.SelectedCity;
import com.kenstevens.stratinit.client.shell.TopShell;
import com.kenstevens.stratinit.client.site.action.post.ActionFactory;
import com.kenstevens.stratinit.client.ui.adapter.CancelCitySelectionAdapter;
import com.kenstevens.stratinit.client.ui.adapter.CedeCitySelectionAdapter;
import com.kenstevens.stratinit.client.ui.adapter.CedeWindow;
import com.kenstevens.stratinit.client.ui.adapter.UpdateCitiesSelectionAdapter;
import com.kenstevens.stratinit.client.ui.image.ImageLibrary;
import com.kenstevens.stratinit.client.util.Spring;
import org.eclipse.swt.widgets.Display;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

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
	protected StratinitEventBus eventBus;
	
	private final CityButtons cityButtons;

	public CityButtonsControl(CityButtons cityButtons) {
		this.cityButtons = cityButtons;
		setInitialImages();
	}
	
	@Subscribe
	public void handleNationListArrivedEvent(NationListArrivedEvent event) {
		NationView ally = db.getAlly();
		cityButtons.getCedeButton().setEnabled(ally != null);
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void addObservers() {
		setButtonListeners();
		eventBus.register(this);
	}

	private void setButtonListeners() {
		cityButtons.getCedeButton().addSelectionListener(
				new CedeCitySelectionAdapter(selectedCity, spring,  cedeWindow, topShell));
		cityButtons.getCancelButton().addSelectionListener(
				new CancelCitySelectionAdapter(selectedCity, actionFactory));
		cityButtons.getUpdateButton().addSelectionListener(
				new UpdateCitiesSelectionAdapter(actionFactory));
	}

	private final void setInitialImages() {
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
