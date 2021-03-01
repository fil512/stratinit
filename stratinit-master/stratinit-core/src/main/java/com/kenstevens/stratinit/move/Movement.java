package com.kenstevens.stratinit.move;

import com.google.common.collect.Sets;
import com.kenstevens.stratinit.graph.Dijkstra;
import com.kenstevens.stratinit.graph.Path;
import com.kenstevens.stratinit.model.AttackType;
import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.util.UnitHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Movement {
	private final Unit unit;
	private final WorldView worldView;
	private final WorldSector worldSector;
	private final SectorGraph graph;
	private final Reacher reacher;
	private final MovementSupply movementSupply;
	private Dijkstra<WorldSector> dij;

	public Movement(Unit unit, WorldView worldView) {
		this.unit = unit;
		this.worldView = worldView;
		this.graph = new SectorGraph(unit, worldView);
		this.worldSector = worldView.getWorldSector(unit);
		this.reacher = new Reacher(unit, worldView);
		this.movementSupply = new MovementSupply(unit, worldView);
	}

	public Set<WorldSector> canReach(boolean inSupply) {
		movementSupply.addSupply();
		return reacher.canReach();
	}

	public boolean isStranded() {
		// Only air units can get stranded
		if (!unit.requiresFuel()) {
			return false;
		}

		int fuel = unit.getFuel();
		if (fuel >= Constants.MAX_RANGE) {
			return false;
		}
		boolean retval = true;
		for (int xdel = -1 * fuel; xdel <= fuel; ++xdel) {
			for (int ydel = -1 * fuel; ydel <= fuel; ++ydel) {
				if (xdel == 0 && ydel == 0) {
					continue;
				}
				WorldSector sector = worldView.getWorldSector(unit.getCoords().x
						+ xdel, unit.getCoords().y + ydel);
				if (sector == null) {
					continue;
				}
				if (sector.canRefuel(unit)) {
					return false;
				}
			}
		}
		return retval;
	}

	public List<SectorCoords> getPath(AttackType attackType, Unit unit,
			SectorCoords targetDestination) {
		// TODO OPT is there a more optimal place to put this?
		movementSupply.addSupply();
		SectorCoords source = unit.getCoords();
		List<SectorCoords> path = new ArrayList<SectorCoords>();
		SectorCoords target = targetDestination;
		WorldSector targetSector = worldView.getWorldSector(target);

		// Don't want to reveal details of target enemy city
		Attack attack = new Attack(targetSector);
		if (attack.canAttack(worldView, attackType, unit)
				|| attack.isEnemyCity()) {
			if (targetSector.getCoords()
					.adjacentTo(worldView, unit.getCoords())) {
				// Already next to it
				return path;
			}
			getDij();
			// Target is an enemy, need to move next to it
			WorldSector sourceSector = worldView.getWorldSector(source);
			List<WorldSector> neighbours = worldView.getNeighbours(targetSector);
			Sector bestNeighour = null;
			double maxWeight = Double.MAX_VALUE;
			// We can't find a path to the sector we're attacking, so let's
			// settle for bestpathing to a neighbour first
			for (WorldSector neighbour : neighbours) {
				if (neighbour.getCoords().equals(unit.getCoords())) {
					bestNeighour = null;
					break;
				}
				double weight = dij.getShortestWeightDistance(sourceSector,
						neighbour);
				if (weight > 0 && weight < maxWeight) {
					maxWeight = weight;
					bestNeighour = neighbour;
				}
			}
			// we can't reach the target sector.
			if (bestNeighour == null) {
				return null;
			}
			// Otherwise move to the neighbour and then attack
			target = bestNeighour.getCoords();
		}
		path = calculateBestPath(source, target);
		return path;
	}

	private List<SectorCoords> calculateBestPath(SectorCoords a, SectorCoords b) {
		getDij();
		Path<WorldSector> path = dij.getShortestPath(worldView.getWorldSector(a),
				worldView.getWorldSector(b));
		List<SectorCoords> retval = new ArrayList<SectorCoords>();
		for (int i = 1; i <= path.getLength(); ++i) {
			retval.add(path.get(i).getCoords());
		}
		if (retval.isEmpty()) {
			return null;
		}
		return retval;
	}

	private void getDij() {
		if (dij == null) {
			dij = new Dijkstra<WorldSector>(graph);
		}
	}

	public List<SectorCoords> getDirectPath(SectorCoords target) {
		List<SectorCoords> path = new ArrayList<SectorCoords>();
		SectorCoords step = new SectorCoords(unit.getCoords());
		path.add(new SectorCoords(step));
		while (!step.equals(target)) {
			moveCloser(step, target);
			path.add(new SectorCoords(step));
		}
		path.add(new SectorCoords(target));
		return path;
	}

	private void moveCloser(SectorCoords step, SectorCoords target) {
		if (step.x < target.x) {
			step.x += 1;
		} else if (step.x > target.x) {
			step.x -= 1;
		}
		if (step.y < target.y) {
			step.y += 1;
		} else if (step.y > target.y) {
			step.y -= 1;
		}
	}

	public boolean isInlandCity(WorldSector targetSector) {
		if (targetSector == null) {
			return false;
		}
		if (targetSector.isWater()) {
			return false;
		}
		return !worldView.isCoastal(targetSector);
	}

	public Result<None> inRange(Unit unit, SectorCoords targetCoords, int moveCost) {
		int distance = moveCost * worldView.distance(unit.getCoords(), targetCoords);
		return canTravelTo(unit, targetCoords, UnitHelper.range(unit), distance);
	}
	
	public Result<None> inMaxRange(Unit unit, SectorCoords targetCoords) {
		int distance = worldView.distance(unit.getCoords(), targetCoords);
		return canTravelTo(unit, targetCoords, UnitHelper.maxRange(unit), distance);
	}
	
	

	public Result<None> canTravelTo(Unit unit, SectorCoords targetCoords,
			int range, int distance) {
		if (range < distance) {

			WorldSector targetSector = worldView.getWorldSector(targetCoords);
			if (!planeLandingWithNoFuel(unit, distance, targetSector)) {
				if (unit.requiresFuel() && range >= unit.getFuel() - 1) {
					return new Result<None>("Insufficient fuel.  Need "
							+ distance + " Have " + unit.getFuel(), false);
				} else {
					return new Result<None>("Insufficient Mobility.  Need "
							+ distance + " Have " + unit.getMobility(), false);
				}
			}
		}
		return Result.trueInstance();
	}

	private boolean planeLandingWithNoFuel(Unit unit, int distance,
			WorldSector targetSector) {
		return airCanEnter(unit, targetSector) && airJustInReach(unit, distance);
	}

	private boolean airJustInReach(Unit unit, int distance) {
		return distance - UnitHelper.range(unit) == 1 && unit.getMobility() >= distance;
	}

	private boolean airCanEnter(Unit unit, WorldSector targetSector) {
		return unit.isAir()
								&& targetSector != null
								&& (targetSector.canRefuel(unit) || targetSector
										.airCanAttack());
	}

	public boolean isOnCoast() {
		if (worldSector == null) {
			return false;
		}
		return worldView.isCoastal(worldSector);
	}

	public Unit getUnit() {
		return unit;
	}

	public WorldView getWorldView() {
		return worldView;
	}

	public boolean isAtSea() {
		return worldSector.isHoldsShipAtSea();
	}

	public boolean isBoarding() {
		return worldSector.isHoldsMyTransport();
	}

	public boolean isHealing() {
		return worldSector.isPlayerCity();
	}

	public Result<None> canEnter(AttackType attackType,
			WorldSector targetSector, Unit unit, boolean unknown) {
		if (!unit.isAlive()) {
			return new Result<None>("Unit " + unit + " is dead.", false);
		}
		if (unit.getCoords().equals(targetSector.getCoords())) {
			return new Result<None>("Unit " + unit + " is already in "
					+ targetSector.getCoords(), false);
		}
		if (unknown
				&& unit.getCoords().distanceTo(worldView,
						targetSector.getCoords()) > 1) {
			return Result.trueInstance();
		}

		Attack attack = new Attack(targetSector);
		if (!attack.canAttack(worldView, attackType, unit)
				&& !canEnter(targetSector, unit)) {
			return new Result<None>("Unit " + unit
					+ " can not attack or enter " + targetSector.getCoords(),
					false);
		}

		return Result.trueInstance();
	}

	private boolean canEnter(WorldSector targetSector, Unit unit) {
		if (unit.isNavy() && isInlandCity(targetSector)) {
			return false;
		}
		return targetSector.canEnter(unit);
	}

	public Set<SectorCoords> canReachUnknown(Unit unit) {
		Set<SectorCoords> retval = Sets.newHashSet();
		int range = UnitHelper.range(unit);
		for (int x = unit.getX() - range; x <= unit.getX() + range; ++x) {
			for (int y = unit.getY() - range; y <= unit.getY() + range; ++y) {
				SectorCoords coords = new SectorCoords(worldView.size(), x, y);
				if (worldView.getWorldSector(coords) == null) {
					retval.add(coords);
				}
			}
		}
		return retval;
	}

}
