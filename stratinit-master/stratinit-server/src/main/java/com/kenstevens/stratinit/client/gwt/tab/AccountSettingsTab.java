package com.kenstevens.stratinit.client.gwt.tab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.kenstevens.stratinit.client.gwt.model.GWTPlayer;
import com.kenstevens.stratinit.client.gwt.service.GWTGameService;
import com.kenstevens.stratinit.client.gwt.service.GWTGameServiceAsync;
import com.kenstevens.stratinit.client.gwt.service.GWTNone;
import com.kenstevens.stratinit.client.gwt.service.GWTResult;
import com.kenstevens.stratinit.client.gwt.service.StatusSetterAsyncCallback;
import com.kenstevens.stratinit.client.gwt.status.StatusSetter;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.VStack;

public class AccountSettingsTab extends Canvas {
	private PasswordItem newPasswordField;
	private TextItem emailField;
	private CheckboxItem emailGameMailCheckBox;
	private GWTGameServiceAsync gameServiceAsync = GWT
	.create(GWTGameService.class);
	private StatusSetter statusSetter;

	public AccountSettingsTab(StatusSetter statusSetter) {
		this.setWidth(470);
		this.setHeight(400);
		this.statusSetter = statusSetter;

		newPasswordField = new PasswordItem("New password", "password");
		newPasswordField.setWidth(120);
		emailField = new TextItem("Email", "email");
		emailField.setWidth(120);
		emailGameMailCheckBox = new CheckboxItem("E-mail me in-game e-mails?");

		DynamicForm form = new DynamicForm();
		form.setFields(newPasswordField, emailField, emailGameMailCheckBox);

		IButton updatePlayerButton = new IButton("Save");
		updatePlayerButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent e) {
				savePlayer();
			}
		});

		VStack column = new VStack();
		column.addMember(form);
		column.addMember(updatePlayerButton);

		this.addChild(column);
		getPlayerData();
	}

	private void getPlayerData() {
		// Set up the callback object.
		AsyncCallback<GWTResult<GWTPlayer>> callback = new AsyncCallback<GWTResult<GWTPlayer>>() {
			public void onFailure(Throwable caught) {
				statusSetter.setText(caught.getMessage());
			}

			public void onSuccess(GWTResult<GWTPlayer> result) {
				if (result.success) {
					GWTPlayer player = result.getValue();
					newPasswordField.setValue(player.getPassword());
					emailField.setValue(player.getEmail());
					emailGameMailCheckBox.setValue(player.isEmailGameMail());
				} else {
					statusSetter.setText("ERROR: "+result.getLastMessage());
				}

			}
		};
		gameServiceAsync.fetchPlayer(callback);
	}

	protected void savePlayer() {
		// Set up the callback object.
		AsyncCallback<GWTResult<GWTNone>> callback = new StatusSetterAsyncCallback<GWTNone>(statusSetter);
		String newPassword = "";
		String email = "";
		if (newPasswordField.getValue() != null) {
			newPassword = newPasswordField.getValue().toString();
		}
		if (emailField.getValue() != null) {
			email = emailField.getValue().toString();
		}
		gameServiceAsync.updatePlayer(
				newPassword,
				email, emailGameMailCheckBox.getValueAsBoolean(), callback);
	}

}
