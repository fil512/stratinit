package com.kenstevens.stratinit.control.selection;

import com.kenstevens.stratinit.control.selection.Selection.Source;
import com.kenstevens.stratinit.event.StratInitEvent;

public class SelectNationEvent extends StratInitEvent<SelectNationEventHandler> {
	public static final Type<SelectNationEventHandler> TYPE = new Type<SelectNationEventHandler>();
	private final Source selectionSource;
	
	public SelectNationEvent(Selection.Source selectionSource) {
		this.selectionSource = selectionSource;
		
	}
	
	@Override
	protected void dispatch(SelectNationEventHandler handler) {
		handler.selectNation(selectionSource);
	}

}
