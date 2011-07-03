package com.kenstevens.stratinit.ui.tabs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gwt.event.shared.HandlerManager;

public class TableControl {
	@Autowired
	protected HandlerManager handlerManager;

	protected Font getBoldTableFont(Table table, Display display) {
		FontData fd = table.getFont().getFontData()[0];
		fd.setStyle(SWT.BOLD);
		return new Font(display, fd);
	}
}
