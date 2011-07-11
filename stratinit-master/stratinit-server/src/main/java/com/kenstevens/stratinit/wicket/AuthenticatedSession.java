package com.kenstevens.stratinit.wicket;

import java.util.Collection;
import java.util.List;

import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.model.PlayerRole;

public class AuthenticatedSession extends AuthenticatedWebSession {
	private static final long serialVersionUID = 1L;
	private Integer playerId;

	public AuthenticatedSession(Request request) {
		super(request);
	}

	@Override
	public boolean authenticate(String userName, String password) {
		boolean authorized = AuthenticatedApplication.get().getPlayerDaoService().authorizePlayer(userName, password);
		if (authorized) {
			playerId = AuthenticatedApplication.get().getPlayerDao().find(userName).getId();
		} else {
			playerId = null;
		}
		return authorized;
	}

	@Override
	public Roles getRoles() {
		List<PlayerRole> roles = AuthenticatedApplication.get().getPlayerDao().getRoles(getPlayer());
		Collection<String> roleStrings = Collections2.transform(roles, new Function<PlayerRole, String>() {
			@Override
			public String apply(PlayerRole playerRole) {
				return playerRole.getRoleName();
			}
		});
		return new Roles(roleStrings.toArray(new String[0]));
	}

	public Player getPlayer() {
		
		if (!isSignedIn()) {
			return Player.makeNonActivePlayer();
		}
		return AuthenticatedApplication.get().getPlayerDao().find(playerId);
	}

	@Override
	public void signOut() {
		super.signOut();
		playerId = null;
	}
}