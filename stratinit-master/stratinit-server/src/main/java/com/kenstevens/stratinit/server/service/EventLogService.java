package com.kenstevens.stratinit.server.service;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.audit.GameEventLog;
import com.kenstevens.stratinit.repo.GameEventLogRepo;
import com.kenstevens.stratinit.type.EventSource;
import com.kenstevens.stratinit.type.GameEventType;
import com.kenstevens.stratinit.type.SectorCoords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class EventLogService {
	@Autowired
	private GameEventLogRepo gameEventLogRepo;

	public void logUserEvent(Nation nation, GameEventType eventType, String description) {
		logUserEvent(nation, eventType, description, null, null);
	}

	public void logUserEvent(Nation nation, GameEventType eventType, String description, SectorCoords coords) {
		logUserEvent(nation, eventType, description, coords, null);
	}

	public void logUserEvent(Nation nation, GameEventType eventType, String description,
							 SectorCoords coords, String detail) {
		if (com.kenstevens.stratinit.dao.CacheDao.isTrainingMode()) {
			return;
		}
		Integer x = coords != null ? coords.x : null;
		Integer y = coords != null ? coords.y : null;
		GameEventLog log = new GameEventLog(nation.getGameId(), nation.getName(), new Date(),
				EventSource.USER, eventType, description, x, y, detail);
		gameEventLogRepo.save(log);
	}

	public void logServerEvent(int gameId, String nationName, GameEventType eventType, String description) {
		logServerEvent(gameId, nationName, eventType, description, null, null);
	}

	public void logServerEvent(int gameId, String nationName, GameEventType eventType,
							   String description, SectorCoords coords) {
		logServerEvent(gameId, nationName, eventType, description, coords, null);
	}

	public void logServerEvent(int gameId, String nationName, GameEventType eventType,
							   String description, SectorCoords coords, String detail) {
		if (com.kenstevens.stratinit.dao.CacheDao.isTrainingMode()) {
			return;
		}
		Integer x = coords != null ? coords.x : null;
		Integer y = coords != null ? coords.y : null;
		GameEventLog log = new GameEventLog(gameId, nationName, new Date(),
				EventSource.SERVER, eventType, description, x, y, detail);
		gameEventLogRepo.save(log);
	}
}
