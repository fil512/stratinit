package com.kenstevens.stratinit.client.control;

import com.google.common.eventbus.Subscribe;
import com.kenstevens.stratinit.client.api.ShellMessage;
import com.kenstevens.stratinit.client.api.ShellMessage.Type;
import com.kenstevens.stratinit.client.api.StatusReportEvent;
import com.kenstevens.stratinit.client.event.StratinitEventBus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Scope("prototype")
@Component
public class HistoryTextControl implements Controller {
	private final StyledText styledText;
	@Autowired
	private StratinitEventBus eventBus;

	public HistoryTextControl(StyledText styledText) {
		this.styledText = styledText;
	}

	@Subscribe
	public void handleStatusReportEvent(StatusReportEvent event) {
		final ShellMessage shellMessage = event.getMessage();
		if (shellMessage.getText() == null || shellMessage.getText().isEmpty()) {
			return;
		}
		final Display display = Display.getDefault();

		if (display.isDisposed())
			return;
		display.asyncExec(new Runnable() {
			public void run() {
				updateStyledText(shellMessage, display);
			}
		});
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void addObservers() {
		eventBus.register(this);

	}

	private void updateStyledText(final ShellMessage shellMessage, final Display display) {
		if (styledText.isDisposed())
			return;
		String text = shellMessage.getText() + "\n";
		Type type = shellMessage.getType();
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
			if (shellMessage.isError()) {
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
