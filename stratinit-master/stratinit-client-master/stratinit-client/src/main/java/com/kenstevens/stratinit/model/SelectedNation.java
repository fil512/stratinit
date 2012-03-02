package com.kenstevens.stratinit.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kenstevens.stratinit.ui.shell.WidgetContainer;

@Repository
public class SelectedNation {
	@Autowired
	WidgetContainer widgetContainer;
	
	private NationView nation;

	public void setNation(NationView nation) {
		this.nation = nation;
	}

	public boolean nationSelected(Nation theNation) {
		return widgetContainer.getTabControl().playerTabSelected()
				&& nation != null
				&& nation.equals(theNation);
	}
	
	public NationView getPlayer() {
		return nation;
	}
}
