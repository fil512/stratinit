package com.kenstevens.stratinit.control;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gwt.event.shared.HandlerManager;
import com.kenstevens.stratinit.ui.shell.Message;
import com.kenstevens.stratinit.ui.shell.StatusReportEvent;
import com.kenstevens.stratinit.ui.shell.StatusReportEventHandler;
import com.kenstevens.stratinit.ui.shell.Message.Type;

@Scope("prototype")
@Component
public class HistoryTextControl implements Controller {
	private final StyledText styledText;
	@Autowired
	private HandlerManager handlerManager;

	public HistoryTextControl(StyledText styledText) {
		this.styledText = styledText;
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void addObservers() {
		handlerManager.addHandler(StatusReportEvent.TYPE,
				new StatusReportEventHandler() {

					@Override
					public void reportStatus(final Message message) {
						if (message.getText() == null || message.getText().isEmpty()) {
							return;
						}
						final Display display = Display.getDefault();

						if (display.isDisposed())
							return;
						display.asyncExec(new Runnable() {
							public void run() {
								updateStyledText(message, display);
							}
						});

					}
				});
	}

	private void updateStyledText(final Message message,
			final Display display) {
		if (styledText.isDisposed())
			return;
		String text = message.getText()+"\n";
		Type type = message.getType();
		int start = styledText.getCharCount();
		int length = text.length();
		int startLine = styledText.getLineCount() - 1;
		styledText.append(text);
		if (type != Type.ACTION) {
			StyleRange style = new StyleRange();
			style.start = start;
			style.length = length;
			style.fontStyle = SWT.BOLD;
			styledText.setStyleRange(style);
			styledText.setLineIndent(startLine, 1, 40);
			if (message.isError()) {
				style = new StyleRange();
				style.start = start;
				style.length = length;
				style.fontStyle = SWT.BOLD;
				style.foreground = display.getSystemColor(SWT.COLOR_RED);
				styledText.setStyleRange(style);
			}
		}
		styledText.layout();
		styledText.setSelection(start);
	}
}
