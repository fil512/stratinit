package com.kenstevens.stratinit.server.gwtservice;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.context.SecurityContextImpl;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.providers.encoding.ShaPasswordEncoder;
import org.springframework.security.userdetails.User;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.kenstevens.stratinit.client.gwt.callback.ServiceSecurityException;
import com.kenstevens.stratinit.client.gwt.model.Authorization;
import com.kenstevens.stratinit.client.gwt.service.GWTAuthorizationService;
import com.kenstevens.stratinit.client.gwt.service.GWTNone;
import com.kenstevens.stratinit.client.gwt.service.GWTResult;
import com.kenstevens.stratinit.dao.PlayerDao;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.model.PlayerRole;


@Service("authorizationService")
public class GWTAuthorizationServiceImpl extends GWTBaseServiceImpl implements
		GWTAuthorizationService {
	@Autowired
	private PlayerDao playerDao;

	private static final long serialVersionUID = 3569478722625576693L;


	public Authorization getAuthorization() throws ServiceSecurityException {
		Authorization authorization = new Authorization();
		String username = getUsername();
		authorization.setUsername(username);
		boolean isAdmin = isAdmin();
		authorization.setAdmin(isAdmin);
		return authorization;
	}

	public GWTNone logout() {
		session.invalidate();
		SecurityContextHolder.clearContext();
		return null;
	}

	@Override
	public GWTResult<String> login(String username, String password) {
		Player player = playerDao.find(username);
		if (player == null) {
			return new GWTResult<String>("Invalid username or password.", false);
		}
		ShaPasswordEncoder encoder = new ShaPasswordEncoder();
		String encodedPassword = encoder.encodePassword(password, null);

		if (!player.getPassword().equals(encodedPassword)) {
			return new GWTResult<String>("Invalid username or password.", false);
		}
		if (!player.isEnabled()) {
			return new GWTResult<String>("Account ["+username+"] has been disabled.  Please contact the site administrator", false);
		}
		List<PlayerRole> playerRoles = playerDao.getRoles(player);
		Collection<GrantedAuthority> galist = Collections2.transform(playerRoles, new Function<PlayerRole, GrantedAuthority>() {
			@Override
			public GrantedAuthority apply(PlayerRole playerRole) {
				return new GrantedAuthorityImpl(playerRole.getRoleName());
			}
		});
		GrantedAuthority[] authorities = galist.toArray(new GrantedAuthority[0]);
		User user = new User(username, password, true, true, true, true, authorities);
		Authentication auth = new UsernamePasswordAuthenticationToken(user, password, authorities);
		SecurityContext sc = new SecurityContextImpl();
		sc.setAuthentication(auth);
		SecurityContextHolder.setContext(sc);

		return new GWTResult<String>("User ["+username+"] logged in.");
	}
}
