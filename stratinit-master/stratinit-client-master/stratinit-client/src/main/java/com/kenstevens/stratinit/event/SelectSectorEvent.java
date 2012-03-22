package com.kenstevens.stratinit.event;

import com.kenstevens.stratinit.ui.selection.Selection;
import com.kenstevens.stratinit.ui.selection.Selection.Source;

public class SelectSectorEvent extends StratInitEvent<SelectSectorEventHandler> {
	public static final Type<SelectSectorEventHandler> TYPE = new Type<SelectSectorEventHandler>();
	private final Source selectionSource;
	
	public SelectSectorEvent(Selection.Source selectionSource) {
		this.selectionSource = selectionSource;
		
	}
	
	@Override
	protected void dispatch(SelectSectorEventHandler handler) {
		handler.selectSector(selectionSource);
	}

}
