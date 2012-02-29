package com.kenstevens.stratinit.move;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.World;
import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.CoordMeasure;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.world.predicate.SectorSuppliesUnitPredicate;
import com.kenstevens.stratinit.world.predicate.SectorToWorldSectorFunction;
public class WorldView implements CoordMeasure {
	private final World world;
	private final Nation nation;
	private final Map<Nation, RelationType> myRelationMap;
	private final Map<Nation, RelationType> theirRelationMap;

	public WorldView(Nation nation, Map<Nation, RelationType> myRelationMap,
			Map<Nation, RelationType> theirRelationMap) {
		world = new World(nation.getGame(), false);
		this.nation = nation;
		this.myRelationMap = myRelationMap;
		this.theirRelationMap = theirRelationMap;
	}

	// Used by client
	public WorldView(Game game) {
		world = new World(game, false);
		this.myRelationMap = null;
		this.theirRelationMap = null;
		this.nation = null;
	}

	public List<WorldSector> getWorldSectorsWithin(SectorCoords coords, int distance,
			boolean includeSelf) {
		return Lists.transform(world.getSectorsWithin(coords, distance,
				includeSelf), new SectorToWorldSectorFunction());
	}

	public List<WorldSector> getWorldSectors() {
		return Lists.transform(world.getSectors(),
				new SectorToWorldSectorFunction());
	}

	public WorldSector getWorldSector(Sector sector) {
		return getWorldSector(sector.getCoords());
	}

	public WorldSector getWorldSector(Unit unit) {
		return getWorldSector(unit.getCoords());
	}

	public WorldSector getWorldSector(int x, int y) {
		return getWorldSector(new SectorCoords(x, y));
	}

	public List<WorldSector> getNeighbours(SectorCoords coords) {
		return getWorldSectorsWithin(coords, 1, false);
	}

	public List<WorldSector> getNeighbours(WorldSector worldSector) {
		return getNeighbours(worldSector.getCoords());
	}
	public WorldSector getWorldSector(SectorCoords coords) {
		return (WorldSector) world.getSector(coords);
	}

	public RelationType getMyRelation(Nation nation) {
		return myRelationMap.get(nation);
	}

	public RelationType getTheirRelation(Nation nation) {
		return theirRelationMap.get(nation);
	}

	public boolean fueling(Unit unit) {
		WorldSector worldSector = getWorldSector(unit);
		return worldSector.canRefuel(unit);
	}

	public int size() {
		return world.size();
	}

	public boolean isCoastal(WorldSector sector) {
		return world.isCoastal(sector);
	}

	public void setWorldSector(WorldSector worldSector) {
		world.setSector(worldSector);
	}

	public List<Sector> getSectors() {
		return world.getSectors();
	}

	public void addAll(List<WorldSector> sectors) {
		for (WorldSector sector : sectors) {
			setWorldSector(sector);
		}
	}

	public List<WorldSector> getNeighbours(SectorCoords coords, int sight) {
		return getWorldSectorsWithin(coords, sight, false);
	}

	public List<WorldSector> getNeighbours(SectorCoords coords, int sight,
			boolean includeSelf) {
		return getWorldSectorsWithin(coords, sight, includeSelf);
	}

	public Game getGame() {
		return nation.getGame();
	}

	public Set<SectorCoords> getNavalSupplyLocations() {
		Set<SectorCoords> retval = new HashSet<SectorCoords>();
		for (Sector sector : getSectors()) {
			WorldSector worldSector = (WorldSector) sector;
			if (worldSector.isSuppliesNavy()) {
				retval.add(worldSector.getCoords());
			}
		}
		return retval;
	}

	public Set<SectorCoords> getLandSupplyLocations() {
		Set<SectorCoords> retval = new HashSet<SectorCoords>();
		for (Sector sector : getSectors()) {
			WorldSector worldSector = (WorldSector) sector;
			if (worldSector.isSuppliesLand()) {
				retval.add(worldSector.getCoords());
			}
		}
		return retval;
	}

	public int distance(SectorCoords source, SectorCoords target) {
		return world.distance(source, target);
	}

	public Iterable<WorldSector> getSupplyingSectors(Unit unit) {
		List<WorldSector> sectors = getWorldSectorsWithin(unit.getCoords(),
				Constants.SUPPLY_RADIUS, true);
		return Iterables.filter(sectors, new SectorSuppliesUnitPredicate(unit));
	}


	public boolean isVulnerable(WorldSector worldSector) {
		return worldSector.isPlayerCity() && isCoastal(worldSector);
	}
}
