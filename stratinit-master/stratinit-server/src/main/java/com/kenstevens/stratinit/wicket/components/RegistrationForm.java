package com.kenstevens.stratinit.wicket.components;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.value.ValueMap;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.StratInit;
import com.kenstevens.stratinit.server.daoservice.PlayerDaoService;
import com.kenstevens.stratinit.wicket.util.InfoResult;

public class RegistrationForm extends Form<ValueMap> {
	private static final long serialVersionUID = 1L;

	@SpringBean
	StratInit stratInit;
	@SpringBean
	PlayerDaoService playerDaoService;

	public RegistrationForm(String id) {
		super(id, new CompoundPropertyModel<ValueMap>(new ValueMap()));

		add(new RequiredTextField<String>("username", String.class));
		add(new RequiredTextField<String>("email", String.class)
				.add(EmailAddressValidator.getInstance()));

		Label pwdLabel = new Label("pwdLabel", "Password:");
		PasswordTextField passwordTextField = new PasswordTextField("password");
		Label confPwdLabel = new Label("confPwdLabel", "Confirm password:");
		PasswordTextField passwordConfirmTextField = new PasswordTextField(
				"passwordConfirm", new Model<String>());
		add(new EqualPasswordInputValidator(passwordTextField,
				passwordConfirmTextField));
		add(passwordTextField);
		add(passwordConfirmTextField);
		add(pwdLabel);
		add(confPwdLabel);
	}

	@Override
	public final void onSubmit() {
		ValueMap values = getModelObject();
		ShaPasswordEncoder encoder = new ShaPasswordEncoder();

		String username = (String) values.get("username");
		String email = (String) values.get("email");
		String password = (String) values.get("password");
		String userAgent = this.getWebRequest().getHeader("User-Agent");

		Result<Player> result = playerDaoService.register(username,
				encoder.encodePassword(password, null), email, userAgent);
		new InfoResult<Player>(this).info(result);
	}
}
