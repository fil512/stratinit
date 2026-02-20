package com.kenstevens.stratinit.cache;

import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.repo.*;
import com.kenstevens.stratinit.type.SectorCoords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import jakarta.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
public class NationCache extends Cacheable {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private NationRepo nationRepo;
	@Autowired
	private CityRepo cityRepo;
	@Autowired
	private SectorSeenRepo sectorSeenRepo;
	@Autowired
	private UnitSeenRepo unitSeenRepo;
	@Autowired
	private LaunchedSatelliteRepo launchedSatelliteRepo;
	@Autowired
	private UnitMoveRepo unitMoveRepo;
	@Autowired
	private UnitRepo unitRepo;

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
		return unitSeenCache.getUnitsSeen().stream()
				.map(UnitSeen::getUnit)
				.collect(Collectors.toList());
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

	public void flush(int gameId) {
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
				unitRepo.save(unit);
			}
			setUnitCacheModified(false);
		}
		if (unitSeenCache.isModified()) {
			logger.debug("Flushing units seen for " + nation
					+ " nation for game #" + gameId);
			unitSeenRepo.saveAll(getUnitsSeen());
			setUnitSeenModified(false);
		}
		if (unitMoveCache.isModified()) {
			logger.debug("Flushing units move for " + nation
					+ " nation for game #" + gameId);
			unitMoveRepo.saveAll(getUnitsMove());
			setUnitMoveModified(false);
		}
		if (launchedSatelliteCache.isModified()) {
			logger.debug("Flushing launched satellites for " + nation
					+ " nation for game #" + gameId);
			launchedSatelliteRepo.saveAll(getLaunchedSatellites());
			setLaunchedSatelliteModified(false);
		}
	}
}
