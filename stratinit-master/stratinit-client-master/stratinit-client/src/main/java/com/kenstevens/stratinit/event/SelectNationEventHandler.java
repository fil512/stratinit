package com.kenstevens.stratinit.event;

import com.google.gwt.event.shared.EventHandler;
import com.kenstevens.stratinit.ui.selection.Selection.Source;

public abstract class SelectNationEventHandler implements EventHandler {
	abstract public void selectNation(Source selectionSource);
}
