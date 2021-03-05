package com.kenstevens.stratinit.client.ui.adapter;

import com.kenstevens.stratinit.client.control.selection.SelectEvent;
import com.kenstevens.stratinit.client.shell.TopShell;
import com.kenstevens.stratinit.client.site.action.post.ActionFactory;
import com.kenstevens.stratinit.client.util.Spring;
import org.eclipse.swt.events.SelectionAdapter;

public class StratinitSelectionAdapter extends SelectionAdapter {
	protected final SelectEvent selectEvent;
	protected final Spring spring;
	protected final TopShell topShell;
	protected final ActionFactory actionFactory;
	
	public StratinitSelectionAdapter(SelectEvent selectEvent, Spring spring,
			ActionFactory actionFactory, TopShell topShell) {
				this.selectEvent = selectEvent;
				this.spring = spring;
				this.actionFactory = actionFactory;
				this.topShell = topShell;
	}
}
