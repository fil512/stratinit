package com.kenstevens.stratinit.control.selection;

import com.kenstevens.stratinit.control.selection.Selection.Source;
import com.kenstevens.stratinit.event.StratInitEvent;

public class SelectNationEvent implements StratInitEvent {
	private final Source selectionSource;
	
	public SelectNationEvent(Selection.Source selectionSource) {
		this.selectionSource = selectionSource;
		
	}

	public Source getSelectionSource() {
		return selectionSource;
	}
}
