package com.kenstevens.stratinit.client.shell;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public final class ColourMap {
	private static final Display DISPLAY = Display.getDefault();

	public static final Color WHITE = DISPLAY.getSystemColor(SWT.COLOR_WHITE);
	public static final Color GRAY = DISPLAY.getSystemColor(SWT.COLOR_GRAY);
	public static final Color RED = DISPLAY.getSystemColor(SWT.COLOR_RED);
	public static final  Color DARK_RED = DISPLAY.getSystemColor(SWT.COLOR_DARK_RED);
	public static final Color BLACK = DISPLAY.getSystemColor(SWT.COLOR_BLACK);
	public static final Color WIDGET_BACKGROUND = DISPLAY.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
	public static final Color WIDGET_FOREGROUND = DISPLAY.getSystemColor(SWT.COLOR_WIDGET_FOREGROUND);
	public static final Color WIDGET_LIGHT_SHADOW = DISPLAY.getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
	public static final Color WIDGET_HIGHLIGHT_SHADOW = DISPLAY.getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW);
	public static final Color WIDGET_NORMAL_SHADOW = DISPLAY.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
	public static final Color YELLOW = DISPLAY.getSystemColor(SWT.COLOR_YELLOW);

	private ColourMap() {}
}
