package com.kenstevens.stratinit.wicket.framework;

import com.kenstevens.stratinit.client.model.Player;
import com.kenstevens.stratinit.client.server.daoservice.PlayerDaoService;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.wicket.util.InfoResult;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class RegistrationForm extends Form<Player> {
	private static final long serialVersionUID = 1L;

	@SpringBean
	PlayerDaoService playerDaoService;
	@SpringBean
	BCryptPasswordEncoder passwordEncoder;

	public RegistrationForm(String id, Player player) {
		super(id, new CompoundPropertyModel<Player>(player));

		RequiredTextField<String> usernameField = new RequiredTextField<String>("username", String.class);
		add(usernameField);
		if (getAuth().isSignedIn()) {
			usernameField.setEnabled(false);
		}
		add(new RequiredTextField<String>("email", String.class)
				.add(EmailAddressValidator.getInstance()));

		PasswordTextField passwordTextField = new PasswordTextField("password");
		PasswordTextField passwordConfirmTextField = new PasswordTextField(
				"passwordConfirm", new Model<String>());
		add(new EqualPasswordInputValidator(passwordTextField,
				passwordConfirmTextField));
		add(passwordTextField);
		passwordTextField.setConvertEmptyInputStringToNull(true);
		passwordConfirmTextField.setConvertEmptyInputStringToNull(true);
		add(passwordConfirmTextField);
		add(new CheckBox("emailGameMail"));
	}

	@Override
	public final void onSubmit() {
		Player player = getModelObject();
		if (player.getPassword() != null) {
			player.setPassword(passwordEncoder.encode(player.getPassword()));
		}
		player.setUserAgent(this.getWebRequest().getHeader("User-Agent"));
		Result<Player> result;
		if (getAuth().isSignedIn()) {
			result = playerDaoService.updatePlayer(player);
		} else {
			result = playerDaoService.register(player);
		}
		new InfoResult<Player>(this).info(result);
	}

	protected AbstractAuthenticatedWebSession getAuth() {
		return ((AbstractAuthenticatedWebSession) getSession());
	}
}
