package com.kenstevens.stratinit.wicket.framework;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

class LoginForm extends Form<LoginForm> {

    private String username;

    private String password;

    public LoginForm(String id) {
        super(id);
        setModel(new CompoundPropertyModel<>(this));
        add(new FeedbackPanel("feedback"));
        add(new RequiredTextField<String>("username"));
        add(new PasswordTextField("password"));
    }

    @Override
    protected void onSubmit() {
        AuthenticatedWebSession session = AuthenticatedWebSession.get();
        if (session.signIn(username, password)) {
            setResponsePage(HomePage.class);
        } else {
            error("Login failed");
        }
    }
}
