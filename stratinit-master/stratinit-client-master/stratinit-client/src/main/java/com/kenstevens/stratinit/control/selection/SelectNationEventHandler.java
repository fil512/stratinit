package com.kenstevens.stratinit.control.selection;

import com.google.gwt.event.shared.EventHandler;
import com.kenstevens.stratinit.control.selection.Selection.Source;

public abstract class SelectNationEventHandler implements EventHandler {
	abstract public void selectNation(Source selectionSource);
}
