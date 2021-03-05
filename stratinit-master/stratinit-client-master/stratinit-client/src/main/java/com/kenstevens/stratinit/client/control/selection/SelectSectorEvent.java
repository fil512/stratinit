package com.kenstevens.stratinit.client.control.selection;

import com.kenstevens.stratinit.client.control.selection.Selection.Source;
import com.kenstevens.stratinit.client.event.StratInitEvent;

public class SelectSectorEvent implements StratInitEvent {
	private final Source selectionSource;
	
	public SelectSectorEvent(Selection.Source selectionSource) {
		this.selectionSource = selectionSource;
		
	}

	public Source getSelectionSource() {
		return selectionSource;
	}
	
}
