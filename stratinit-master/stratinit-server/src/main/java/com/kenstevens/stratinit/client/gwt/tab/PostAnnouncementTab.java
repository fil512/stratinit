package com.kenstevens.stratinit.client.gwt.tab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.kenstevens.stratinit.client.gwt.service.GWTGameService;
import com.kenstevens.stratinit.client.gwt.service.GWTGameServiceAsync;
import com.kenstevens.stratinit.client.gwt.service.GWTResult;
import com.kenstevens.stratinit.client.gwt.service.StatusSetterAsyncCallback;
import com.kenstevens.stratinit.client.gwt.status.StatusSetter;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.VStack;

public class PostAnnouncementTab extends Canvas {


	private TextItem subjectField;
	private TextAreaItem bodyField;
	private GWTGameServiceAsync gameServiceAsync = GWT
	.create(GWTGameService.class);
	private StatusSetter statusSetter;

	public PostAnnouncementTab(StatusSetter statusSetter) {
		this.setWidth(470);
		this.setHeight(400);
		this.statusSetter = statusSetter;

		subjectField = new TextItem("Subject");
		subjectField.setWidth(400);
		bodyField = new TextAreaItem("Body");
		bodyField.setWidth(400);
		bodyField.setHeight(240);

		DynamicForm form = new DynamicForm();
		form.setFields(subjectField, bodyField);

		IButton postButton = new IButton("Post");
		postButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent e) {
				post();
			}
		});

		VStack column = new VStack();
		column.addMember(form);
		column.addMember(postButton);

		this.addChild(column);
	}

	private void post() {
		// Set up the callback object.
		AsyncCallback<GWTResult<Integer>> callback = new StatusSetterAsyncCallback<Integer>(statusSetter);
		String subject = "";
		String body = "";
		if (subjectField.getValue() != null) {
			subject = subjectField.getValue().toString();
		}
		if (bodyField.getValue() != null) {
			body = bodyField.getValue().toString();
		}
		gameServiceAsync.postAnnouncement(subject, body, callback);
	}
}
