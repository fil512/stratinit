package com.kenstevens.stratinit.client.control.selection;

import com.kenstevens.stratinit.client.event.StratInitEvent;

public class SelectSectorEvent implements StratInitEvent {
	private final Selection.Source selectionSource;

	public SelectSectorEvent(Selection.Source selectionSource) {
		this.selectionSource = selectionSource;

	}

	public Selection.Source getSelectionSource() {
		return selectionSource;
	}

}
