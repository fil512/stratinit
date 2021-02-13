package com.kenstevens.stratinit.dao.impl;

import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.cache.NationCache;
import com.kenstevens.stratinit.cache.NationCacheToNationFunction;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.dao.impl.predicates.*;
import com.kenstevens.stratinit.model.*;
import com.kenstevens.stratinit.model.audit.UnitBuildAudit;
import com.kenstevens.stratinit.repo.*;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.CoordMeasure;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import com.kenstevens.stratinit.world.predicate.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Predicates.and;

@Service
public class UnitDaoImpl extends CacheDaoImpl implements UnitDao {
	@Autowired
	private DataCache dataCache;
	@Autowired
	private UnitRepo unitRepo;
	@Autowired
	private UnitSeenRepo unitSeenRepo;
	@Autowired
	private UnitMoveRepo unitMoveRepo;
	@Autowired
	private UnitBuildAuditRepo unitBuildAuditRepo;
	@Autowired
	private LaunchedSatelliteRepo launchedSatelliteRepo;

	@Override
	public Unit findUnit(Integer unitId) {
		return dataCache.getUnit(unitId);
	}

	@Override
	public UnitSeen findUnitSeen(Nation nation, Unit unit) {
		return getNationCache(nation).getUnitSeen(unit.getId());
	}

	@Override
	public UnitSeen findUnitSeen(UnitSeenPK unitSeenPK) {
		return findUnitSeen(unitSeenPK.getNation(), unitSeenPK.getUnit());
	}

	@Override
	public List<Unit> getAllUnits() {
		return dataCache.getAllUnits();
	}

	@Override
	public List<UnitBuildAudit> getBuildAudits(int gameId, String username) {
		// FIXME is And right?
		return unitBuildAuditRepo.findByGameIdAndUsername(gameId, username);
	}

	@Override
	public List<UnitBuildAudit> getBuildAudits(Game game) {
		return unitBuildAuditRepo.findByGameId(game.getId());
	}

	@Override
	public Collection<Nation> getOtherNationsThatCanSeeThisSub(CoordMeasure coordMeasure, Unit sub) {
		Nation nation = sub.getNation();
		List<NationCache> nationCaches = getGameCache(nation).getNationCaches();
		return Collections2.transform(
				Collections2.filter(nationCaches,
				and(new OtherNationPredicate(nation), new CanSeeSubPredicate(coordMeasure, sub))),
				new NationCacheToNationFunction());
	}

	@Override
	public Collection<Nation> getOtherNationsThatCanSeeThisUnit(CoordMeasure coordMeasure, Unit targetUnit) {
		Nation nation = targetUnit.getNation();
		List<NationCache> nationCaches = getGameCache(nation).getNationCaches();
		return Collections2.transform(
				Collections2.filter(nationCaches,
				and(new OtherNationPredicate(nation), new CanSeeUnitPredicate(coordMeasure, targetUnit))),
				new NationCacheToNationFunction());
	}

	@Override
	public Collection<Nation> getOtherNationsThatSeeThisUnit(Unit unit) {
		Nation nation = unit.getNation();
		List<NationCache> nationCaches = getGameCache(nation).getNationCaches();
		return Collections2.transform(
				Collections2.filter(nationCaches,
				and(new OtherNationPredicate(nation), new SeesUnitPredicate(unit))),
				new NationCacheToNationFunction());
	}

	@Override
	public Collection<LaunchedSatellite> getSatellites(Nation nation) {
		return getNationCache(nation).getLaunchedSatellites();
	}

	@Override
	public List<Unit> getUnits(Nation nation) {
		return getNationUnits(nation);
	}

	@Override
	public Collection<Unit> getUnits(Game game, SectorCoords coords) {
		Collection<Unit> units = getUnits(game);
		return Collections2.filter(units, new InSectorPredicate(coords));
	}

	@Override
	public Collection<Unit> getUnits(Game game) {
		return getGameCache(game).getUnits();
	}

	@Override
	public Collection<Unit> getUnits(Sector sector) {
		return getUnits(sector.getGame(), sector.getCoords());
	}

	@Override
	public Collection<Unit> getSeenUnits(Nation nation) {
		return getNationCache(nation).getSeenUnits();
	}

	@Override
	public List<UnitSeen> getUnitsSeen(Game game) {
		List<UnitSeen> retval = Lists.newArrayList();
		for (NationCache nationCache : getGameCache(game).getNationCaches()) {
			retval.addAll(nationCache.getUnitsSeen());
		}
		return retval;
	}

	@Override
	public List<UnitMove> getUnitMoves(Game game) {
		List<UnitMove> retval = Lists.newArrayList();
		for (NationCache nationCache : getGameCache(game).getNationCaches()) {
			retval.addAll(nationCache.getUnitsMove());
		}
		return retval;
	}

	@Override
	public Collection<Unit> getUnitsThatCanInterdictThisUnit(CoordMeasure coordMeasure, Nation nation, Unit targetUnit) {
		return Collections2.filter(getNationUnits(nation), new UnitCanInterdictUnitPredicate(coordMeasure, targetUnit));
	}

	@Override
	public boolean isAnEscort(CoordMeasure coordMeasure, Unit unit) {
		return Iterables.any(
		getUnitsWithin(coordMeasure, unit.getNation(), unit.getCoords(), Constants.ESCORT_RADIUS), new UnitIsCapitalShip());
	}

	@Override
	public Collection<Unit> getUnitsThatCanCounterFireThisUnit(CoordMeasure coordMeasure, Nation nation, Unit targetUnit, SectorCoords excludeCoords) {
		return Collections2.filter(getNationUnits(nation), new UnitCanCounterFireUnitPredicate(coordMeasure, targetUnit, excludeCoords));
	}

	private List<Unit> getNationUnits(Nation nation) {
		return getNationCache(nation).getUnits();
	}

	@Override
	public Collection<Unit> getUnitsWithin(CoordMeasure coordMeasure, Nation nation,
			SectorCoords coords, int distance) {
		return Collections2.filter(getNationUnits(nation), new UnitWithinPredicate(coordMeasure, coords, distance));
	}

	@Override
	public void merge(Unit unit) {
		if (unit.isAlive()) {
			getNationCache(unit.getNation()).setUnitCacheModified(true);
		} else {
			unitRepo.save(unit);
			getGameCache(unit.getGame()).remove(unit);
		}
	}

	@Override
	public void merge(UnitSeen unitSeen) {
		if (unitSeen.isEnabled()) {
			getNationCache(unitSeen.getNation()).setUnitSeenModified(true);
		} else {
			unitSeenRepo.save(unitSeen);
			getNationCache(unitSeen.getNation()).remove(unitSeen);
		}
	}

	@Override
	public void merge(UnitMove unitMove) {
		unitMoveRepo.save(unitMove);
	}

	@Override
	public void save(Unit unit) {
		unitRepo.save(unit);
		getGameCache(unit.getGame()).add(unit);
	}

	@Override
	public void save(LaunchedSatellite satellite) {
		launchedSatelliteRepo.save(satellite);
		getNationCache(satellite.getNation()).add(satellite);
	}

	@Override
	public void save(UnitSeen unitSeen) {
		unitSeenRepo.save(unitSeen);
		getNationCache(unitSeen.getNation()).add(unitSeen);
	}

	@Override
	public void save(@Nonnull UnitMove unitMove) {
		unitMoveRepo.save(unitMove);
		getNationCache(unitMove.getUnit().getNation()).add(unitMove);
	}

	@Override
	public void save(UnitBuildAudit unitBuildAudit) {
		unitBuildAuditRepo.save(unitBuildAudit);
	}

	@Override
	@Transactional
	public void delete(Unit unit) {
		unitSeenRepo.deleteByUnit(unit);
		unitRepo.delete(unit);
		getGameCache(unit.getGame()).remove(unit);
	}

	@Override
	public void removeUnits(Game game) {
		for (Unit unit : getUnits(game)) {
			delete(unit);
		}
	}

	@Override
	public void delete(UnitSeen unitSeen) {
		unitSeenRepo.delete(unitSeen);
		getNationCache(unitSeen.getNation()).remove(unitSeen);
	}

	@Override
	public void delete(UnitMove unitMove) {
		unitMoveRepo.delete(unitMove);
		getNationCache(unitMove.getUnit().getNation()).remove(unitMove);
	}

	@Override
	public UnitSeen saveOrUpdate(UnitSeen unitSeen) {
		UnitSeen found = findUnitSeen(unitSeen.getUnitSeenPK());
		if (found == null) {
			save(unitSeen);
			return unitSeen;
		} else {
			found.resetExpiry();
			found.setEnabled(true);
			merge(found);
			return found;
		}
	}

	@Override
	public List<UnitSeen> getUnitsSeen(Unit unit) {
		List<NationCache> nationCaches = getGameCache(unit.getGame()).getNationCaches();
		List<UnitSeen> retval = new ArrayList<UnitSeen>();
		for (NationCache nationCache : nationCaches) {
			UnitSeen unitSeen = nationCache.getUnitSeen(unit.getId());
			if (unitSeen != null) {
				retval.add(unitSeen);
			}
		}
		return retval;
	}

	@Override
	public int getNumberOfCapitalShips(Nation nation) {
		return Collections2.filter(getUnits(nation), new UnitIsCapitalShip()).size();
	}

	@Override
	public void transferUnit(Unit unit, Nation nation) {
		merge(unit);
		clearUnitMove(unit);
		getGameCache(unit.getGame()).transferUnit(unit, nation);
		merge(unit);
	}

	@Override
	public void clearUnitMove(Unit unit) {
		unitMoveRepo.deleteByUnit(unit);
		unit.setUnitMove(null);
	}

	@Override
	public void transferUnitSeen(UnitSeen unitSeen, Nation oldOwner) {
		delete(unitSeen);
		UnitSeen newUnitSeen = new UnitSeen(oldOwner, unitSeen.getUnit());
		save(newUnitSeen);
	}

	@Override
	public Collection<Unit> getUnitsOfType(Nation nation, UnitType unitType) {
		Collection<Unit> units = getUnits(nation);
		return Collections2.filter(units, new UnitTypePredicate(unitType));
	}
	
	@Override
	public Collection<Unit> getMissiles(Nation nation) {
		Collection<Unit> units = getUnits(nation);
		return Collections2.filter(units, new UnitDevistatesPredicate());
	}
}
