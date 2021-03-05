package com.kenstevens.stratinit.client.control.selection;

import com.kenstevens.stratinit.client.control.selection.Selection.Source;
import com.kenstevens.stratinit.client.event.StratInitEvent;

//TODO REF this should probably be a type of selectSector event

public class SelectUnitsEvent implements StratInitEvent {
	private final Source selectionSource;
	
	public SelectUnitsEvent(Selection.Source selectionSource) {
		this.selectionSource = selectionSource;
		
	}

	public Source getSelectionSource() {
		return selectionSource;
	}
}
