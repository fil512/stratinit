package com.kenstevens.stratinit.server.daoserviceimpl;

import com.kenstevens.stratinit.model.GameHistory;
import com.kenstevens.stratinit.model.GameHistoryNation;
import com.kenstevens.stratinit.model.GameHistoryTeam;
import com.kenstevens.stratinit.repo.GameHistoryNationRepo;
import com.kenstevens.stratinit.repo.GameHistoryRepo;
import com.kenstevens.stratinit.repo.GameHistoryTeamRepo;
import com.kenstevens.stratinit.server.daoservice.GameHistoryDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameHistoryDaoServiceImpl implements GameHistoryDaoService {
	@Autowired
	private GameHistoryRepo gameHistoryRepo;
	@Autowired
	private GameHistoryTeamRepo gameHistoryTeamRepo;
	@Autowired
	private GameHistoryNationRepo gameHistoryNationRepo;

	@Override
	public void save(GameHistory gameHistory) {
		gameHistoryRepo.save(gameHistory);
	}

	@Override
	public void save(GameHistoryTeam gameHistoryTeam) {
		gameHistoryTeamRepo.save(gameHistoryTeam);
	}

	@Override
	public void save(GameHistoryNation gameHistoryNation) {
		gameHistoryNationRepo.save(gameHistoryNation);
	}

}
