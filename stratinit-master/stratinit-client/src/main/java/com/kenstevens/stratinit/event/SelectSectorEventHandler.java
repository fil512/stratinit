package com.kenstevens.stratinit.event;

import com.google.gwt.event.shared.EventHandler;
import com.kenstevens.stratinit.ui.selection.Selection.Source;

public abstract class SelectSectorEventHandler implements EventHandler {
	abstract public void selectSector(Source selectionSource);
}
