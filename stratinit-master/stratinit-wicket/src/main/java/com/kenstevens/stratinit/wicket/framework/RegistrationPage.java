package com.kenstevens.stratinit.wicket.framework;

import com.kenstevens.stratinit.client.model.Player;
import com.kenstevens.stratinit.client.server.daoservice.PlayerDaoService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.spring.injection.annot.SpringBean;

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