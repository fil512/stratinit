package com.kenstevens.stratinit.server.gwtservice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.annotation.Secured;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.client.gwt.callback.ServiceSecurityException;
import com.kenstevens.stratinit.client.gwt.model.GWTPlayer;
import com.kenstevens.stratinit.client.gwt.service.GWTPlayerService;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.PlayerDaoService;
import com.kenstevens.stratinit.server.gwtrequest.translate.GWTPlayerTranslate;


@Service("playerService")
public class GWTPlayerServiceImpl extends GWTBaseServiceImpl implements
		GWTPlayerService {

	private static final long serialVersionUID = 7339900818224826708L;
	@Autowired
	private PlayerDaoService playerDaoServiceImpl;

	@Secured ({"ROLE_USER", "ROLE_ADMIN"})
	@Override
	public List<GWTPlayer> fetch() throws ServiceSecurityException {
		Result<Player> presult = getPlayerResult();
		if (!presult.isSuccess()) {
			return null;
		}
		List<GWTPlayer> retval = new ArrayList<GWTPlayer>();
		for (Player player : playerDaoServiceImpl.getPlayers()) {
			GWTPlayer gplayer = playerToGplayer(player);
			retval.add(gplayer);
		}
		return retval;
	}

	private GWTPlayer playerToGplayer(Player player) {
		GWTPlayer retval = GWTPlayerTranslate.translate(player);


		if (!isAdmin()) {
			retval.setEmail("");
		}
		retval.setPassword("");
		return retval;
	}
}
