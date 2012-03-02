package com.kenstevens.stratinit.event;

import com.kenstevens.stratinit.ui.selection.Selection;
import com.kenstevens.stratinit.ui.selection.Selection.Source;

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
