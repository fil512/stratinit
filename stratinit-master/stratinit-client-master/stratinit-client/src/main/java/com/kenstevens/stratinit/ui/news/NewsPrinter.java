package com.kenstevens.stratinit.ui.news;

import com.kenstevens.stratinit.shell.ColourMap;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;

import java.util.List;

public class NewsPrinter {

	protected final StyledText styledText;

	public NewsPrinter(StyledText styledText) {
		this.styledText = styledText;
	}

	protected void append(String text) {
		if (text.trim().isEmpty()) {
			styledText.append("\n");
		} else {
			styledText.append("\t- "+text+"\n");
		}
	}

	protected void appendAll(List<String> strings) {
		for (String text : strings) {
			append(text);
		}
	}

	protected void addText(String text, StyleRange style) {
		int start = styledText.getCharCount();
		int length = text.length();
		styledText.append(text);
		style.start = start;
		style.length = length;
		styledText.setStyleRange(style);
	}

	protected void printTitle(String title) {
		StyleRange style = new StyleRange();
		style.fontStyle = SWT.BOLD;
		style.background = ColourMap.WIDGET_LIGHT_SHADOW;
		addText("\n"+title+"\n", style);
	}

	protected void printSubtitle(String title) {
		StyleRange style = new StyleRange();
		style.fontStyle = SWT.BOLD;
		addText("\n"+title+"\n", style);
	}

	protected void printSubSubTitle(String title) {
		StyleRange style = new StyleRange();
		style.fontStyle = SWT.ITALIC;
		addText("\n"+title+"\n", style);
	}

}