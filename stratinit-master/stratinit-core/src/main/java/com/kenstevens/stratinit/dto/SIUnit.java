package com.kenstevens.stratinit.dto;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;


@Schema(description = "Unit state including position, type, HP, and private data (mobility, ammo, fuel) for the owning player")
public class SIUnit implements StratInitDTO {
    private static final long serialVersionUID = 1L;
    public int id;
    public SectorCoords coords;
    public SectorCoords nextCoords;
    public int nationId;
    public UnitType type;
    public int mobility;
    public int hp;
    public int ammo;
    public int fuel;
	public Date lastUpdated;
	public Date created;

	public SIUnit() {}
	
	public SIUnit(Unit unit) {
		id = unit.getId();
		coords = unit.getCoords();
		nationId = unit.getNation().getNationId();
		type = unit.getType();
		hp = unit.getHp();
	}

	public void addPrivateData(Nation nation, Unit unit) {
		if (nation.equals(unit.getNation())) {
			id = unit.getId();
			mobility = unit.getMobility();
			ammo = unit.getAmmo();
			fuel = unit.getFuel();
			lastUpdated = unit.getLastUpdated();
			created = unit.getCreated();
			if (unit.getUnitMove() != null) {
				nextCoords = unit.getUnitMove().getCoords();
			}
		}
	}
}
