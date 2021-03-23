package com.kenstevens.stratinit.client.model;

import com.kenstevens.stratinit.type.CoordMeasure;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.SectorType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class World implements CoordMeasure {
	private final Sector[][] world;
	private final Game game;
	private final int size;
	private final int islands;

	public World(Game game, boolean create) {
		this.game = game;
		this.size = game.getGamesize();
		this.islands = game.getIslands();
		world = new Sector[size][size];
		if (create) {
			for (int x = 0; x < size; ++x) {
				for (int y = 0; y < size; ++y) {
					world[x][y] = new Sector(game, new SectorCoords(x, y),
							SectorType.WATER);
				}
			}
		}
	}

	public void setSector(Sector sector) {
		int x = sector.getCoords().x;
		int y = sector.getCoords().y;
		world[x][y] = sector;
	}

	@Nonnull
	public Sector getSector(SectorCoords coords) {
		return getSector(coords.x, coords.y);
	}

	@Nonnull
	public Sector getSector(int x, int y) {
		if (x < 0 || x >= size) {
			throw new IndexOutOfBoundsException("x value " + x + " is not between " + 0 + " and " + size);
		}
		if (y < 0 || y >= size) {
			throw new IndexOutOfBoundsException("y value " + y + " is not between " + 0 + " and " + size);
		}
		Sector retval = world[x][y];
		if (retval == null) {
			return new UnknownSector(game, x, y);
		}
		return retval;
	}

	public void setType(int x, int y, SectorType type) {
		getSector(x, y).setType(type);
	}

	public int size() {
		return size;
	}

	public int getIslands() {
		return islands;
	}

	public List<Sector> getNeighbours(SectorCoords coords) {
		return getSectorsWithin(coords, 1, false);
	}

	public List<Sector> getSectorsWithin(SectorCoords coords, int distance,
			boolean includeSelf) {
        List<Sector> retval = new ArrayList<Sector>();
        for (SectorCoords neighbour : coords.sectorsWithin(size, distance,
                includeSelf)) {
			Sector sector = getSector(neighbour.x, neighbour.y);
			if (!sector.isUnknown()) {
				retval.add(sector);
			}
		}
        return retval;
    }

	public List<Sector> getNeighbours(Sector sector, int distance) {
		return getSectorsWithin(sector.getCoords(), distance, false);
	}

	public int percentWater() {
		int total = 0;
		int water = 0;
		for (int x = 0; x < size; ++x) {
			for (int y = 0; y < size; ++y) {
				Sector sector = world[x][y];
				++total;
				if (sector.getType().equals(SectorType.WATER)) {
					++water;
				}
			}
		}
		return 100 * water / total;
	}

	public @Nullable
	Sector getSectorOrNull(SectorCoords coords) {
		Sector retval = getSector(coords.x, coords.y);
		if (retval.isUnknown()) {
			return null;
		}
		return retval;
	}

	public List<Sector> getSectors() {
		List<Sector> retval = new ArrayList<Sector>();
		for (int x = 0; x < size; ++x) {
			for (int y = 0; y < size; ++y) {
				Sector sector = getSector(x, y);
				if (!sector.isUnknown()) {
					retval.add(sector);
				}
			}
		}
		return retval;
	}

	public boolean isCoastal(Sector sector) {
		for (Sector neighbour : getNeighbours(sector)) {
			if (neighbour.isWater()) {
				return true;
			}
		}
		return false;
	}

	public List<Sector> getNeighbours(Sector sector) {
		return getNeighbours(sector.getCoords());
	}

	@Nullable
	public Sector getSectorOrNull(Unit unit) {
		return getSectorOrNull(unit.getCoords());
	}

	public Game getGame() {
		return game;
	}

	public List<Sector> getSectorsWithin(SectorCoords coords, int distance) {
		return getSectorsWithin(coords, distance, true);
	}

	public int distance(SectorCoords source, SectorCoords target) {
		return SectorCoords.distance(size, source, target);
	}

	public boolean isAtSea(Sector sector) {
		if (!sector.isWater()) {
			return false;
		}
		for (Sector neighbour : getNeighbours(sector)) {
			if (!neighbour.isWater()) {
				return false;
			}
		}
		return true;
	}
}
