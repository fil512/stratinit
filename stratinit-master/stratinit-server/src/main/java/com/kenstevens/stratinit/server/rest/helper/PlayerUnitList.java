package com.kenstevens.stratinit.server.rest.helper;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
public class PlayerUnitList {
	@Autowired
	private UnitDao unitDao;
	@Autowired
	private GameDao gameDao;
	@Autowired
	private UnitDaoService unitDaoService;

	public List<SIUnit> getUnits(final Nation nation) {
		List<Unit> units = unitDao.getUnits(nation);
		return unitsToSIUnits(nation, units);
	}

	private List<SIUnit> unitsToSIUnits(final Nation nation, Collection<Unit> units) {
		return Lists.newArrayList(Collections2.transform(units, new Function<Unit, SIUnit>() {
			public SIUnit apply(Unit unit) {
				return unitToSIUnit(nation, unit);
			}
		}));
	}

	public List<Unit> siunitToUnit(Collection<SIUnit> units) {
		return Lists.newArrayList(Collections2.transform(units, new Function<SIUnit, Unit>() {
			public Unit apply(SIUnit siunit) {
				return siunitToUnit(siunit);
			}
		}));
	}

	private Unit siunitToUnit(SIUnit siunit) {
		return unitDao.findUnit(siunit.id);
	}

	private SIUnit unitToSIUnit(Nation nation, Unit unit) {
		SIUnit siunit = new SIUnit(unit);
		siunit.addPrivateData(nation, unit);
		return siunit;
	}

	public List<SIUnit> getSeenUnits(Nation nation) {
		Collection<Nation> allies = gameDao.getFriendsAndAllies(nation);
		Set<Unit> units = unitDaoService.getTeamSeenUnits(nation, allies);
		units.addAll(unitDaoService.getAllyUnits(allies));
		return unitsToSIUnits(nation, units);
	}
}
