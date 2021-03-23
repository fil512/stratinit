package com.kenstevens.stratinit.client.shell;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BrowserWindow {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private Browser browser;

	private Text location;

	public void open(Shell shell, String targetUrl) {
		Shell window = new Shell(shell);
		createContents(window, targetUrl);

		window.open();

		browser.setUrl(targetUrl);
	}

	public void createContents(Shell shell, String targetUrl) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		shell.setLayout(gridLayout);
		ToolBar toolbar = new ToolBar(shell, SWT.NONE);
		ToolItem itemBack = new ToolItem(toolbar, SWT.PUSH);
		itemBack.setText("Back");
		ToolItem itemForward = new ToolItem(toolbar, SWT.PUSH);
		itemForward.setText("Forward");
		ToolItem itemStop = new ToolItem(toolbar, SWT.PUSH);
		itemStop.setText("Stop");
		ToolItem itemRefresh = new ToolItem(toolbar, SWT.PUSH);
		itemRefresh.setText("Refresh");
		ToolItem itemGo = new ToolItem(toolbar, SWT.PUSH);
		itemGo.setText("Go");

		GridData data = createData(shell, toolbar);
		try {
			browser = new Browser(shell, SWT.NONE);
		} catch (SWTError e) {
			logger.error("Could not instantiate Browser: "+e.getMessage(), e);
			return;
		}
		browser.setLayoutData(data);

		final Label status = new Label(shell, SWT.NONE);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		status.setLayoutData(data);

		final ProgressBar progressBar = new ProgressBar(shell, SWT.NONE);
		data = new GridData();
		data.horizontalAlignment = GridData.END;
		progressBar.setLayoutData(data);

		Listener listener = addListeners(location, status, progressBar);
		itemBack.addListener(SWT.Selection, listener);
		itemForward.addListener(SWT.Selection, listener);
		itemStop.addListener(SWT.Selection, listener);
		itemRefresh.addListener(SWT.Selection, listener);
		itemGo.addListener(SWT.Selection, listener);
		location.addListener(SWT.DefaultSelection, new Listener() {
			public void handleEvent(Event e) {
				browser.setUrl(location.getText());
			}
		});

	}

	private GridData createData(Shell shell, ToolBar toolbar) {
		GridData data = new GridData();
		data.horizontalSpan = 3;
		toolbar.setLayoutData(data);

		Label labelAddress = new Label(shell, SWT.NONE);
		labelAddress.setText("Address");

		location = new Text(shell, SWT.BORDER);
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.horizontalSpan = 2;
		data.grabExcessHorizontalSpace = true;
		location.setLayoutData(data);

		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.horizontalSpan = 3;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		return data;
	}

	private Listener addListeners(final Text location, final Label status,
			final ProgressBar progressBar) {
		/* event handling */
		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				ToolItem item = (ToolItem) event.widget;
				String string = item.getText();
				if (string.equals("Back")) {
					browser.back();
				} else if (string.equals("Forward")) {
					browser.forward();
				} else if (string.equals("Stop")) {
					browser.stop();
				} else if (string.equals("Refresh")) {
					browser.refresh();
				} else if (string.equals("Go")) {
					browser.setUrl(location.getText());
				}
			}
		};
		browser.addProgressListener(new ProgressListener() {
			public void changed(ProgressEvent event) {
				if (event.total == 0)
					return;
				int ratio = event.current * 100 / event.total;
				progressBar.setSelection(ratio);
			}

			public void completed(ProgressEvent event) {
				progressBar.setSelection(0);
			}
		});
		browser.addStatusTextListener(new StatusTextListener() {
			public void changed(StatusTextEvent event) {
				status.setText(event.text);
			}
		});
		browser.addLocationListener(new LocationListener() {
			public void changed(LocationEvent event) {
				if (event.top) {
					location.setText(event.location);
				}
			}

			public void changing(LocationEvent event) {
			}
		});
		return listener;
	}
}
