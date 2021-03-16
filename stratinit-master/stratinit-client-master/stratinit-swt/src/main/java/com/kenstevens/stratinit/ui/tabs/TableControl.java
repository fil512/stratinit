package com.kenstevens.stratinit.ui.tabs;

import com.kenstevens.stratinit.client.event.StratinitEventBus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.springframework.beans.factory.annotation.Autowired;

public class TableControl {
	@Autowired
	protected StratinitEventBus eventBus;

	protected Font getBoldTableFont(Table table, Display display) {
		FontData fd = table.getFont().getFontData()[0];
		fd.setStyle(SWT.BOLD);
		return new Font(display, fd);
	}
}
