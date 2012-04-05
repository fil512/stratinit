package com.kenstevens.stratinit.server.daoserviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dal.GameHistoryDal;
import com.kenstevens.stratinit.model.GameHistory;
import com.kenstevens.stratinit.model.GameHistoryNation;
import com.kenstevens.stratinit.model.GameHistoryTeam;
import com.kenstevens.stratinit.server.daoservice.GameHistoryDaoService;

@Service
public class GameHistoryDaoServiceImpl implements GameHistoryDaoService {
	@Autowired
	private GameHistoryDal gameHistoryDal;

	@Override
	public void persist(GameHistory gameHistory) {
		gameHistoryDal.persist(gameHistory);
	}

	@Override
	public void persist(GameHistoryTeam gameHistoryTeam) {
		gameHistoryDal.persist(gameHistoryTeam);
	}

	@Override
	public void persist(GameHistoryNation gameHistoryNation) {
		gameHistoryDal.persist(gameHistoryNation);
	}

}
