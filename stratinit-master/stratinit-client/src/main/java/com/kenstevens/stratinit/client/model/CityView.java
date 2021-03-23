package com.kenstevens.stratinit.client.model;

import com.kenstevens.stratinit.dto.SICityUpdate;
import com.kenstevens.stratinit.type.SectorType;

public class CityView extends City {
    private boolean deleted;
    private boolean isNew;

    private static final long serialVersionUID = 1L;

    public CityView(Game game, NationView nation, SICityUpdate sicity) {
        Sector sector = new Sector(game, sicity.coords, SectorType.PLAYER_CITY);
        this.setCityPK(new CityPK(sector));
        this.setNation(nation);
        this.setBuild(sicity.build, sicity.lastUpdated);
        this.setNextBuild(sicity.nextBuild);
        this.setSwitchOnTechChange(sicity.switchOnTechChange);
        if (sicity.nextCoords != null) {
            this.setCityMove(new CityMove(this, sicity.nextCoords));
        }
    }

	public CityView copyFrom(CityView city) {
		Sector sector = new Sector(city.getParentGame(), city.getCoords(), SectorType.PLAYER_CITY);
		this.setCityPK(new CityPK(sector));
		this.setNation(city.getNation());
		this.setBuild(city.getBuild(), city.getLastUpdated());
		this.setNextBuild(city.getNextBuild());
		this.setSwitchOnTechChange(city.isSwitchOnTechChange());
		if (city.getCityMove() == null) {
			this.setCityMove(null);
		} else {
			this.setCityMove(new CityMove(this, city.getCityMove().getCoords()));
		}
		return this;
	}
	
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public boolean isNew() {
		return isNew;
	}
}
