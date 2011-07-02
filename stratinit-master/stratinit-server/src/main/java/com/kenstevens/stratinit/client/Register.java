package com.kenstevens.stratinit.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.kenstevens.stratinit.client.gwt.service.GWTNone;
import com.kenstevens.stratinit.client.gwt.service.GWTRegisterService;
import com.kenstevens.stratinit.client.gwt.service.GWTRegisterServiceAsync;
import com.kenstevens.stratinit.client.gwt.service.GWTResult;
import com.kenstevens.stratinit.client.gwt.widget.ExceptionWindow;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Register implements EntryPoint {
	protected static final String HOME = "../index.html";
	private final Label errorLabel = new Label();
	private GWTRegisterServiceAsync registerServiceAsync = GWT
			.create(GWTRegisterService.class);
	private TextItem usernameItem;
	private TextItem emailItem;
	private PasswordItem passwordItem;
	private IButton forgottenButton;
	private IButton emailForgottenButton;
	private VLayout layout;
	private IButton registerButton;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

        layout = new VLayout(20);

        final DynamicForm form = new DynamicForm();
        usernameItem = new TextItem();
        usernameItem.setTitle("Username");
        usernameItem.setRequired(true);

        emailItem = new TextItem();
        emailItem.setTitle("Email");
        emailItem.setRequired(true);

        passwordItem = new PasswordItem();
        passwordItem.setTitle("Password");
        passwordItem.setRequired(true);

        form.setFields(new FormItem[] {usernameItem, emailItem, passwordItem});
        registerButton = new IButton("Register");
		// Listen for mouse events on the Add button.
		registerButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
				register();
			}
		});

        forgottenButton = new IButton("I forgot my password");
        forgottenButton.setAutoFit(true);
		// Listen for mouse events on the Add button.
        forgottenButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
				forgotten();
			}
		});

        emailForgottenButton = new IButton("Email me my password");
        emailForgottenButton.setAutoFit(true);
		// Listen for mouse events on the Add button.
        emailForgottenButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
				emailForgotten();
				setRegister(true);
            }
		});
        setRegister(true);
        
        Img img = new Img("../../images/Splash.jpg", 495, 194);
        layout.addMember(img);
        layout.addMember(form);
        layout.addMember(errorLabel);
        layout.addMember(registerButton);
        layout.addMember(forgottenButton);
        layout.addMember(emailForgottenButton);

        layout.draw();

		// Listen for keyboard events in the input box.
		passwordItem.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getKeyName().equals("Enter")) {
					register();
				}
			}
		});
		
		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
			public void onUncaughtException(Throwable e) {
				new ExceptionWindow().open(e);
			}
		});
	}

	protected void emailForgotten() {
		assertProxy();

		// Set up the callback object.
		AsyncCallback<GWTResult<GWTNone>> callback = new AsyncCallback<GWTResult<GWTNone>>() {
			public void onFailure(Throwable caught) {
				errorLabel.setContents(caught.getMessage());
			}

			public void onSuccess(GWTResult<GWTNone> result) {
				if (result.success) {
					// TODO GWT how to do this in smartgwt
					Window.open(HOME, "_self", "");
					errorLabel.setContents(result.getLastMessage());
				} else {
					errorLabel.setContents(result.getLastMessage());
				}

			}
		};
		String username = null;
		if (usernameItem.getValue() != null) {
			username = usernameItem.getValue().toString();
		}
		String email = null;
		if (emailItem.getValue() != null) {
			email = emailItem.getValue().toString();
		}
		registerServiceAsync.forgottenPassword(username,
				email, callback);
	}

	private void assertProxy() {
		// Initialize the service proxy.
		if (registerServiceAsync == null) {
			registerServiceAsync = GWT.create(GWTRegisterService.class);
		}
	}
	
	protected void forgotten() {
		setRegister(false);
	}

	private void setRegister(boolean register) {
		passwordItem.setVisible(register);
		registerButton.setVisible(register);
		forgottenButton.setVisible(register);
        emailForgottenButton.setVisible(!register);
        if (!register) {
        	errorLabel.setContents("Please enter your username or e-mail address and click ");
        }
	}

	private void register() {
		assertProxy();

		// Set up the callback object.
		AsyncCallback<GWTResult<GWTNone>> callback = new AsyncCallback<GWTResult<GWTNone>>() {
			public void onFailure(Throwable caught) {
				errorLabel.setContents(caught.getMessage());
			}

			public void onSuccess(GWTResult<GWTNone> result) {
				if (result.success) {
					// TODO GWT how to do this in smartgwt
					Window.open(HOME, "_self", "");
					errorLabel.setContents(result.getLastMessage());
				} else {
					errorLabel.setContents(result.getLastMessage());
				}

			}
		};
		registerServiceAsync.register(usernameItem.getValue().toString(),
				passwordItem.getValue().toString(), emailItem.getValue().toString(), callback);
	}
}
