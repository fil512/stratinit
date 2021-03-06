package com.kenstevens.stratinit.client.ui.tabs;

import com.google.common.collect.Maps;
import com.kenstevens.stratinit.client.control.MapController;
import com.kenstevens.stratinit.client.event.ArrivedDataEventAccumulator;
import com.kenstevens.stratinit.client.event.UnitListReplacementArrivedEvent;
import com.kenstevens.stratinit.client.model.Data;
import com.kenstevens.stratinit.client.shell.TabControl;
import com.kenstevens.stratinit.client.site.action.post.ActionFactory;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TabManager implements TabControl {
	@Autowired
	ActionFactory actionFactory;
	@Autowired
	ArrivedDataEventAccumulator arrivedDataEventAccumulator;
	@Autowired
	Data db;

	int tabIndex = 0;
	int tabSelected;

	private final Map<String, TabItem> tabMap = Maps.newHashMap();
	private final Map<String, Integer> tabIndexMap = Maps.newHashMap();
	private TabFolder tabFolder;
	private int unitTabIndex;
	private int battleTabIndex;
	private int cityTabIndex;
	private int playerTabIndex;
	private int futureTabIndex;
	private int supplyTabIndex;
	private FutureTabItemControl futureTabItemControl;
	private MapController mapController;

	public int register(String title, TabItem tabItem) {
		tabMap.put(title, tabItem);
		tabItem.setText(title);
		tabIndexMap.put(title, tabIndex);
		return tabIndex++;
	}

	public void setTabFolder(TabFolder tabFolder) {
		this.tabFolder = tabFolder;
	}

	public void setControllers(FutureTabItemControl futureTabItemControl,
			MapController mapController) {
		this.futureTabItemControl = futureTabItemControl;
		this.mapController = mapController;
		addListeners();
	}

	private void addListeners() {
		tabFolder.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (!db.isLoaded()) {
					return;
				}
				switchTabMightChangeMap();
				if (futureTabSelected()) {
					futureTabItemControl.setContents();
				} else if (battleTabSelected()) {
					actionFactory.battleLog();
				} else if (playerTabSelected()) {
					actionFactory.getNations();
				} else if (unitTabSelected()) {
					arrivedDataEventAccumulator
							.addEvent(new UnitListReplacementArrivedEvent());
					arrivedDataEventAccumulator.fireEvents();
				}
			}
		});
	}

	private void switchTabMightChangeMap() {
		int lastTabSelected = tabSelected;
		tabSelected = tabFolder.getSelectionIndex();
		if (toOrFrom(lastTabSelected, battleTabIndex)
				|| toOrFrom(lastTabSelected, cityTabIndex)
				|| toOrFrom(lastTabSelected, playerTabIndex)
				|| toOrFrom(lastTabSelected, supplyTabIndex)) {
			mapController.buildImage();
		}
	}

	private boolean toOrFrom(int lastTabSelected, int tabIndex) {
		return lastTabSelected == tabIndex
				|| tabFolder.getSelectionIndex() == tabIndex;
	}

	public TabFolder getTabFolder() {
		return tabFolder;
	}

	public void switchToSectorTab() {
		final Display display = Display.getDefault();

		if (display.isDisposed()) {
			return;
		}
		display.asyncExec(new Runnable() {
			public void run() {
				tabFolder.setSelection(0);
				switchTabMightChangeMap();
			}
		});
	}

	private boolean futureTabSelected() {
		return tabSelected == futureTabIndex;
	}

	@Override
	public boolean battleTabSelected() {
		return tabSelected == battleTabIndex;
	}

	@Override
	public boolean unitTabSelected() {
		return tabSelected == unitTabIndex;
	}

	@Override
	public boolean playerTabSelected() {
		return tabSelected == playerTabIndex;
	}

	@Override
	public boolean cityTabSelected() {
		return tabSelected == cityTabIndex;
	}

	@Override
	public boolean supplyTabSelected() {
		return tabSelected == supplyTabIndex;
	}

	public void setCityTabIndex(int cityTabIndex) {
		this.cityTabIndex = cityTabIndex;
	}

	public void setPlayerTabIndex(int playerTabIndex) {
		this.playerTabIndex = playerTabIndex;
	}

	public void setSupplyTabIndex(int supplyTabIndex) {
		this.supplyTabIndex = supplyTabIndex;
	}

	public void setFutureTabIndex(int futureTabIndex) {
		this.futureTabIndex = futureTabIndex;
	}

	public void setBattleTabIndex(int battleTabIndex) {
		this.battleTabIndex = battleTabIndex;
	}

	public int getUnitTabIndex() {
		return unitTabIndex;
	}

	public void setUnitTabIndex(int unitTabIndex) {
		this.unitTabIndex = unitTabIndex;
	}

}
