package com.kenstevens.stratinit.control;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class TextControl {
	private final Control control;

	public TextControl(Control control) {
		this.control = control;
	}

	public void setText(final String message, final int color) {
		final Display display = Display.getDefault();

		if (control.isDisposed()) {
			return;
		}
		display.asyncExec(new Runnable() {
			public void run() {
				if (control == null || message == null) {
					return;
				}
				if (control.isDisposed())
					return;
				control.setForeground(display.getSystemColor(color));
				if (control instanceof Label) {
					((Label)control).setText(message);
				} else if (control instanceof Text) {
					((Text)control).setText(message);
				}
			}
		});
	}

	public void setError(String message) {
		setText(message, SWT.COLOR_RED);
	}

	public void setMessage(String message) {
		setText(message, SWT.COLOR_BLACK);
	}
}
