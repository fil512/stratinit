package com.kenstevens.stratinit.client.gwt.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.kenstevens.stratinit.client.gwt.callback.AutoErrorHandlingAsyncCallback;
import com.kenstevens.stratinit.client.gwt.callback.LoginWindow;
import com.kenstevens.stratinit.client.gwt.service.GWTAuthorizationService;
import com.kenstevens.stratinit.client.gwt.service.GWTAuthorizationServiceAsync;
import com.kenstevens.stratinit.client.gwt.service.GWTResult;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyDownEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyDownHandler;
import com.smartgwt.client.widgets.layout.VStack;

public class LoginWindowImpl implements LoginWindow {

	private final Window window = new Window();

	private TextItem usernameField;

	private TextItem passwordField;

	private GWTAuthorizationServiceAsync authorizationServiceAsync = GWT
			.create(GWTAuthorizationService.class);

	private Label statusLabel;

	public LoginWindowImpl() {
		window.setTitle("Login");
		window.setWidth(200);
		window.setHeight(400);
		window.setAutoSize(true);
		window.setTop(240);
		window.setLeft(140);
		window.setCanDragReposition(true);
		window.setCanDragResize(true);
		Canvas canvas = new Canvas();

		statusLabel = new Label();
		statusLabel.setContents("Please login");
		usernameField = new TextItem("Username", "username");
		usernameField.setWidth(120);
		passwordField = new PasswordItem("Password", "password");
		passwordField.setWidth(120);
		passwordField.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getKeyName().equals("Enter")) {
					login();
				}
			}

		});

		DynamicForm form = new DynamicForm();
		form.setAutoFocus(true);
		form.setFields(usernameField, passwordField);
		form.focusInItem(usernameField);
		IButton loginButton = new IButton("Login");
		loginButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent e) {
				login();
			}
		});
		VStack vstack = new VStack();
		vstack.addMember(statusLabel);
		vstack.addMember(form);
		vstack.addMember(loginButton);
		canvas.addChild(vstack);
		window.addItem(canvas);
	}

	public void open() {
		window.show();
	}

	private void login() {

		AsyncCallback<GWTResult<String>> callback = new AutoErrorHandlingAsyncCallback<GWTResult<String>>(new LoginWindowImpl()) {

			public void onSuccess(GWTResult<String> result) {
				if (result.isSuccess()) {
					window.destroy();
					com.google.gwt.user.client.Window.Location.reload();
				} else {
					statusLabel.setContents(result.getLastMessage());
				}
			}

		};
		String username = (String)usernameField.getValue();
		String password = (String)passwordField.getValue();
		authorizationServiceAsync.login(username, password, callback);
	}
}
