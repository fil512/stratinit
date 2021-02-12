package com.kenstevens.stratinit.server.daoserviceimpl;

import com.kenstevens.stratinit.model.GameHistory;
import com.kenstevens.stratinit.model.GameHistoryNation;
import com.kenstevens.stratinit.model.GameHistoryTeam;
import com.kenstevens.stratinit.repo.GameHistoryDal;
import com.kenstevens.stratinit.server.daoservice.GameHistoryDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
