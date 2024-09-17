package com.kenstevens.stratinit.client.control.selection;

import com.kenstevens.stratinit.client.api.Selection;
import com.kenstevens.stratinit.client.event.StratInitEvent;

public class SelectNationEvent implements StratInitEvent {
	private final Selection.Source selectionSource;

	public SelectNationEvent(Selection.Source selectionSource) {
		this.selectionSource = selectionSource;

	}

	public Selection.Source getSelectionSource() {
		return selectionSource;
	}
}
