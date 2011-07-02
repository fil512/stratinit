package com.kenstevens.stratinit.client.gwt.model;


public class UnitListGridRecord extends StratInitListGridRecord<Integer, GWTUnit> {
	private GWTUnit unit;

	public UnitListGridRecord(GWTUnit unit) {
		setValues(unit);
	}

	@Override
	public final void setValues(GWTUnit unit) {
		this.unit = unit;
		setId(unit.id);
		setCoords(unit.coords);
		setUnitType(unit.unitType);
		setMobility(unit.mobility);
		setHp(unit.hp);
		setAmmo(unit.ammo);
		setFuel(unit.fuel);
	}

	public final void setId(int id) {
        setAttribute("id", id);
    }
	public final void setCoords(GWTSectorCoords coords) {
        setAttribute("coords", coords.x+","+coords.y);
        setAttribute("x", coords.x);
        setAttribute("y", coords.y);
    }
	public final void setUnitType(GWTUnitType type) {
        setAttribute("type", type.toString());
    }
	public final void setMobility(int mobility) {
        setAttribute("mobility", mobility);
    }
	public final void setHp(int hp) {
        setAttribute("hp", hp);
    }
	public final void setAmmo(int ammo) {
        setAttribute("ammo", ammo);
    }
	public final void setFuel(int fuel) {
        setAttribute("fuel", fuel);
    }

	public GWTUnit getUnit() {
		return unit;
	}
}
