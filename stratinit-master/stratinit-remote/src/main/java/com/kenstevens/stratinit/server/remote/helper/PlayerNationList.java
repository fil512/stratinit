package com.kenstevens.stratinit.server.remote.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;

@Service
public class PlayerNationList {
	@Autowired
	private DataCache dataCache;
	@Autowired
	private SectorDao sectorDao;
	@Autowired
	private GameDao gameDao;
	@Autowired
	private UnitDaoService unitDaoService;

	public List<SINation> getNations(final Nation me, final boolean includePower) {
		List<Nation> nations = dataCache.getGameCache(me.getGameId())
				.getNations();
		Collection<Nation> allies = gameDao.getAllies(me);
		return nationsToSINations(me, includePower, nations, allies);
	}

	private List<SINation> nationsToSINations(final Nation me,
			final boolean includePower, List<Nation> nations, final Collection<Nation> allies) {
		return Lists.newArrayList(Collections2.transform(nations,
				new Function<Nation, SINation>() {
					public SINation apply(Nation nation) {
						return nationToSINation(me, nation, includePower, allies.contains(nation));
					}
				}));
	}

	private SINation nationToSINation(Nation me, Nation nation,
			boolean includePower, boolean isAlly) {
		SINation sination = new SINation(nation);
		if (includePower) {
			sination.cities = sectorDao.getNumberOfCities(nation);
			sination.power = unitDaoService.getPower(nation);
		}
		if (me != null) {
			sination.addPrivateData(me, nation);
		}
		if (isAlly) {
			sination.tech = nation.getTech();
		}
		return sination;
	}

	private List<SIGame> nationsToSIGames(List<Nation> nations) {
		return Lists.newArrayList(Collections2.transform(nations,
				new Function<Nation, SIGame>() {
					public SIGame apply(Nation nation) {
						return new SIGame(nation.getGame(), nation.isNoAlliances());
					}
				}));
	}

	public List<SIGame> getJoinedGames(Player player) {
		List<Nation> nations = gameDao.getNations(player);
		return nationsToSIGames(nations);
	}

	public SINation getMyNation(Nation nation) {
		return nationToSINation(nation, nation, false, false);
	}

	public List<SINation> getNations(Game game) {
		List<Nation> nations = dataCache.getGameCache(game)
		.getNations();
		return nationsToSINations(null, true, nations, new ArrayList<Nation>());
	}
}
