package com.kenstevens.stratinit.cache;

import com.google.common.collect.Collections2;
import com.kenstevens.stratinit.model.*;
import com.kenstevens.stratinit.repo.*;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.world.predicate.UnitSeenToUnitFunction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

public class NationCache extends Cacheable {
	private final Log logger = LogFactory.getLog(getClass());

	private final Nation nation;
	private final SectorSeenCache sectorSeenCache = new SectorSeenCache();
	private final CityCache cityCache = new CityCache();
	private final UnitCache unitCache = new UnitCache();
	private final UnitSeenCache unitSeenCache = new UnitSeenCache();
	private final UnitMoveCache unitMoveCache = new UnitMoveCache();
	private final CityMoveCache cityMoveCache = new CityMoveCache();
	private final LaunchedSatelliteCache launchedSatelliteCache = new LaunchedSatelliteCache();

	public NationCache(Nation nation) {
		this.nation = nation;
	}

	public Nation getNation() {
		return nation;
	}

	public void add(City city) {
		cityCache.add(city);
	}
	
	public List<City> getCities() {
		return cityCache.getCities();
	}
	
	public void add(SectorSeen sectorSeen) {
		sectorSeenCache.add(sectorSeen);
	}

	public SectorSeen getSectorSeen(SectorCoords coords) {
		return sectorSeenCache.get(coords);
	}

	public Collection<Sector> getSectorsSeenSectors(World world) {
		return sectorSeenCache.getSectorsSeenSectors(world);
	}

	public Collection<SectorSeen> getSectorsSeen() {
		return sectorSeenCache.getSectorsSeen();
	}

	public void setSectorSeenModified(boolean modified) {
		sectorSeenCache.setModified(modified);
	}

	public void setCityCacheModified(boolean modified) {
		cityCache.setModified(modified);
	}

	public void remove(City city) {
		cityCache.remove(city);
	}

	public void add(Unit unit) {
		unitCache.add(unit);
	}

	public void setUnitCacheModified(boolean modified) {
		unitCache.setModified(modified);
	}

	@Nonnull
	public List<Unit> getUnits() {
		return unitCache.getUnits();
	}
	
	public void add(UnitSeen unitSeen) {
		unitSeenCache.add(unitSeen);
	}

	public void add(UnitMove unitMove) {
		unitMoveCache.add(unitMove);
	}

	public void add(CityMove cityMove) {
		cityMoveCache.add(cityMove);
	}

	public UnitSeen getUnitSeen(int unitId) {
		return unitSeenCache.get(unitId);
	}

	public UnitMove getUnitMove(int unitId) {
		return unitMoveCache.get(unitId);
	}

	public CityMove getCityMove(SectorCoords coords) {
		return cityMoveCache.get(coords);
	}

	public Collection<Unit> getUnitSeenUnits() {
		return unitSeenCache.getUnitSeenUnits();
	}

	public Collection<Unit> getSeenUnits() {
		return Collections2.transform(unitSeenCache.getUnitsSeen(), new UnitSeenToUnitFunction());
	}
	
	public Collection<UnitSeen> getUnitsSeen() {
		return unitSeenCache.getUnitsSeen();
	}

	public Collection<UnitMove> getUnitsMove() {
		return unitMoveCache.getUnitsMove();
	}


	public Collection<CityMove> getCityMoves() {
		return cityMoveCache.getCityMoves();
	}

	public void setUnitSeenModified(boolean modified) {
		unitSeenCache.setModified(modified);
	}
	
	public void setUnitMoveModified(boolean modified) {
		unitMoveCache.setModified(modified);
	}
	
	public void add(LaunchedSatellite launchedSatellite) {
		launchedSatelliteCache.add(launchedSatellite);
	}

	public void remove(LaunchedSatellite launchedSatellite) {
		launchedSatelliteCache.remove(launchedSatellite);
	}
	
	public Collection<LaunchedSatellite> getLaunchedSatellites() {
		return launchedSatelliteCache.getLaunchedSatellites();
	}

	public void setLaunchedSatelliteModified(boolean modified) {
		launchedSatelliteCache.setModified(modified);
	}

	public void remove(Unit unit) {
		unitSeenCache.remove(unit);
		unitCache.remove(unit);
	}

	public void remove(UnitSeen unitSeen) {
		unitSeenCache.remove(unitSeen.getUnit());
	}

	public void remove(UnitMove unitMove) {
		unitMoveCache.remove(unitMove.getUnit().getId());
	}

	public void remove(CityMove cityMove) {
		cityMoveCache.remove(cityMove.getCity().getCoords());
	}

	// FIXME move to service
	public void flush(int gameId, NationRepo nationRepo, SectorRepo sectorRepo, CityRepo cityRepo, SectorSeenRepo sectorSeenRepo, UnitDal unitDal) {
		if (isModified()) {
			logger.debug("Flushing " + nation + " nation for game #"
					+ gameId);
			nationRepo.save(nation);
			setModified(false);
		}
		if (sectorSeenCache.isModified()) {
			logger.debug("Flushing sectors seen for " + nation
					+ " nation for game #" + gameId);
			sectorSeenRepo.saveAll(getSectorsSeen());
			setSectorSeenModified(false);
		}
		if (cityCache.isModified()) {
			logger.debug("Flushing cities for " + nation
					+ " nation for game #" + gameId);
			for (City city : getCities()) {
				cityRepo.save(city);
			}
			setCityCacheModified(false);
		}
		if (unitCache.isModified()) {
			logger.debug("Flushing units for " + nation
					+ " nation for game #" + gameId);
			for (Unit unit : getUnits()) {
				unitDal.flush(unit);
			}
			setUnitCacheModified(false);
		}
		if (unitSeenCache.isModified()) {
			logger.debug("Flushing units seen for " + nation
					+ " nation for game #" + gameId);
			unitDal.flushUnitsSeen(getUnitsSeen());
			setUnitSeenModified(false);
		}
		if (unitMoveCache.isModified()) {
			logger.debug("Flushing units move for " + nation
					+ " nation for game #" + gameId);
			unitDal.flushUnitsMove(getUnitsMove());
			setUnitMoveModified(false);
		}
		if (launchedSatelliteCache.isModified()) {
			logger.debug("Flushing launched satellites for " + nation
					+ " nation for game #" + gameId);
			unitDal.flushLaunchedSatellites(getLaunchedSatellites());
			setLaunchedSatelliteModified(false);
		}
	}
}
