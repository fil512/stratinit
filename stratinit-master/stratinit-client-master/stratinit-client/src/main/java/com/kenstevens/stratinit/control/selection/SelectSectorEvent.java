package com.kenstevens.stratinit.control.selection;

import com.kenstevens.stratinit.control.selection.Selection.Source;
import com.kenstevens.stratinit.event.StratInitEvent;

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
