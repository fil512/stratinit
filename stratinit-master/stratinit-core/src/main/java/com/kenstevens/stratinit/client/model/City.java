package com.kenstevens.stratinit.client.model;

import com.kenstevens.stratinit.type.CityType;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.apache.commons.lang3.time.DateUtils;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.util.Date;


@Entity
public class City extends GameUpdatable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private CityPK cityPK;

	@ManyToOne
	private Nation nation;
	private CityType type;

	private UnitType build;

	private UnitType nextBuild;

	private boolean switchOnTechChange;
	
	@Transient
	private CityMove cityMove;

	public City() {
	}

	public City(Sector sector, Nation nation, UnitType build) {
		this.cityPK =new CityPK(sector);
		this.nation = nation;
		this.setBuild(build);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cityPK == null) ? 0 : cityPK.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		City other = (City) obj;
		if (cityPK == null) {
			return other.cityPK == null;
		} else return cityPK.equals(other.cityPK);
	}

	private void setType(CityType type) {
		this.type = type;
	}

	public CityType getType() {
		return type;
	}

	private void setBuild(UnitType build) {
		Date lastUpdated;
		if (getParentGame().hasStarted()) {
			lastUpdated = new Date();
		} else {
			lastUpdated = this.getParentGame().getStartTime();
		}
		setBuild(build, lastUpdated);
	}

	public void setBuild(UnitType build, Date lastUpdated) {
		this.build = build;
		this.setLastUpdated(lastUpdated);
		this.setType(UnitBase.getUnitBase(build).getBuiltIn());
	}

	public UnitType getBuild() {
		return build;
	}

	public void setNextBuild(UnitType nextBuild) {
		this.nextBuild = nextBuild;
	}

	public UnitType getNextBuild() {
		return nextBuild;
	}

	public void setNation(Nation nation) {
		this.nation = nation;
	}

	public Nation getNation() {
		return nation;
	}

	public void setCityPK(CityPK cityPK) {
		this.cityPK = cityPK;
	}

	public CityPK getCityPK() {
		return cityPK;
	}

	@Override
	public long getUpdatePeriodMilliseconds() {
		return UnitBase.getUnitBase(getBuild()).getProductionTime() * DateUtils.MILLIS_PER_HOUR;
	}

	public SectorCoords getCoords() {
		return cityPK.getCoords();
	}

	public int getGameId() {
		return nation.getGame().getId();
	}

	public int getX() {
		return getCoords().x;
	}

	public int getY() {
		return getCoords().y;
	}

	public String getBuildingString() {
		return build == null ? "" : build.toString();
	}

	public String getNextString() {
		return nextBuild == null ? "" : nextBuild.toString();
	}

	@Override
	public String toString() {
		return type + " at " + getCoords();
	}

	@Override
	public Object getKey() {
		return cityPK;
	}

	@Override
	public Game getParentGame() {
		return nation.getGame();
	}

	public boolean isRadar() {
		return type == CityType.TECH || type == CityType.BASE;
	}

	public boolean isAirport() {
		return type == CityType.AIRPORT;
	}

	public int getSightRadius() {
		int radius = Constants.CITY_VIEW_DISTANCE;
		if (isRadar()) {
			radius = getNation().getRadarRadius();
		} else if (isAirport()) {
			radius = Constants.AIRPORT_VIEW_RADIUS;
		}
		return radius;
	}

	public boolean isBase() {
		return type == CityType.BASE;
	}

	public boolean isPort() {
		return type == CityType.PORT;
	}

	public void setSwitchOnTechChange(boolean switchOnTechChange) {
		this.switchOnTechChange = switchOnTechChange;
	}

	public boolean isSwitchOnTechChange() {
		return switchOnTechChange;
	}

	@Override
	public boolean isKeyUnique() {
		return true;
	}

	public void setCityMove(CityMove cityMove) {
		this.cityMove = cityMove;
	}

	public CityMove getCityMove() {
		return cityMove;
	}
}
