package com.kenstevens.stratinit.model;

import java.util.Date;

import com.kenstevens.stratinit.type.CityType;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.SectorType;
import com.kenstevens.stratinit.type.UnitType;

public class WorldSector extends Sector {
	private static final long serialVersionUID = 1L;
	// Used by everything else
	private RelationType myRelation;
	private RelationType theirRelation;
	private boolean holdsFriendlyCarrier = false;
	private boolean holdsMyTransport = false;
	private boolean suppliesLand = false;
	private boolean suppliesNavy = false;
	private boolean holdsMyCapital = false;
	private CityType cityType;
	private UnitType topUnitType;
	private Unit topUnit;
	private Nation nation;
	private int flak = 0;
	private int cannons = 0;
	// Used transiently by Movement
	private boolean canReach;
	private boolean enoughMoves;
	private boolean inSupply;
	private int distance;
	// Only used client side
	private Date lastSeen;
	private boolean visible = false;

	public WorldSector(Sector sector) {
		this(sector.getGame(), sector.getCoords(), sector.getType(), sector.getIsland());
	}

	public WorldSector(Game game, SectorCoords coords, SectorType sectorType, int island) {
		super(game, coords, sectorType);
		this.setIsland(island);
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getDistance() {
		return distance;
	}

	public void setCanReach(boolean canLand) {
		this.canReach = canLand;
	}

	public boolean isCanReach() {
		return canReach;
	}

	public void setEnoughMoves(boolean enoughMoves) {
		this.enoughMoves = enoughMoves;
	}

	public boolean isEnoughMoves() {
		return enoughMoves;
	}

	public boolean canEnter(Unit unit) {
		if (offLimits()) {
			return false;
		}
		if (unit.isAir()) {
			return airCanEnter();
		} else if (unit.isNavy()) {
			return navyCanEnter();
		} else if (unit.isLand()) {
			if (unit.isEngineer()) {
				return landCanEnter() || navyCanEnter();
			} else {
				return landCanEnter();
			}
		} else if (unit.isTech()) {
			return true;
		} else {
			throw new IllegalStateException("Unit " + toString()
					+ " has undefined type");
		}
	}



	public boolean isEmptyCity() {
		return isNeutralCity() || type == SectorType.PLAYER_CITY && isEmpty();
	}

	public boolean isNeutralCity() {
		return type == SectorType.NEUTRAL_CITY;
	}

	public boolean isWasteland() {
		return type == SectorType.WASTELAND;
	}

	public boolean canRefuel(Unit air) {
		if (air.getType() == UnitType.HELICOPTER) {
			return onMyTeam() && (isPlayerCity() || holdsMyCapital);
		}
		if (!isFuelingStation()) {
			return false;
		}
		if (air.isLightAir()) {
			return isFuelingStation();
		}
		return onMyTeam() && isAirport();
	}

	private boolean isFuelingStation() {
		return onMyTeam()
				&& (isAirport() || isBase() || isHoldsFriendlyCarrier());
	}

	private boolean isAirport() {
		return cityType == CityType.AIRPORT;
	}

	private boolean isBase() {
		return cityType == CityType.BASE;
	}

	public boolean airCanEnter() {
		return !airCannotEnter();
	}

	private boolean airCannotEnter() {
		return isNeutralCity() || isWasteland();
	}

	private boolean offLimits() {
		return myRelation != null && theirRelation != null && !onMyTeam();
	}

	public boolean onMyTeam() {
		return myRelation != null && myRelation.isTeam()
				&& theirRelation != null && theirRelation.isTeam();
	}

	public boolean landCanEnter() {
		return type == SectorType.LAND || type == SectorType.PLAYER_CITY
				|| isNeutralCity() || isHoldsMyTransport();
	}

	public boolean navyCanEnter() {
		return isPort() || isEmptyWater();
	}

	private boolean isEmptyWater() {
		return isWater() && !isHoldsShipAtSea();
	}

	public boolean isPort() {
		return cityType == CityType.PORT || cityType == CityType.BASE;
	}

	public void setMyRelation(RelationType relation) {
		this.myRelation = relation;
	}

	public boolean isHoldsShipAtSea() {
		return isWater() && hasNavyUnit();
	}

	public void setHoldsFriendlyCarrier(boolean holdsFriendlyCarrier) {
		this.holdsFriendlyCarrier = holdsFriendlyCarrier;
	}

	public boolean isHoldsFriendlyCarrier() {
		return holdsFriendlyCarrier;
	}

	public String toString() {
		return type.toString().toLowerCase();
	}

	public RelationType getMyRelation() {
		return myRelation;
	}


	public boolean airCanAttack() {
		return myRelation == RelationType.WAR;
	}

	public void setSuppliesLand(boolean suppliesLand) {
		this.suppliesLand = suppliesLand;
	}

	public boolean isSuppliesLand() {
		return suppliesLand;
	}

	public void setSuppliesNavy(boolean suppliesNavy) {
		this.suppliesNavy = suppliesNavy;
	}

	public boolean isSuppliesNavy() {
		return suppliesNavy;
	}

	public boolean supplies(Unit unit) {
		if (unit.isLand()) {
			return suppliesLand;
		}
		if (unit.isNavy()) {
			return suppliesNavy;
		}
		return false;
	}

	public void setTheirRelation(RelationType theirRelation) {
		this.theirRelation = theirRelation;
	}

	public RelationType getTheirRelation() {
		return theirRelation;
	}

	public void setCityType(CityType cityType) {
		this.cityType = cityType;
		if (cityType == CityType.FORT) {
			addFlak(Constants.FORT_FLAK);
			addCannons(Constants.FORT_CANNONS);
		} else if (cityType == CityType.AIRPORT) {
			addFlak(Constants.AIRPORT_FLAK);
		} else if (cityType == CityType.BASE) {
			addFlak(Constants.BASE_FLAK);
			addCannons(Constants.BASE_CANNONS);
		} else if (cityType == CityType.PORT) {
			addCannons(Constants.PORT_CANNONS);
		}
	}

	public CityType getCityType() {
		return cityType;
	}

	public void addFlak(int flak) {
		this.setFlak(this.getFlak() + flak);
	}

	public void addCannons(int cannons) {
		this.setCannons(this.getCannons() + cannons);
	}

	public int getFlak() {
		return flak;
	}

	public int getCannons() {
		return cannons;
	}

	public int getCityDefense(Unit attackingUnit) {
		if (attackingUnit.isAir()) {
			return flak;
		} else if (attackingUnit.isNavy()) {
			return cannons;
		} else {
			return 0;
		}
	}

	public void setNation(Nation nation) {
		this.nation = nation;
	}

	public Nation getNation() {
		return nation;
	}

	public void setHoldsMyCapital(boolean holdsMyCapital) {
		this.holdsMyCapital = holdsMyCapital;
	}

	public boolean isHoldsMyCapital() {
		return holdsMyCapital;
	}


	// TODO REF
	public boolean isMyCity() {
		return isPlayerCity() && isMine();
	}

	public boolean isMine() {
		return myRelation == RelationType.ME;
	}

	// TODO REF
	public boolean isEmpty() {
		return getTopUnitType() == null;
	}

	public boolean isAlly() {
		return myRelation == RelationType.ALLIED;
	}

	public UnitType getTopUnitType() {
		return topUnitType;
	}

	public void setTopUnit(Unit topUnit) {
		this.topUnit = topUnit;
		if (topUnit != null) {
			this.setTopUnitType(topUnit.getType());
		}
	}

	public Unit getTopUnit() {
		return topUnit;
	}

	public boolean hasLandUnit() {
		return getTopUnitType() != null && getUnitBase().isLand();

	}

	public boolean hasAirUnit() {
		return getTopUnitType() != null && getUnitBase().isAir();

	}

	public boolean hasNavyUnit() {
		return getTopUnitType() != null && getUnitBase().isNavy();

	}

	private UnitBase getUnitBase() {
		return UnitBase.getUnitBase(getTopUnitType());
	}

	public boolean isHoldsUnits() {
		return getTopUnitType() != null;
	}

	public void setHoldsMyTransport(boolean holdsTransport) {
		this.holdsMyTransport = holdsTransport;
	}

	public boolean isHoldsMyTransport() {
		return holdsMyTransport;
	}

	public String getDescription() {
		if (isPlayerCity()) {
			if (getCityType() == null) {
				return getPrefix() + toString();
			} else {
				return getPrefix() + getCityType().toString().toLowerCase();
			}
		} else if (getTopUnitType() != null) {
			return getPrefix() + getTopUnitType().toString().toLowerCase();
		} else {
			return toString();
		}
	}

	public String getPrefix() {
		if (nation == null) {
			return "";
		}
		return nation.toString() + " ";
	}

	public void setInSupply(boolean inSupply) {
		this.inSupply = inSupply;
	}

	public boolean isInSupply() {
		return inSupply;
	}

	public boolean isFort() {
		return getCityType() == CityType.FORT || getCityType() == CityType.BASE;
	}

	public void setTopUnitType(UnitType topUnitType) {
		this.topUnitType = topUnitType;
	}

	public void setFlak(int flak) {
		this.flak = flak;
	}

	public void setCannons(int cannons) {
		this.cannons = cannons;
	}

	public void setLastSeen(Date lastSeen) {
		this.lastSeen = lastSeen;
	}

	public Date getLastSeen() {
		return lastSeen;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isVisible() {
		return visible;
	}

	public void pickTopUnit(Unit unit) {
		if (unit.isNavy()) {
			setTopUnit(unit);
		} else if (unit.isAir() && getTopUnitType() == null
				|| hasLandUnit()) {
			setTopUnit(unit);
		} else if (unit.isLand() && getTopUnitType() == null) {
			setTopUnit(unit);
		} else if (getTopUnitType() == null) {
			setTopUnit(unit);
		}
	}

}
