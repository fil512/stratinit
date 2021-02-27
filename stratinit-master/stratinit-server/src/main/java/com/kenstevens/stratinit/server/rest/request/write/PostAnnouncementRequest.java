package com.kenstevens.stratinit.server.rest.request.write;

import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.MessageDaoService;
import com.kenstevens.stratinit.server.daoservice.PlayerDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class PostAnnouncementRequest extends PlayerWriteRequest<Integer> {
	private final String subject;
	private final String body;
	@Autowired
	private GameDao gameDao;
	@Autowired
	private PlayerDaoService playerDaoService;
	@Autowired
	private MessageDaoService messageDaoService;

	public PostAnnouncementRequest(String subject, String body) {
		this.subject = subject;
		this.body = body;
	}

	@Override
	protected Result<Integer> executeWrite() {
		Player player = getPlayer();
		if (!playerDaoService.isAdmin(player)) {
			return new Result<Integer>("Only administrators may post bulletins.", false);
		}
		int count = 0;
		for (Game game : gameDao.getAllGames()) {
			messageDaoService.postBulletin(game, subject, body);
			++count;
		}

		return new Result<Integer>("Bulletin posted to " + count + " games.", true, Integer.valueOf(count));
	}

	@Override
	protected int getCommandCost() {
		return 0;
	}
}
