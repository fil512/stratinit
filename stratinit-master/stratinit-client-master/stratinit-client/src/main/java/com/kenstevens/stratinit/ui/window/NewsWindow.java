package com.kenstevens.stratinit.ui.window;

import com.kenstevens.stratinit.dto.news.SINewsLogsDay;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.model.Message;
import com.kenstevens.stratinit.model.NewsLogList;
import com.kenstevens.stratinit.shell.ColourMap;
import com.kenstevens.stratinit.shell.StratInitWindow;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.ui.news.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Shell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class NewsWindow implements StratInitWindow {
	private StyledText styledText;
	private Shell dialog;

	@Autowired
	private Data db;

	/**
	 * Open the window
	 * @wbp.parser.entryPoint
	 */
	public void open(Shell shell) {
		dialog = new Shell(shell);
		dialog.setLayout(new FormLayout());
		createContents();
		dialog.open();
	}


	private void createContents() {
		dialog.setSize(526, 506);
		dialog.setText("News");

		styledText = new StyledText(dialog, SWT.BORDER| SWT.V_SCROLL | SWT.H_SCROLL | SWT.READ_ONLY | SWT.RESIZE);
		final FormData fdStyledText = new FormData();
		fdStyledText.bottom = new FormAttachment(100, -11);
		fdStyledText.right = new FormAttachment(100, -8);
		fdStyledText.left = new FormAttachment(0, 10);
		fdStyledText.top = new FormAttachment(0, 35);
		styledText.setLayoutData(fdStyledText);
	}

	private void addText(String text, StyleRange style) {
		int start = styledText.getCharCount();
		int length = text.length();
		styledText.append(text);
		style.start = start;
		style.length = length;
		styledText.setStyleRange(style);
	}

	private void addNewsContents(SINewsLogsDay siNewsDay) {
		new BulletinPrinter(styledText).print(siNewsDay.getBulletins());
		new ForeignAffairsPrinter(styledText).print(siNewsDay.getForeignAffairs());
		new VersusPrinter(styledText).print(siNewsDay);
		new NeutralConquestPrinter(styledText).print(siNewsDay.getNeutralConquests());
		new FirstsPrinter(styledText).print(siNewsDay.getFirsts());
	}

	private boolean printMessageChanges() {
		Date lastLoginTime = db.getLastLoginTime();
		styledText.append("Last login time: "+lastLoginTime+"\n\n");
		
		lastLoginTime = new Date(lastLoginTime.getTime() - Constants.SECONDS_PER_DAY * 1000);
		List<Message> newMessageBoardMessages = db.getMessageBoard().newMessages(lastLoginTime);
		if (!newMessageBoardMessages.isEmpty()) {
			styledText.append("Recent posts:\n");
			for (Message message : newMessageBoardMessages) {
				styledText.append("  "+message.getAuthor()+": "+message.getSubject()+"\n");
			}
		}
		return !newMessageBoardMessages.isEmpty();
	}

	public void updateNews(NewsLogList newsList) {
		Date since = db.getLastLoginTime();
		if (since == null) {
			since = new Date();
		}

		printMessageChanges();

		for (SINewsLogsDay sinewsDay : newsList) {
			printDayHeader(sinewsDay);
			addNewsContents(sinewsDay);
		}
	}


	private void printDayHeader(SINewsLogsDay sinewsDay) {
		StyleRange style = new StyleRange();
		style.fontStyle = SWT.BOLD;
		style.background = ColourMap.WIDGET_NORMAL_SHADOW;
		addText("\n  == Day "+(sinewsDay.getDay()-1)+" ==\n", style);
	}

	public boolean isDisposed() {
		return dialog.isDisposed();
	}
}
