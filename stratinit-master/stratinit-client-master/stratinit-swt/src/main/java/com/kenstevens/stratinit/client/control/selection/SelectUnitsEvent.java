package com.kenstevens.stratinit.client.control.selection;

import com.kenstevens.stratinit.client.api.Selection;
import com.kenstevens.stratinit.client.event.StratInitEvent;

//TODO REF this should probably be a type of selectSector event

public class SelectUnitsEvent implements StratInitEvent {
	private final Selection.Source selectionSource;

	public SelectUnitsEvent(Selection.Source selectionSource) {
		this.selectionSource = selectionSource;

	}

	public Selection.Source getSelectionSource() {
		return selectionSource;
	}
}
