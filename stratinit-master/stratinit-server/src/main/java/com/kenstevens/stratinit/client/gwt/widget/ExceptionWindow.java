package com.kenstevens.stratinit.client.gwt.widget;

import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;

public class ExceptionWindow {

	private final Window window = new Window();
	private TextAreaItem textArea;

	public ExceptionWindow() {
		window.setTitle("An Error Occurred");
		window.setTop(10);
		window.setLeft(10);
		window.setAutoSize(true);
		window.setCanDragReposition(true);
		window.setCanDragResize(true);
		final DynamicForm form = new DynamicForm();
		textArea = new TextAreaItem();
		textArea.setShowTitle(false);
		textArea.setWidth(900);
		textArea.setHeight(600);
		form.setFields(textArea);
		form.setPadding(5);
		form.setHeight100();
		form.setWidth100();
		window.addItem(form);
	}

	public void open(Throwable e) {
		textArea.setValue("Please report this error at http://groups.google.com/group/stratinit\n\n"+e.toString());
		window.show();
	}
}
