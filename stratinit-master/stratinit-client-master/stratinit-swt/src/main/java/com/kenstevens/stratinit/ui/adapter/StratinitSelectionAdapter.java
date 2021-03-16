package com.kenstevens.stratinit.ui.adapter;

import com.kenstevens.stratinit.client.api.IEventSelector;
import com.kenstevens.stratinit.client.site.action.post.ActionFactory;
import com.kenstevens.stratinit.client.util.Spring;
import com.kenstevens.stratinit.shell.TopShell;
import org.eclipse.swt.events.SelectionAdapter;

public class StratinitSelectionAdapter extends SelectionAdapter {
	protected final IEventSelector eventSelector;
	protected final Spring spring;
	protected final TopShell topShell;
	protected final ActionFactory actionFactory;
	
	public StratinitSelectionAdapter(IEventSelector eventSelector, Spring spring,
									 ActionFactory actionFactory, TopShell topShell) {
				this.eventSelector = eventSelector;
				this.spring = spring;
				this.actionFactory = actionFactory;
				this.topShell = topShell;
	}
}
