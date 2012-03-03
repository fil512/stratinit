package com.kenstevens.stratinit.ui.window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Shell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.ui.shell.StratInitWindow;


@Component
public class MapWindow implements StratInitWindow {
	@Autowired
	private SmallMapImageManager smallMapImageManager;

	private Canvas canvas;

	public void open(Shell shell) {
		Shell dialog = new Shell(shell);
		dialog.setLayout(new FillLayout());

		Rectangle rect = smallMapImageManager.getSmallBounds();
		dialog.setSize(rect.width+10, rect.height+40);
		dialog.setLocation(0, 0);

		createContents(dialog);
		dialog.layout();
		dialog.open();
	}

	private void createContents(Shell shell) {
		canvas = new Canvas(shell, SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE);
	}

	public Canvas getCanvas() {
		return canvas;
	}

}
