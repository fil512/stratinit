package com.kenstevens.stratinit.wicket.game;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.server.daoservice.GameDaoService;
import com.kenstevens.stratinit.server.remote.helper.PlayerNationList;

@Service
public class GameActiveListProvider implements GameListProvider {
	@Autowired
	private GameDao gameDao;
	@Autowired
	private GameDaoService gameDaoService;
	@Autowired
	private PlayerNationList playerNationList;
	
	@Override
	public List<GameTable> getGameTableList() {
		List<GameTable> retval = new ArrayList<GameTable>();
		List<Game> games = gameDao.getAllGames();
		for (Game game : games) {
			if (game.hasStarted()) {
				GameTable gameTable = new GameTable(game);
				gameTable.setTeams(gameDaoService.getTeams(game));
				gameTable.setNations(playerNationList.getNations(game));
				retval.add(gameTable);
			}
		}
		return retval;
	}

	@Override
	public List<SINation> getNations(int gameId) {
		throw new NotImplementedException();
	}

}
