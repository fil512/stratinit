package com.kenstevens.stratinit.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.springframework.stereotype.Component;

@Component
public class SplashWindow {

	private ProgressBar progressBar;
	private Shell splash;
	private Image image;

	public void open(final int barMaximum) {
		final Display display = Display.getDefault();
		image = new Image(display, 300, 300);
		splash = new Shell(SWT.ON_TOP);
		progressBar = new ProgressBar(splash, SWT.NONE);
		progressBar.setMaximum(barMaximum);
		Label label = new Label(splash, SWT.NONE);
		label.setImage(image);
		FormLayout layout = new FormLayout();
		splash.setLayout(layout);
		FormData labelData = new FormData();
		labelData.right = new FormAttachment(100, 0);
		labelData.bottom = new FormAttachment(100, 0);
		label.setLayoutData(labelData);
		FormData progressData = new FormData();
		progressData.left = new FormAttachment(0, 5);
		progressData.right = new FormAttachment(100, -5);
		progressData.bottom = new FormAttachment(100, -5);
		progressBar.setLayoutData(progressData);
		splash.pack();
		Rectangle splashRect = splash.getBounds();
		Rectangle displayRect = display.getBounds();
		int x = (displayRect.width - splashRect.width) / 2;
		int y = (displayRect.height - splashRect.height) / 2;
		splash.setLocation(x, y);
		splash.open();

	}

	public void pushProgressBar() {
		final Display display = Display.getDefault();
		display.asyncExec(new Runnable() {
			public void run() {
				int selection = progressBar.getSelection();
				progressBar.setSelection(++selection);
			}
		});
	}

	public void close() {
		final Display display = Display.getDefault();
		display.asyncExec(new Runnable() {
			public void run() {
				splash.close();
				image.dispose();
			}
		});
	}
}
