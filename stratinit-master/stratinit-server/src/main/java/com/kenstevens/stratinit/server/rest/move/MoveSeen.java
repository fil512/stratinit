package com.kenstevens.stratinit.server.rest.move;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Sector;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.client.model.UnitSeen;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.type.SectorCoords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Scope("prototype")
public class MoveSeen {
	@Autowired
	SectorDaoService sectorDaoService;
	@Autowired
	UnitDaoService unitDaoService;

	private final Set<UnitSeen> seen = new HashSet<UnitSeen>();
	private final Set<Unit> seeUnit = new HashSet<Unit>();
	private final Set<Sector> seeSector = new HashSet<Sector>();
	private final Set<Sector> subSeeSector = new HashSet<Sector>();
	private final Set<UnitSeen> seenDone = new HashSet<UnitSeen>();
	private final Nation nation;

	public MoveSeen(Nation nation) {
		this.nation = nation;
	}

	public void add(Unit unit, Sector sector) {
		seeSector.add(sector);
		if (unit != null && unit.isCanSeeSubs()) {
			subSeeSector.add(sector);
		}
	}

	private void addSeeUnit(Unit unit) {
		seeUnit.add(unit);
	}

	public void persistSeen() {
		for (UnitSeen unitSeen : seen) {
			if (seenDone.contains(unitSeen)) {
				continue;
			}
			unitDaoService.saveOrUpdate(unitSeen);
			seenDone.add(unitSeen);
		}
	}

	public void persist() {
		persistSeen();
		Map<SectorCoords, List<Unit>> unitMap = unitDaoService.getUnitMap(nation.getGame());
		for (Sector sector : seeSector) {
			sectorDaoService.saveIfNew(nation, sector);
			List<Unit> units = unitMap.get(sector.getCoords());
			if (units == null) {
				continue;
			}
			addUnitsSeen(units, sector);
		}

		for (Unit unit : seeUnit) {
			unitDaoService.saveOrUpdate(nation, unit);
		}
	}

	private void addUnitsSeen(List<Unit> unitsSeen, Sector sector) {
		for (Unit unit : unitsSeen) {
			if (unit.getNation().equals(nation)) {
				continue;
			}
			if (unit.isSubmarine() && !subSeeSector.contains(sector)) {
				continue;
			}
			if (!unit.isAlive()) {
				continue;
			}
			addSeeUnit(unit);
		}
	}

	public void add(Nation otherNation, List<Unit> units) {
		if (nation.equals(otherNation)) {
			throw new IllegalStateException("May not see yourself.");
		}
		for (Unit unit : units) {
			seen.add(new UnitSeen(otherNation, unit));
		}
	}
}
