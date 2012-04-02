package com.kenstevens.stratinit.wicket.login;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.server.daoservice.PlayerDaoService;
import com.kenstevens.stratinit.wicket.AuthenticatedPage;
import com.kenstevens.stratinit.wicket.components.RegistrationForm;

public class RegistrationPage extends AuthenticatedPage {

	private static final long serialVersionUID = 1L;

	@SpringBean
	PlayerDaoService playerDaoService;

	public RegistrationPage() {
		if (isSignedIn()) {
			add(new Label("title", getUsername() + " Account"));
		} else {
			add(new Label("title", "Strategic Initiative Registration"));
		}
		if (isSignedIn()) {
			Player player = playerDaoService.findPlayer(getUsername());
			if (player != null) {
				add(new PlayerStatPanel("playerStatPanel", player));
			} else {
				add(new Label("playerStatPanel"));
			}
		} else {
			add(new Label("playerStatPanel"));
		}
	}

	@Override
	protected void onBeforeRender() {
		if (!hasBeenRendered()) {
			init();
		}
		super.onBeforeRender();
	}

	final void init() {
		Player player = null;
		if (isSignedIn()) {
			player = playerDaoService.findPlayer(getUsername());
		}
		if (player == null) {
			player = new Player();
		}
		add(new RegistrationForm("registrationForm", player));
	}

}