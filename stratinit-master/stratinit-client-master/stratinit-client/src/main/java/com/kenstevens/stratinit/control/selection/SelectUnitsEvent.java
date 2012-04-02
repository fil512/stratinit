package com.kenstevens.stratinit.control.selection;

import com.kenstevens.stratinit.control.selection.Selection.Source;
import com.kenstevens.stratinit.event.StratInitEvent;

//TODO REF this should probably be a type of selectSector event

public class SelectUnitsEvent extends StratInitEvent<SelectUnitsEventHandler> {
	public static final Type<SelectUnitsEventHandler> TYPE = new Type<SelectUnitsEventHandler>();
	private final Source selectionSource;
	
	public SelectUnitsEvent(Selection.Source selectionSource) {
		this.selectionSource = selectionSource;
		
	}
	
	@Override
	protected void dispatch(SelectUnitsEventHandler handler) {
		handler.selectUnits(selectionSource);
	}

}
